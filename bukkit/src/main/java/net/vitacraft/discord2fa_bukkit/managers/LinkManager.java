package net.vitacraft.discord2fa_bukkit.managers;

import net.vitacraft.discord2fa.base.BaseLinkManager;
import net.vitacraft.discord2fa.storage.models.Account;
import net.vitacraft.discord2fa_bukkit.Discord2FA;
import org.bukkit.entity.Player;

import java.util.HashMap;
import net.dv8tion.jda.api.entities.Member;

public class LinkManager implements BaseLinkManager {
    private final HashMap<String, Member> linking = new HashMap<>();
    public void queLink(Member member, String code) {
        linking.put(code, member);
    }
    public void tryLink(Player player, String code) {
        if (linking.containsKey(code)) {
            Member member = linking.get(code);
            linking.remove(code);
            try {
                Discord2FA.getStorageManager().linkAccount(player.getUniqueId().toString(), member.getId());
                Discord2FA.getDiscordUtils().giveRole(member.getId());
                player.sendMessage(Discord2FA.getMessages().get("linkSuccess").replace("%member%", member.getEffectiveName()));
                Discord2FA.getVerifyManager().linked(player);
            } catch (Exception e) {
                player.sendMessage("§cAn error occurred while linking your account! Contact an administrator!: " + e.getMessage());
            }
        } else {
            player.sendMessage(Discord2FA.getMessages().get("invalidCode"));
        }
    }

    @Override
    public HashMap<String, Member> getLinking() {
        return linking;
    }

    public void unlink(Player player) {
        Account account = Discord2FA.getStorageManager().findAccountByUUID(player.getUniqueId().toString());
        Discord2FA.getStorageManager().unlinkAccount(player.getUniqueId().toString());
        Discord2FA.getDiscordUtils().giveRole(account.getDiscordID(),false);
    }
}
