package org.smartregister.configuration;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.ConfigurableMemberProfileRowData;

import java.util.List;

public interface ConfigurableMemberProfileRowDataProvider {

    List<ConfigurableMemberProfileRowData> getRowData(CommonPersonObjectClient client);
}