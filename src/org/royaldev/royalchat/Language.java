package org.royaldev.royalchat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public enum Language {

    NO_TOWNY_TOWN,
    NO_TOWNY_TITLE,
    NO_TOWNY_SURNAME,
    NO_TOWNY_NATION,

    NO_FACTIONS_POWER,
    NO_FACTIONS_TAG,
    NO_FACTIONS_TITLE,
    NO_FACTIONS_FACTION,

    NO_CHANNEL,
    CHANNEL_JOINED,
    CHANNELS_OFF,
    CHANNEL_WRONG_PASSWORD,
    CHANNEL_EXTRA_PASSWORD,
    CHANNEL_MISSING_PASSWORD,

    NO_WORLD,

    COMMAND_ONLY_PLAYERS,

    RCHAT_RELOADED,
    NO_PERMISSIONS_SYSTEM,
    NO_PERMISSIONS,
    NO_PERMISSIONS_CONSOLE;

    /**
     * Gets the message.
     *
     * @return Message
     */
    @Override
    public String toString() {
        return LanguageHelper.getString(name());
    }

    protected static class LanguageHelper {

        private static Properties p = new Properties();

        /**
         * Gets a property that is never null.
         *
         * @param node Node to get
         * @return String or "Language property "node" not defined."
         */
        private static String getString(String node) {
            String prop = p.getProperty(node);
            if (prop == null) prop = "Language property \"" + node + "\" not defined.";
            return prop;
        }

        protected LanguageHelper(File f) throws IOException {
            final Reader in = new InputStreamReader(new FileInputStream(f), "UTF-8");
            LanguageHelper.p.load(in);
        }

        protected LanguageHelper(String s) throws IOException {
            final Reader in = new InputStreamReader(new FileInputStream(new File(s)), "UTF-8");
            LanguageHelper.p.load(in);
        }
    }

}
