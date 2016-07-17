package me.hiztree.pancakevote;

import com.google.common.collect.Maps;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

public class PancakeVote extends JavaPlugin {

    private static Permission permission;

    private static Logger logger = Logger.getLogger("Pan");

    private static File configFile;
    private static FileConfiguration config;

    private static String prefix;
    private static String rankupMessage;
    private static String voteCheckMessage;
    private static TreeMap<Integer, String> groups;

    public void onEnable() {
        if (!setupPermissions()) {
            logger.severe("There was an error accessing permissions!");
            return;
        }

        File directory = new File("plugins/me.hiztree.pancakevote.PancakeVote");
        configFile = new File(directory, "config.yml");

        if (!directory.exists())
            directory.mkdirs();

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                saveResource("config.yml", true);
            } catch (IOException e) {
                logger.severe("Could not create config.yml!");
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        prefix = config.getString("Prefix", "&8[&bPancakeVote&8] ");
        rankupMessage = config.getString("RankupMessage", "&6You have ranked up to &7{ARG0}&6.");
        voteCheckMessage = config.getString("VoteCheckMessage", "&6You have voted &7{ARG0}&6 times.");

        Map<Integer, String> tempMap = Maps.newHashMap();

        for (String groupName : config.getConfigurationSection("Groups").getKeys(false)) {
            int requiredVotes = config.getInt("Groups." + groupName);

            tempMap.put(requiredVotes, groupName);
        }

        groups = new TreeMap<Integer, String>(tempMap);

        PancakeVoteCommands ex = new PancakeVoteCommands();
        getCommand("pancakevoteadd").setExecutor(ex);
        getCommand("votes").setExecutor(ex);

        Bukkit.getPluginManager().registerEvents(new PancakeVoteListener(), this);
    }

    public void onDisable() {
        groups.clear();
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static void onVote(Player player, int add) {
        String groupName = permission.getPrimaryGroup(player);
        int votes = 0;
        int groupVotes = getKeyFromValue(groupName);
        String key = "Data." + player.getUniqueId().toString();

        if (config.contains(key)) {
            votes = config.getInt(key);
        }

        votes = votes + add;

        config.set(key, votes);

        try {
            config.save(configFile);
        } catch (IOException e) {
            logger.severe("Could not save the config.yml!");
            return;
        }

        if (groups.containsValue(groupName)) {
            String updateGroup = null;

            for (Map.Entry<Integer, String> entry : groups.entrySet()) {
                if (votes >= entry.getKey()) {
                    updateGroup = entry.getValue();
                }
            }

            logger.info("Group:" + updateGroup);

            if (updateGroup != null) {
                if ((groupVotes < getKeyFromValue(updateGroup)) && !(updateGroup.equalsIgnoreCase(groupName))) {
                    for (String group : permission.getPlayerGroups(player)) {
                        permission.playerRemoveGroup(player, group);
                    }

                    permission.playerAddGroup(player, updateGroup);

                    sendMessage(player, rankupMessage.replace("{ARG0}", updateGroup));
                }
            }
        }
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public static int getKeyFromValue(String value) {
        for (int integer : groups.keySet()) {
            if (groups.get(integer).equalsIgnoreCase(value)) {
                return integer;
            }
        }

        return 0;
    }

    public static FileConfiguration getVoteConfig() {
        return config;
    }

    public static String getVoteCheckMessage() {
        return voteCheckMessage;
    }
}
