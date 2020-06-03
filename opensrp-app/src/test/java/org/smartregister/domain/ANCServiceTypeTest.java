package org.smartregister.domain;

import org.junit.Test;
import org.smartregister.view.contract.ANCClient;

import static org.junit.Assert.assertEquals;
import static org.smartregister.domain.ANCServiceType.ANC_1;
import static org.smartregister.domain.ANCServiceType.ANC_2;
import static org.smartregister.domain.ANCServiceType.ANC_3;
import static org.smartregister.domain.ANCServiceType.ANC_4;
import static org.smartregister.domain.ANCServiceType.DELIVERY_PLAN;
import static org.smartregister.domain.ANCServiceType.EMPTY;
import static org.smartregister.domain.ANCServiceType.HB_TEST;
import static org.smartregister.domain.ANCServiceType.IFA;
import static org.smartregister.domain.ANCServiceType.KB_IUD;
import static org.smartregister.domain.ANCServiceType.KB_Implant;
import static org.smartregister.domain.ANCServiceType.KB_Injection_Cyclofem;
import static org.smartregister.domain.ANCServiceType.KB_Injection_Depoprovera;
import static org.smartregister.domain.ANCServiceType.PNC_1;
import static org.smartregister.domain.ANCServiceType.PNC_2;
import static org.smartregister.domain.ANCServiceType.PNC_3;
import static org.smartregister.domain.ANCServiceType.TT_1;
import static org.smartregister.domain.ANCServiceType.TT_2;
import static org.smartregister.domain.ANCServiceType.TT_BOOSTER;

/**
 * Created by Vincent Karuri on 03/06/2020
 */
public class ANCServiceTypeTest {

    @Test
    public void testANCServiceTypeEnums() {
        assertEquals("ANC 1", ANC_1.serviceName());
        assertEquals("ANC 1",ANC_1.displayName());
        assertEquals(ANCClient.CATEGORY_ANC, ANC_1.category());

        assertEquals("ANC 2", ANC_2.serviceName());
        assertEquals("ANC 2",ANC_2.displayName());
        assertEquals(ANCClient.CATEGORY_ANC, ANC_2.category());

        assertEquals("ANC 3", ANC_3.serviceName());
        assertEquals("ANC 3",ANC_3.displayName());
        assertEquals(ANCClient.CATEGORY_ANC, ANC_3.category());

        assertEquals("ANC 4", ANC_4.serviceName());
        assertEquals("ANC 4",ANC_4.displayName());
        assertEquals(ANCClient.CATEGORY_ANC, ANC_4.category());

        assertEquals("TT 1", TT_1.serviceName());
        assertEquals("TT", TT_1.displayName());
        assertEquals("TT 1", TT_1.serviceDisplayName());
        assertEquals(ANCClient.CATEGORY_TT, TT_1.category());

        assertEquals("TT 2", TT_2.serviceName());
        assertEquals("TT 2", TT_2.displayName());
        assertEquals("TT 2", TT_2.serviceDisplayName());
        assertEquals(ANCClient.CATEGORY_TT, TT_2.category());

        assertEquals("TT Booster", TT_BOOSTER.serviceName());
        assertEquals("TT B", TT_BOOSTER.displayName());
        assertEquals("TT B", TT_BOOSTER.serviceDisplayName());
        assertEquals(ANCClient.CATEGORY_TT, TT_BOOSTER.category());

        assertEquals("IFA", IFA.serviceName());
        assertEquals("IFA Tablets", IFA.displayName());
        assertEquals(ANCClient.CATEGORY_IFA, IFA.category());

        assertEquals("Hb", HB_TEST.serviceName());
        assertEquals("Hb Test", HB_TEST.displayName());
        assertEquals(ANCClient.CATEGORY_HB, HB_TEST.category());

        assertEquals("Delivery Plan", DELIVERY_PLAN.serviceName());
        assertEquals("Delivery Plan", DELIVERY_PLAN.displayName());
        assertEquals(ANCClient.CATEGORY_DELIVERY_PLAN, DELIVERY_PLAN.category());

        assertEquals("PNC 1", PNC_1.serviceName());
        assertEquals("PNC 1", PNC_1.displayName());
        assertEquals(ANCClient.CATEGORY_PNC, PNC_1.category());

        assertEquals("PNC 2", PNC_2.serviceName());
        assertEquals("PNC 2", PNC_2.displayName());
        assertEquals(ANCClient.CATEGORY_PNC, PNC_2.category());


        assertEquals("PNC 3", PNC_3.serviceName());
        assertEquals("PNC 3", PNC_3.displayName());
        assertEquals(ANCClient.CATEGORY_PNC, PNC_3.category());

        assertEquals("", EMPTY.serviceName());
        assertEquals("", EMPTY.category());

        assertEquals("KB IUD", KB_IUD.serviceName());
        assertEquals("KB IUD", KB_IUD.displayName());
        assertEquals("kb", KB_IUD.category());

        assertEquals("KB Implant", KB_Implant.serviceName());
        assertEquals("KB Implant", KB_Implant.displayName());
        assertEquals("kb", KB_Implant.category());

        assertEquals("KB Injection Cyclofem", KB_Injection_Cyclofem.serviceName());
        assertEquals("KB Injection Cyclofem", KB_Injection_Cyclofem.displayName());
        assertEquals("kb", KB_Injection_Cyclofem.category());

        assertEquals("KB Injection Depoprovera", KB_Injection_Depoprovera.serviceName());
        assertEquals("KB Injection Depoprovera", KB_Injection_Depoprovera.displayName());
        assertEquals("kb", KB_Injection_Depoprovera.category());
    }
}
