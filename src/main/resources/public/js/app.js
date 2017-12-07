function styling() {

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
}

function initMap() {
    var map = new google.maps.Map(document.getElementById('map-container'), {
        zoom: 6,
    });
    var geocoder = new google.maps.Geocoder();
    geocodeAddress(geocoder, map);
}

function geocodeAddress(geocoder, resultsMap) {

    $.getJSON("/locationData", function (response) {
        $.each(response, function (key, value) {
            $.each(value, function (selector, data) {
                var address = data['location'].toString();
                geocoder.geocode({'address': address}, function (results, status) {
                    if (status === 'OK') {
                        resultsMap.setCenter(results[0].geometry.location);
                        var marker = new google.maps.Marker({
                            map: resultsMap,
                            position: results[0].geometry.location,
                            url: data['url'].toString()
                        });
                        google.maps.event.addListener(marker, 'click', function () {
                            $.ajax({
                                method: 'POST',
                                url: '/locations/'+data['index'].toString(),
                                data: {
                                    index: data['index'].toString(),
                                    cityName: address,
                                },
                                success: function() {
                                    window.location.href = marker.url;
                                }
                            });


                        });
                    } else {
                        alert('I know no city with the following name: ' + address);
                    }
                });
            });
        });
    });

}

function main() {
    styling();
    initMap();
}

$(document).ready(main);