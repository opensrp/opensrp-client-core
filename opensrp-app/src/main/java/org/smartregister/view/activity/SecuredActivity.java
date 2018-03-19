package org.smartregister.view.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.broadcastreceivers.OpenSRPClientBroadCastReceiver;
import org.smartregister.event.Listener;
import org.smartregister.service.ZiggyService;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.FormController;
import org.smartregister.view.controller.NavigationController;

import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static org.smartregister.AllConstants.ALERT_NAME_PARAM;
import static org.smartregister.AllConstants.CloudantSync;
import static org.smartregister.AllConstants.ENTITY_ID;
import static org.smartregister.AllConstants.ENTITY_ID_PARAM;
import static org.smartregister.AllConstants.FIELD_OVERRIDES_PARAM;
import static org.smartregister.AllConstants.FORM_NAME_PARAM;
import static org.smartregister.AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE;
import static org.smartregister.event.Event.ON_LOGOUT;
import static org.smartregister.util.Log.logInfo;

public abstract class SecuredActivity extends AppCompatActivity {
    public static final String LOG_TAG = "SecuredActivity";
    protected final int MENU_ITEM_LOGOUT = 2312;
    protected Listener<Boolean> logoutListener;
    protected FormController formController;
    protected ANMController anmController;
    protected NavigationController navigationController;
    protected ZiggyService ziggyService;
    private String metaData;
    private OpenSRPClientBroadCastReceiver openSRPClientBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ziggyService = context().ziggyService();

        logoutListener = new Listener<Boolean>() {
            public void onEvent(Boolean data) {
                finish();
            }
        };
        ON_LOGOUT.addListener(logoutListener);

        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication) getApplication();
            application.logoutCurrentUser();
            return;
        }

        formController = new FormController(this);
        anmController = context().anmController();
        navigationController = new NavigationController(this, anmController);
        onCreation();

        // Intent replicationServiceIntent = new Intent(this, ReplicationIntentService.class);
        //startService(replicationServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication) getApplication();
            application.logoutCurrentUser();
            return;
        }

        onResumption();
        setupReplicationBroadcastReceiver();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.switchLanguageMenuItem) {
            String newLanguagePreference = context().userService().switchLanguagePreference();
            Toast.makeText(this, "Language preference set to " + newLanguagePreference + ". "
                    + "Please restart the application.", LENGTH_SHORT).show();

            return super.onOptionsItemSelected(item);
        } else if (i == MENU_ITEM_LOGOUT) {
            DrishtiApplication application = (DrishtiApplication) getApplication();
            application.logoutCurrentUser();

            return super.onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Attaches a logout menu item to the provided menu
     *
     * @param menu The menu to attach the logout menu item
     */
    protected void attachLogoutMenuItem(Menu menu) {
        menu.add(0, MENU_ITEM_LOGOUT, menu.size(), R.string.logout_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        attachLogoutMenuItem(menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(openSRPClientBroadCastReceiver);

    }

    protected abstract void onCreation();

    protected abstract void onResumption();

    public void startFormActivity(String formName, String entityId, String metaData) {
        launchForm(formName, entityId, metaData, FormActivity.class);
    }

    public void startMicroFormActivity(String formName, String entityId, String metaData) {
        launchForm(formName, entityId, metaData, MicroFormActivity.class);
    }

    private void launchForm(String formName, String entityId, String metaData, Class formType) {
        this.metaData = metaData;

        Intent intent = new Intent(this, formType);
        intent.putExtra(FORM_NAME_PARAM, formName);
        intent.putExtra(ENTITY_ID_PARAM, entityId);
        addFieldOverridesIfExist(intent);
        startActivityForResult(intent, FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE);
    }

    private void addFieldOverridesIfExist(Intent intent) {
        if (hasMetadata()) {
            Map<String, String> metaDataMap = new Gson()
                    .fromJson(this.metaData, new TypeToken<Map<String, String>>() {
                    }.getType());
            if (metaDataMap.containsKey(FIELD_OVERRIDES_PARAM)) {
                intent.putExtra(FIELD_OVERRIDES_PARAM, metaDataMap.get(FIELD_OVERRIDES_PARAM));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isSuccessfulFormSubmission(resultCode)) {
            logInfo("Form successfully saved. MetaData: " + metaData);
            if (hasMetadata()) {
                Map<String, String> metaDataMap = new Gson()
                        .fromJson(metaData, new TypeToken<Map<String, String>>() {
                        }.getType());
                if (metaDataMap.containsKey(ENTITY_ID) && metaDataMap
                        .containsKey(ALERT_NAME_PARAM)) {
                    CoreLibrary.getInstance().context().alertService()
                            .changeAlertStatusToInProcess(metaDataMap.get(ENTITY_ID),
                                    metaDataMap.get(ALERT_NAME_PARAM));
                }
            }
        }
    }

    private boolean isSuccessfulFormSubmission(int resultCode) {
        return resultCode == AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE;
    }

    private boolean hasMetadata() {
        return this.metaData != null && !this.metaData.equalsIgnoreCase("undefined");
    }

    /**
     * Called by CloudantSyncHandler when it receives a replication complete callback.
     * CloudantSyncHandler takes care of calling this on the main thread.
     */
    public void replicationComplete() {
        //Toast.makeText(getApplicationContext(), "Replication Complete", Toast.LENGTH_LONG).show();
    }

    /**
     * Called by TasksModel when it receives a replication error callback.
     * TasksModel takes care of calling this on the main thread.
     */
    public void replicationError() {
        Log.e(LOG_TAG, "error()");
        //Toast.makeText(getApplicationContext(), "Replication Error", Toast.LENGTH_LONG).show();
    }

    private void setupReplicationBroadcastReceiver() {
        // The filter's action is BROADCAST_ACTION
        IntentFilter opensrpClientIntentFilter = new IntentFilter(
                CloudantSync.ACTION_DATABASE_CREATED);
        opensrpClientIntentFilter.addAction(CloudantSync.ACTION_REPLICATION_COMPLETED);
        opensrpClientIntentFilter.addAction(CloudantSync.ACTION_REPLICATION_ERROR);
        opensrpClientIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        opensrpClientIntentFilter.addAction("android.intent.action.TIME_SET");

        openSRPClientBroadCastReceiver = new OpenSRPClientBroadCastReceiver(this);
        // Registers the OpenSRPClientBroadCastReceiver and its intent filters
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(openSRPClientBroadCastReceiver, opensrpClientIntentFilter);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected Context context() {
        return CoreLibrary.getInstance().context().updateApplicationContext(this.getApplicationContext());
    }
}
