package com.enderdevelopment.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.event.ClickEvent;

public final class Main extends JavaPlugin implements Listener {

    private NamespacedKey campKey;

    @Override
    public void onEnable() {
        campKey = new NamespacedKey(this, "camp_item");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("colonie")) {
            if (args.length == 2 && args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("camp")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("colonie.give")) {
                        giveCampItem(player);
                        player.sendMessage(Component.text("You have been given a camp item.").color(NamedTextColor.GREEN));
                    } else {
                        player.sendMessage(Component.text("You do not have permission to use this command.").color(NamedTextColor.RED));
                    }
                } else {
                    sender.sendMessage(Component.text("This command can only be used by players.").color(NamedTextColor.RED));
                }
                return true;
            }
        }
        return false;
    }

    private void giveCampItem(Player player) {
        ItemStack campItem = new ItemStack(Material.CAMPFIRE);
        ItemMeta meta = campItem.getItemMeta();
        meta.setDisplayName("Camp Item");
        meta.getPersistentDataContainer().set(campKey, PersistentDataType.STRING, "camp");
        campItem.setItemMeta(meta);
        player.getInventory().addItem(campItem);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        giveCampItem(player);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        if (itemInHand.getType() == Material.CAMPFIRE) {
            ItemMeta meta = itemInHand.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(campKey, PersistentDataType.STRING)) {
                String value = meta.getPersistentDataContainer().get(campKey, PersistentDataType.STRING);
                if (value != null && value.equals("camp")) {
                    event.setCancelled(true);
                    createCampStructure(event.getBlockPlaced());
                }
            }
        }
    }

    private void createCampStructure(Block campfireBlock) {
        // Create a simple camp structure with a chest and some food
        Block chestBlock = campfireBlock.getWorld().getBlockAt(campfireBlock.getLocation().add(1, 0, 0));
        chestBlock.setType(Material.CHEST);
        Chest chest = (Chest) chestBlock.getState();
        Inventory chestInventory = chest.getInventory();
        chestInventory.addItem(new ItemStack(Material.APPLE, 5));
        chestInventory.addItem(new ItemStack(Material.BREAD, 3));
        chestInventory.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        chest.update();
    }
}