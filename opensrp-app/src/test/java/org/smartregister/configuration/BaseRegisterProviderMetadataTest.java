package org.smartregister.configuration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.util.CoreDbConstants;

import java.util.HashMap;
import java.util.Map;

public class BaseRegisterProviderMetadataTest extends BaseUnitTest {

    private BaseRegisterProviderMetadata providerMetadata;
    private Map<String, String> patientColumnMaps;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        providerMetadata = new BaseRegisterProviderMetadata();
        patientColumnMaps = new HashMap<>();
        patientColumnMaps.put(CoreDbConstants.KEY.MOTHER_FIRST_NAME, "Alicia");
        patientColumnMaps.put(CoreDbConstants.KEY.MOTHER_LAST_NAME, "Keys");
        patientColumnMaps.put(CoreDbConstants.KEY.MOTHER_MIDDLE_NAME, "Tubman");
        patientColumnMaps.put(CoreDbConstants.KEY.FIRST_NAME, "Kevin");
        patientColumnMaps.put(CoreDbConstants.KEY.MIDDLE_NAME, "Kevin");
        patientColumnMaps.put(CoreDbConstants.KEY.LAST_NAME, "Kevin");
        patientColumnMaps.put(CoreDbConstants.KEY.DOB, "Kevin");

    }

    @Test
    public void getGuardianFirstNameReturnsCorrectName() {
        Assert.assertEquals("Alicia", providerMetadata.getGuardianFirstName(patientColumnMaps));
    }
}
