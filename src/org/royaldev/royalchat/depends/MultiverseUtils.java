package org.royaldev.royalchat.depends;

import org.bukkit.World;
import org.royaldev.royalchat.RoyalChat;

public class MultiverseUtils {

    public static String getMVWorldName(World w) {
        if (w == null) throw new NullPointerException("w can't be null!");
        if (RoyalChat.mvc == null) return w.getName();
        return RoyalChat.mvc.getMVWorldManager().getMVWorld(w).getColoredWorldString();
    }

}
