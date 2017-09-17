package org.smartregister.commonregistry;


import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.repository.Repository;
import org.smartregister.service.AlertService;

import java.util.HashMap;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by onaio on 29/08/2017.
 */

public class CommonRepositoryTest extends BaseUnitTest {

    @InjectMocks
    private CommonRepository commonRepository;

    @InjectMocks
    private Repository repository;

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