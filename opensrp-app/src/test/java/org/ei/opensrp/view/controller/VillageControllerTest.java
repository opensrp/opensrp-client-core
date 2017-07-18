package org.ei.opensrp.view.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ei.opensrp.repository.AllEligibleCouples;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.view.contract.Village;
import org.ei.opensrp.view.contract.Villages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VillageControllerTest {
    @Mock
    private AllEligibleCouples allEligibleCouples;
    private VillageController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new VillageController(allEligibleCouples, new Cache<String>(), new Cache<Villages>());
    }

    @Test
    public void shouldLoadVillages() throws Exception {
        List<Village> expectedVillages = asList(new Village("village1"), new Village("village2"));
        when(allEligibleCouples.villages()).thenReturn(asList("village1", "village2"));

        String villages = controller.villages();
        List<Village> actualVillages = new Gson().fromJson(villages, new TypeToken<List<Village>>() {
        }.getType());
        assertEquals(actualVillages, expectedVillages);
    }
}
