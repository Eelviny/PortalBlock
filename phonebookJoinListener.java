package PortalBlock;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;


public class phonebookJoinListener implements Listener {
	
	@EventHandler(priority = EventPriority.NORMAL)
	public synchronized void onLogin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(!player.hasPlayedBefore() || player.isOp()){
			ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bm = (BookMeta) is.getItemMeta();
			bm.setDisplayName("§8Phonebook");
			bm.setAuthor("§8Just§3Game");
			PortalFile pt = new PortalFile();
			ArrayList<PortalPoint> list = pt.getPoints();
			ArrayList<String> addresses = new ArrayList<String>();
			for(PortalPoint pp: list){
				if(pp.getWorld().equals(player.getWorld()))
					addresses.add(addSpaces(pp.getName()+":")+ "X: "+pp.getX()+" Y: "+ pp.getY()+" Z: "+ pp.getZ());
			}
			bm.setPages(addresses);
			is.setItemMeta(bm);
			player.getInventory().addItem(is);
		}
	}
	
	private String addSpaces(String string){
		if(string.length() < 19){
			do{
				string += " ";
			}while(string.length() < 19);
		}
		return string;
	}

}
