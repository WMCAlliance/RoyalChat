package org.royaldev.royalchat.dependencies;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.UPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.royaldev.royalchat.Language.NO_FACTIONS_FACTION;
import static org.royaldev.royalchat.Language.NO_FACTIONS_POWER;
import static org.royaldev.royalchat.Language.NO_FACTIONS_TAG;
import static org.royaldev.royalchat.Language.NO_FACTIONS_TITLE;

public class FactionsUtils {
    /**
     * Gets the UPlayer for a name
     *
     * @param name Name of UPlayer to get
     * @return UPlayer or null
     */
    public static UPlayer getUPlayer(String name) {
        return UPlayer.get(name);
    }

    /**
     * Gets the UPlayer for a player
     *
     * @param p Player to get UPlayer of
     * @return UPlayer or null
     */
    public static UPlayer getUPlayer(Player p) {
        return UPlayer.get(p);
    }

    /**
     * Gets the faction tag to be sent to someone (with colors!)
     *
     * @param from Person sending message
     * @param to   Person receiving message
     * @return Faction tag with prefix & colors
     */
    public static String getColoredFactionTag(final UPlayer from, final UPlayer to) {
        String relationColor = to.getRelationTo(from).getColor().toString();
        String factionTag = from.getFactionName();
        final Rel role = from.getRole();
        String prefix = (role == null) ? "" : role.getPrefix();
        StringBuilder sb = new StringBuilder();
        sb.append(relationColor);
        sb.append(prefix);
        sb.append(factionTag);
        sb.append(ChatColor.RESET);
        return sb.toString();
    }

    /**
     * Gets the faction tag to be sent to someone (no colors)
     *
     * @param fp Person to get tag of
     * @return Faction tag
     */
    public static String getFactionTag(final UPlayer fp) {
        String factionTag = fp.getFactionName();
        final Rel role = fp.getRole();
        String prefix = (role == null) ? "" : role.getPrefix();
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(factionTag);
        sb.append(ChatColor.RESET);
        return sb.toString();
    }

    /**
     * Replaces all Factions variables.
     *
     * @param format Format with Factions variables
     * @param cs     CommandSender sending message
     * @return format with replaced variables
     */
    public static String replaceFactions(final String format, final CommandSender cs) {
        String message = format;
        UPlayer fp = getUPlayer(cs.getName());
        boolean isFP = fp != null;
        message = message.replace("{factionstitle}", (isFP) ? fp.getTitle() : NO_FACTIONS_TITLE.toString());
        message = message.replace("{factionsfaction}", (isFP) ? fp.getFactionName() : NO_FACTIONS_FACTION.toString());
        message = message.replace("{factionstag}", (isFP) ? getFactionTag(fp) : NO_FACTIONS_TAG.toString());
        message = message.replace("{factionspower}", (isFP) ? String.valueOf(fp.getPowerRounded()) : NO_FACTIONS_POWER.toString());
        return message;
    }
}
