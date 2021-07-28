package org.smartregister;

import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;

/***
 * This interface provides a common entry point for processing obs values during client prcessing.
 *
 * Native forms custom fields can implement and use this interface to overide default behaviour
 * when saving the event
 */
public interface NativeFormFieldProcessor {

    /***
     *
     * @param event The event object being composed during client processing
     * @param jsonObject Native forms field object to be processed
     *
     */
    void processJsonField(Event event, JSONObject jsonObject);
}
