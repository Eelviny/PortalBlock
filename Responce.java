package PortalBlock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class Responce {
	
	private String responce = null;
	
	private String message;
	private Player player;
	private int limit;
	private Block block;
	
	private boolean isDead = false;
	private boolean isGone = false;
	private Location location;
	private int counter;
	
	
	public Responce(String message, Player player, int limit, Block block){
		this.message = message;
		this.player = player;
		this.limit = limit;
		this.block = block;
		this.location = player.getLocation();
	}
	
	public String getResponce(){
		
		Listener listener = new Listener(){
			
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

		};
		
		Thread counterUp = new Thread(){
			 public void run() {
				 while(getCounter() <= limit){
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
		Bukkit.getPluginManager().registerEvents( listener, PortalBlock.getInstance());
		counterUp.start();
		
		while(responce == null && !isDead && !isGone && block.getLocation().distance(getLocation()) < 10 && getCounter() < limit){}
		
		HandlerList.unregisterAll(listener);
		
		return responce;
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
}
