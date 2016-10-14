package org.terasology.uniqueservernames.event;

import org.terasology.entitySystem.event.Event;
import org.terasology.network.ServerEvent;

/**
 * Send from the client to the server to check if a name is valid.
 */
@ServerEvent
public class CheckValidNameEvent implements Event {

    private String nameToCheck;

    public CheckValidNameEvent() {
    }

    public CheckValidNameEvent(String nameToCheck) {
        this.nameToCheck = nameToCheck;
    }

    public String getNameToCheck() {
        return nameToCheck;
    }

    public void setNameToCheck(String nameToCheck) {
        this.nameToCheck = nameToCheck;
    }

}
