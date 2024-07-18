package net.vitacraft.discord2fa_bukkit;

import net.vitacraft.discord2fa.*;
import net.vitacraft.discord2fa.bstats.BukkitMetrics;
import net.vitacraft.discord2fa.discord.DiscordUtils;
import net.vitacraft.discord2fa.messages.Messages;
import net.vitacraft.discord2fa.storage.CommonStorageManager;
import net.vitacraft.discord2fa.util.UpdateChecker;
import net.vitacraft.discord2fa_bukkit.commands.Discord2FACommand;
import net.vitacraft.discord2fa_bukkit.commands.LinkCommand;
import net.vitacraft.discord2fa_bukkit.commands.UnlinkCommand;
import net.vitacraft.discord2fa_bukkit.managers.LinkManager;
import net.vitacraft.discord2fa_bukkit.managers.VerifyManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.plugin.Plugin;
import java.util.Objects;

public final class Discord2FA extends JavaPlugin {
    private static Plugin plugin;
    private static Common common;

    @Override
    public void onEnable() {
        enablePlugin();
    }

    private void enablePlugin() {
        plugin = this;

        LinkManager linkManager = new LinkManager();
        VerifyManager verifyManager = new VerifyManager();

        try {
            common = new Common(linkManager, verifyManager, getDataFolder().getAbsoluteFile().toPath());
        } catch (CommonException e) {
            disable(e.getMessage());
            return;
        }

        verifyManager.loadConfig();

        getServer().getPluginManager().registerEvents(verifyManager, this);
        Objects.requireNonNull(getCommand("link")).setExecutor(new LinkCommand());
        Objects.requireNonNull(getCommand("unlink")).setExecutor(new UnlinkCommand());
        Objects.requireNonNull(getCommand("discord2fa")).setExecutor(new Discord2FACommand(this));
        String versionMessage = UpdateChecker.generateUpdateMessage(getDescription().getVersion());
        if (versionMessage != null) {
            plugin.getLogger().severe(versionMessage);
        }
        try {
            enableBStats();
        } catch (Exception ignore){}
    }

    private void enableBStats(){
        int pluginID = 21448;
        new BukkitMetrics(this, pluginID);
    }

    public void reload(){
        plugin.reloadConfig();
        common.reload();
    }

    public static void disable(String reason){
        try {
            common.disable(reason);
        } catch (Exception ignore) {}
        plugin.getLogger().severe(reason);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }

    @Override
    public void onDisable() {
        try{
            common.shutdown();
        } catch (Exception ignore){

        }
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static LinkManager getLinkManager() {
        return (LinkManager) common.getLinkManager();
    }

    public static VerifyManager getVerifyManager() {
        return (VerifyManager) common.getVerifyManager();
    }

    public static DiscordUtils getDiscordUtils() {
        return common.getDiscordUtils();
    }

    public static CommonStorageManager getStorageManager() {
        return common.getStorageManager();
    }

    public static Messages getMessages(){
        return common.getMessages();
    }

    public static Common getCommon() {
        return common;
    }
}
