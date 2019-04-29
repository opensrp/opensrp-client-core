package org.smartregister.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by samuelgithengi on 4/29/19.
 */
public class PlanDefinitionTest {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .create();

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private String planDefinitionJSON = "{\"identifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"version\":\"1\",\"name\":\"2019_IRS_Season\",\"title\":\"2019 IRS Operational Plan\",\"status\":\"active\",\"date\":\"2019-03-27\",\"effectivePeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-07-31\"},\"useContext\":null,\"jurisdiction\":[{\"code\":\"3421\"},{\"code\":\"3429\"},{\"code\":\"3436\"},{\"code\":\"3439\"}],\"goal\":[{\"id\":\"BCC_complete\",\"description\":\"Complete BCC for the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of BCC communication activities that happened\",\"detail\":{\"detailQuantity\":{\"value\":\"1\",\"comparator\":\">=\",\"unit\":\"each\"}},\"due\":\"2019-04-01\"}]},{\"id\":\"90_percent_of_structures_sprayed\",\"description\":\"Spray 90 % of structures in the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of structures sprayed\",\"detail\":{\"detailQuantity\":{\"value\":\"90\",\"comparator\":\">=\",\"unit\":\"percent\"}},\"due\":\"2019-05-31\"}]}],\"action\":[{\"identifier\":\"990af508-f1a9-4793-841f-49a7b6438827\",\"prefix\":\"1\",\"title\":\"Perform BCC\",\"description\":\"Perform BCC for the operational area\",\"code\":\"BCC\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-10\"},\"reason\":\"Routine\",\"goalId\":\"BCC_complete\",\"subjectCodableConcept\":{\"text\":\"Operational_Area\"},\"taskTemplate\":\"Action1_Perform_BCC\"},{\"identifier\":\"8276be06-97d3-4815-8d39-0bc158dc1d91\",\"prefix\":\"2\",\"title\":\"Spray Structures\",\"description\":\"Visit each structure in the operational area and attempt to spray\",\"code\":\"IRS\",\"timingPeriod\":{\"start\":\"2019-04-10\",\"end\":\"2019-07-31\"},\"reason\":\"Routine\",\"goalId\":\"90_percent_of_structures_sprayed\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"Action2_Spray_Structures\"}]}";

    @Test
    public void testDeserialize() {
        PlanDefinition planDefinition = gson.fromJson(planDefinitionJSON, PlanDefinition.class);
        assertNotNull(planDefinition);
        assertEquals("4708ca0a-d0d6-4199-bb1b-8701803c2d02", planDefinition.getIdentifier());
        assertEquals("1", planDefinition.getVersion());
        assertEquals("2019_IRS_Season", planDefinition.getName());
        assertEquals("2019 IRS Operational Plan", planDefinition.getTitle());
        assertEquals("active", planDefinition.getStatus());
        assertEquals("2019-03-27", planDefinition.getDate().toString(formatter));

        assertEquals(2, planDefinition.getGoals().size());

        assertEquals("BCC_complete", planDefinition.getGoals().get(0).getId());
        assertEquals("Complete BCC for the operational area", planDefinition.getGoals().get(0).getDescription());
        assertEquals("medium-priority", planDefinition.getGoals().get(0).getPriority());
        assertEquals(1, planDefinition.getGoals().get(0).getTargets().size());

        Target target = planDefinition.getGoals().get(0).getTargets().get(0);
        assertEquals("Number of BCC communication activities that happened", target.getMeasure());
        assertEquals("2019-04-01", target.getDue().toString(formatter));
        assertEquals(1, target.getDetail().getDetailQuantity().getValue(), 0.000001);
        assertEquals(">=", target.getDetail().getDetailQuantity().getComparator());
        assertEquals("each", target.getDetail().getDetailQuantity().getUnit());


        assertEquals(2, planDefinition.getActions().size());

        Action action = planDefinition.getActions().get(1);
        assertEquals("8276be06-97d3-4815-8d39-0bc158dc1d91", action.getIdentifier());
        assertEquals(2, action.getPrefix());
        assertEquals("Spray Structures", action.getTitle());
        assertEquals("Visit each structure in the operational area and attempt to spray", action.getDescription());
        assertEquals("IRS", action.getCode());

        assertEquals("2019-04-10", action.getTimingPeriod().getStart().toString(formatter));
        assertEquals("2019-07-31", action.getTimingPeriod().getEnd().toString(formatter));
        assertEquals("Routine", action.getReason());
        assertEquals("90_percent_of_structures_sprayed", action.getGoalId());
        //assertEquals("Residential_Structure", action.getSubjectCodableConcept().getValue());
        assertEquals("Action2_Spray_Structures", action.getTaskTemplate());

    }

}
