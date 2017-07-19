package org.opensrp.event;

public interface Listener<T> {
    void onEvent(T data);
}
