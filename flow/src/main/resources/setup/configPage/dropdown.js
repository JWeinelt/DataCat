class Dropdown {

    constructor(element) {
        this.element = element;

        this.button = element.querySelector(".dropdown-button");
        this.menu = element.querySelector(".dropdown-menu");
        this.label = element.querySelector(".dropdown-label");
        this.arrow = element.querySelector(".dropdown-arrow");
        this.options = element.querySelectorAll(".dropdown-option");

        this.open = false;
        this.value = null;

        this.eid = element.id;

        this.init();
    }

    init() {
        this.options.forEach(option => {
            option.addEventListener("click", () => {
                if (option.dataset.value === "disabled") return;
                this.select(option.dataset.value);
                this.close();
            });
        });

        this.button.addEventListener("click", e => {
            e.stopPropagation();

            if (this.open) {
                this.close();
            } else {
                Dropdown.closeAll();
                this.openMenu();
            }
        });

        if (this.options.length > 0) {
            this.select(this.options[0].dataset.value);
        }

        Dropdown.instances.push(this);
    }

    updateOptions() {
        this.options = this.element.querySelectorAll(".dropdown-option");
        console.log("Updated options: " + this.options.length);

        this.options.forEach(option => {
            option.addEventListener("click", () => {
                if (option.dataset.value === "disabled") return;
                this.select(option.dataset.value);
                this.close();
            });
        });
    }

    select(value) {
        console.log("Selected: " + value);
        this.value = value;

        const option = [...this.options].find(o => o.dataset.value === value);

        if (option) {
            this.label.textContent = option.textContent.trim();
        }

        this.element.dispatchEvent(new CustomEvent("change", {
            detail: {
                value: this.value
            }
        }));
    }

    getValue() {
        return this.value;
    }

    setValue(value) {
        this.select(value);
    }

    eid() {
        return this.eid;
    }

    openMenu() {
        if (this.disabled) return;
        this.open = true;

        this.menu.classList.remove("opacity-0", "-translate-y-2", "pointer-events-none");
        this.menu.classList.add("opacity-100", "translate-y-0");

        this.arrow.classList.add("rotate-180");
    }

    close() {
        this.open = false;

        this.menu.classList.add("opacity-0", "-translate-y-2", "pointer-events-none");
        this.menu.classList.remove("opacity-100", "translate-y-0");

        this.arrow.classList.remove("rotate-180");
    }

    disable() {
        this.button.classList.add("text-gray-400");
        this.button.classList.remove("hover:border-blue-500");
        this.button.classList.add("hover:border-gray-500");
        this.disabled = true;
    }

    enable() {
        this.button.classList.remove("text-gray-400");
        this.button.classList.add("hover:border-blue-500");
        this.button.classList.remove("hover:border-gray-500");
        this.disabled = false;
    }

    static closeAll() {
        Dropdown.instances.forEach(dropdown => dropdown.close());
    }

}

Dropdown.instances = [];

document.addEventListener("click", () => Dropdown.closeAll());