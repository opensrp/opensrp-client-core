var pageView = (function () {
    var callbackForReload = function () {
    };
    var callbackForReloadPhoto = function (caseId, photoPath) {
    };
    var callbackForStartProgressIndicator = function () {
    };
    var callbackForStopProgressIndicator = function () {
    };
    var callbackForUpdateANMDetails = function (anmDetailsJSON) {
    };

    return {
        reload: function () {
            callbackForReload();
        },
        reloadPhoto: function (caseId, photoPath) {
            if (!callbackForReloadPhoto) {
                console.error('No callback registered for callbackForReloadPhoto.');
            }
            callbackForReloadPhoto(caseId, photoPath);
        },
        startProgressIndicator: function () {
            callbackForStartProgressIndicator();
        },
        stopProgressIndicator: function () {
            callbackForStopProgressIndicator();
        },
        updateANMDetails: function (anmDetailsJSON) {
            callbackForUpdateANMDetails(anmDetailsJSON);
        },
        onReload: function (callBack) {
            callbackForReload = callBack;
        },
        onReloadPhoto: function (callBack) {
            callbackForReloadPhoto = callBack;
        },
        onStartProgressIndicator: function (callBack) {
            callbackForStartProgressIndicator = callBack;
        },
        onStopProgressIndicator: function (callBack) {
            callbackForStopProgressIndicator = callBack;
        },
        onUpdateANMDetails: function (callBack) {
            callbackForUpdateANMDetails = callBack;
        }
    };
})();
