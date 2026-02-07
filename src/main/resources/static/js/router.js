const routes = {
    "/": loadHomePage,
    "/login": loadLoginPage,
    "/register": loadRegisterPage,
    "/employees": loadEmployeesPage
};

function navigate(event) {
    event.preventDefault();
    const path = event.currentTarget.getAttribute("href");
    history.pushState({}, "", path);
    route();
}

window.addEventListener("popstate", route);

function route() {
    console.log("Routing to:", window.location.pathname);
    const path = window.location.pathname;

    if (path.startsWith("/employees/")) {
        const id = path.split("/")[2];
        loadEmployeeDetailsPage(id);
        return;
    }

    const handler = routes[path] || loadHomePage;
    handler();
}

// Initial call
route();
