const pages = document.querySelectorAll(".page");
const navItems = document.querySelectorAll(".nav-item");


function showPage(id) {
    if (id === "finish") {
        generateFinishReport();
    }

    pages.forEach(page => {
        if (page.dataset.page === id) {

            page.classList.remove("hidden");

            page.classList.add(
                "animate-fade"
            );

            updateUrlParam('page', id);
        } else {

            page.classList.add("hidden");
            page.classList.remove("animate-fade");

        }
    });

    navItems.forEach(item => {
        if (item.classList.contains('next-page')) return;
        item.classList.remove(
            "bg-blue-600",
            "text-white"
        );
        if(item.dataset.target === id){

            item.classList.add(
                "bg-blue-600",
                "text-white"
            );
        }
    });
}

function updateUrlParam(key, value) {
    const url = new URL(window.location);
    url.searchParams.set(key, value);
    window.history.pushState({}, '', url);
}


navItems.forEach(item => {
    item.addEventListener(
        "click",
        () => {
            if (item.classList.contains('disabled')) return;
            showPage(item.dataset.target);
        }
    );

});

let urlParams = new URLSearchParams(window.location.search);
if (urlParams.has('page')) {
    showPage(urlParams.get('page'));
} else {
    showPage('welcome');
}