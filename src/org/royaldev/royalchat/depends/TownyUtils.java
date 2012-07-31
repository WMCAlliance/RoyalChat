package org.royaldev.royalchat.depends;

import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalchat.RUtils;
import org.royaldev.royalchat.RoyalChat;

public class TownyUtils {

    private static Resident getResident(Player p) {
        if (RoyalChat.towny == null) return null;
        try {
            return TownyUniverse.getDataSource().getResident(p.getName());
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    private static String getTitle(Player p) {
        if (RoyalChat.towny == null) return "";
        if (p == null) return "";
        Resident r = getResident(p);
        if (r == null) return "";
        return r.getTitle();
    }

    private static String getTown(Player p) {
        if (RoyalChat.towny == null) return "";
        if (p == null) return "";
        Resident r = getResident(p);
        if (r == null) return "";
        try {
            return r.getTown().getName();
        } catch (NotRegisteredException e) {
            return "";
        }
    }

    private static String getNation(Player p) {
        if (RoyalChat.towny == null) return "";
        if (p == null) return "";
        Resident r = getResident(p);
        if (r == null) return "";
        try {
            Town t = r.getTown();
            String nation = t.getNation().getName();
            if (nation == null) return "";
            return nation;
        } catch (NotRegisteredException e) {
            return "";
        }
    }

    private static String getPrefix(Player p) {
        if (RoyalChat.towny == null) return "";
        if (p == null) return "";
        Resident r = getResident(p);
        if (r == null) return "";
        String prefix = TownyFormatter.getNamePrefix(r);
        if (prefix == null) return "";
        return prefix;
    }

    private static String getSuffix(Player p) {
        if (RoyalChat.towny == null) return "";
        if (p == null) return "";
        Resident r = getResident(p);
        if (r == null) return "";
        String suffix = TownyFormatter.getNamePostfix(r);
        if (suffix == null) return "";
        return suffix;
    }

    private static String getSurname(Player p) {
        if (RoyalChat.towny == null) return "";
        if (p == null) return "";
        Resident r = getResident(p);
        if (r == null) return "";
        String surname = r.getSurname();
        if (surname == null) return "";
        return surname;
    }

    /**
     * Replaces any Towny variables with their values. If Towny is not installed, or if cs is not a player, they are removed.
     *
     * @param s  String with Towny variables
     * @param cs Message sender
     * @return Processed string
     */
    public static String replaceTownyVars(String s, CommandSender cs) {
        if (RoyalChat.towny == null) return RUtils.removeAllTownyVars(s);
        if (!(cs instanceof Player)) return RUtils.removeAllTownyVars(s);
        Player p = (Player) cs;
        s = s.replaceAll("(?i)\\{townytown\\}", getTown(p));
        s = s.replaceAll("(?i)\\{townyprefix\\}", getPrefix(p));
        s = s.replaceAll("(?i)\\{townysuffix\\}", getSuffix(p));
        s = s.replaceAll("(?i)\\{townynation\\}", getNation(p));
        s = s.replaceAll("(?i)\\{townysurname\\}", getSurname(p));
        s = s.replaceAll("(?i)\\{townytitle\\}", getTitle(p));
        return s;
    }

}
