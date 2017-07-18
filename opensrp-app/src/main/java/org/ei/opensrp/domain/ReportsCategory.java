package org.ei.opensrp.domain;

import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.opensrp.domain.ReportIndicator.*;

public enum ReportsCategory {
    FPS("Family Planning Services", asList(IUD, CONDOM, CONDOM_QTY, OCP, OCP_SC, OCP_ST, OCP_C_OTHERS,
            MALE_STERILIZATION, FEMALE_STERILIZATION, FS_BPL, FS_APL)),

    ANC_SERVICES("ANC Services", asList(EARLY_ANC_REGISTRATIONS, LATE_ANC_REGISTRATIONS, TOTAL_ANC_REGISTRATIONS, SUB_TT, TT1, TT2, TTB, ANC4)),

    PREGNANCY_OUTCOMES("Pregnancy Outcomes", asList(LIVE_BIRTH, STILL_BIRTH, EARLY_ABORTIONS, LATE_ABORTIONS,
            SPONTANEOUS_ABORTION, DELIVERY, INSTITUTIONAL_DELIVERY, D_HOM, D_SC, D_PHC, D_CHC, D_SDH, D_DH, D_PRI,
            CESAREAN, CESAREAN_GOV, CESAREAN_PRI)),

    PNC_SERVICES("PNC Services", asList(PNC3)),

    CHILD_SERVICES("Child Services", asList(INFANT_BALANCE_ON_HAND, INFANT_REG, INFANT_BALANCE_TOTAL, INFANT_LEFT,
            INFANT_MORTALITY, INFANT_BALANCE_BALANCE, INFANT_BALANCE_OA_CHILDREN, INFANT_BALANCE_LESS_THAN_ONE_YEAR, INFANT_BALANCE_LESS_THAN_FIVE_YEAR,
            PENTAVALENT3_OR_OPV3, DPT_BOOSTER_OR_OPV_BOOSTER, DPT_BOOSTER2, HEP, OPV, MEASLES,
            PENTAVALENT_1, PENTAVALENT_2, PENTAVALENT_3,
            BCG, LBW, BF_POST_BIRTH, WEIGHED_AT_BIRTH,
            VIT_A_1, VIT_A_1_FOR_FEMALE_CHILD, VIT_A_1_FOR_MALE_CHILD,
            VIT_A_2, VIT_A_2_FOR_FEMALE_CHILD, VIT_A_2_FOR_MALE_CHILD,
            VIT_A_5_FOR_FEMALE_CHILD, VIT_A_5_FOR_MALE_CHILD, VIT_A_9_FOR_FEMALE_CHILD, VIT_A_9_FOR_MALE_CHILD,
            VIT_A_FOR_FEMALE, VIT_A_FOR_MALE,
            CHILD_DIARRHEA,
            CHILD_MORTALITY_DUE_TO_DIARRHEA,
            IB_1Y, OPV3, OPV_BOOSTER, JE, DPT_BOOSTER1)),

    MORTALITY("Mortality", asList(ENM, NM, LNM, INFANT_MORTALITY, CHILD_MORTALITY, MMA, MMD, MMP, MM)),

    BENEFICIARY_SCHEMES("Beneficiary Schemes", asList(ANCS_AND_PNCS_WITH_BPL));

    private String description;
    private List<ReportIndicator> indicators;

    ReportsCategory(String description, List<ReportIndicator> indicators) {
        this.description = description;
        this.indicators = indicators;
    }

    public List<ReportIndicator> indicators() {
        return indicators;
    }

    public String description() {
        return description;
    }

    public String value() {
        return name();
    }
}
