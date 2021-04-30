package me.burzakrual.rtp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class RandomTPCommand extends BukkitCommand {
    public static int RTP_COOLDOWN = 10000;

    private static HashMap<UUID, Long> cooldownMap = new HashMap<>();

    public RandomTPCommand() {
        super("randomteleport", "Used to use the plugin.", "/randomteleport", Arrays.asList(new String[] { "rtp", "rteleport", "randomtp", "tpr", "teleportrandom", "tprandom" }));
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] arguments) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You need to be a player to be able to use this command.");
            return false;
        }
        Player player = (Player)sender;
        TeleportMapping found = null;
        for (TeleportMapping mapping : Plugin.teleportMappings) {
            if (mapping.fromWorld.equalsIgnoreCase(player.getWorld().getName())) {
                found = mapping;
                break;
            }
        }
        if (found == null) {
            player.sendMessage("You cannot random teleport in this world!");
            return false;
        }
        UUID uuid = player.getUniqueId();
        if (player.isOp() || !cooldownMap.containsKey(uuid) || System.currentTimeMillis() - ((Long)cooldownMap.get(uuid)).longValue() > RTP_COOLDOWN) {
            player.sendMessage(ChatColor.GRAY + "Searching for a safe location to teleport you to...");
            int search = 0;
            while (search != -1) {
                if (search++ > 15) {
                    player.sendMessage(ChatColor.RED + "Could not find a safe location! Please try again!");
                    search = -1;
                    cooldownMap.put(uuid, Long.valueOf(System.currentTimeMillis()));
                    continue;
                }
                int x = found.centerX + ThreadLocalRandom.current().nextInt(0, found.maxX) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
                int z = found.centerZ + ThreadLocalRandom.current().nextInt(0, found.maxZ) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
                World world = Bukkit.getWorld(found.destinationWorld);
                Block block = world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
                block.getChunk().load(true);
                player.sendMessage(ChatColor.GREEN + "Success!");
                Location teleportLocation = block.getLocation().add(0.5D, 1.0D, 0.5D);
                teleportLocation.setWorld(world);
                player.teleport(teleportLocation);
                cooldownMap.put(uuid, Long.valueOf(System.currentTimeMillis()));
                System.out.println(String.valueOf(player.getName()) + " has randomteleported to: " + teleportLocation.toString());
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "You have to wait " + ((RTP_COOLDOWN - System.currentTimeMillis() - ((Long)cooldownMap.get(uuid)).longValue()) / 1000L) + " seconds until you can use this command again!");
        }
        return false;
    }

    public String getPermission() {
        return "randomtp.command";
    }
}