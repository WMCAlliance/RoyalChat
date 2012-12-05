package org.royaldev.royalchat.dependencies;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.command.CommandSender;

import static org.royaldev.royalchat.Language.NO_TOWNY_NATION;
import static org.royaldev.royalchat.Language.NO_TOWNY_SURNAME;
import static org.royaldev.royalchat.Language.NO_TOWNY_TITLE;
import static org.royaldev.royalchat.Language.NO_TOWNY_TOWN;

public class TownyUtils {

    public static String replaceTowny(final String format, final CommandSender cs) {
        String message = format;
        Resident r;
        try {
            r = TownyUniverse.getDataSource().getResident(cs.getName());
        } catch (NotRegisteredException e) {
            r = null;
        }
        boolean isResident = r != null;
        try {
            message = message.replace("{townytown}", (isResident) ? r.getTown().getName() : NO_TOWNY_TOWN.toString());
        } catch (NotRegisteredException e) {
            message = message.replace("{town}", NO_TOWNY_TOWN.toString());
        }
        message = message.replace("{townytitle}", (isResident) ? r.getTitle() : NO_TOWNY_TITLE.toString());
        message = message.replace("{townysurname}", (isResident) ? r.getSurname() : NO_TOWNY_SURNAME.toString());
        try {
            message = message.replace("{townynation}", (isResident) ? r.getTown().getName() : NO_TOWNY_NATION.toString());
        } catch (NotRegisteredException e) {
            message = message.replace("{townynation}", NO_TOWNY_NATION.toString());
        }
        return message;
    }

}
