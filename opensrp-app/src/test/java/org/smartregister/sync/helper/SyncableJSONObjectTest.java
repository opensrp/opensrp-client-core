package org.smartregister.sync.helper;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncFilter;

import static org.junit.Assert.assertEquals;

/**
 * Created by Richard Kareko on 11/17/20.
 */

public class SyncableJSONObjectTest extends BaseUnitTest {

    SyncableJSONObject syncableJSONObject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testConstructor() throws JSONException {
        String username = "testuser";
        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(username);
        CoreLibrary.getInstance().context().allSharedPreferences().saveDefaultLocalityId(username, "location-id1");
        CoreLibrary.getInstance().context().allSharedPreferences().saveDefaultTeam(username, "testteam");
        CoreLibrary.getInstance().context().allSharedPreferences().saveDefaultTeamId(username, "test-team-id");
        syncableJSONObject = new SyncableJSONObject("{\"name\" : \"testObject\"}");

        assertEquals("testObject", syncableJSONObject.getString("name"));
        assertEquals("testuser", syncableJSONObject.getString(SyncFilter.PROVIDER.value()));
        assertEquals("location-id1", syncableJSONObject.getString(SyncFilter.LOCATION.value()));
        assertEquals("testteam", syncableJSONObject.getString(SyncFilter.TEAM.value()));
        assertEquals("test-team-id", syncableJSONObject.getString(SyncFilter.TEAM_ID.value()));
    }
}
