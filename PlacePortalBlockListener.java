package portalBlock;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class PlacePortalBlockListener implements Listener {
	
	private HashMap<String, String> messageData = PortalBlock.getMessageData();
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void blockPlace(BlockPlaceEvent e){
		Block block = e.getBlockPlaced();
		
		if(block.getType() == Material.ENDER_PORTAL_FRAME){
			if(!e.getPlayer().hasPermission("portalblock.use")){
				e.getPlayer().sendMessage(messageData.get("portalblock.nopermission"));
				e.setCancelled(true);
			}else{
				if(!e.isCancelled()){
					PortalFile pt = new PortalFile();
					String name = "";
					ItemMeta im = e.getItemInHand().getItemMeta();
					if(im.getDisplayName() != null && !im.getDisplayName().equalsIgnoreCase("End Portal")){
						 name = im.getDisplayName();
						 if(pt.contains(name)){
							 e.getPlayer().sendMessage(messageData.get("portalblock.alreadyexist").replace("%name%", name));
							 do{
									name =  generateRandomName();
								}while(pt.contains(name));
						 }
					}else{
						do{
							name =  generateRandomName();
						}while(pt.contains(name));
					}
					
					e.getPlayer().sendMessage(messageData.get("portalblock.create").replace("%name%", name));	
					if(PortalBlock.sendConsoleMessage())
						PortalBlock.getConsole().sendMessage(messageData.get("portalblock.console.create")
								.replace("%player%", e.getPlayer().getDisplayName()).replace("%name%", name));
					
					pt.addPoint(new PortalPoint(block.getX(), block.getY(), block.getZ(), block.getWorld(), name));
				}
			}
		}
	}
	
	private String generateRandomName(){
		StringBuilder name = new StringBuilder();
		
		Random random = new Random();
		for(int i = 0; i<=3; i++){
			int I = random.nextInt(9);
			name.append(I);
		}
		
		for(int i = 0; i<=1; i++){
			name.append(String.valueOf((char)(random.nextInt(26) + 'a')).toUpperCase());
		}
		return name.toString();
	}
}

