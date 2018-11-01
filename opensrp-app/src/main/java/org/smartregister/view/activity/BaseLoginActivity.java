package org.smartregister.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.smartregister.R;
import org.smartregister.domain.LoginResponse;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseLoginContract;

public abstract class BaseLoginActivity extends AppCompatActivity implements BaseLoginContract.View, TextView.OnEditorActionListener, View.OnClickListener {
    private ProgressDialog progressDialog;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));
//
//        mLoginPresenter = new BaseLoginPresenter(this);
//        mLoginPresenter.setLanguage();
//        setupViews(mLoginPresenter);
//
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Settings");
        return true;
    }



    protected abstract void initializeLoginChildViews();

//
//    protected void setUpLoginViews(BaseLoginContract.Presenter presenter){
//        presenter.positionViews();
//        initializeLoginChildViews();
//        initializeProgressDialog();
//        setListenerOnShowPasswordCheckbox();
//        renderBuildInfo();
//
//    }

    protected void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

    public void showProgress(final boolean show) {
        if (show) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }
    @Override
    public void updateProgressMessage(String message) {
        progressDialog.setTitle(message);

    }

    protected void setListenerOnShowPasswordCheckbox(CheckBox showPasswordCheckBox,final EditText passwordEditText) {
        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void showErrorDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(org.smartregister.R.string.login_failed_dialog_title))
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();

    }


    @Override
    public void hideKeyboard() {
        Log.i(getClass().getName(), "Hiding Keyboard " + DateTime.now().toString());
        Utils.hideKeyboard(this);
    }
    protected void renderBuildInfo() {
        TextView application_version = findViewById(R.id.login_build_text_view);
        if (application_version != null) {
            try {
                application_version.setText(String.format(getString(R.string.app_version), Utils.getVersion(this), Utils.getBuildDate(true)));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getUserTeamId(LoginResponse loginResponse) {
        return Utils.getUserDefaultTeamId(loginResponse.payload());
    }
    @Override
    public Activity getActivityContext() {
        return this;

    }





}
