package com.potatosaucevfx.whitelistsync2.commands;

import com.potatosaucevfx.whitelistsync2.Utilities;
import com.potatosaucevfx.whitelistsync2.service.BaseService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandOp implements CommandExecutor {


    private JavaPlugin plugin;
    private BaseService service;
    private Server server;

    private final String USAGE_STRING = "/wlop <list|op|deop|sync|copyServerToDatabase>";

    public CommandOp(JavaPlugin plugin, BaseService service) {
        this.plugin = plugin;
        this.service = service;
        this.server = plugin.getServer();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (plugin.getConfig().getBoolean("general.sync-ops")) {
            if (args.length > 0) {
                //Action for showing list
                if (args[0].equalsIgnoreCase("list")) {

                    sender.sendMessage(Utilities.FormatOpUsersOutput(service.pullOppedNamesFromDatabase(server)));

                } // Actions for adding a player to whitelist
                else if (args[0].equalsIgnoreCase("op")) {

                    if (args.length > 1) {

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

                        if (player != null) {

                            if (service.addPlayerToDatabaseOp(player)) {
                                player.setOp(true);
                                sender.sendMessage(player.getName() + " opped!");
                            } else {
                                sender.sendMessage("Error opping " + player.getName() + "!");
                            }

                        } else {
                            sender.sendMessage("User " + args[1] + " not found!");
                        }

                    } else {
                        sender.sendMessage("You must specify a name to op!");
                    }

                } // Actions for removing player from whitelist
                else if (args[0].equalsIgnoreCase("deop")) {

                    if (args.length > 1) {

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

                        if (player != null) {

                            if (service.removePlayerFromDatabaseOp(player)) {
                                player.setOp(false);
                                sender.sendMessage(player.getName() + " de-opped!");
                            } else {
                                sender.sendMessage("Error de-opping " + player.getName() + "!");
                            }

                        } else {
                            sender.sendMessage("User " + args[1] + " not found!");
                        }

                    } else {
                        sender.sendMessage("You must specify a valid name to deop!");
                    }

                } else if (args[0].equalsIgnoreCase("sync")) {

                    if (service.updateLocalOpListFromDatabase(server)) {
                        sender.sendMessage("Local up to date with database!");
                    } else {
                        sender.sendMessage("Error syncing local to database!");
                    }

                } // Sync server to database
                else if (args[0].equalsIgnoreCase("copyservertodatabase")) {

                    if (service.pushLocalOpListToDatabase(server)) {
                        sender.sendMessage("Pushed local to database!");
                    } else {
                        sender.sendMessage("Error pushing local to database!");
                    }

                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage("Whitelist Sync Op management is not enabled. You must enable it in the config.");
        }

        return true;
    }
}
