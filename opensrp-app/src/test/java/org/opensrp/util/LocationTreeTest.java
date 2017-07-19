package org.opensrp.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.Assert;

import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by Dimas Ciputra on 3/23/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LocationTreeTest {
    private LocationTree locationTree;
    private String locationJSONString;

    @Before
    public void setUp() throws Exception{
        locationJSONString = "{\"locationsHierarchy\":{\"map\" : {\"765cb701-9e61-4ead-afb9-a63c943f4f14\":{\"id\":\"765cb701-9e61-4ead-afb9-a63c943f4f14\",\"label\":\"testloc4\",\"node\":{\"creator\":null,\"dateCreated\":null,\"editor\":null,\"dateEdited\":null,\"voided\":false,\"dateVoided\":null,\"voider\":null,\"voidReason\":null,\"locationId\":\"765cb701-9e61-4ead-afb9-a63c943f4f14\",\"name\":\"testloc4\",\"address\":null,\"identifiers\":null,\"parentLocation\":null,\"tags\":null,\"attributes\":null},\"children\":null,\"parent\":null}}}}";

    }

    @Test
    public void shouldShowLocationTree() throws Exception {
        locationTree = EntityUtils.fromJson(locationJSONString, LocationTree.class);
        assertNotNull(locationTree);
        System.out.println("shouldShowLocationTree "+ new Gson().toJson(locationTree));
    }

    @Test
    public void shouldFoundALocation() throws Exception {
        locationTree = EntityUtils.fromJson(locationJSONString, LocationTree.class);
        Location l = locationTree.findLocation("765cb701-9e61-4ead-afb9-a63c943f4f14");
        assertNotNull(l);
        assertEquals(l.getName(), "testloc4");
        System.out.println("shouldFoundALocation " +new Gson().toJson(l));
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }

}
