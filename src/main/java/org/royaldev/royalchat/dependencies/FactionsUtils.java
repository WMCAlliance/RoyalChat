package org.royaldev.royalchat.dependencies;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.royaldev.royalchat.Language.NO_FACTIONS_FACTION;
import static org.royaldev.royalchat.Language.NO_FACTIONS_POWER;
import static org.royaldev.royalchat.Language.NO_FACTIONS_TAG;
import static org.royaldev.royalchat.Language.NO_FACTIONS_TITLE;

public class FactionsUtils {

    /**
     * Gets the faction tag to be sent to someone (with colors!)
     *
     * @param from Person sending message
     * @param to   Person receiving message
     * @return Faction tag with prefix & colors
     */
    public static String getColoredFactionTag(final FPlayer from, final FPlayer to) {
        final String relationColor = to.getRelationTo(from).getColor().toString();
        final String factionTag = from.getFaction().getTag();
        final Role role = from.getRole();
        String prefix = (role == null) ? "" : role.getPrefix();
        return relationColor + prefix + factionTag + ChatColor.RESET;
    }

    /**
     * Gets the FPlayer for a player
     *
     * @param p Player to get FPlayer of
     * @return FPlayer or null
     */
    public static FPlayer getFPlayer(final Player p) {
        return FPlayers.getInstance().getByPlayer(p);
    }

    /**
     * Gets the FPlayer for a name
     *
     * @param name Name of FPlayer to get
     * @return FPlayer or null
     */
    public static FPlayer getFPlayer(final String name) {
        return FPlayers.getInstance().getById(name);
    }

    /**
     * Gets the faction tag to be sent to someone (no colors)
     *
     * @param fp Person to get tag of
     * @return Faction tag
     */
    public static String getFactionTag(final FPlayer fp) {
        final String factionTag = fp.getFaction().getTag();
        final Role role = fp.getRole();
        final String prefix = (role == null) ? "" : role.getPrefix();
        return prefix + factionTag + ChatColor.RESET;
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
        final FPlayer fp = FactionsUtils.getFPlayer(cs.getName());
        boolean isFP = fp != null;
        message = message.replace("{factionstitle}", (isFP) ? fp.getTitle() : NO_FACTIONS_TITLE.toString());
        message = message.replace("{factionsfaction}", (isFP) ? fp.getFaction().getTag() : NO_FACTIONS_FACTION.toString());
        message = message.replace("{factionstag}", (isFP) ? getFactionTag(fp) : NO_FACTIONS_TAG.toString());
        message = message.replace("{factionspower}", (isFP) ? String.valueOf(fp.getPowerRounded()) : NO_FACTIONS_POWER.toString());
        return message;
    }
}
