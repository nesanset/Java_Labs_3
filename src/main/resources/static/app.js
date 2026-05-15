const state = {
    selectedMissionId: null
};

const missionList = document.querySelector('#missionList');
const missionFacts = document.querySelector('#missionFacts');
const missionContent = document.querySelector('#missionContent');
const statusBox = document.querySelector('#statusBox');
const importForm = document.querySelector('#importForm');
const missionFile = document.querySelector('#missionFile');
const reportType = document.querySelector('#reportType');

document.querySelector('#refreshButton').addEventListener('click', loadArchive);
document.querySelector('#showReportButton').addEventListener('click', showReport);
importForm.addEventListener('submit', importMission);

loadArchive();

async function loadArchive(){
    try{
        const missions = await requestJson('/api/missions');
        renderArchive(missions);
        setStatus('Архив обновлен.');
    }catch (error){
        setStatus(error.message, true);
    }
}

async function importMission(event){
    event.preventDefault();
    if (!missionFile.files.length){
        setStatus('Выберите файл миссии.', true);
        return;
    }
    const formData = new FormData();
    formData.append('file', missionFile.files[0]);
    try{
        const response = await fetch('/api/missions/import', {
            method:'POST',
            body:formData
        });
        if (!response.ok){
            throw new Error(await readError(response));
        }
        const result = await response.json();
        state.selectedMissionId = result.missionId;
        setStatus(result.message);
        await loadArchive();
        await selectMission(result.missionId);
    }catch (error){
        setStatus(error.message, true);
    }
}

function renderArchive(missions){
    missionList.innerHTML = '';
    if (!missions.length){
        missionList.innerHTML = '<div class="notice">Сохраненных миссий пока нет.</div>';
        return;
    }
    for (const mission of missions){
        const button = document.createElement('button');
        button.className = 'mission-card';
        if (mission.missionId === state.selectedMissionId){
            button.classList.add('active');
        }
        button.type = 'button';
        button.innerHTML = `
            <strong>${escapeHtml(mission.missionId)}</strong>
            <span>Дата: ${escapeHtml(mission.date || '-')}</span>
            <span>Локация: ${escapeHtml(mission.location || '-')}</span>
            <span>Итог: ${escapeHtml(mission.outcome || '-')}</span>
        `;
        button.addEventListener('click', () => selectMission(mission.missionId));
        missionList.appendChild(button);
    }
}

async function selectMission(missionId){
    try{
        const mission = await requestJson(`/api/missions/${encodeURIComponent(missionId)}`);
        state.selectedMissionId = mission.missionId;
        renderFacts(mission);
        missionContent.textContent = JSON.stringify(mission, null, 2);
        document.querySelectorAll('.mission-card').forEach(card => card.classList.remove('active'));
        const active = Array.from(document.querySelectorAll('.mission-card'))
                .find(card => card.textContent.includes(mission.missionId));
        if (active){
            active.classList.add('active');
        }
    }catch (error){
        setStatus(error.message, true);
    }
}

function renderFacts(mission){
    const facts = [
        ['Mission ID', mission.missionId],
        ['Дата', mission.date],
        ['Локация', mission.location],
        ['Итог', mission.outcome],
        ['Урон', mission.damageCost],
        ['Техник', mission.techniques?.length],
        ['Участников', mission.participants?.length]
    ];
    missionFacts.innerHTML = facts.map(([label, value]) => `
        <div class="fact">
            <span>${escapeHtml(label)}</span>
            <strong>${escapeHtml(value ?? '-')}</strong>
        </div>
    `).join('');
}

async function showReport(){
    if (!state.selectedMissionId){
        setStatus('Сначала выберите миссию.', true);
        return;
    }
    try{
        const response = await fetch(reportUrl('textreport'));
        if (!response.ok){
            throw new Error(await readError(response));
        }
        missionContent.textContent = await response.text();
    }catch (error){
        setStatus(error.message, true);
    }
}

function reportUrl(kind){
    const id = encodeURIComponent(state.selectedMissionId);
    const type = encodeURIComponent(reportType.value);
    return `/api/missions/${id}/${kind}?reportType=${type}`;
}

async function requestJson(url){
    const response = await fetch(url);
    if (!response.ok){
        throw new Error(await readError(response));
    }
    return response.json();
}

async function readError(response){
    try{
        const body = await response.json();
        return body.message || body.error || response.statusText;
    }catch (error){
        return response.statusText;
    }
}

function setStatus(message, isError = false){
    statusBox.textContent = message;
    statusBox.classList.toggle('error', isError);
}

function escapeHtml(value){
    return String(value)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#039;');
}
