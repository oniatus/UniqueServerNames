package org.terasology.uniqueservernames.event;

import org.terasology.entitySystem.event.Event;
import org.terasology.network.ServerEvent;

/**
 * Send from the client to the server to submit a new name.
 */
@ServerEvent
public class SubmitNameEvent implements Event {

    private String name;

    public SubmitNameEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
