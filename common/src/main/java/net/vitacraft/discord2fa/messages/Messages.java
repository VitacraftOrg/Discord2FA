package net.vitacraft.discord2fa.messages;

import net.vitacraft.discord2fa.Common;
import net.vitacraft.discord2fa.util.ConfigUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

public class Messages {
    private final HashMap<String, String> messages = new HashMap<>();
    private ConfigUtil configUtil;
    private ConfigUtil backup;
    private static Path dir;

    public Messages(String lang, Path dir){
        dir = new File(dir.toString() + "/lang/").toPath();
        Messages.dir = dir;
        try{
            configUtil = new ConfigUtil(dir,lang + ".yml");
            if (configUtil.getConfig() == null) throw new NullPointerException("Config Util is null");
        } catch (Exception ignore){
            backup = new ConfigUtil(dir, "lang/en.yml");
        }
    }

    public void setLanguage(String lang){
        try{
            configUtil = new ConfigUtil(dir, lang);
        } catch (Exception ignore){
            backup = new ConfigUtil(dir, "/en.yml");
        }
    }

    public String get(String key) {
        return messages.computeIfAbsent(key, this::retrieveMessageFromConfig);
    }


    private String retrieveMessageFromConfig(String key) {
        String retrievedMessage = "";
        if (configUtil != null) {
            retrievedMessage = configUtil.getConfig().getString(key);
        }
        if (retrievedMessage == null) {
            retrievedMessage = backup.getConfig().getString(key);
        }
        if (retrievedMessage == null) {
            retrievedMessage = "§c§lThis is not a bug do not report it! §c[Discord2FA >> Messages.yml] The following message is either missing or not set: §e" + key;
        }
        return retrievedMessage.replace("&","§");
    }

    public void reload(Common common) {
        setLanguage(common.getConfigString("language"));
        messages.clear();
    }
}
