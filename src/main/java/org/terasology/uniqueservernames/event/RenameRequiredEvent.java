package org.terasology.uniqueservernames.event;

import org.terasology.entitySystem.event.Event;
import org.terasology.network.OwnerEvent;

/**
 * Send from the server to the client if the client has to select a new name.
 */
@OwnerEvent
public class RenameRequiredEvent implements Event {

}
