package me.hiztree.pancakevote;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PancakeVoteCommands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("pancakevoteadd")) {
            if (sender.hasPermission("pancakevote.modify")) {
                if (args.length == 2) {
                    Player selected = Bukkit.getPlayer(args[0]);

                    if (selected != null) {
                        int amount = 0;

                        try {
                            amount = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            PancakeVote.sendMessage(sender, "&cPlease specify a valid number of votes!");
                            return true;
                        }

                        PancakeVote.onVote(selected, amount);
                        PancakeVote.sendMessage(sender, "&7You have successfully added &6" + amount + " &7votes to &6" + args[0] + "&7!");
                    } else {
                        PancakeVote.sendMessage(sender, "&cThat player is not online!");
                    }
                } else {
                    PancakeVote.sendMessage(sender, "&cUsage: &7/pancakevoteadd <name> <amount>&c.");
                }
            } else {
                PancakeVote.sendMessage(sender, "&cYou do not have enough permission to perform this command!");
            }
        } else if (label.equalsIgnoreCase("votes")) {
            if (sender instanceof Player) {
                String key = "Data." + ((Player) sender).getUniqueId().toString();
                int votes = 0;

                if (PancakeVote.getVoteConfig().contains(key)) {
                    votes = PancakeVote.getVoteConfig().getInt(key);
                }

                PancakeVote.sendMessage(sender, PancakeVote.getVoteCheckMessage().replace("{ARG0}", String.valueOf(votes)));
            } else {
                sender.sendMessage("[me.hiztree.pancakevote.PancakeVote] You must be a player to perform this command!");
            }
        }

        return true;
    }
}
