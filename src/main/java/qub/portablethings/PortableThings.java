package qub.portablethings;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;


public final class PortableThings extends JavaPlugin implements Listener {
    boolean fromPunch = true;
    boolean fromInventory = true;
    boolean allowWorkbench = true;
    boolean allowEnderChest = true;
    boolean allowAnvil = true;
    boolean damageAnvil = true;
    private FileConfiguration config;
    private HashMap<UUID,ItemStack> shulkerMap = new HashMap();

    private void fillConfig() {
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        this.config = this.getConfig();
        this.config.addDefault("fromPunch", true);
        this.config.addDefault("fromInventory", true);
        this.config.addDefault("allowWorkbench", true);
        this.config.addDefault("allowEnderChest", true);
        this.config.addDefault("allowAnvil", true);
    }

    private void matchConfig() {
        fromPunch = config.getBoolean("fromPunch");
        fromInventory = config.getBoolean("fromInventory");
        allowWorkbench = config.getBoolean("allowWorkbench");
        allowEnderChest = config.getBoolean("allowEnderChest");
        allowAnvil = config.getBoolean("allowAnvil");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equals("togglefrompunch")) {
            fromPunch = !fromPunch;
            config.set("fromPunch", fromPunch);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("fromPunch Toggled: " + fromPunch);
            }
        } else if (command.getName().equals("togglefrominventory")) {
            fromInventory = !fromInventory;
            config.set("fromPunch", fromInventory);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("fromInventory Toggled: " + fromInventory);
            }
        } else if (command.getName().equals("toggleallowworkbench")) {
            config.set("togglwallowworkbench", allowWorkbench);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("allowWorkbench Toggled: " + allowWorkbench);
            }
        } else if (command.getName().equals("toggleallowenderchest")) {
            allowEnderChest = !allowEnderChest;
            config.set("fromPunch", allowEnderChest);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("allowEnderChest Toggled: " + allowEnderChest);
            }
        } else if (command.getName().equals("toggleallowanvil")) {
            allowAnvil = !allowAnvil;
            config.set("fromPunch", allowAnvil);
            saveConfig();
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage("allowAnvil Toggled: " + allowAnvil);
            }
        } 
        return true;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        fillConfig();
        matchConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean activateItem(@NotNull ItemStack itemStack, Player player) {
        Material item = itemStack.getType();
        if (allowWorkbench && item == Material.CRAFTING_TABLE) {
            player.openWorkbench(null, true);
            return true;
        } else if (allowEnderChest && item == Material.ENDER_CHEST) {
            player.openInventory(player.getEnderChest());
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN,0.5f, Util.getRandomNumber(0.5f,0.75f));
            return true;
        } else if (allowAnvil && item == Material.ANVIL || item == Material.CHIPPED_ANVIL || item == Material.DAMAGED_ANVIL) {
            player.openAnvil(null, true);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND,0.5f, Util.getRandomNumber(0.5f,0.75f));
            return true;
        }
        return false;
    }
    @EventHandler
    public void onInventoryClicked(InventoryClickEvent ev) {
        if (fromInventory) {
            if (ev.getCurrentItem() != null) {
                ItemStack itemStack = ev.getCurrentItem();
                Player player = (Player) ev.getWhoClicked();
                if (ev.getClick().isRightClick()&&ev.isShiftClick()) {
                    if(activateItem(itemStack, player))
                    {
                        ev.setCancelled(true);
                        ev.setResult(Event.Result.DENY);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent ev) {
        if (fromPunch) {
            Action action = ev.getAction();
            if (ev.getItem() != null) {
                ItemStack itemStack = ev.getItem();
                Player player = ev.getPlayer();
                if (action.isLeftClick()) {
                    activateItem(itemStack, player);
                }
            }
        }
    }
}