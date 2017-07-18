/* globals $, window */

function Modal() {

    var hideModal = function (targetModalToShow) {
        $('#blur').hide();
        $("#modal-container").hide();
        $(targetModalToShow).hide();
        $(".page").css("height", "auto").css("overflow", "auto");
    };

    var showModal = function (targetModalToShow) {
        $("#modal-goes-here").append($(targetModalToShow));
        $("#blur").show();
        $("#modal-container").show();
        $(targetModalToShow).show();
        $(".page").css('height', $(window).height()).css("overflow", "hidden");

        $("#blur").click(function (event) {
            hideModal(targetModalToShow);
            event.stopPropagation();
            return true;
        });

        $(targetModalToShow).click(function (event) {
            hideModal(targetModalToShow);
            event.stopPropagation();
            return true;
        });
    };

    var initializeModalContainer = function () {
        $("body").prepend($('<div id="blur"></div><div id="modal-container" class="modal-container"><img src="../img/triangle.png" class="modal-triangle"/><div id="modal-goes-here"></div></div>'));
    };

    initializeModalContainer();
    return {
        bindToClick: function () {
            $('body [data-modal-target]').click(function (event) {
                $("body").scrollTop(0);
                showModal($(this).data('modal-target'));
            });
        }
    };
}
