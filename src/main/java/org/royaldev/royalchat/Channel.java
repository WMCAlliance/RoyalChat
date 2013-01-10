package org.royaldev.royalchat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Channel {

    private final String name;
    private final boolean defaultChannel;
    private final List<String> members = new ArrayList<String>();

    private boolean omnipresent;
    private boolean multiworld;
    private String chatFormat;
    private String password;
    private double chatRadius;

    public Channel(final boolean defaultChannel, final boolean omnipresent, final boolean multiworld, final String name, final String password, final String chatFormat, final double chatRadius) {
        this.defaultChannel = defaultChannel;
        this.omnipresent = omnipresent;
        this.multiworld = multiworld;
        this.name = name;
        this.password = password;
        this.chatFormat = chatFormat;
        this.chatRadius = chatRadius;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getDefaultChannel() {
        return defaultChannel;
    }

    public boolean getOmnipresent() {
        return omnipresent;
    }

    public void setOmnipresent(boolean b) {
        omnipresent = b;
    }

    public String getChatFormat() {
        return chatFormat;
    }

    public void setChatFormat(String s) {
        chatFormat = s;
    }

    public double getChatRadius() {
        return chatRadius;
    }

    public void setChatRadius(double d) {
        chatRadius = d;
    }

    public boolean getMultiworld() {
        return multiworld;
    }

    public void setMultiworld(boolean b) {
        multiworld = b;
    }

    public void addMember(String name) {
        synchronized (members) {
            if (members.contains(name)) return;
            members.add(name);
        }
    }

    public void addMember(CommandSender cs) {
        if (!(cs instanceof Player)) return;
        synchronized (members) {
            if (members.contains(cs.getName())) return;
            members.add(cs.getName());
        }
    }

    public void removeMember(String name) {
        synchronized (members) {
            if (!members.contains(name)) return;
            members.remove(name);
        }
    }

    public void removeMember(CommandSender cs) {
        if (!(cs instanceof Player)) return;
        synchronized (members) {
            if (!members.contains(cs.getName())) return;
            members.remove(cs.getName());
        }
    }

    public List<String> getMembers() {
        return members;
    }

    public boolean isPasswordProtected() {
        return password != null;
    }

}
