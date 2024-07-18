package net.vitacraft.discord2fa.storage.models;

import net.dv8tion.jda.api.entities.User;
import net.vitacraft.discord2fa.Common;

import java.util.concurrent.CompletableFuture;

public class Account {
    private final String discordID;
    private final String minecraftUUID;

    public Account(String discordID, String minecraftUUID) {
        this.discordID = discordID;
        this.minecraftUUID = minecraftUUID;
    }

    public String getDiscordID() {
        return discordID;
    }

    public String getMinecraftUUID() {
        return minecraftUUID;
    }

    public CompletableFuture<User> getUser() {
        CompletableFuture<User> future = new CompletableFuture<>();
        Common.getInstance().getShardManager().retrieveUserById(discordID).queue(
                future::complete,
                future::completeExceptionally
        );
        return future;
    }
}
