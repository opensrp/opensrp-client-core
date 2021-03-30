package org.smartregister.shadows;


import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.util.ConfigurationInstancesHelper;

@Implements(ConfigurationInstancesHelper.class)
public class ConfigurationInstancesHelperShadow {

    @Implementation
    public static <T> T newInstance(Class<T> clas) {
        return Mockito.mock(clas);
    }
}
