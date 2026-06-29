if (!localStorage.getItem('lang')) localStorage.setItem('lang', 'en');

let translationJSON;

let options = {}

async function translate() {
    const response = await fetch('translation.json');
    const translations = await response.json();
    translationJSON = translations;

    const langData = translations[localStorage.getItem('lang')];

    document.querySelectorAll('[data-i18n]').forEach(element => {
        const key = element.getAttribute('data-i18n');
        element.innerHTML = langData[key] || key;
    });

    let langDropdown = document.getElementById('lang-drop');
    for (let i = 0; i < translations.length; i++) {
        let option = document.createElement('button');
        option.innerHTML = `<button class="dropdown-option w-full px-4 tpy-3 text-left transition hover:bg-gray-100 dark:hover:bg-gray-800"
                                            data-value="${translations.get(i)}">
                                        ${translations['__language']}
                                    </button>`
        langDropdown.appendChild(option);
        console.log('Added new translation dropdown option: ' + translations.get(i) + '')
    }
}

function getTranslation(key) {
    return translationJSON[localStorage.getItem('lang')][key] || key;
}

async function prepareLanguages() {
    console.log('Preparing languages...');
    const response = await fetch('translation.json');
    const translations = await response.json();
    const langDropdown = document.querySelector('#lang-drop');
    Object.keys(translations).forEach(key => {
        const option = document.createElement('button');
        option.className =
            "dropdown-option w-full px-4 py-3 text-left transition hover:bg-gray-100 dark:hover:bg-gray-800";
        option.dataset.value = key;
        option.textContent =
            translations[key]['__language'];
        langDropdown.appendChild(option);
        console.log(
            'Added new translation dropdown option: ' + key
        );
    });
}

function setOption(key, value) {
    options[key] = value;
}

function generateFinishReport() {
    let section = document.getElementById('review-section');
    let keys = Object.keys(options);
    for (let i = 0; i < keys.length; i++) {
        let key = keys[i];
        let value = options[key];
        section.innerHTML += `<div class="flex items-center justify-between">
                                <div class="text-sm font-medium text-gray-900 dark:text-white">${getTranslation(key)}</div>
                                <div class="text-sm text-gray-500 dark:text-gray-400">${value}</div>
                            </div>`;
    }
}

/*window.addEventListener('beforeunload', (event) => {
    event.preventDefault();
    event.returnValue = '';
    return '';
});*/

const languageDropdown = new Dropdown(
    document.querySelector("#language-dropdown")
);
languageDropdown.element.addEventListener("change", event => {
    localStorage.setItem('lang', event.detail.value);
    setOption('basic.app.language', getTranslation('language.' + event.detail.value));
    translate();
});

const updateChannelDropdown = new Dropdown(
    document.querySelector("#update-channel-dropdown")
);
updateChannelDropdown.element.addEventListener("change", event => {
    localStorage.setItem('updateChannel', event.detail.value);
    setOption('basic.app.updateChannel', getTranslation('updateChannel.' + event.detail.value));
});

const updateBehaviorDropdown = new Dropdown(
    document.querySelector("#update-behavior-dropdown")
);
updateBehaviorDropdown.element.addEventListener("change", event => {
    localStorage.setItem('updateBehavior', event.detail.value);
});


const databaseTypeDropdown = new Dropdown(
    document.querySelector("#database-type-dropdown")
);
databaseTypeDropdown.element.addEventListener("change", event => {
    let warnText = document.getElementById('db-warning');
    let defaultPort = 0;
    if (event.detail.value === "mysql") defaultPort = 3306;
    if (event.detail.value === "mariadb") defaultPort = 3306;
    if (event.detail.value === "postgresql") {
        defaultPort = 5432;
        warnText.innerText = '⚠ Currently, there no full support for PostgreSQL.';
        warnText.classList.remove('hidden');
    }
    if (event.detail.value === "sqlserver") defaultPort = 1433;
    if (event.detail.value === "other") {
        defaultPort = 1234;

        warnText.innerText = '⚠ You must install the JDBC driver as a yarn later to use your database.';
        warnText.classList.remove('hidden');
    }

    document.getElementById('db-port').value = defaultPort;
});

document.getElementById('2nd-factor-enable').addEventListener('change', function() {
    let section = document.getElementById('second-factor-options');
    if (this.checked) {
        section.classList.remove('hidden');
    } else {
        section.classList.add('hidden');
    }
});

document.getElementById('use-auto-update').addEventListener('change', function() {
    if (this.checked) {
        updateBehaviorDropdown.enable();
        updateChannelDropdown.enable();
    } else {
        updateBehaviorDropdown.disable();
        updateChannelDropdown.disable();
    }
});

prepareLanguages().then(() => {
    languageDropdown.updateOptions();
    languageDropdown.enable();
});
translate();