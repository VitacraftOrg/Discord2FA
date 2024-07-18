package net.vitacraft.discord2fa.storage.file;

import net.vitacraft.discord2fa.storage.models.Account;
import net.vitacraft.discord2fa.util.ConfigUtil;

import java.nio.file.Path;
import java.util.Set;

public class FileWrapper {
    private final ConfigUtil config;

    public FileWrapper(Path dir){
        config = new ConfigUtil(dir, "Accounts.yml");
    }

    public Account findAccountByUUID(String uuid){
        String discordID = config.getConfig().getString(uuid);
        if (discordID == null) return null;
        else{
            return new Account(discordID,uuid);
        }
    }

    public Account findAccountByDiscordID(String discordID){
        Set<String> uuids = config.getConfig().getKeys(false);
        for (String uuid : uuids) {
            String storedDiscordID = config.getConfig().getString(uuid);
            if (storedDiscordID != null && storedDiscordID.equals(discordID)) {
                return new Account(discordID, uuid);
            }
        }
        return null;
    }

    public void createAccount(String uuid, String discordId){
        config.getConfig().set(uuid, discordId);
        config.save();
    }

    public void deleteAccount(String uuid){
        config.getConfig().set(uuid, null);
        config.save();
    }
}
