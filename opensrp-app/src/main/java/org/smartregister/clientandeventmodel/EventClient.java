package org.smartregister.clientandeventmodel;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 30-09-2020.
 */
public class EventClient {

    private Event event;
    private Client client;

    public EventClient(Event event, Client client) {
        this.event = event;
        this.client = client;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
