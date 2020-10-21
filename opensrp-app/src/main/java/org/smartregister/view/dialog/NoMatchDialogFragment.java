package org.smartregister.view.dialog;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import org.smartregister.R;
import org.smartregister.view.activity.BaseRegisterActivity;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-09-2020
 */

@SuppressLint("ValidFragment")
public class NoMatchDialogFragment extends DialogFragment {

    private final NoMatchDialogActionHandler noMatchDialogActionHandler = new NoMatchDialogActionHandler();
    private final BaseRegisterActivity baseRegisterActivity;
    private final String uniqueId;

    public NoMatchDialogFragment(BaseRegisterActivity baseRegisterActivity, String uniqueId) {
        this.uniqueId = uniqueId;
        this.baseRegisterActivity = baseRegisterActivity;
    }

    @Nullable
    public static NoMatchDialogFragment launchDialog(BaseRegisterActivity activity, String dialogTag, String whoAncId) {
        NoMatchDialogFragment noMatchDialogFragment = new NoMatchDialogFragment(activity, whoAncId);
        if (activity != null) {
            FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
            Fragment prev = activity.getFragmentManager().findFragmentByTag(dialogTag);
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);

            noMatchDialogFragment.show(fragmentTransaction, dialogTag);

            return noMatchDialogFragment;
        } else {
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_no_match, container, false);
        Button cancel = dialogView.findViewById(R.id.cancel_no_match_dialog);
        cancel.setOnClickListener(noMatchDialogActionHandler);
        Button advancedSearch = dialogView.findViewById(R.id.go_to_advanced_search);
        advancedSearch.setOnClickListener(noMatchDialogActionHandler);

        return dialogView;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        baseRegisterActivity.setSearchTerm("");
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class NoMatchDialogActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.cancel_no_match_dialog) {

                dismiss();
                baseRegisterActivity.setSearchTerm("");
            } else if (view.getId() == R.id.go_to_advanced_search) {
                baseRegisterActivity.setSearchTerm("");
                goToAdvancedSearch(uniqueId);
                baseRegisterActivity.setSelectedBottomBarMenuItem(R.id.action_search);
                dismiss();
            }
        }

        private void goToAdvancedSearch(String uniqueId) {
            Timber.i(uniqueId);
            // TODO Implement Advanced Search Page
        }
    }
}