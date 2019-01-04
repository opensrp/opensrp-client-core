package org.smartregister.event;

import org.apache.commons.lang3.ObjectUtils;
import org.smartregister.domain.FetchStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that provides callbacks to OpenSRP application conscious events
 * @param <T>
 *
 * @since 2018-01-01
 * @version 0.1
 * @author OpenSRPLegends
 */
public class Event<T> {
    public static final Event<FetchStatus> ON_DATA_FETCHED = new Event<FetchStatus>();
    public static final Event<CapturedPhotoInformation> ON_PHOTO_CAPTURED = new
            Event<CapturedPhotoInformation>();
    public static final Event<Boolean> ON_LOGOUT = new Event<Boolean>();
    public static final Event<Boolean> SYNC_STARTED = new Event<Boolean>();
    public static final Event<Boolean> SYNC_COMPLETED = new Event<Boolean>();
    public static final Event<String> FORM_SUBMITTED = new Event<String>();
    public static final Event<String> ACTION_HANDLED = new Event<String>();

    List<WeakReference<Listener<T>>> listeners;

    public Event() {
        listeners = new ArrayList<WeakReference<Listener<T>>>();
    }

    public void addListener(Listener<T> listener) {
        listeners.add(new WeakReference<Listener<T>>(listener));
    }

    public void removeListener(Listener<T> listener) {
        WeakReference<Listener<T>> listenerToRemove = null;
        for (WeakReference<Listener<T>> l : listeners) {
            if (ObjectUtils.equals(listener, l.get())) {
                listenerToRemove = l;
                break;
            }
        }
        listeners.remove(listenerToRemove);
    }

    public void notifyListeners(T data) {
        for (WeakReference<Listener<T>> listener : listeners) {
            if (listener.get() != null) {
                listener.get().onEvent(data);
            }
        }
    }
}
