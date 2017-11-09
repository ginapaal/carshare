$(document).ready(function () {
    $("input#username").on({
        keydown: function (event) {
            if (event.which === 32)
                return false;
        },
        change: function () {
            this.value = this.value.replace(/\s/g, "");
        }
    });
});