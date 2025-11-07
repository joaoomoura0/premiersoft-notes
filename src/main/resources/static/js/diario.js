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

    if(e.target.closest('.btn-back-list')){
        showOccurrenceList();
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

    const closeBtn = e.target.closest('.close-btn');
    if(closeBtn){
        if (closeBtn.closest('#occurrenceModal')) {
            closeModal();

        } else if (closeBtn.closest('#periodOccurrenceModal')) {
            closePeriodModal();
        }
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

    let searchContainer = document.getElementById('searchContainer');

    if (!searchContainer) {
        searchContainer = document.createElement('div');
        searchContainer.id = 'searchContainer';
        searchContainer.innerHTML = `
            <div class="form-group" style="margin-bottom: 15px;">
                <label for="searchColaborador">
                    <i class="fas fa-search"></i> Pesquisar Colaborador:
                </label>
                <input type="text" id="searchColaborador" 
                       placeholder="Digite o nome do Colaborador..." 
                       style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
            </div>
        `;
        occurrenceList.before(searchContainer);
    } else {

        searchContainer.style.display = 'block';

        const searchInput = document.getElementById('searchColaborador');
        if (searchInput) searchInput.value = '';
    }

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

    const searchInput = document.getElementById('searchColaborador');
    if (searchInput) {
        searchInput.removeEventListener('input', filterOccurrences);
        searchInput.addEventListener('input', filterOccurrences);
        filterOccurrences();
    }
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
    console.log('viewOccurrenceDetails chamado para ID:', id);

    fetch(`/diario/api/detalhe/${id}`)
        .then(r => {
            if (!r.ok) {
                console.error('ERRO HTTP:', r.status, r.statusText);
                throw new Error('Erro ao carregar detalhes: Status ' + r.status);
            }
            return r.json();
        })
        .then(ocorrencia => {

            console.log('Ocorrência carregada:', ocorrencia);

            console.log('Dados da Ocorrência Recebidos:', ocorrencia);
            showOccurrenceDetail(ocorrencia);
        })
        .catch(e => {
            console.error('Falha ao processar a ocorrência:', e);
            alert('Não foi possível carregar os detalhes da ocorrência. Verifique o console.');
        });
}

function showOccurrenceDetail(ocorrencia) {
    const listContainer = document.getElementById('occurrenceListContainer');
    const formContainer = document.getElementById('occurrenceFormContainer');

    if(listContainer) listContainer.style.display = 'none';
    if(formContainer) formContainer.style.display = 'none';

    let detailContainer = document.getElementById('occurrenceDetailContainer');

    const occurrenceModalBody = document.querySelector('#occurrenceModal .modal-body');

    if (!detailContainer) {
        detailContainer = document.createElement('div');
        detailContainer.id = 'occurrenceDetailContainer';
        if (occurrenceModalBody) {
            occurrenceModalBody.appendChild(detailContainer);
        } else {
            console.error('O corpo do modal de ocorrência não foi encontrado.');
            return;
        }
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

    const fieldsToEnable = ['colaboradorId', 'account', 'origemTipoPeriodo', 'clockifyCliente', 'tipo', 'status', 'descricao'];
    fieldsToEnable.forEach(id => {
        const field = document.getElementById(id);
        if(field) field.disabled = false;
    });

    $('#colaboradorId').prop('disabled', false).trigger('change');
    $('#account').prop('disabled', false).trigger('change');
    $('#origemTipoPeriodo').prop('disabled', false).trigger('change');
    $('#clockifyCliente').prop('disabled', false).trigger('change');
    $('#tipo').prop('disabled', false).trigger('change');
    $('#status').prop('disabled', false).trigger('change');

    const detailContainer = document.getElementById('occurrenceDetailContainer');
    if(detailContainer) detailContainer.style.display = 'none';
}

function openForm(){
    const idField = document.getElementById('ocorrenciaId');
    if(idField) idField.value = '';
    const form = document.getElementById('occurrenceForm');
    if(form) form.reset();

    const fieldsToEnable = ['colaboradorId', 'account', 'origemTipoPeriodo', 'clockifyCliente', 'tipo', 'status', 'descricao'];
    fieldsToEnable.forEach(id => {
        const field = document.getElementById(id);
        if(field) field.disabled = false;
    });

    $('#colaboradorId').prop('disabled', false).trigger('change');
    $('#account').prop('disabled', false).trigger('change');
    $('#origemTipo').prop('disabled', false).trigger('change');
    $('#clockifyCliente').prop('disabled', false).trigger('change');
    $('#tipo').prop('disabled', false).trigger('change');
    $('#status').prop('disabled', false).trigger('change');

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

function editOccurrence(id) {
    fetch(`/diario/api/detalhe/${id}`)
        .then(r => r.ok ? r.json() : Promise.reject('Erro ao carregar detalhes'))
        .then(ocorrencia => {
            const idField = document.getElementById('ocorrenciaId');
            const dataField = document.getElementById('ocorrenciaData');
            const colaboradorField = document.getElementById('colaboradorId');
            const accountField = document.getElementById('account');
            const origemField = document.getElementById('origemTipo'); // ✅ campo correto
            const clockifyField = document.getElementById('clockifyCliente');
            const tipoField = document.getElementById('tipo');
            const statusField = document.getElementById('status');
            const descricaoField = document.getElementById('descricao');

            if (idField) idField.value = ocorrencia.id;
            if (dataField) dataField.value = ocorrencia.data;
            if (colaboradorField) colaboradorField.value = ocorrencia.colaborador?.id || '';
            if (accountField) accountField.value = ocorrencia.account || '';
            if (origemField) origemField.value = ocorrencia.origemTipo || '';
            if (clockifyField) clockifyField.value = ocorrencia.clockifyCliente || '';
            if (tipoField) tipoField.value = ocorrencia.tipo || '';
            if (statusField) statusField.value = ocorrencia.status || '';
            if (descricaoField) descricaoField.value = ocorrencia.descricao || '';

            if (window.jQuery && $.fn.select2) {
                $('#colaboradorId').val(ocorrencia.colaborador?.id || '').trigger('change');
                $('#account').val(ocorrencia.account || '').trigger('change');
                $('#tipo').val(ocorrencia.tipo || '').trigger('change');
                $('#status').val(ocorrencia.status || '').trigger('change');

                // 🔥 garante que o Select2 da origem renderizou antes de setar
                setTimeout(() => {
                    $('#origemTipo').val(ocorrencia.origemTipo || '').trigger('change');
                    $('#clockifyCliente').val(ocorrencia.clockifyCliente || '').trigger('change');

                    // ✅ agora sim chama o toggleClockify sem quebrar o form
                    toggleClockify();

                    // 🔒 bloqueia os campos após o valor estar certo
                    $('#colaboradorId, #account, #origemTipo, #clockifyCliente, #tipo')
                        .prop('disabled', true)
                        .trigger('change');
                    $('#status').prop('disabled', false);
                }, 200);
            }

            // ✅ chama toggleClockify depois de definir o valor da origem
            // toggleClockify();

            // agora sim desabilita os selects
            $('#colaboradorId, #account, #origemTipo, #clockifyCliente, #tipo')
                .prop('disabled', true)
                .trigger('change');
            $('#status').prop('disabled', false);

            const formContainer = document.getElementById('occurrenceFormContainer');
            const listContainer = document.getElementById('occurrenceListContainer');
            const detailContainer = document.getElementById('occurrenceDetailContainer');
            const deleteBtn = document.getElementById('deleteOcurrenceBtn');

            if (formContainer) formContainer.style.display = 'block';
            if (listContainer) listContainer.style.display = 'none';
            if (detailContainer) detailContainer.style.display = 'none';
            if (deleteBtn) deleteBtn.style.display = 'block';
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

const periodModalElement = document.getElementById('periodOccurrenceModal');

function closePeriodModal() {
    if (periodModalElement) {
        periodModalElement.style.display = 'none';
        const form = document.getElementById('periodForm');
        if (form) form.reset();

        if(window.jQuery && $.fn.select2){
            $('#periodForm .select-filtro').val('').trigger('change');
            toggleClockifyPeriodo();
        }
    }
}

function toggleClockifyPeriodo() {
    const origemSelect = document.getElementById('origemTipoPeriodo');
    const clockifyContainer = document.getElementById('clockifyClientesPeriodoContainer');
    const clockifySelect = document.getElementById('clockifyClientePeriodo');

    if (origemSelect && clockifyContainer && clockifySelect) {
        if (origemSelect.value === 'Clockify') {
            clockifyContainer.style.display = 'block';
            clockifySelect.required = true;
        } else {
            clockifyContainer.style.display = 'none';
            clockifySelect.required = false;
            if(window.jQuery && $.fn.select2){
                $(clockifySelect).val('').trigger('change');
            } else {
                clockifySelect.value = '';
            }
        }
    }
}

function openPeriodModal() {
    const modal = document.getElementById('periodOccurrenceModal');
    if (modal) {
        modal.style.display = 'flex'; // Abre o modal
        // Certifica-se de que o campo do Clockify está no estado correto
        toggleClockifyPeriodo();

        // Se estiver usando Select2, re-inicializa os selects para garantir
        if(window.jQuery && $.fn.select2){
            $('#periodForm .select-filtro').each(function(){
                // Se ainda não estiver inicializado ou para garantir que o dropdownParent funcione
                if(!$(this).data('select2')){
                    $(this).select2({
                        dropdownParent: $('#periodOccurrenceModal'),
                        width: 'resolve'
                    });
                }
            });
        }
    }
}

function filterOccurrences() {
    const searchInput = document.getElementById('searchColaborador');
    if (!searchInput) return;

    const filterText = searchInput.value.toLowerCase();
    const occurrenceCards = document.querySelectorAll('#occurrenceList .occurrence-card');

    occurrenceCards.forEach(card => {
        // Encontra o nome do colaborador dentro do card.
        // O nome está no <span> do Colaborador:
        // <span><strong>Colaborador:</strong> ${o.colaborador?.nomeCompleto || 'N/A'}</span>
        const collaboratorSpan = card.querySelector('.occurrence-info span');

        // Assume que o primeiro <span> de .occurrence-info contém o nome.
        if (collaboratorSpan && collaboratorSpan.textContent.includes('Colaborador:')) {
            const fullText = collaboratorSpan.textContent;
            // Extrai apenas o nome, removendo "Colaborador:" e espaços
            const collaboratorName = fullText.substring(fullText.indexOf(':') + 1).trim().toLowerCase();

            if (collaboratorName.includes(filterText)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        } else {
            // Se o formato interno for inesperado, pelo menos mostre o card se não houver filtro.
            card.style.display = filterText === '' ? 'block' : 'none';
        }
    });
}

document.addEventListener('DOMContentLoaded', function(){

    if(window.jQuery && $.fn.select2){

        $('#occurrenceModal .select-filtro:not(#colaboradorId)').each(function(){
            if(!$(this).data('select2')){
                $(this).select2({
                    dropdownParent: $('#occurrenceModal'),
                    width: 'resolve'
                });
            }
        });

        $('#periodOccurrenceModal .select-filtro').each(function(){
            if(!$(this).data('select2')){
                $(this).select2({
                    dropdownParent: $('#periodOccurrenceModal'),
                    width: 'resolve'
                });
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


    const origemSelect = document.getElementById('origemTipoPeriodo');
    const clockifyContainer = document.getElementById('clockifyClientesContainer');

    if (origemSelect && clockifyContainer) {

        function toggleClockify() {
            const origemSelect = document.getElementById('origemTipoPeriodo');
            const clockifyContainer = document.getElementById('clockifyClientesPeriodoContainer');
            const clockifySelect = document.getElementById('clockifyClientePeriodo');

            if (!origemSelect || !clockifyContainer) return;

            if (origemSelect.value === 'Clockify') {
                clockifyContainer.style.display = 'block';
                if (clockifySelect) clockifySelect.required = true;
            } else {
                clockifyContainer.style.display = 'none';
                if (clockifySelect) {
                    clockifySelect.required = false;
                    clockifySelect.value = '';
                }
            }
        }

        $(origemSelect).on('change.select2', toggleClockify);
        toggleClockify();
    }


    const origemPeriodoSelect = document.getElementById('origemTipoPeriodo');

    if (origemPeriodoSelect) {
        $(origemPeriodoSelect).on('change.select2', toggleClockifyPeriodo);
        toggleClockifyPeriodo();
    }

    const openPeriodModalBtn = document.getElementById('openPeriodModalBtnSidebar');
    if (openPeriodModalBtn) {
        openPeriodModalBtn.addEventListener('click', function(e) {
            e.preventDefault();
            openPeriodModal();
        });
    }

    document.querySelectorAll('.close-btn-period').forEach(btn => {
        btn.addEventListener('click', closePeriodModal);
    });
});