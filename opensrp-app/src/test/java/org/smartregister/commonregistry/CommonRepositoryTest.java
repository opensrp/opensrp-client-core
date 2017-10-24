package org.smartregister.commonregistry;


import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.fest.assertions.api.ANDROID;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.commonregistry.shared.FakeRepository;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.Repository;
import org.smartregister.service.AlertService;
import org.smartregister.util.Session;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by onaio on 29/08/2017.
 */
@PrepareForTest({DrishtiApplication.class})
public class CommonRepositoryTest extends BaseUnitTest {

    public static final String ADDITIONALCOLUMN = "ADDITIONALCOLUMN";
    public static final String CUSTOMRELATIONALID = "CUSTOMRELATIONALID";

    @InjectMocks
    private CommonRepository commonRepository;

    @InjectMocks
    private Repository repository;

    @InjectMocks
    private FakeRepository fakerepository;

    @Mock
    private CommonFtsObject commonFtsObject;

    @Mock
    private AlertService alertService;


    @Mock
    private Context context;

    @Mock
    private SQLiteDatabase sqliteDatabase;

//    @Before
//    public void setUp() {
//
//        initMocks(this);
//        assertNotNull(commonRepository);
//    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() throws Exception {
        String tablename = "";
        String [] tableColumns = new String[] {};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(tablename,tableColumns);
        assertNotNull(commonRepository);
        Assert.assertNotNull(new CommonRepository(commonFtsObject,tablename,tableColumns));
    }

   @Test
    public void instantiatesSuccessfullyOnConstructorCallWithAdditionalColumns() throws Exception {
        String tablename = "";
        String [] tableColumns = new String[] {CommonRepository.Relational_Underscore_ID, CommonRepository.BASE_ENTITY_ID_COLUMN, ADDITIONALCOLUMN, CUSTOMRELATIONALID};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        when(commonFtsObject.getCustomRelationalId(anyString())).thenReturn(CUSTOMRELATIONALID);
        Assert.assertNotNull(new CommonRepository(commonFtsObject,tablename,tableColumns));
    }

    @Test
    public void addCallsDatabaseInsert1times() throws Exception {
        String tablename = "";
        String [] tableColumns = new String[] {};
        commonRepository = new CommonRepository(tablename,tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        commonRepository.add(new CommonPersonObject("","",new HashMap<String, String>(),""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(anyString(),isNull(String.class),any(ContentValues.class));
    }





}