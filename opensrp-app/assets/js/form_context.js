/* globals window, alert */

function FormBridge() {
    var formContext = window.formContext;
    if (typeof formContext === "undefined" && typeof FakeFormContext !== "undefined") {
        formContext = new FakeFormContext();
    }

    return {
        delegateToFormLaunchView: function (formName, entityId, metaData) {
            return formContext.startFormActivity(formName, entityId, metaData);
        },
        delegateToMicroFormLaunchView: function (formName, entityId, metaData) {
            return formContext.startMicroFormActivity(formName, entityId, metaData);
        }
    };
}

function FakeFormContext() {
    return {
        startFormActivity: function (formName, entityId, metaData) {
            alert("Launching form: " + formName + ", for entityId: '" + entityId + "'" + ", with metaData: '" + metaData + "'");
        },
        startMicroFormActivity: function (formName, entityId, metaData) {
            alert("Launching micro form: " + formName + ", for entityId: '" + entityId + "'" + ", with metaData: '" + metaData + "'");
        }
    };
}
