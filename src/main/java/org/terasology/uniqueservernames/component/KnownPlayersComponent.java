package org.terasology.uniqueservernames.component;

import com.google.common.collect.Maps;
import org.terasology.entitySystem.Component;
import org.terasology.network.ClientInfoComponent;

import java.util.Map;

/**
 * Storage for known player names. The storage is case insensitive.
 */
public class KnownPlayersComponent implements Component {

    private Map<String, String> playerIdToName = Maps.newHashMap();

    /**
     * Returns true if the player id is already registered.
     */
    public boolean containsId(String playerId) {
        return playerIdToName.containsKey(playerId);
    }

    /**
     * Returns true if the name (case insensitive) is already registered.
     */
    public boolean containsName(String playerName) {
        return playerIdToName.containsValue(playerName.toLowerCase());
    }

    /**
     * Registers a new name for the given playerId. The name is 
     * @param playerId Id of the player (From {@link ClientInfoComponent}).
     * @param playerName Name of the player (case insensitive).
     * @throws RuntimeException if the id or name is already registered.
     */
    public void register(String playerId, String playerName) {
        if (containsId(playerId)) {
            throw new RuntimeException("A player with id " + playerId + " is already registered.");
        }
        if (containsName(playerName)) {
            throw new RuntimeException("The name " + playerName + " is already registered.");
        }
        playerIdToName.put(playerId, playerName.toLowerCase());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((playerIdToName == null) ? 0 : playerIdToName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KnownPlayersComponent other = (KnownPlayersComponent) obj;
        if (playerIdToName == null) {
            if (other.playerIdToName != null)
                return false;
        } else if (!playerIdToName.equals(other.playerIdToName))
            return false;
        return true;
    }
}
