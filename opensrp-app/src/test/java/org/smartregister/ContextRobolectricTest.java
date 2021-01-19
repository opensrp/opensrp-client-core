package org.smartregister;

import android.content.res.Configuration;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.commonregistry.CommonRepositoryInformationHolder;
import org.smartregister.repository.DrishtiRepository;

import java.util.ArrayList;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 19-01-2021.
 */
public class ContextRobolectricTest extends BaseRobolectricUnitTest {

    @Test
    public void getInstanceShouldCallConstructorWhenContextIsNull() {
        ReflectionHelpers.setStaticField(Context.class, "context", null);
        Assert.assertNull(ReflectionHelpers.getStaticField(Context.class, "context"));

        Context.getInstance();

        Assert.assertNotNull(ReflectionHelpers.getStaticField(Context.class, "context"));
    }

    @Test
    public void setInstanceShouldReturnNullWhenContextPassedIsNull() {
        Assert.assertNull(Context.setInstance(null));
    }

    @Test
    public void applicationContextShouldReturnNullWhenApplicationContextIsNull() {
        android.content.Context context = ReflectionHelpers.getField(Context.getInstance(), "applicationContext");
        Context.getInstance().setApplicationContext(null);

        // Call the actual method
        Assert.assertNull(Context.getInstance().applicationContext());

        // Return the previous context
        Context.getInstance().setApplicationContext(context);
    }

    @Test
    public void applicationContextShouldReturnThrowExceptionAndReturnApplicationContext() {
        android.content.Context context = ReflectionHelpers.getField(Context.getInstance(), "applicationContext");
        android.content.Context spiedContext = Mockito.spy(context);
        Context.getInstance().setApplicationContext(spiedContext);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new Exception("An exception");
            }
        }).when(spiedContext).getResources();

        // Call the actual method
        Assert.assertEquals(spiedContext, Context.getInstance().applicationContext());

        Mockito.verify(spiedContext, Mockito.times(0)).createConfigurationContext(Mockito.any(Configuration.class));

        // Return the previous context
        Context.getInstance().setApplicationContext(context);
    }

    @Test
    public void sharedRepositoriesShouldAddBindTypesAsRepositories() {
        ArrayList<CommonRepositoryInformationHolder> bindtypes = new ArrayList<CommonRepositoryInformationHolder>();
        String[] columnNames = new String[]{"base_entity_id", "fname", "lname"};
        String tableName = "ec_client_test";
        bindtypes.add(new CommonRepositoryInformationHolder(tableName, columnNames));
        Context context = Context.getInstance();
        Context spiedContext = Mockito.spy(context);
        Context.setInstance(spiedContext);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ReflectionHelpers.setField(spiedContext, "bindtypes", bindtypes);
                return null;
            }
        }).when(spiedContext).assignbindtypes();

        // Call the actual method
        ArrayList<DrishtiRepository> repositories = spiedContext.sharedRepositories();


        // Verifications & assertions
        boolean repositoryFoundWithColumns = false;
        for (DrishtiRepository drishtiRepository: repositories) {
            if (drishtiRepository instanceof CommonRepository && tableName.equals(((CommonRepository) drishtiRepository).TABLE_NAME)) {
                CommonRepository commonRepository = ((CommonRepository) drishtiRepository);
                Assert.assertEquals(7, commonRepository.common_TABLE_COLUMNS.length);
                Assert.assertEquals("base_entity_id", commonRepository.common_TABLE_COLUMNS[4]);
                Assert.assertEquals("fname", commonRepository.common_TABLE_COLUMNS[5]);
                Assert.assertEquals("lname", commonRepository.common_TABLE_COLUMNS[6]);
                repositoryFoundWithColumns = true;
            }
        }


        Assert.assertTrue(repositoryFoundWithColumns);
        Mockito.verify(spiedContext).assignbindtypes();

        // Return context instance
        Context.setInstance(context);
    }

    @Test
    public void alertServiceShouldGenerateAlertServiceWhenAlertServiceIsNull() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "alertService"));
        CommonFtsObject commonFtsObject = new CommonFtsObject(new String[]{"ec_client_test"});
        Context.getInstance().updateCommonFtsObject(commonFtsObject);
        Context.getInstance().assignbindtypes();

        // Call the method under test
        Assert.assertNotNull(Context.getInstance().alertService());

        Context.getInstance().updateCommonFtsObject(null);
    }

    @Test
    public void smartRegisterClientsCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "smartRegisterClientsCache"));

        Assert.assertNotNull(Context.getInstance().smartRegisterClientsCache());
    }

    @Test
    public void ecClientsCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "ecClientsCache"));

        Assert.assertNotNull(Context.getInstance().ecClientsCache());
    }

    @Test
    public void fpClientsCache() {
        Assert.assertNull(ReflectionHelpers.getField(Context.getInstance(), "fpClientsCache"));

        Assert.assertNotNull(Context.getInstance().fpClientsCache());
    }
}
