package org.ei.opensrp.view.controller;

import org.ei.opensrp.util.Log;

public class HomeController {
    private UpdateController updateController;

    public HomeController(UpdateController updateController) {
        this.updateController = updateController;
    }

    public void pageHasFinishedLoading() {
        updateController.pageHasFinishedLoading();
    }

    public void log(String text) {
        Log.logInfo(text);
    }
}

