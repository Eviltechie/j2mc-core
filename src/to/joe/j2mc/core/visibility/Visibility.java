package to.joe.j2mc.core.visibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;
import to.joe.j2mc.core.exceptions.NoPlayersException;
import to.joe.j2mc.core.exceptions.TooManyPlayersException;

public class Visibility {

    /**
     * @param searcher
     *            set as null for accessing all players on server
     * @return
     */
    public List<Player> getOnlinePlayers(Player searcher) {
        List<Player> players = Arrays.asList(J2MC_Manager.getCore().getServer().getOnlinePlayers());
        if (searcher == null) {
            return players;
        } else {
            Iterator<Player> playerIterator = players.iterator();
            while (playerIterator.hasNext()) {
                Player p = playerIterator.next();
                if (!searcher.canSee(p) && !searcher.equals(p))
                    playerIterator.remove();
            }
            return players;
        }
    }
    
    /**
     * @param target
     * @param searcher
     *            set as null for accessing all players on server
     * @return player
     * @throws TooManyPlayersException
     * @throws NoPlayersException
     */
    public Player getPlayer(String target, CommandSender searcher) throws BadPlayerMatchException {
        return this.getPlayer(target, searcher, (String) null);
    }

    /**
     * @param target
     * @param searcher
     *            set as null for accessing all players on server
     * @param toIgnore
     *            users to ignore
     * @return player
     * @throws TooManyPlayersException
     * @throws NoPlayersException
     */
    public Player getPlayer(String target, CommandSender searcher, String... toIgnore) throws BadPlayerMatchException {

        final List<Player> players = new ArrayList<Player>();
        final Set<String> toIgnoreSet = new HashSet<String>();
        if (toIgnore != null) {
            for (int i = 0; i < toIgnore.length; i++) {
                if (toIgnore[i] != null) {
                    toIgnoreSet.add(toIgnore[i].toLowerCase());
                }
            }
        }
        for (final Player player : J2MC_Manager.getCore().getServer().getOnlinePlayers()) {
            boolean canSee = !(searcher instanceof Player) | ((Player)searcher).canSee(player) | ((Player)searcher).equals(player);
            if (!toIgnoreSet.contains(player.getName()) && canSee) {
                players.add(player);
            }
            if (player.getName().equalsIgnoreCase(target) && !toIgnoreSet.contains(player.getName())) {
                return player;
            }
        }
        if (players.size() > 1) {
            StringBuilder sb = new StringBuilder();
            for (Player player : players) {
                sb.append(player.getName());
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
            throw new TooManyPlayersException(sb.toString());
        }
        if (players.size() == 0) {
            throw new NoPlayersException();
        }
        return players.get(0);
    }

    /**
     * Is the player vanished?
     * 
     * @param player
     * @return
     */
    public boolean isVanished(Player player) {
        try {
            return VanishNoPacket.isVanished(player.getName());
        } catch (final VanishNotLoadedException e) {
            J2MC_Manager.getCore().buggerAll("VanishNoPacket DIED");
        }
        return false;
    }

}
