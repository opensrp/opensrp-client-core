package org.smartregister.clientandeventmodel;

/**
 * Created by samuelgithengi on 7/6/18.
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
