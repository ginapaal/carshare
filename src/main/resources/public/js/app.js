$(document).ready(function () {

    $('input#username').on({
        keydown: function (event) {
            if (event.which === 32)
                return false;
        },
        change: function () {
            this.value = this.value.replace(/\s/g, '');
        }
    });

    $('.change-pic').on('click', function () {
        $('.change-pic').addClass('hidden');
        $('.picture-link').removeClass('hidden');
        $('.upload-profile-pic').removeClass('hidden');
        $('.cancel-profile-pic').removeClass('hidden');
    });

    $('.upload-profile-pic').on('click', function () {
        $('.change-pic').removeClass('hidden');
    });

    $('.cancel-profile-pic').on('click', function () {
        $('.change-pic').removeClass('hidden');
        $('.picture-link').addClass('hidden');
        $('.upload-profile-pic').addClass('hidden');
        $('.cancel-profile-pic').addClass('hidden');
    });

});