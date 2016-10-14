package org.terasology.uniqueservernames.event;

import org.terasology.entitySystem.event.Event;
import org.terasology.network.OwnerEvent;

/**
 * Send from the server to the client as response to an {@link CheckValidNameEvent} if a name is unique.
 */
@OwnerEvent
public class CheckValidNameResponseEvent implements Event {

    private String nameToCheck;
    private boolean isValid;

    public CheckValidNameResponseEvent() {
    }

    public CheckValidNameResponseEvent(String nameToCheck, boolean isValid) {
        this.nameToCheck = nameToCheck;
        this.isValid = isValid;
    }

    public String getNameToCheck() {
        return nameToCheck;
    }

    public void setNameToCheck(String nameToCheck) {
        this.nameToCheck = nameToCheck;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }
}
