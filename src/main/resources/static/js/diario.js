// Array para nomes de meses e dias (em português)
const monthNames = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];

// Estado
let currentDate = new Date();
let currentOccurrences = [];

// Funções de Utilidade
function formatDate(date) {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
}

// Lógica do Calendário e Renderização
function renderCalendar() {
    const month = currentDate.getMonth();
    const year = currentDate.getFullYear();

    document.getElementById('currentMonthYear').textContent = `${monthNames[month]} de ${year}`;

    const calendarGrid = document.getElementById('calendarGrid');
    if (!calendarGrid) return;

    calendarGrid.innerHTML = '';

    const firstDayOfMonth = new Date(year, month, 1).getDay();
    const lastDayOfMonth = new Date(year, month + 1, 0).getDate();

    for (let i = 0; i < firstDayOfMonth; i++) {
        const emptyCell = document.createElement('div');
        emptyCell.classList.add('day-cell', 'empty-cell');
        calendarGrid.appendChild(emptyCell);
    }

    for (let day = 1; day <= lastDayOfMonth; day++) {
        const dayCell = document.createElement('div');
        dayCell.classList.add('day-cell');
        dayCell.dataset.day = day;
        dayCell.dataset.date = formatDate(new Date(year, month, day));

        const dayNumber = document.createElement('span');
        dayNumber.classList.add('day-number');
        dayNumber.textContent = day;
        dayCell.appendChild(dayNumber);

        dayCell.addEventListener('click', () => openDayModal(dayCell.dataset.date));

        calendarGrid.appendChild(dayCell);
    }

    loadOccurrencesForMonth(month + 1, year);
}

// Navegação do Calendário
document.getElementById('prevMonth')?.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar();
});

document.getElementById('nextMonth')?.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar();
});


// Comunicação com a API (AJAX/FETCH)
function loadOccurrencesForMonth(month, year) {
    currentOccurrences = [];

    fetch(`/diario/api/mes?mes=${month}&ano=${year}`)
        .then(response => {
            if (!response.ok) throw new Error('Erro ao carregar dados do mês.');
            return response.json();
        })
        .then(data => {
            currentOccurrences = data;

            const daysWithOccurrences = new Set(data.map(o => o.data.substring(8, 10)));

            daysWithOccurrences.forEach(dayStr => {
                const day = parseInt(dayStr, 10);
                const dayCell = document.querySelector(`.day-cell[data-day="${day}"]`);
                if (dayCell) {
                    dayCell.classList.add('com-ocorrencia');
                }
            });
        })
        .catch(error => {
            console.error("Erro ao carregar ocorrências do mês:", error);
        });
}

// Ação principal de abrir o modal
function openDayModal(dateString) {
    const listContainer = document.getElementById('occurrenceListContainer');
    const formContainer = document.getElementById('occurrenceFormContainer');
    const deleteBtn = document.getElementById('deleteOcurrenceBtn');
    const modalTitle = document.getElementById('modalTitle');
    const ocorrenciaData = document.getElementById('ocorrenciaData');
    const occurrenceList = document.getElementById('occurrenceList');
    const noOccurrencesMessage = document.getElementById('noOccurrencesMessage');
    const modalElement = document.getElementById('occurrenceModal');

    if (!listContainer || !formContainer || !deleteBtn || !modalTitle || !ocorrenciaData || !occurrenceList || !noOccurrencesMessage || !modalElement) {
        console.error("ERRO FATAL: Um ou mais elementos do modal não foram encontrados no DOM. Verifique os IDs.");
        return;
    }

    listContainer.style.display = 'block';
    formContainer.style.display = 'none';
    deleteBtn.style.display = 'none';

    const displayDate = dateString.split('-').reverse().join('/');
    modalTitle.textContent = `Ocorrências em ${displayDate}`;
    ocorrenciaData.value = dateString;

    const dailyOccurrences = currentOccurrences.filter(o => o.data === dateString);
    occurrenceList.innerHTML = '';

    if (dailyOccurrences.length > 0) {
        noOccurrencesMessage.style.display = 'none';
        dailyOccurrences.forEach(o => {
            const item = document.createElement('h5');
            item.textContent = `${o.tipo} - ${o.colaborador.nomeCompleto} (${o.status})`;
            item.dataset.id = o.id;
            item.title = "Clique para editar";

            item.addEventListener('click', () => editOccurrence(o.id));
            occurrenceList.appendChild(item);
        });
    } else {
        noOccurrencesMessage.style.display = 'block';
    }

    modalElement.style.display = 'flex';
}

