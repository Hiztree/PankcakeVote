package me.hiztree.pancakevote;

import com.vexsoftware.votifier.model.VotifierEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PancakeVoteListener implements Listener {

    @EventHandler
    public void onVote(VotifierEvent event) {
        Player player = Bukkit.getPlayer(event.getVote().getUsername());

        if (player != null) {
            PancakeVote.onVote(player, 1);
        }
    }
}
