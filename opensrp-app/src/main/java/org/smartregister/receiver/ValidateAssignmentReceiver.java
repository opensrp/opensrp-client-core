package org.smartregister.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.dto.UserAssignmentDTO;
import org.smartregister.sync.helper.ValidateAssignmentHelper;

import java.io.Serializable;

/**
 * Created by samuelgithengi on 9/21/20.
 */
public class ValidateAssignmentReceiver extends BroadcastReceiver {

    private final UserAssignmentListener userAssignmentListener;

    public ValidateAssignmentReceiver(UserAssignmentListener userAssignmentListener) {
        this.userAssignmentListener = userAssignmentListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            Serializable removedAssignmentsSerializable = data.getSerializable(ValidateAssignmentHelper.ASSIGNMENTS_REMOVED);
            if (removedAssignmentsSerializable instanceof UserAssignmentDTO) {
                UserAssignmentDTO userAssignment = (UserAssignmentDTO) removedAssignmentsSerializable;
                userAssignmentListener.onUserAssignmentRevoked(userAssignment);
            }
        }
    }

    public interface UserAssignmentListener {

        void onUserAssignmentRevoked(UserAssignmentDTO userAssignment);
    }
}
