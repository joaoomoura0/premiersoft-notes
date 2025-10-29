const monthNames = ["Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"];
let currentDate = new Date();
let currentOccurrences = [];

function getDescricaoAmigavel(enumValue) {
    return TIPO_OCORRENCIA_MAP[enumValue] || enumValue;
}

function formatDate(date){
    const y = date.getFullYear();
    const m = String(date.getMonth()+1).padStart(2,'0');
    const d = String(date.getDate()).padStart(2,'0');
    return `${y}-${m}-${d}`;
}

function renderCalendar(){
    const month = currentDate.getMonth();
    const year = currentDate.getFullYear();
    const title = document.getElementById('currentMonthYear');
    if(title) title.textContent = `${monthNames[month]} de ${year}`;

    const calendarGrid = document.getElementById('calendarGrid');
    if(!calendarGrid) return;
    calendarGrid.innerHTML = '';

    const firstDayOfMonth = new Date(year, month, 1).getDay();
    const lastDayOfMonth = new Date(year, month + 1, 0).getDate();

    for(let i=0;i<firstDayOfMonth;i++){
        const emptyCell = document.createElement('div');
        emptyCell.classList.add('day-cell','empty-cell');
        calendarGrid.appendChild(emptyCell);
    }

    for(let day=1; day<=lastDayOfMonth; day++){
        const dayCell = document.createElement('div');
        dayCell.classList.add('day-cell');
        dayCell.dataset.day = String(day).padStart(2,'0');
        dayCell.dataset.date = formatDate(new Date(year, month, day));
        const dayNumber = document.createElement('span');
        dayNumber.classList.add('day-number');
        dayNumber.textContent = day;
        dayCell.appendChild(dayNumber);
        calendarGrid.appendChild(dayCell);
    }

    const today = new Date();
    if(today.getMonth() === month && today.getFullYear() === year){
        const todayDay = String(today.getDate()).padStart(2,'0');
        const todayCell = calendarGrid.querySelector(`.day-cell[data-day="${todayDay}"]`);
        if(todayCell) todayCell.classList.add('today');
    }

    loadOccurrencesForMonth(month + 1, year);
}

document.addEventListener('click', function(e){
    const dayCell = e.target.closest('.day-cell');
    if(dayCell && !dayCell.classList.contains('empty-cell')){
        const date = dayCell.dataset.date;
        if(date) openDayModal(date);
        return;
    }

    if(e.target.closest('.close-btn')){
        closeModal();
        return;
    }

    if(e.target.id === 'openFormBtn'){
        openForm();
        return;
    }

    if(e.target.id === 'cancelFormBtn'){
        cancelForm();
        return;
    }

    if(e.target.id === 'deleteOcurrenceBtn'){
        deleteOccurrence();
        return;
    }

    if(e.target.closest('.btn-back-list')){
        showOccurrenceList();
        return;
    }
});

const prevBtn = document.getElementById('prevMonth');
const nextBtn = document.getElementById('nextMonth');
if(prevBtn) prevBtn.addEventListener('click', ()=>{ currentDate.setMonth(currentDate.getMonth()-1); renderCalendar(); });
if(nextBtn) nextBtn.addEventListener('click', ()=>{ currentDate.setMonth(currentDate.getMonth()+1); renderCalendar(); });

function loadOccurrencesForMonth(month, year){
    currentOccurrences = [];
    fetch(`/diario/api/mes?mes=${month}&ano=${year}`)
        .then(r => r.ok ? r.json() : Promise.reject('Erro ao carregar dados'))
        .then(data => {
            currentOccurrences = data;
            data.forEach(o => {
                const cell = document.querySelector(`.day-cell[data-date="${o.data}"]`);
                if(cell) cell.classList.add('com-ocorrencia');
            });
        })
        .catch(e => console.error(e));
}

