package net.vitacraft.discord2fa;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.vitacraft.discord2fa.base.BaseLinkManager;
import net.vitacraft.discord2fa.messages.Messages;
import net.vitacraft.discord2fa.base.BaseVerifyManager;
import net.vitacraft.discord2fa.discord.DiscordUtils;
import net.vitacraft.discord2fa.storage.CommonStorageManager;
import net.vitacraft.discord2fa.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class Common extends ListenerAdapter {
    private static Common common;
    private final Messages messages;
    private final ShardManager shardManager;
    private final BaseLinkManager lm;
    private final BaseVerifyManager vm;
    private final CommonStorageManager sm;
    private final ConfigUtil config;
    private final Path dir;
    private DiscordUtils discordUtils;

    public Messages getMessages() {
        return messages;
    }

    public void disable(String s) {
        log(s);
        shutdown();
    }

    public String getConfigString(String s) {
        return config.getConfig().getString(s);
    }

    public ConfigUtil getConfig() {
        return config;
    }

    public BaseLinkManager getLinkManager() {
        return lm;
    }

    public CommonStorageManager getStorageManager() {
        return sm;
    }

    public BaseVerifyManager getVerifyManager() {
        return vm;
    }

    public void log(String s) {
        System.out.println(s);
    }

    public void reload() {
        messages.reload(this);
        sm.reload(this, dir);
    }

    public Common(BaseLinkManager lm, BaseVerifyManager vm, Path dir) throws CommonException {
        common = this;
        this.dir = dir;

        config = new ConfigUtil(dir, "config.yml");
        messages = new Messages(getConfigString("language"), dir);
        this.lm = lm;
        this.vm = vm;

        try {
            this.sm = new CommonStorageManager(this, dir);
        } catch (Exception e) {
            throw new CommonException("Disabling due to being unable to initialize Storage! - " + e.getMessage());
        }

        try{
            String token = getConfigString("discord.token");
            DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT).enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.setStatus(OnlineStatus.ONLINE);
            shardManager = builder.build();
            shardManager.addEventListener(this);
        } catch (Exception e) {
            throw new CommonException("Disabling due to being unable to load Discord Bot! - " + e.getMessage());
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        discordUtils = new DiscordUtils(this);
        shardManager.addEventListener(discordUtils);
        log("Discord Bot is ready!");
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public void shutdown() {
        if (sm != null) {
            sm.disable();
            log("Disabled Database wrapper...");
        }
        try{
            shardManager.shutdown();
            log("Disabled Discord Bot...");
        } catch (Exception ignore) {
        }
    }

    public static Common getInstance() {
        return common;
    }

    public DiscordUtils getDiscordUtils() {
        return discordUtils;
    }
}