// Fecha o Modal
document.querySelector('.close-btn')?.addEventListener('click', () => {
    document.getElementById('occurrenceModal').style.display = 'none';
    document.getElementById('occurrenceForm').reset();
});

// Ações do Formulário (Nova Ocorrência / Cancelar)
document.getElementById('openFormBtn')?.addEventListener('click', () => {
    document.getElementById('ocorrenciaId').value = '';
    document.getElementById('occurrenceForm').reset();
    document.getElementById('occurrenceFormContainer').style.display = 'block';
    document.getElementById('occurrenceListContainer').style.display = 'none';
    document.getElementById('deleteOcurrenceBtn').style.display = 'none';
});

document.getElementById('cancelFormBtn')?.addEventListener('click', () => {
    document.getElementById('occurrenceFormContainer').style.display = 'none';
    document.getElementById('occurrenceListContainer').style.display = 'block';
});

// Lógica de Edição
function editOccurrence(id) {
    fetch(`/diario/api/detalhe/${id}`)
        .then(response => {
            if (!response.ok) throw new Error('Erro ao carregar detalhes da ocorrência.');
            return response.json();
        })
        .then(ocorrencia => {
            document.getElementById('ocorrenciaId').value = ocorrencia.id;
            document.getElementById('ocorrenciaData').value = ocorrencia.data;
            document.getElementById('colaboradorId').value = ocorrencia.colaborador.id;
            document.getElementById('tipo').value = ocorrencia.tipo;
            document.getElementById('status').value = ocorrencia.status;
            document.getElementById('descricao').value = ocorrencia.descricao;

            if (window.jQuery && $.fn.select2) { // Incluí window.jQuery para ser mais seguro
                // 1. Define o valor no campo de seleção
                $('#colaboradorId').val(ocorrencia.colaborador.id);
                // 2. Aciona o evento de mudança exigido pelo Select2
                $('#colaboradorId').trigger('change');
            }

            document.getElementById('occurrenceFormContainer').style.display = 'block';
            document.getElementById('occurrenceListContainer').style.display = 'none';
            document.getElementById('deleteOcurrenceBtn').style.display = 'block';
        })
        .catch(error => {
            console.error("Erro na Edição:", error);
            alert("Não foi possível carregar os detalhes para edição.");
        });
}

// Lógica de Exclusão
document.getElementById('deleteOcurrenceBtn')?.addEventListener('click', () => {
    const id = document.getElementById('ocorrenciaId').value;
    if (id && confirm("Tem certeza que deseja excluir esta ocorrência permanentemente?")) {
        fetch(`/diario/remover/${id}`, { method: 'POST' })
            .then(response => {
                if (!response.ok) throw new Error('Erro ao excluir a ocorrência.');
                alert("Ocorrência excluída com sucesso!");
                document.getElementById('occurrenceModal').style.display = 'none';
                renderCalendar();
            })
            .catch(error => {
                console.error("Erro ao excluir:", error);
                alert("Erro ao excluir ocorrência. Tente novamente.");
            });
    }
});

document.addEventListener('DOMContentLoaded', () => {
    if ($.fn.select2) {
        $('.select-filtro').select2({
            dropdownParent: $('#occurrenceModal')
        });
    }

    const menuToggle = document.getElementById('menu-toggle');
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('main-content');
    if(menuToggle && sidebar && mainContent) {
        menuToggle.addEventListener('click', () => {
            sidebar.classList.toggle('active');
            mainContent.classList.toggle('shifted');
        });
    }

    renderCalendar();
});

const today = new Date();
const todayDay = today.getDate();
const todayMonth = today.getMonth();
const todayYear = today.getFullYear();

if (todayMonth === currentDate.getMonth() && todayYear === currentDate.getFullYear()) {
    const todayCell = document.querySelector(`.day-cell[data-day="${todayDay}"]`);
    if (todayCell) todayCell.classList.add('today');
}