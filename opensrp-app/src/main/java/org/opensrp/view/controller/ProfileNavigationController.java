package org.opensrp.view.controller;

import android.content.Intent;
import org.opensrp.view.activity.ANCDetailActivity;
import org.opensrp.view.activity.ChildDetailActivity;
import org.opensrp.view.activity.EligibleCoupleDetailActivity;
import org.opensrp.view.activity.PNCDetailActivity;

import static org.opensrp.AllConstants.CASE_ID;

public class ProfileNavigationController {

    public static void navigateToECProfile(android.content.Context context, String caseId) {
        Intent intent = new Intent(context.getApplicationContext(), EligibleCoupleDetailActivity.class);
        intent.putExtra(CASE_ID, caseId);
        context.startActivity(intent);
    }

    public static void navigateToANCProfile(android.content.Context context, String caseId) {
        Intent intent = new Intent(context.getApplicationContext(), ANCDetailActivity.class);
        intent.putExtra(CASE_ID, caseId);
        context.startActivity(intent);
    }

    public static void navigateToPNCProfile(android.content.Context context, String caseId) {
        Intent intent = new Intent(context.getApplicationContext(), PNCDetailActivity.class);
        intent.putExtra(CASE_ID, caseId);
        context.startActivity(intent);
    }

    public static void navigateToChildProfile(android.content.Context context, String caseId) {
        Intent intent = new Intent(context.getApplicationContext(), ChildDetailActivity.class);
        intent.putExtra(CASE_ID, caseId);
        context.startActivity(intent);
    }
}
