package org.smartregister.configuration;


public interface BaseMemberProfileOptions {

    Class<? extends ConfigurableMemberProfileRowDataProvider> getMemberProfileDataProvider();

}
