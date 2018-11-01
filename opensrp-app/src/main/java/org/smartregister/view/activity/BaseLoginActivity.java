package org.smartregister.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.smartregister.R;
import org.smartregister.view.contract.BaseLoginContract;

public abstract class BaseLoginActivity extends AppCompatActivity implements BaseLoginContract.View, TextView.OnEditorActionListener, View.OnClickListener {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));
        BaseLoginContract.Presenter presenter = null;
        setUpLoginViews(presenter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase("Settings")) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected abstract void initializeLoginChildViews();

    protected abstract void setListenerOnShowPasswordCheckbox();

    protected abstract void renderBuildInfo();

    protected void setUpLoginViews(BaseLoginContract.Presenter presenter){
        presenter.positionViews();
        initializeLoginChildViews();
        initializeProgressDialog();
        setListenerOnShowPasswordCheckbox();
        renderBuildInfo();

    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

}
