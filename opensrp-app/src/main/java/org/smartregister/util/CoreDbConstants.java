package org.smartregister.util;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-09-2020
 */

public interface CoreDbConstants {

    // TODO: Remove unused constants here

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    interface KEY {
        String ID = "_id";
        String MOTHER_FIRST_NAME = "mother_first_name";
        String MOTHER_MIDDLE_NAME = "mother_middle_name";
        String MOTHER_LAST_NAME = "mother_last_name";
        String HOME_ADDRESS = "home_address";

        String REGISTER_TYPE = "register_type";

        String FIRST_NAME = "first_name";
        String MIDDLE_NAME = "middle_name";
        String LAST_NAME = "last_name";
        String DOB = "dob";

        String GENDER = "gender";

        String REGISTER_ID = "register_id";
        String BASE_ENTITY_ID = "base_entity_id";

        String TABLE = "ec_client";
        String OPENSRP_ID = "opensrp_id";
        String LAST_INTERACTED_WITH = "last_interacted_with";
        String DATE_REMOVED = "date_removed";
    }

    interface Column {

        interface OpdCheckIn {
            String ID = "_id";
            String VISIT_ID = "visit_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String VISIT_TYPE = "visit_type";
            String CREATED_AT = "created_at";
        }

        interface OpdMultiSelectOptions {
            String ID = "_id";
            String JSON = "json";
            String TYPE = "type";
            String VERSION = "version";
            String CREATED_AT = "created_at";
            String APP_VERSION = "app_version";
        }

        interface OpdVisit {
            String ID = "_id";
            String VISIT_DATE = "visit_date";
            String PROVIDER_ID = "provider_id";
            String LOCATION_ID = "location_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String CREATED_AT = "created_at";
        }

        interface Client {
            String ID = "_id";
            String PHOTO = "photo";
            String FIRST_NAME = "first_name";
            String LAST_NAME = "last_name";
            String BASE_ENTITY_ID = "base_entity_id";
            String DOB = "dob";
            String OPENSRP_ID = "opensrp_id";
            String RELATIONALID = "relationalid";
            String NATIONAL_ID = "national_id";
            String GENDER = "gender";
        }

        interface OpdDetails {
            String ID = "_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String PENDING_DIAGNOSE_AND_TREAT = "pending_diagnose_and_treat";
            String CURRENT_VISIT_START_DATE = "current_visit_start_date";
            String CURRENT_VISIT_END_DATE = "current_visit_end_date";
            String CURRENT_VISIT_ID = "visit_id";
            String CREATED_AT = "created_at";
        }

        interface OpdDiagnosisAndTreatmentForm {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String FORM = "form";
            String CREATED_AT = "created_at";
        }

        interface OpdOutcome {
            String BASE_ENTITY_ID = "base_entity_id";
            String DISCHARGED_ALIVE = "discharged_alive";
            String DISCHARGED_HOME = "discharged_home";
            String REFERRAL = "referral";
            String REFERRAL_LOCATION = "referral_location";
            String REFERRAL_LOCATION_SPECIFY = "referral_location_specify";
            String VISIT_ID = "visit_id";
        }

        interface OpdDiagnosisDetail {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String DIAGNOSIS = "diagnosis";
            String TYPE = "type";
            String DISEASE = "disease";
            String ICD10_CODE = "icd10_code";
            String CODE = "code";
            String DETAILS = "details";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
            String VISIT_ID = "visit_id";
            String DIAGNOSIS_TYPE = "diagnosis_type";
            String DIAGNOSIS_SAME = "diagnosis_same";
        }

        interface OpdTreatmentDetail {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String MEDICINE = "medicine";
            String TYPE = "type";
            String DOSAGE = "dosage";
            String DURATION = "duration";
            String NOTE = "note";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
            String VISIT_ID = "visit_id";
            String PROPERTY = "property";
            String FREQUENCY = "frequency";
            String SPECIAL_INSTRUCTIONS = "special_instructions";
            String TREATMENT_TYPE_SPECIFY = "treatment_type_specify";
            String TREATMENT_TYPE = "treatment_type";
        }

        interface OpdServiceDetail {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String FEE = "fee";
            String VISIT_ID = "visit_id";
            String DETAILS = "details";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
        }

        interface OpdTestConducted {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String TEST_TYPE = "test_type";
            String TEST_NAME = "test_name";
            String RESULT = "result";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
            String VISIT_ID = "visit_id";
            String DETAILS = "details";
        }
    }

    interface Table {

        String EC_CLIENT = "ec_client";
        String OPD_CHECK_IN = "opd_check_in";
        String OPD_VISIT = "opd_visit";
        String OPD_DETAILS = "opd_details";
        String OPD_DIAGNOSIS_AND_TREATMENT_FORM = "opd_diagnosis_and_treatment_form";
        String OPD_DIAGNOSIS = "opd_diagnosis";
        String OPD_DIAGNOSIS_DETAIL = "opd_diagnosis_detail";
        String OPD_TREATMENT_DETAIL = "opd_treatment_detail";
        String OPD_TREATMENT = "opd_treatment";
        String OPD_TREATMENT_AND_MANAGEMENT = "opd_treatment";
        String OPD_SERVICE_DETAIL = "opd_service_detail";
        String OPD_TEST_CONDUCTED = "opd_test_conducted";
        String OPD_OUTCOME = "opd_outcome";

        String OPD_TEST = "opd_test";
        String OPD_MULTI_SELECT_LIST_OPTION = "opd_multi_select_list_option";

    }
}
