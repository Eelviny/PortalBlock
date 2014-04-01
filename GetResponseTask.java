package PortalBlock;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class GetResponseTask extends BukkitRunnable {
	
	private HashMap<String, String> messageData = PortalBlock.getMessageData();
	 
		private String responce = null;
		
		private Boolean firstTime = true;
		private String mode = "";
		
		private String message;
		private String denyMessage;
		private Player player;
		private int time;
		private int distance;
		private Block block;
		
		private boolean isDead = false;
		private boolean isGone = false;
		private Location location;
		private int counter;
		
		private Listener listener;
		
	    public GetResponseTask(String message, String denyMessage, Player player, int time, int distance, 
	    		Block block, String mode) {
	    	this.message = message;
	    	this.mode = mode;
	        this.denyMessage = denyMessage;
		    this.player = player;
			this.time = time;
			this.distance = distance;
			this.block = block;
			this.location = player.getLocation();
	    }
	 
		@Override
	    public synchronized void run() {
	    	
	    	if(firstTime){
	    		
	    		 listener = new Listener(){
	    			
	    			@EventHandler(priority = EventPriority.NORMAL)
	    			public void onLogout(PlayerQuitEvent event){
	    				if(event.getPlayer() == player){
	    					isGone = true;
	    				}
	    			}
	    			
	    			@EventHandler(priority = EventPriority.NORMAL)
	    			public void onDeath(PlayerDeathEvent event){
	    				if(event.getEntity() == player){
	    					isDead = true;
	    				}
	    			}
	    			
	    			@EventHandler(priority = EventPriority.NORMAL)
	    			public void onMove(PlayerMoveEvent event){
	    				if(event.getPlayer() == player){
	    					setLocation(player.getLocation());
	    				}
	    			}
	    			
	    			@EventHandler(priority = EventPriority.NORMAL)
	    			public void playerChat(AsyncPlayerChatEvent event){
	    				if(event.getPlayer() == player){
	    					event.setCancelled(true);
	    					responce = event.getMessage();
	    				}
	    			}
	    			
	    			@SuppressWarnings("deprecation")
					@EventHandler(priority = EventPriority.LOW)
	    			public void blockClick(PlayerInteractEvent e){
	    				if(e.getClickedBlock() != null)
	    				if(e.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME){
	    					if(e.getPlayer().equals(player)){
		    					if(e.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking() ||
		    							e.getAction() == Action.LEFT_CLICK_BLOCK && player.isSneaking()){
		    						
		    						if(e.getAction() == Action.LEFT_CLICK_BLOCK && player.isSneaking()){
		    							PortalHandlers.breakBlock(player, block, true);
		    						}else{
		    							PortalHandlers.dropItem(new ItemStack(Material.EYE_OF_ENDER), block);
		    							PortalHandlers.toggleEye(block, false);
		    							player.sendMessage(denyMessage);
		    							player.getWorld().playSound(player.getLocation(),Sound.NOTE_BASS, 3, 1);
		    							player.getWorld().playSound(player.getLocation(),Sound.ITEM_PICKUP,5, 1);
		    						}
		    				
	    							Cancel();
	    							HandlerList.unregisterAll(listener);
	    							e.setCancelled(true);
	    							player.updateInventory();
		    					}
	    					}else{
	    						e.getPlayer().sendMessage(messageData.get("portalblock.inuse"));
	    					}
	    				}
	    			}
	    		};
	    		
	    		Thread counterUp = new Thread(){
	    			 public void run() {
	    				 while(getCounter() <= time + 1){
	    					setCounter(getCounter() + 1);
	    					try {
	    						Thread.sleep(1000);
	    					} catch (InterruptedException e) {
	    						e.printStackTrace();
	    					}
	    				 }
	    			 }
	    		};
	    		
	    		this.player.sendMessage(message);
	    		Bukkit.getPluginManager().registerEvents(listener, PortalBlock.getInstance());
	    		counterUp.start();
	    		
	    		firstTime = false;

	    	}else{
	       
	    		if(responce != null || isDead || isGone || block.getLocation().distance(getLocation()) >= distance 
	    					|| getCounter() >= time){
	    			
	    			String id = responce;
					if(id != null){
						
						if(this.mode.equals("ID")){
							PortalFile pt = new PortalFile();
							PortalPoint portalPoint = pt.getPoint(id);
							if(portalPoint != null){
								if(portalPoint.equals(pt.getPoint(block.getLocation()))){
									player.sendMessage(messageData.get("portalblock.same"));
									cancelTeleport();
								}else if (block.getWorld().getBlockAt(portalPoint.getLocation()).getType() 
										    != Material.ENDER_PORTAL_FRAME){
									player.sendMessage(messageData.get("portalblock.notexist")
											.replace("%point%", id));
									cancelTeleport();
									pt.removePoint(portalPoint.getLocation());
								}else{
									player.teleport(portalPoint.getLocation());
									PortalHandlers.toggleEye(block, false);
									PortalBlock.setNotUse(player.getDisplayName());
									player.sendMessage(messageData.get("portalblock.teleport").replace("%point%", portalPoint.getName()));
								
									if(PortalBlock.sendConsoleMessage())
										PortalBlock.getConsole().sendMessage(messageData.get("portalblock.console.teleport")
												.replace("%player%", player.getDisplayName()).replace("%point%", portalPoint.getName()));
								}
							}else{
								player.sendMessage(messageData.get("portalblock.notexist")
										.replace("%point%", id));
								cancelTeleport();
							}
						}else if(this.mode.equals("Name")){
							//this code has been kept in case Eelviny changes his mind.
						}else{
							try{
								throw new UnknownModeException();
							}catch (UnknownModeException e){
								e.printStackTrace();
								System.out.println(this.mode);
							}
						}
						
					}else{
						player.sendMessage(denyMessage);
						cancelTeleport();
					}
					HandlerList.unregisterAll(listener);
	    			this.cancel();
	    		} 
	    	}
	    }
	    
	    	private synchronized void Cancel(){
	    		this.cancel();
	    	}
	    	
	    	private synchronized Location getLocation(){
	    		return location;
	    	}
	    	
	    	private synchronized void setLocation(Location location){
	    		this.location = location;
	    	}
	    	
	    	private synchronized int getCounter(){
	    		return counter;
	    	}
	    	
	    	private synchronized void setCounter(int count){
	    		this.counter = count;
	    	}
	    	
	    	private synchronized void cancelTeleport(){
	    		player.getWorld().playSound(player.getLocation(),Sound.NOTE_BASS, 3, 1);
				player.getWorld().playSound(player.getLocation(),Sound.ITEM_PICKUP,5, 1);
				PortalHandlers.dropItem(new ItemStack(Material.EYE_OF_ENDER), block);
				PortalBlock.setNotUse(player.getDisplayName());
				PortalHandlers.toggleEye(block, false);
	    	}
	 
	}

class UnknownModeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnknownModeException(){
	}
}

