package org.smartregister.repository.dao;

import com.ibm.fhir.model.resource.QuestionnaireResponse;

import org.smartregister.converters.EventConverter;
import org.smartregister.pathevaluator.dao.EventDao;
import org.smartregister.repository.EventClientRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by samuelgithengi on 9/3/20.
 */
public class EventDaoImpl extends EventClientRepository implements EventDao {

    @Override
    public List<QuestionnaireResponse> findEventsByEntityIdAndPlan(String resourceId, String
            planIdentifier) {
        return fetchEvents(String.format("select %s from %s where %s =? and (%s is null or %s !=? )", event_column.json,
                clientTable.name(), event_column.baseEntityId, event_column.planId, event_column.planId), new String[]{resourceId, planIdentifier})
                .stream()
                .map(EventConverter::convertEventToEncounterResource)
                .collect(Collectors.toList());
    }
}
