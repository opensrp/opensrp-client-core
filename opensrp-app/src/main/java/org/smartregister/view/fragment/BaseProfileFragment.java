package org.smartregister.view.fragment;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.smartregister.view.activity.BaseProfileActivity;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public abstract class BaseProfileFragment extends SecuredFragment implements View.OnTouchListener {

    private GestureDetectorCompat gestureDetector;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestureDetector = new GestureDetectorCompat(this.getActivity(), new ProfileFragmentsSwipeListener());
        view.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        return gestureDetector.onTouchEvent(event);
    }

    class ProfileFragmentsSwipeListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            final int SWIPE_MIN_DISTANCE = 120;
            final int SWIPE_MAX_OFF_PATH = 300;
            final int SWIPE_THRESHOLD_VELOCITY = 200;
            try {
                if (Math.abs(event1.getX() - event2.getX()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }
                if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {

                    ((BaseProfileActivity) getActivity()).getProfileAppBarLayout().setExpanded(false, true);
                } else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {

                    ((BaseProfileActivity) getActivity()).getProfileAppBarLayout().setExpanded(true, true);
                }
            } catch (Exception e) {
                // nothing
            }
            return super.onFling(event1, event2, velocityX, velocityY);

        }
    }
}