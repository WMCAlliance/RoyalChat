package org.royaldev.royalchat.listeners;

import com.massivecraft.factions.entity.UPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.royaldev.royalchat.Channel;
import org.royaldev.royalchat.RoyalChat;
import org.royaldev.royalchat.dependencies.FactionsUtils;
import org.royaldev.royalchat.rcommands.CmdAdminChat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ChatListener implements Listener {

    private final RoyalChat plugin;

    public ChatListener(RoyalChat instance) {
        plugin = instance;
    }

    @EventHandler
    public void channelAdder(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!plugin.useChannels) return;
        if (plugin.dm.isInChannel(p)) return;
        Channel c = plugin.dm.getDefaultChannel();
        if (c == null) return;
        c.addMember(p);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ohLordFactionsSucksSoBad(AsyncPlayerChatEvent e) {
        if (!plugin.withFactions) return;
        UPlayer from = FactionsUtils.getUPlayer(e.getPlayer());
        String format = e.getFormat().replace("%%", "%"); // fix sanitized chat, as this is no longer vanilla
        if (!format.contains("{factionscoloredtag}")) return;
        plugin.getServer().getConsoleSender().sendMessage(format.replace("{factionscoloredtag}", FactionsUtils.getFactionTag(from)));
        for (Player t : e.getRecipients()) {
            UPlayer to = FactionsUtils.getUPlayer(t);
            t.sendMessage(format.replace("{factionscoloredtag}", FactionsUtils.getColoredFactionTag(from, to)));
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        Player p = e.getPlayer();
        boolean isAdminChat = CmdAdminChat.isToggled(p);
        if (isAdminChat) {
            e.getRecipients().clear();
            String format = plugin.getConfig().getString("chat.admin-chat-format", "&b[Admin] {dispname}:&3 {message}");
            format = plugin.dm.formatChat(p, format, e.getMessage(), false, true, false, true);
            for (Player t : plugin.getServer().getOnlinePlayers()) {
                if (!plugin.isAuthorized(t, "rchat.adminchat")) continue;
                t.sendMessage(format);
            }
            Logger.getLogger("Minecraft").info(format);
            e.setFormat("");
            e.setMessage("");
            e.setCancelled(true);
            return;
        }
        if (plugin.dm.isInChannel(p) && plugin.useChannels) {
            Channel c = plugin.dm.getChannelOf(p);
            if (c == null) return;
            if (c.getOmnipresent()) {
                String format = plugin.getConfig().getString("channels." + c.getName() + ".chat-format");
                if (format == null) return;
                format = plugin.dm.formatChat(p, format, e.getMessage(), false, false, true, true);
                e.setFormat(format);
                return;
            }
            e.getRecipients().clear();
            for (String name : c.getMembers()) {
                Player t = plugin.getServer().getPlayer(name);
                if (t == null) continue;
                e.getRecipients().add(t);
            }
            for (Player pl : plugin.getServer().getOnlinePlayers())
                if (plugin.isAuthorized(pl, "rchat.channels.snoop")) e.getRecipients().add(pl);
            Double chatRadius = c.getChatRadius();
            if (chatRadius == null) chatRadius = 0D;
            if (chatRadius > 0D) {
                final List<Entity> ents = p.getNearbyEntities(chatRadius, chatRadius, chatRadius);
                final List<Player> toRemove = new ArrayList<Player>(); // not using this list will result in a CCME
                for (Player t : e.getRecipients()) {
                    if (t.equals(p)) continue;
                    if (ents.contains(t)) continue;
                    toRemove.add(t);
                }
                e.getRecipients().removeAll(toRemove);
            }
            String format = plugin.getConfig().getString("channels." + c.getName() + ".chat-format");
            if (format == null) return;
            format = plugin.dm.formatChat(p, format, e.getMessage(), false, false, true, true);
            e.setFormat(format);
            return;
        }
        String format = plugin.getConfig().getString("chat.default-chat-format");
        format = plugin.dm.formatChat(p, format, e.getMessage(), false, false, false, true);
        e.setFormat(format); // set message as well?
    }

}
