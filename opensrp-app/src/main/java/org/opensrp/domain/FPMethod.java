package org.opensrp.domain;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.Context;
import org.opensrp.R;

import java.util.Locale;
import java.util.Map;

import static org.opensrp.domain.TimelineEvent.*;
import static org.opensrp.util.Log.logWarn;

public enum FPMethod {
    CONDOM {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return forFPCondomRenew(caseId, details);
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_condom);
        }
    },
    IUD {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return forFPIUDRenew(caseId, details);
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_iucd);
        }
    },
    OCP {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return forFPOCPRenew(caseId, details);
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_ocp);
        }
    },
    DMPA_INJECTABLE {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return forFPDMPARenew(caseId, details);
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_dmpa);
        }
    },
    MALE_STERILIZATION {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_male_sterilization);
        }
    },
    FEMALE_STERILIZATION {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_female_sterilization);
        }
    },
    ECP {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_ecp);
        }
    },
    TRADITIONAL_METHODS {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_traditional);
        }
    },
    LAM {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_lam);
        }
    },
    CENTCHROMAN {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_centchroman);
        }
    },
    NONE_PS {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_none_ps);
        }
    },
    NONE_SS {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.fp_register_service_mode_none_ss);
        }
    },
    NONE {
        public TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details) {
            return null;
        }

        @Override
        public String displayName() {
            return Context.getInstance().applicationContext().getString(R.string.ec_register_no_fp);
        }
    };

    public abstract TimelineEvent getTimelineEventForRenew(String caseId, Map<String, String> details);

    public abstract String displayName();

    public static FPMethod tryParse(String method, FPMethod defaultMethod) {
        try {
            return StringUtils.isBlank(method) ? defaultMethod : FPMethod.valueOf(method.toUpperCase(Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            logWarn("Unknown current FP method : " + method + " Exception : " + e);
            return defaultMethod;
        }
    }
}
