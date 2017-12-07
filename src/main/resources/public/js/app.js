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


    var $paypalInfo = $('#paypal-info');
    var $cardInfo = $('#card-info');
    var $paymentForm = $('#payment-form');

    $('#paypal-radio').on('click', function () {
        if (!$paypalInfo.is(":visible")) {
            $cardInfo.detach();
            $cardInfo.children().required = false;

            $paymentForm.append($paypalInfo);
            $paypalInfo.children().required = true;

            $paypalInfo.toggle(400);
            $cardInfo.toggle(400);
        }
    });

    $('#card-radio').on('click', function () {
        if (!$cardInfo.is(":visible")) {
            $paypalInfo.detach();
            $cardInfo.children().required = true;

            $paymentForm.append($cardInfo);
            $paypalInfo.children().required = false;

            $paypalInfo.toggle(400);
            $cardInfo.toggle(400);
        }
    });
});