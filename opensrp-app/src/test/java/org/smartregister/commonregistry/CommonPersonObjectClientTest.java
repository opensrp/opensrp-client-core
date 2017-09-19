package org.smartregister.commonregistry;


import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMapOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by onaio on 29/08/2017.
 */

public class CommonPersonObjectClientTest extends BaseUnitTest {

    @Test
    public void instantiatesSuccessfullyOnConstructorCall(){
        String caseID = "caseID";
        String name = "name";
        Map<String,String> stringMap = new HashMap<String, String>();
        assertNotNull(new CommonPersonObjectClient(caseID,stringMap,name));
    }

    @Test
    public void testGettersandSetters(){
        String caseID = "caseID";
        String name = "name";
        Map<String,String> stringMap = new HashMap<String, String>();

        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(name,stringMap,caseID);
        commonPersonObjectClient.setCaseId(caseID);
        commonPersonObjectClient.setName(name);
        commonPersonObjectClient.setDetails(stringMap);
        commonPersonObjectClient.setColumnmaps(stringMap);

        assertEquals(name,commonPersonObjectClient.getName());
        assertEquals(stringMap,commonPersonObjectClient.getDetails());
        assertEquals(stringMap,commonPersonObjectClient.getColumnmaps());
        assertEquals(caseID,commonPersonObjectClient.getCaseId());
    }
}