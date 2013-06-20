package ca.mickealn.expbottles.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import ca.mickealn.expbottles.util.ExpBottlesConfigurationContext;

public final class ExpBottlesListener implements Listener {

    private final int expPerBottle;

    public ExpBottlesListener(ExpBottlesConfigurationContext configurationContext) {
        expPerBottle = configurationContext.expPerBottle;
    }

    @EventHandler
    public void onOpenEnhantTable(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player && InventoryType.ENCHANTING.equals(event.getInventory().getType()) && Material.GLASS_BOTTLE.equals(event.getPlayer().getItemInHand().getType())) {
            Player player = (Player) event.getPlayer();
            int originalExp = player.getTotalExperience();
            int bottleCount = player.getItemInHand().getAmount();
            int numEnchantedBottles = Math.min(originalExp / expPerBottle, bottleCount);

            if (numEnchantedBottles > 0) {
                player.setLevel(0); // Set everything to 0 to prevent doubling of EXP
                player.setTotalExperience(0);

                player.setItemInHand(new ItemStack(Material.EXP_BOTTLE, numEnchantedBottles));
                player.giveExp(Math.max(0, originalExp - numEnchantedBottles * expPerBottle));
                if (bottleCount > numEnchantedBottles) {
                    player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GLASS_BOTTLE, bottleCount - numEnchantedBottles));
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExpEvent(ExpBottleEvent event) {
        event.setExperience(expPerBottle);
    }
}
