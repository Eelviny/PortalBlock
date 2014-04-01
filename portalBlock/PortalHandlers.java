package portalBlock;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class PortalHandlers {
	
	public static HashMap<String, String> messageData = PortalBlock.getMessageData();
	
	public static synchronized void breakBlock(Player player, Block block, Boolean send){
		
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		if(send){
			PortalBlock.getInstance().getServer().getPluginManager().callEvent(event);
		}
		
		if(!event.isCancelled()){
			
			PortalFile pt = new PortalFile();
			
			ItemStack is = new ItemStack(Material.ENDER_PORTAL_FRAME);
			PortalPoint portalPoint = pt.getPoint(block.getLocation());
			if(portalPoint != null){
				String blockName = portalPoint.getName();
				is = copyName(is, blockName);
	
				player.getWorld().dropItemNaturally(block.getLocation(), is);
				pt.removePoint(block.getLocation());
				
				if(hasEye(block)){
					player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.EYE_OF_ENDER));
				}
				player.sendMessage(messageData.get("portalblock.break").replace("%name%", blockName));
				if(PortalBlock.sendConsoleMessage())
					PortalBlock.getConsole().sendMessage(messageData.get("portalblock.console.break")
							.replace("%player%", player.getDisplayName()).replace("%name%", blockName));
			}else{
				player.getWorld().dropItemNaturally(block.getLocation(), is);
				if(hasEye(block)){
					player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.EYE_OF_ENDER));
				}
			}
				block.setType(Material.AIR);
				if(!player.getGameMode().equals(GameMode.CREATIVE)){
					player.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 120, 100);
					player.getWorld().playSound(player.getLocation(), Sound.GLASS, 1, (float) .8 );
				}
			
		}
	}
	
	public static synchronized boolean pointExists(Block block){
		PortalFile pt = new PortalFile();
		return pt.contains(block.getLocation());
	}
	
	public static synchronized String getRandomID(){
		PortalFile pt = new PortalFile();
		String name = "";
		do{
			name =  generateRandomName();
		}while(pt.contains(name));
		return name;
	}
	
	private static String generateRandomName(){
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

	public static synchronized ItemStack copyName(ItemStack input, String copy){
		ItemMeta im = input.getItemMeta();
		im.setDisplayName(copy);
		input.setItemMeta(im);
		return input;
	}
	
	public static synchronized void dropItem(ItemStack item, Block block){
		Location loc = new Location(block.getWorld(), block.getX(), block.getY() + 1, block.getZ());
		block.getWorld().dropItemNaturally(loc, item);
	}
	
	@SuppressWarnings("deprecation")
	public static synchronized boolean toggleEye(Block block, boolean Boolean){
		if(block.getType() == Material.ENDER_PORTAL_FRAME){
			if(Boolean)
				block.setData((byte) 4);
			else
				block.setData((byte)0);
			
			return true	;
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static boolean hasEye(Block block){
		if(block.getType() == Material.ENDER_PORTAL_FRAME){
			if(block.getData() < 4)
				return false;
			else
				return true;
		}else{
			return false;
		}
	}

}
