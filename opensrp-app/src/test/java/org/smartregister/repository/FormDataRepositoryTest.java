package org.smartregister.repository;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.commonregistry.CommonRepositoryInformationHolder;

import java.util.ArrayList;

/**
 * Created by kaderchowdhury on 12/11/17.
 */
@PrepareForTest({CoreLibrary.class})
public class FormDataRepositoryTest extends BaseUnitTest{

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private Context context;

    @Mock
    private DristhiConfiguration dristhiConfiguration;
    @Mock
    private CoreLibrary coreLibrary;

    @Test
    public void assertFormDataRepositoryInitiaization() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CoreLibrary.class);
        CoreLibrary.init(context);
        Context.bindtypes = new ArrayList<CommonRepositoryInformationHolder>();
        CommonRepositoryInformationHolder bt = new CommonRepositoryInformationHolder("BINDTYPENAME",new String[]{"A","B"});
        Context.bindtypes.add(bt);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.configuration()).thenReturn(dristhiConfiguration);
        PowerMockito.when(dristhiConfiguration.appName()).thenReturn("NULL");
        PowerMockito.when(context.commonrepository(Mockito.anyString())).thenReturn(Mockito.mock(CommonRepository.class));
        FormDataRepository formDataRepository = new FormDataRepository();
        org.junit.Assert.assertNotNull(formDataRepository);
    }
}
