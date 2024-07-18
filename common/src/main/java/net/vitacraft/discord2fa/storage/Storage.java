package net.vitacraft.discord2fa.storage;

import net.vitacraft.discord2fa.storage.models.Account;

public interface Storage {
    boolean isLinked(String uuid);
    void linkAccount(String uuid, String discordID);
    boolean isLinkedByDiscord(String discordID);
    void unlinkAccount(String uuid);
    Account findAccountByUUID(String uuid);
    Account findAccountByDiscordID(String discordID);
}