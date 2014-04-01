package portalBlock;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;


public class ClickPortalBlockListener implements Listener {
	
	private HashMap<String, String> messageData = PortalBlock.getMessageData();
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void blockClick(PlayerInteractEvent e){
		Block block = e.getClickedBlock();
		Player player = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()){
			if(block.getType() == Material.ENDER_PORTAL_FRAME){
				if(!PortalHandlers.hasEye(block) && !PortalBlock.inUse(block)){
					if(player.getItemInHand().getType() == Material.EYE_OF_ENDER){
						if(player.hasPermission("portalblock.use")){
							if(!PortalHandlers.pointExists(block)){
								PortalFile pf = new PortalFile();
								pf.addPoint(new PortalPoint(block.getX(), block.getY(), block.getZ(), 
										block.getWorld(), PortalHandlers.getRandomID()));
							}
							
							e.setCancelled(true);
							PortalHandlers.toggleEye(block, true);
							player.getWorld().playSound(player.getLocation(),Sound.CLICK, 2, 1);
							PlayerInventory inven = player.getInventory();
							ItemStack it = new ItemStack(Material.EYE_OF_ENDER, inven.getItemInHand().getAmount()-1);
						
							if(!player.getGameMode().equals(GameMode.CREATIVE))
								inven.setItemInHand(it);
				
							PortalBlock.setInUse(block, player.getDisplayName());
							@SuppressWarnings("unused")
							BukkitTask getResponce = new GetResponseTask(messageData.get("portalblock.getid")
									, messageData.get("portalblock.cancel"), player, PortalBlock.getTime(), PortalBlock.getDistance(), 
									block, "ID").runTaskTimer(PortalBlock.getInstance(), 1, 5);
							}
					}
					
				}else if(!PortalBlock.inUse(block)){
					e.setCancelled(true);
					player.getWorld().playSound(player.getLocation(),Sound.ITEM_PICKUP,5, 1);
					PortalHandlers.dropItem(new ItemStack(Material.EYE_OF_ENDER), block);
					PortalHandlers.toggleEye(block, false);
				}else{
					e.setCancelled(true);
					PortalBlock.setNotUse(player.getDisplayName());
				}
			}
		}else if(e.getAction() == Action.LEFT_CLICK_BLOCK && player.isSneaking() && player.hasPermission("portalblock.use")){
			if(block.getType() == Material.ENDER_PORTAL_FRAME){
				e.setCancelled(true);
				if(!PortalBlock.inUse(block)){
					PortalHandlers.breakBlock(player, block, true);
					PortalBlock.setNotUse(player.getDisplayName());
				}else{
					player.sendMessage(messageData.get("portalblock.inuse"));
				}
			}
		}else if(e.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSneaking() && player.hasPermission("portalblock.use")){
			if(block.getType() == Material.ENDER_PORTAL_FRAME){
				e.setCancelled(true);
				PortalFile pt = new PortalFile();
				if(!PortalHandlers.pointExists(block)){
					pt.addPoint(new PortalPoint(block.getX(), block.getY(), block.getZ(), 
							block.getWorld(), PortalHandlers.getRandomID()));
				}
				player.getWorld().playSound(player.getLocation(),Sound.CLICK, 2, 1);
				player.sendMessage(messageData.get("portalblock.id").replace("%name%", pt.getPoint(block.getLocation()).getName()));
			}
		}else if(e.getAction() == Action.LEFT_CLICK_BLOCK && !player.isSneaking() && player.hasPermission("portalblock.use")
				&& player.getGameMode().equals(GameMode.CREATIVE)){
			if(block.getType() == Material.ENDER_PORTAL_FRAME){
				e.setCancelled(true);
				if(!PortalBlock.inUse(block)){
					PortalHandlers.breakBlock(player, block, true);
					PortalBlock.setNotUse(player.getDisplayName());
				}else{
					player.sendMessage(messageData.get("portalblock.inuse"));
				}
			}
			
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryChange(InventoryClickEvent e){
		Inventory inven = e.getInventory();
		if(inven instanceof AnvilInventory){
			if(e.getRawSlot() == 2){
				ItemStack item = e.getCurrentItem();
				
				if(item != null){
					if(item.getType().equals(Material.ENDER_PORTAL_FRAME)){
						ItemMeta meta = item.getItemMeta();
						
						if(meta != null){
							if(meta.hasDisplayName()){
								String displayName = meta.getDisplayName();
								PortalFile pf = new PortalFile();
								if(pf.contains(displayName)){
									e.setCancelled(true);
									 ((Player) e.getWhoClicked()).sendMessage(messageData.get("portalblock.alreadyexist")
											.replace("%name%", displayName));
								}
							}
						}
					}
				}
			}
		}
	}
}



