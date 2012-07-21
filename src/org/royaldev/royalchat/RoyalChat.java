package org.royaldev.royalchat;

/*
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 This plugin was written by jkcclemens <jkc.clemens@gmail.com>.
 If forked and not credited, alert him.
*/

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.royaldev.royalchat.listeners.RoyalChatPListener;
import org.royaldev.royalchat.listeners.SpoutListener;
import org.royaldev.royalchat.utils.Channeler;
import org.royaldev.royalchat.utils.Formatter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RoyalChat extends JavaPlugin {

    public String version;

    public Logger log = Logger.getLogger("Minecraft");

    public static Permission permission = null;
    public static Chat chat = null;

    public boolean spout;

    public Formatter f = new Formatter(this);
    public Channeler c = new Channeler(this);

    public Metrics m = null;

    public List<Player> acd = new ArrayList<Player>();

    private final RoyalChatPListener playerListener = new RoyalChatPListener(this);

    public Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public Boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    public String formatBase = null;
    public String formatMeBase = null;
    public String formatSay = null;
    public String formatAdmin = null;
    public String joinMessage = null;
    public String quitMessage = null;
    public String kickMessage = null;
    public String worldMessage = null;
    public Boolean firstWordCapital = null;
    public Boolean highlightAtUser = null;
    public Boolean highlightUrls = null;
    public Boolean smokeAtUser = null;
    public Boolean dispCounter = null;
    public Boolean dispNotify = null;
    public Boolean remCaps = null;
    public Boolean useAtSign = null;
    public Boolean interWorld = null;
    public Boolean useChannels = null;
    public Float capsPerc = null;
    public Float maxRadius = null;

    public void loadConfiguration() {
        formatBase = colorize(getConfig().getString("chat-format"));
        formatMeBase = colorize(getConfig().getString("me-format"));
        formatSay = colorize(getConfig().getString("say-format"));
        formatAdmin = colorize(getConfig().getString("admin-format"));
        joinMessage = colorize(getConfig().getString("join-message"));
        kickMessage = colorize(getConfig().getString("kick-message"));
        quitMessage = colorize(getConfig().getString("quit-message"));
        worldMessage = colorize(getConfig().getString("world-message"));
        firstWordCapital = getConfig().getBoolean("first-word-capital");
        highlightAtUser = getConfig().getBoolean("highlight-at-user");
        highlightUrls = getConfig().getBoolean("highlight-urls");
        smokeAtUser = getConfig().getBoolean("smoke-at-user");
        dispCounter = getConfig().getBoolean("display-messages-counter");
        dispNotify = getConfig().getBoolean("display-messages-achievements");
        remCaps = getConfig().getBoolean("remove-all-caps");
        useAtSign = getConfig().getBoolean("use-at-sign");
        interWorld = getConfig().getBoolean("interworld");
        useChannels = getConfig().getBoolean("use-channels");
        capsPerc = (float) this.getConfig().getDouble("caps-removal-percent");
        maxRadius = (float) this.getConfig().getDouble("chat-radius");
    }

    public String colorize(String text) {
        if (text == null) return null;
        return text.replaceAll("(&([a-f0-9k-orR]))", "\u00A7$2");
    }

    public String returnAlias(World w) {
        if (w == null) return "";
        Plugin mv = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (mv == null) return w.getName();
        MultiverseCore mvc = (MultiverseCore) mv;
        MultiverseWorld mvw = mvc.getMVWorldManager().getMVWorld(w);
        return mvw.getColoredWorldString();
    }

    public void onEnable() {

        if (!new File(getDataFolder() + File.separator + "config.yml").exists()) saveDefaultConfig();

        try {
            m = new Metrics(this);
            m.start();
        } catch (Exception e) {
            getLogger().warning("Could not start metrics!");
        }

        spout = getServer().getPluginManager().isPluginEnabled("Spout");

        version = this.getDescription().getVersion();

        loadConfiguration();

        setupChat();
        setupPermissions();

        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(playerListener, this);
        if (spout) pm.registerEvents(new SpoutListener(this), this);

        RoyalChatCommands cmdExec = new RoyalChatCommands(this);

        getCommand("rchat").setExecutor(cmdExec);
        getCommand("me").setExecutor(cmdExec);
        getCommand("rclear").setExecutor(cmdExec);
        getCommand("say").setExecutor(cmdExec);
        getCommand("ac").setExecutor(cmdExec);
        getCommand("ch").setExecutor(cmdExec);

        log.info("[RoyalChat] Version " + this.version + " initiated.");

    }

    public void onDisable() {

        log.info("[RoyalChat] Version " + this.version + " disabled.");

    }

}
