package org.smartregister.view.contract;


import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.domain.ChildServiceType;

import static org.smartregister.AllConstants.COMMA_WITH_SPACE;
import static org.smartregister.AllConstants.SPACE;
import static org.smartregister.util.StringUtil.humanize;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.util.DateUtil.formatDate;
import static org.smartregister.util.StringUtil.replaceAndHumanize;
import static org.smartregister.util.StringUtil.replaceAndHumanizeWithInitCapText;

public interface ChildSmartRegisterClient extends SmartRegisterClient {
List<String> illnessAcronyms = new ArrayList<String>(
        Arrays.asList(Context.getInstance().getStringResource(R.string.str_child_illness_ari),
                Context.getInstance().getStringResource(R.string.str_child_illness_sam)));

    class ChildSickStatus {
        public static ChildSickStatus noDiseaseStatus = new ChildSickStatus(null, null, null);

        private String diseases;
        private String otherDiseases;
        private String date;

        public ChildSickStatus(String diseases, String otherDiseases, String date) {
            this.diseases = diseases;
            this.otherDiseases = otherDiseases;
            this.date = date;
        }

        public String diseases() {
            return getDiseasesCapitalizeIfAcronymsOrHumanize() + (isBlank(otherDiseases) ? "" : (", " + replaceAndHumanizeWithInitCapText(otherDiseases, SPACE, COMMA_WITH_SPACE)));
        }

        private String getDiseasesCapitalizeIfAcronymsOrHumanize() {
            return replaceAndHumanizeWithInitCapText(illnessAcronyms.contains(diseases) ? diseases.toUpperCase() : diseases, SPACE, COMMA_WITH_SPACE);
        }

        public String date() {
            return formatDate(date);
        }
    }

    String gender();

    String weight();

    String thayiCardNumber();

    String motherEcNumber();

    String dateOfBirth();

    String fatherName();

    String motherName();

    String locationStatus();

    List<ServiceProvidedDTO> serviceProvided();

    ServiceProvidedDTO lastServiceProvided();

    ServiceProvidedDTO illnessVisitServiceProvided();

    ChildSickStatus sickStatus();

    boolean isBcgDone();

    boolean isOpvDone();

    boolean isHepBDone();

    boolean isPentavDone();

    String bcgDoneDate();

    String opvDoneDate();

    String hepBDoneDate();

    String pentavDoneDate();

    boolean isMeaslesDone();

    boolean isOpvBoosterDone();

    boolean isDptBoosterDone();

    boolean isVitaminADone();

    String measlesDoneDate();

    String opvBoosterDoneDate();

    String dptBoosterDoneDate();

    String vitaminADoneDate();

    List<AlertDTO> alerts();

    AlertDTO getAlert(ChildServiceType measles);

    boolean isDataError();
}
