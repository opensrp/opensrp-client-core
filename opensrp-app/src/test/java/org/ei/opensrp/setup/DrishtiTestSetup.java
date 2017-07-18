package org.ei.opensrp.setup;

import org.robolectric.bytecode.ClassInfo;
import org.robolectric.bytecode.Setup;

public class DrishtiTestSetup extends Setup {
    @Override
    public boolean shouldInstrument(ClassInfo classInfo) {
        return super.shouldInstrument(classInfo)
                || classInfo.getName().equals("org.ei.opensrp.Context")
                || classInfo.getName().equals("org.ei.opensrp.view.controller.ECSmartRegisterController")
                || classInfo.getName().equals("org.ei.opensrp.view.controller.VillageController");
    }
}
