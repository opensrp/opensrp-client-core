package org.ei.opensrp.event;

public interface Listener<T> {
    void onEvent(T data);
}