function openDayModal(dateString){
    const listContainer = document.getElementById('occurrenceListContainer');
    const formContainer = document.getElementById('occurrenceFormContainer');
    const deleteBtn = document.getElementById('deleteOcurrenceBtn');
    const modalTitle = document.getElementById('modalTitle');
    const ocorrenciaData = document.getElementById('ocorrenciaData');
    const occurrenceList = document.getElementById('occurrenceList');
    const noOccurrencesMessage = document.getElementById('noOccurrencesMessage');
    const modalElement = document.getElementById('occurrenceModal');

    if(!modalElement || !occurrenceList || !ocorrenciaData || !modalTitle) return;

    if(listContainer) listContainer.style.display = 'block';
    if(formContainer) formContainer.style.display = 'none';
    if(deleteBtn) deleteBtn.style.display = 'none';

    const displayDate = dateString.split('-').reverse().join('/');
    modalTitle.textContent = `Ocorrências em ${displayDate}`;
    ocorrenciaData.value = dateString;

    const dailyOccurrences = currentOccurrences.filter(o => o.data === dateString);
    occurrenceList.innerHTML = '';

    if(dailyOccurrences.length > 0){
        if(noOccurrencesMessage) noOccurrencesMessage.style.display = 'none';
        dailyOccurrences.forEach(o => {
            const card = document.createElement('div');
            card.classList.add('occurrence-card');
            card.dataset.id = o.id;

            const statusClass = getStatusClass(o.status);

            card.innerHTML = `
                <div class="occurrence-card-header">
                    <span class="occurrence-badge ${statusClass}">${o.status || 'N/A'}</span>
                    <span class="occurrence-tipo"><i class="fas fa-tag"></i> ${o.tipo || 'N/A'}</span>
                </div>
                <div class="occurrence-card-body">
                    <div class="occurrence-info">
                        <i class="fas fa-user"></i>
                        <span><strong>Colaborador:</strong> ${o.colaborador?.nomeCompleto || 'N/A'}</span>
                    </div>
                    ${o.account ? `
                    <div class="occurrence-info">
                        <i class="fas fa-briefcase"></i>
                        <span><strong>Account:</strong> ${o.account}</span>
                    </div>
                    ` : ''}
                    ${o.origemTipo ? `
                    <div class="occurrence-info">
                        <i class="fas fa-building"></i>
                        <span><strong>Origem:</strong> ${o.origemTipo}</span>
                    </div>
                    ` : ''}
                    ${o.clockifyCliente ? `
                    <div class="occurrence-info">
                        <i class="fas fa-clock"></i>
                        <span><strong>Cliente Clockify:</strong> ${o.clockifyCliente}</span>
                    </div>
                    ` : ''}
                    ${o.descricao ? `
                    <div class="occurrence-description">
                        <i class="fas fa-file-alt"></i>
                        <span>${truncateText(o.descricao, 80)}</span>
                    </div>
                    ` : ''}
                </div>
                <div class="occurrence-card-footer">
                    <button class="btn-view-details" onclick="viewOccurrenceDetails(${o.id})">
                        <i class="fas fa-eye"></i> Ver Detalhes
                    </button>
                </div>
            `;

            occurrenceList.appendChild(card);
        });
    } else {
        if(noOccurrencesMessage) noOccurrencesMessage.style.display = 'block';
    }

    if(window.jQuery && $.fn.select2){
        $('.select-filtro').each(function(){
            if(!$(this).hasClass('select2-hidden-accessible')){
                $(this).select2({ dropdownParent: $('#occurrenceModal') });
            }
        });
    }

    modalElement.style.display = 'flex';
}

function getStatusClass(status) {
    const statusMap = {
        'PENDENTE': 'status-pendente',
        'EM_ANDAMENTO': 'status-andamento',
        'CONCLUIDO': 'status-concluido',
        'CANCELADO': 'status-cancelado'
    };
    return statusMap[status] || 'status-default';
}

