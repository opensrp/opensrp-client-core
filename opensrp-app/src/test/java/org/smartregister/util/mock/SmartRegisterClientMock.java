package org.smartregister.util.mock;

import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by ndegwamartin on 2020-03-03.
 */
public class SmartRegisterClientMock implements SmartRegisterClient {
    @Override
    public String entityId() {
        return "test-identifier";
    }

    @Override
    public String name() {
        return "Tester";
    }

    @Override
    public String displayName() {
        return "Tester 1";
    }

    @Override
    public String village() {
        return "Test Village";
    }

    @Override
    public String wifeName() {
        return "Tester Wife";
    }

    @Override
    public String husbandName() {
        return "Tester Husband";
    }

    @Override
    public int age() {
        return 16;
    }

    @Override
    public int ageInDays() {
        return 0;
    }

    @Override
    public String ageInString() {
        return "(" + age() + ")";
    }

    @Override
    public boolean isSC() {
        return false;
    }

    @Override
    public boolean isST() {
        return false;
    }

    @Override
    public boolean isHighRisk() {
        return false;
    }

    @Override
    public boolean isHighPriority() {
        return false;
    }

    @Override
    public boolean isBPL() {
        return false;
    }

    @Override
    public String profilePhotoPath() {
        return null;
    }

    @Override
    public String locationStatus() {
        return null;
    }

    @Override
    public boolean satisfiesFilter(String filterCriterion) {
        return false;
    }

    @Override
    public int compareName(SmartRegisterClient client) {
        return this.name().compareToIgnoreCase(client.name());
    }
}
