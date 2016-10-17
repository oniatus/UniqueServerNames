package org.terasology.uniqueservernames.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.network.ClientComponent;
import org.terasology.network.events.ConnectedEvent;
import org.terasology.registry.In;
import org.terasology.uniqueservernames.component.KnownPlayersComponent;
import org.terasology.uniqueservernames.event.CheckValidNameEvent;
import org.terasology.uniqueservernames.event.CheckValidNameResponseEvent;
import org.terasology.uniqueservernames.event.RenameRequiredEvent;
import org.terasology.uniqueservernames.event.SubmitNameEvent;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;

import java.util.Iterator;

import static org.terasology.uniqueservernames.system.ClientEntityUtil.getPlayerId;
import static org.terasology.uniqueservernames.system.ClientEntityUtil.getPlayerName;
import static org.terasology.uniqueservernames.system.ClientEntityUtil.renameClient;

@RegisterSystem(RegisterMode.AUTHORITY)
public class ServerUniqueNameSystem extends BaseComponentSystem {

    private static final Logger LOG = LoggerFactory.getLogger(ServerUniqueNameSystem.class);

    private static final String RENAME_ACTION_ID = "ACTION_RENAME";
    private static final long RENAME_ACTION_DELAY_MS = 2000;

    @In
    private DelayManager delayManager;

    @In
    private EntityManager entityManager;

    private Random random = new FastRandom();

    private KnownPlayersComponent knownPlayersComponent;

    @Override
    public void postBegin() {
        knownPlayersComponent = loadOrCreateKnownPlayerStore();
    }

    @SuppressWarnings("unchecked")
    private KnownPlayersComponent loadOrCreateKnownPlayerStore() {
        Iterator<EntityRef> global = entityManager.getEntitiesWith(KnownPlayersComponent.class).iterator();
        while (global.hasNext()) {
            return global.next().getComponent(KnownPlayersComponent.class);
        }
        //if the entity does not exist, create it
        KnownPlayersComponent knownPlayers = new KnownPlayersComponent();
        EntityRef entity = entityManager.create(knownPlayers);
        entity.setAlwaysRelevant(true);
        return entity.getComponent(KnownPlayersComponent.class);
    }

    @ReceiveEvent(components = ClientComponent.class, priority = EventPriority.PRIORITY_HIGH)
    public void onConnect(ConnectedEvent event, EntityRef entity) {
        String playerId = getPlayerId(entity);
        boolean newPlayer = !knownPlayersComponent.containsId(playerId);
        if (newPlayer) {
            String oldName = getPlayerName(entity);
            String newName = "Unnamed-" + random.nextString(5);
            renameClient(entity, newName);
            LOG.info("{} connected for the first time and has been renamed to {}. Requesting renaming.", oldName, newName);
            //TODO the connected event seems to be fired before the remote client has an active connection
            //sending the event immediately did not work together with network replication
            delayManager.addDelayedAction(entity, RENAME_ACTION_ID, RENAME_ACTION_DELAY_MS);
        }
    }

    @ReceiveEvent
    public void onDelayedExplosion(DelayedActionTriggeredEvent event, EntityRef entity) {
        if (event.getActionId().equals(RENAME_ACTION_ID)) {
            entity.send(new RenameRequiredEvent());
        }
    }

    @ReceiveEvent
    public void onCheckUniqueName(CheckValidNameEvent event, EntityRef entity) {
        String nameToCheck = event.getNameToCheck();
        boolean valid = isUniqueName(nameToCheck);
        entity.send(new CheckValidNameResponseEvent(nameToCheck, valid));
    }

    @ReceiveEvent
    public void onSubmitName(SubmitNameEvent event, EntityRef entity) {
        String newName = event.getName();
        if (isUniqueName(newName)) {
            renameAndRegister(newName, entity);
        } else {
            //the client submitted a invalid name -> ask again
            entity.send(new RenameRequiredEvent());
        }
    }

    private boolean isUniqueName(String nameToCheck) {
        return !knownPlayersComponent.containsName(nameToCheck);
    }

    private void renameAndRegister(String newName, EntityRef entity) {
        String oldName = getPlayerName(entity);
        LOG.info("Client selected a new name, renaming {} to {}", oldName, newName);
        knownPlayersComponent.register(getPlayerId(entity), newName);
        renameClient(entity, newName);
    }

}