function truncateText(text, maxLength) {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

function viewOccurrenceDetails(id) {
    fetch(`/diario/api/detalhe/${id}`)
        .then(r => r.ok ? r.json() : Promise.reject('Erro ao carregar detalhes'))
        .then(ocorrencia => {
            showOccurrenceDetail(ocorrencia);
        })
        .catch(e => {
            console.error(e);
            alert('Não foi possível carregar os detalhes da ocorrência.');
        });
}

function showOccurrenceDetail(ocorrencia) {
    const listContainer = document.getElementById('occurrenceListContainer');
    const formContainer = document.getElementById('occurrenceFormContainer');

    if(listContainer) listContainer.style.display = 'none';
    if(formContainer) formContainer.style.display = 'none';

    let detailContainer = document.getElementById('occurrenceDetailContainer');
    if (!detailContainer) {
        detailContainer = document.createElement('div');
        detailContainer.id = 'occurrenceDetailContainer';
        document.querySelector('.modal-body').appendChild(detailContainer);
    }

    const statusClass = getStatusClass(ocorrencia.status);

    detailContainer.innerHTML = `
        <div class="detail-header">
            <button class="btn-back-list">
                <i class="fas fa-arrow-left"></i> Voltar
            </button>
            <h4><i class="fas fa-info-circle"></i> Detalhes da Ocorrência</h4>
        </div>
        
        <div class="detail-content">
            <div class="detail-card">
                <div class="detail-row">
                    <span class="detail-label"><i class="fas fa-flag"></i> Status</span>
                    <span class="occurrence-badge ${statusClass}">${ocorrencia.status || 'N/A'}</span>
                </div>
                
                <div class="detail-row">
                    <span class="detail-label"><i class="fas fa-tag"></i> Tipo</span>
                    <span class="detail-value">${ocorrencia.tipo || 'N/A'}</span>
                </div>
                
                <div class="detail-row">
                    <span class="detail-label"><i class="fas fa-calendar"></i> Data</span>
                    <span class="detail-value">${ocorrencia.data ? ocorrencia.data.split('-').reverse().join('/') : 'N/A'}</span>
                </div>
                
                <div class="detail-row">
                    <span class="detail-label"><i class="fas fa-user"></i> Colaborador</span>
                    <span class="detail-value">${ocorrencia.colaborador?.nomeCompleto || 'N/A'}</span>
                </div>
                
                ${ocorrencia.account ? `
                <div class="detail-row">
                    <span class="detail-label"><i class="fas fa-briefcase"></i> Account</span>
                    <span class="detail-value">${ocorrencia.account}</span>
                </div>
                ` : ''}
                
                ${ocorrencia.origemTipo ? `
                <div class="detail-row">
                    <span class="detail-label"><i class="fas fa-building"></i> Origem</span>
                    <span class="detail-value">${ocorrencia.origemTipo}</span>
                </div>
                ` : ''}
                
                ${ocorrencia.clockifyCliente ? `
                <div class="detail-row">
                    <span class="detail-label"><i class="fas fa-clock"></i> Cliente Clockify</span>
                    <span class="detail-value">${ocorrencia.clockifyCliente}</span>
                </div>
                ` : ''}
                
                ${ocorrencia.descricao ? `
                <div class="detail-row detail-description">
                    <span class="detail-label"><i class="fas fa-file-alt"></i> Descrição</span>
                    <p class="detail-value">${ocorrencia.descricao}</p>
                </div>
                ` : ''}
            </div>
            
            <div class="detail-actions">
                <button class="btn btn-primary" onclick="editOccurrenceFromDetail(${ocorrencia.id})">
                    <i class="fas fa-edit"></i> Editar
                </button>
                <button class="btn btn-danger" onclick="deleteOccurrenceFromDetail(${ocorrencia.id})">
                    <i class="fas fa-trash"></i> Excluir
                </button>
            </div>
        </div>
    `;

    detailContainer.style.display = 'block';
}

function showOccurrenceList() {
    const listContainer = document.getElementById('occurrenceListContainer');
    const detailContainer = document.getElementById('occurrenceDetailContainer');

    if(listContainer) listContainer.style.display = 'block';
    if(detailContainer) detailContainer.style.display = 'none';
}

function editOccurrenceFromDetail(id) {
    editOccurrence(id);
}

function deleteOccurrenceFromDetail(id) {
    if(!confirm('Tem certeza que deseja excluir esta ocorrência permanentemente?')) return;

    fetch(`/diario/remover/${id}`, { method: 'POST' })
        .then(r => {
            if(!r.ok) throw new Error('Erro ao excluir');
            alert('Ocorrência excluída com sucesso!');
            closeModal();
            renderCalendar();
        })
        .catch(e => {
            console.error(e);
            alert('Erro ao excluir ocorrência.');
        });
}

function closeModal(){
    const modal = document.getElementById('occurrenceModal');
    if(modal) modal.style.display = 'none';
    const form = document.getElementById('occurrenceForm');
    if(form) form.reset();

    const detailContainer = document.getElementById('occurrenceDetailContainer');
    if(detailContainer) detailContainer.style.display = 'none';
}

function openForm(){
    const idField = document.getElementById('ocorrenciaId');
    if(idField) idField.value = '';
    const form = document.getElementById('occurrenceForm');
    if(form) form.reset();
    const formContainer = document.getElementById('occurrenceFormContainer');
    const listContainer = document.getElementById('occurrenceListContainer');
    const detailContainer = document.getElementById('occurrenceDetailContainer');

    if(formContainer) formContainer.style.display = 'block';
    if(listContainer) listContainer.style.display = 'none';
    if(detailContainer) detailContainer.style.display = 'none';

    const deleteBtn = document.getElementById('deleteOcurrenceBtn');
    if(deleteBtn) deleteBtn.style.display = 'none';
}

function cancelForm(){
    const formContainer = document.getElementById('occurrenceFormContainer');
    const listContainer = document.getElementById('occurrenceListContainer');
    if(formContainer) formContainer.style.display = 'none';
    if(listContainer) listContainer.style.display = 'block';
}

function editOccurrence(id){
    fetch(`/diario/api/detalhe/${id}`)
        .then(r => r.ok ? r.json() : Promise.reject('Erro ao carregar detalhes'))
        .then(ocorrencia => {
            const idField = document.getElementById('ocorrenciaId');
            const dataField = document.getElementById('ocorrenciaData');
            const colaboradorField = document.getElementById('colaboradorId');
            const accountField = document.getElementById('account');
            const origemField = document.getElementById('origemTipo');
            const clockifyField = document.getElementById('clockifyCliente');
            const tipoField = document.getElementById('tipo');
            const statusField = document.getElementById('status');
            const descricaoField = document.getElementById('descricao');

            if(idField) idField.value = ocorrencia.id;
            if(dataField) dataField.value = ocorrencia.data;
            if(colaboradorField) colaboradorField.value = ocorrencia.colaborador?.id || '';
            if(accountField) accountField.value = ocorrencia.account || '';
            if(origemField) origemField.value = ocorrencia.origemTipo || '';
            if(clockifyField) clockifyField.value = ocorrencia.clockifyCliente || '';
            if(tipoField) tipoField.value = ocorrencia.tipo || '';
            if(statusField) statusField.value = ocorrencia.status || '';
            if(descricaoField) descricaoField.value = ocorrencia.descricao || '';

            if(window.jQuery && $.fn.select2){
                $('#colaboradorId').val(ocorrencia.colaborador?.id || '').trigger('change');
                $('#account').val(ocorrencia.account || '').trigger('change');
                $('#origemTipo').val(ocorrencia.origemTipo || '').trigger('change');
                $('#clockifyCliente').val(ocorrencia.clockifyCliente || '').trigger('change');
            }

            const formContainer = document.getElementById('occurrenceFormContainer');
            const listContainer = document.getElementById('occurrenceListContainer');
            const detailContainer = document.getElementById('occurrenceDetailContainer');
            const deleteBtn = document.getElementById('deleteOcurrenceBtn');

            if(formContainer) formContainer.style.display = 'block';
            if(listContainer) listContainer.style.display = 'none';
            if(detailContainer) detailContainer.style.display = 'none';
            if(deleteBtn) deleteBtn.style.display = 'block';
        })
        .catch(e => {
            console.error(e);
            alert('Não foi possível carregar os detalhes para edição.');
        });
}

function deleteOccurrence(){
    const id = document.getElementById('ocorrenciaId')?.value;
    if(!id) return;
    if(!confirm('Tem certeza que deseja excluir esta ocorrência permanentemente?')) return;

    fetch(`/diario/remover/${id}`, { method: 'POST' })
        .then(r => {
            if(!r.ok) throw new Error('Erro ao excluir');
            alert('Ocorrência excluída com sucesso!');
            closeModal();
            renderCalendar();
        })
        .catch(e => {
            console.error(e);
            alert('Erro ao excluir ocorrência.');
        });
}

document.addEventListener('DOMContentLoaded', function(){
    if(window.jQuery && $.fn.select2){
        $('.select-filtro').each(function(){
            if(!$(this).hasClass('select2-hidden-accessible')){
                $(this).select2({ dropdownParent: $('#occurrenceModal') });
            }
        });
    }
    const menuToggle = document.getElementById('menu-toggle');
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('main-content');
    if(menuToggle && sidebar && mainContent){
        menuToggle.addEventListener('click', ()=>{ sidebar.classList.toggle('active'); mainContent.classList.toggle('shifted'); });
    }
    renderCalendar();

    const origemSelect = document.getElementById('origemTipo');
    const clockifyContainer = document.getElementById('clockifyClientesContainer');
    const clockifySelect = document.getElementById('clockifyCliente');

    if (origemSelect && clockifyContainer && clockifySelect) {
        function toggleClockify() {
            if (origemSelect.value === 'Clockify') {
                clockifyContainer.style.display = 'block';
                clockifySelect.required = true;
            } else {
                clockifyContainer.style.display = 'none';
                clockifySelect.required = false;
                clockifySelect.value = '';
            }
        }

        $(origemSelect).on('change.select2', toggleClockify);
        toggleClockify();
    }
});