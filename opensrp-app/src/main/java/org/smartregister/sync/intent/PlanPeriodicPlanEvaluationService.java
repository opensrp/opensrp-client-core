package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.google.common.collect.Sets;

import org.apache.commons.collections.CollectionUtils;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.PlanDefinition;

import java.util.Set;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlanPeriodicPlanEvaluationService extends IntentService {


    public PlanPeriodicPlanEvaluationService() {
        super("PlanPeriodicPlanEvaluationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            String[] planIds = intent.getStringArrayExtra(AllConstants.INTENT_KEY.PLAN_IDS);
            if (planIds != null && planIds.length > 0) {
                Set<String> planIdsSet = Sets.newHashSet(planIds);
                Set<PlanDefinition> planDefinitions = CoreLibrary.getInstance().context().getPlanDefinitionRepository().findPlanDefinitionByIds(planIdsSet);

                for (Pl)
            }
        }

        CoreLibrary.getInstance().context().getPlanDefinitionRepository().findPlanDefinitionByIds()

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}