package me.burzakrual.rtp;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    public void insertCustomCommand(BukkitCommand command) {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap)bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(command.getName(), (Command)command);
        } catch (NoSuchFieldException|SecurityException|IllegalArgumentException|IllegalAccessException e) {
            e.printStackTrace();
            getLogger().log(Level.SEVERE, "Failed to create randomtp command!");
        }
    }

    public static HashSet<TeleportMapping> teleportMappings = new HashSet<>();

    public void onEnable() {
        saveDefaultConfig();
        for (String map : getConfig().getStringList("validWorlds")) {
            String[] entry = map.split(":");
            if (entry.length != 6) {
                Bukkit.getServer().getPluginManager().disablePlugin((org.bukkit.plugin.Plugin)this);
                (new Exception("INVALID WORLD MAPPING: " + map)).printStackTrace();
                getLogger().log(Level.SEVERE, "Disabling randomtp plugin!");
            }
            teleportMappings.add(new TeleportMapping(entry[4], entry[5], Integer.parseInt(entry[0]), Integer.parseInt(entry[1]), Integer.parseInt(entry[2]), Integer.parseInt(entry[3])));
        }
        getLogger().log(Level.INFO, "RandomTP plugin enabled succesfully. Detected mappings are written below");
        for (TeleportMapping mapping : teleportMappings)
            getLogger().log(Level.INFO,
                    "From world '" + mapping.fromWorld + "' to '" + mapping.destinationWorld + "' with center x & z at '" + mapping.centerX + " & " + mapping.centerZ + "' with max x & z '" + mapping.maxX + " & " + mapping.maxZ + "'");
        insertCustomCommand(new RandomTPCommand());
        RandomTPCommand.RTP_COOLDOWN = getConfig().getInt("commandCooldownMilliseconds");
        getLogger().log(Level.INFO, "Set RTP Cooldown to " + RandomTPCommand.RTP_COOLDOWN + " milliseconds.");
    }
}
