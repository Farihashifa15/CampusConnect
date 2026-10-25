// CampusConnect Interactive JavaScript
document.addEventListener("DOMContentLoaded", function () {
    // Enable Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll("[data-bs-toggle='tooltip']"));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Confirmation handlers for destructive / important actions
    document.querySelectorAll("[data-confirm]").forEach(function (element) {
        element.addEventListener("click", function (e) {
            const message = element.getAttribute("data-confirm") || "Are you sure you want to proceed with this action?";
            if (!confirm(message)) {
                e.preventDefault();
                return false;
            }
        });
    });

    // Star rating picker in feedback form
    const ratingInputs = document.querySelectorAll(".star-rating-select input[type='radio']");
    ratingInputs.forEach(function (radio) {
        radio.addEventListener("change", function () {
            const val = parseInt(this.value);
            document.querySelectorAll(".star-rating-label").forEach(function (lbl, idx) {
                if (idx < val) {
                    lbl.classList.add("text-warning");
                    lbl.classList.remove("text-muted");
                } else {
                    lbl.classList.remove("text-warning");
                    lbl.classList.add("text-muted");
                }
            });
        });
    });

    // Client-side quick filter for tables
    const tableSearch = document.getElementById("tableSearchInput");
    if (tableSearch) {
        tableSearch.addEventListener("keyup", function () {
            const query = this.value.toLowerCase();
            const rows = document.querySelectorAll(".filterable-table tbody tr");
            rows.forEach(function (row) {
                const text = row.textContent.toLowerCase();
                row.style.display = text.indexOf(query) > -1 ? "" : "none";
            });
        });
    }
});