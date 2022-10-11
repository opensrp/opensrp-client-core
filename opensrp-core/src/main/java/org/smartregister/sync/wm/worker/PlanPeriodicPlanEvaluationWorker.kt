package org.smartregister.sync.wm.worker

import android.content.Context
import android.text.TextUtils
import androidx.work.WorkerParameters
import org.joda.time.DateTime
import org.smartregister.AllConstants
import org.smartregister.CoreLibrary
import org.smartregister.domain.Action
import org.smartregister.domain.Jurisdiction
import org.smartregister.job.PlanPeriodicEvaluationJob
import org.smartregister.pathevaluator.TriggerType
import org.smartregister.pathevaluator.plan.PlanEvaluator
import org.smartregister.sync.helper.PeriodicTriggerEvaluationHelper
import org.smartregister.sync.wm.workerrequest.SyncWorkRequest
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class PlanPeriodicPlanEvaluationWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {
    private val notificationDelegate = WorkerNotificationDelegate(context, TAG)

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")
        return try {
            if (inputData.keyValueMap.isNotEmpty()){
                val planId = inputData.getString(AllConstants.INTENT_KEY.PLAN_ID)
                val actionIdentifier =
                    inputData.getString(AllConstants.INTENT_KEY.ACTION_IDENTIFIER)
                val actionCode = inputData.getString(AllConstants.INTENT_KEY.ACTION_CODE)
                val actionJsonString = inputData.getString(AllConstants.INTENT_KEY.ACTION)

                if (TextUtils.isEmpty(planId) || TextUtils.isEmpty(actionJsonString)
                    || TextUtils.isEmpty(actionIdentifier) || TextUtils.isEmpty(actionCode)
                ) {
                    Timber.e(
                        Exception(),
                        "Periodic action was not evaluated since planId, action, action-identifier OR action-code was empty"
                    )
                    return Result.failure()
                }

                val action = PlanPeriodicEvaluationJob.gson.fromJson(
                    actionJsonString,
                    Action::class.java
                )
                if (action == null){
                    Timber.e(
                        Exception(),
                        "An error occurred and the service did not evaluate the plan/action"
                    )
                    return Result.failure()
                }

                val planDefinitionRepository = CoreLibrary.getInstance().context()
                    .planDefinitionRepository
                val planDefinition = planDefinitionRepository.findPlanDefinitionById(planId)


                val timeNow = DateTime.now()
                if (planDefinition.effectivePeriod != null && planDefinition.effectivePeriod.end.isBefore(timeNow)
                    || action.timingPeriod != null && action.timingPeriod.end.isBefore(timeNow)
                ) {
                    SyncWorkRequest.cancelWork(applicationContext, this::class.java, workName = generateWorkName(actionIdentifier!!, actionCode!!))

                    PeriodicTriggerEvaluationHelper().cancelJobsForAction( // TODO: Test cancel all previous/queued work requests
                        actionIdentifier,
                        actionCode
                    )
                } else {
                    val allSharedPreferences =
                        CoreLibrary.getInstance().context().allSharedPreferences()
                    val planEvaluator = PlanEvaluator(allSharedPreferences.fetchRegisteredANM())
                    val jurisdiction = Jurisdiction(
                        allSharedPreferences.fetchDefaultLocalityId(allSharedPreferences.fetchRegisteredANM())
                    )

                    // TODO: Change this to evaluate a single action
                    planEvaluator.evaluatePlanAction(
                        planDefinition,
                        TriggerType.PERIODIC,
                        jurisdiction,
                        null,
                        action
                    )
                }
            }

            Result.success().apply {
                notificationDelegate.notify("Success!!")
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure().apply {
                notificationDelegate.notify("Error: ${e.message}")
            }
        }

    }

    fun generateWorkName(actionIdentifier: String, actionCode: String) = "$TAG-$actionCode-$actionIdentifier"

    companion object {
        const val TAG = "PlanPeriodicPlanEvaluationWorker"
    }
}