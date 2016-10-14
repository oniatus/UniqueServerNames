package org.terasology.uniqueservernames.system;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.network.ClientComponent;
import org.terasology.network.ClientInfoComponent;

class ClientEntityUtil {
    public static String getPlayerName(EntityRef clientEntity) {
        return getDisplayNameComponent(clientEntity).name;
    }

    public static DisplayNameComponent getDisplayNameComponent(EntityRef clientEntity) {
        EntityRef clientInfo = clientEntity.getComponent(ClientComponent.class).clientInfo;
        DisplayNameComponent displayNameComponent = clientInfo.getComponent(DisplayNameComponent.class);
        return displayNameComponent;
    }

    public static String getPlayerId(EntityRef clientEntity) {
        EntityRef clientInfo = clientEntity.getComponent(ClientComponent.class).clientInfo;
        ClientInfoComponent clientInfoComponent = clientInfo.getComponent(ClientInfoComponent.class);
        return clientInfoComponent.playerId;
    }

    public static void renameClient(EntityRef clientEntity, String newName) {
        ClientComponent clientInfoComponent = clientEntity.getComponent(ClientComponent.class);
        EntityRef clientInfo = clientInfoComponent.clientInfo;
        DisplayNameComponent displayNameComponent = clientInfo.getComponent(DisplayNameComponent.class);
        displayNameComponent.name = newName;
        clientInfo.saveComponent(displayNameComponent);
    }

}
