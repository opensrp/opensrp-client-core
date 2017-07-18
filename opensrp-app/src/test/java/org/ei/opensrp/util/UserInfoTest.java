package org.ei.opensrp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.api.domain.Location;
import org.opensrp.api.domain.User;
import org.opensrp.api.util.EntityUtils;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

/**
 * Created by Dimas Ciputra on 3/24/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class UserInfoTest {
    private String responseJSON;

    @Before
    public void setUp() {
        responseJSON = "{\"location\":{\"locationId\":\"cd4ed528-87cd-42ee-a175-5e7089521ebd\",\"name\":\"testloc\",\"voided\":false},\"user\":{\"username\":\"admin\",\"roles\":[\"Provider\",\"System Developer\"],\"baseEntityId\":\"baa5c5d3-cebe-11e4-9a12-040144de7001\",\"baseEntity\":{\"id\":\"baa5c5d3-cebe-11e4-9a12-040144de7001\",\"firstName\":\"Super User\",\"middleName\":\"\",\"lastName\":\"\",\"gender\":\"M\",\"attributes\":{\"Location\":\"cd4ed528-87cd-42ee-a175-5e7089521ebd\",\"Health Center\":\"2\"},\"voided\":false},\"voided\":false}}";

    }

    @Test
    public void shouldBeAbleParseTree() {
        Map<String, String> tree = new GsonBuilder().create().fromJson(responseJSON, Map.class);
        Assert.assertNotNull(tree.get("location"));
        Assert.assertNotNull(tree.get("user"));
    }

    @Test
    public void shouldAbleToReadUserInfo() {
        Map<String, String> tree = EntityUtils.fromJson(responseJSON, Map.class);
        String userJSONString = new Gson().toJson(tree.get("user"));

        User user = EntityUtils.fromJson(userJSONString, User.class);

        Assert.assertNotNull(user);
        Assert.assertEquals(user.getUsername(), "admin");
        Assert.assertTrue(user.getRoles().contains("Provider"));

        System.out.println(new Gson().toJson(user));
    }

    @Test
    public void shouldAbleToReadUserLocation() {
        Map<String, String> tree = EntityUtils.fromJson(responseJSON, Map.class);
        String userLocationJSONString = new Gson().toJson(tree.get("location"));

        Location user = EntityUtils.fromJson(userLocationJSONString, Location.class);

        Assert.assertNotNull(user);
        Assert.assertEquals(user.getName(), "testloc");
    }
}
