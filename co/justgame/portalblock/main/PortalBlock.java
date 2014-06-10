package co.justgame.portalblock.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import co.justgame.portalblock.io.PortalFile;
import co.justgame.portalblock.listeners.ClickPortalBlockListener;
import co.justgame.portalblock.listeners.PlacePortalBlockListener;
import co.justgame.portalblock.point.PortalPoint;

public class PortalBlock extends JavaPlugin{
	
	public static Plugin portalBlock;
	public static boolean sendConsoleMessages;
	public static int denyTime = 10;
	public static int denyDistance = 10;
	
	
	FileConfiguration config;
	
	public static HashMap<String, String> messageData = new HashMap<String, String>();
	public static HashMap<String, Block> inUse = new HashMap<String, Block>();
	
	@Override
	public void onEnable(){
		getLogger().info("PortalBlock has been enabled");
		portalBlock = this;
		
		config = this.getConfig();

		config.options().header("");

		config.addDefault("consolemessages", false);
		config.addDefault("cancel.time", 20);
		config.addDefault("cancel.distance", 10);
		config.addDefault("recipe.userecipe", true);
		config.addDefault("recipe.map", "XXX, XXX, XXX");
		config.addDefault("recipe.ingredients.endstone.key", "X");
		config.addDefault("recipe.ingredients.endstone.material", "ENDER_STONE");
		
		
		config.options().copyDefaults(true);
		saveConfig();
		
		File Messages = new File(getDataFolder() + File.separator + "messages.yml");
		if(!Messages.exists()) try{
			Messages.createNewFile();
		}catch (IOException e){
			e.printStackTrace();
		}

		setUpMessagesYML();
		
		readConfig();
		
		try{
			FileConfiguration config = YamlConfiguration.loadConfiguration(Messages);
			for(String message: config.getConfigurationSection("").getKeys(true)){
				messageData.put(message, formatString(config.getString(message)));
			}
		}catch (Exception e){
			getConsole().sendMessage(formatString("[QuickChat] " + "&cError loading messages.yml!"));
		}
		
		getServer().getPluginManager().registerEvents(new ClickPortalBlockListener(), this);
		getServer().getPluginManager().registerEvents(new PlacePortalBlockListener(), this);
	}
	
	@Override
	public void onDisable(){
		getLogger().info("PortalBlock has been disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("portal")){
			if(!(sender instanceof Player)){
					sender.sendMessage(messageData.get("portalblock.console"));
					return true;
			}
			
			if( sender.hasPermission("portalblock.command")){
				if(args.length > 1){
					
					Player player = (Player) sender;
					PortalFile pt = new PortalFile();
					PortalPoint portalPoint = pt.getPoint(StringUtils.join(args, " "));
					if(portalPoint != null){
						player.teleport(portalPoint.getLocation());
						player.sendMessage(messageData.get("portalblock.command.teleport").replace("%point%", portalPoint.getName()));
					}else{
						player.sendMessage(messageData.get("portalblock.command.notexist")
								.replace("%point%", args[0]));
					}
				}else{
					sender.sendMessage(messageData.get("portalblock.command.usage"));
				}
			}else{
				sender.sendMessage(messageData.get("portalblock.command.nopermission"));
			}
		}
		return true;
	}

	public static synchronized ConsoleCommandSender getConsole(){
		return Bukkit.getServer().getConsoleSender();
	}
	
	public static synchronized HashMap<String, String> getMessageData(){
		return messageData;
	}
	
	public static synchronized Plugin getInstance(){
		return portalBlock;
	}
	
	public static synchronized int getTime(){
		return denyTime;
	}
	
	public static synchronized int getDistance(){
		return denyDistance;
	}
	
	public static synchronized boolean sendConsoleMessage(){
		return sendConsoleMessages;
	}
	
	public static synchronized void setInUse(Block b, String player){
		inUse.put(player, b);
	}
	
	public static synchronized void setNotUse(String player){
		inUse.remove(player);
	}
	
	public static synchronized boolean isUsing(String player, Block b){
		if(inUse.containsKey(player)){
			if(inUse.get(player).equals(b)){
				return true;
			}
		}
		return false;
	}
	
	public static synchronized boolean inUse(Block b){
		if(inUse.containsValue(b))
			return true;
		return false;
	}
	
	public static synchronized boolean inUse(String p){
		if(inUse.containsKey(p))
			return true;
		return false;
	}
	
	
	private void setUpMessagesYML(){
		setMessage("portalblock.getid", "&aEnter the name of another portal block:");
		setMessage("portalblock.id", "&aPortal ID: %name%");
		setMessage("portalblock.console", "&cThis command cannot be used by Console!");
		setMessage("portalblock.usage", "&cUsage: /portal <ID>");
		setMessage("portalblock.notexist", "&cPortal block &4%point%&c does not exist!");
		setMessage("portalblock.alreadyexist", "&cA portal block named &4%name%&c already exists!");
		setMessage("portalblock.create", "&aCreated new portal block! ID: %name%");
		setMessage("portalblock.break", "&cRemoved portal block &4%name%");
		setMessage("portalblock.nopermission", "&cYou do not have permission to place a portal block!");
		setMessage("portalblock.teleport", "&dTeleported to... &8%point%");
		setMessage("portalblock.cancel", "&cTeleport request canceled!");
		setMessage("portalblock.inuse", "&cThis portal block is already in use!");
		setMessage("portalblock.same", "&cYou cannot teleport to the portal you are using!");
		
		setMessage("portalblock.command.nopermission", "&cYou do not have permission to use this command!");
		setMessage("portalblock.command.usage", "&cUsage: /portal <ID>");
		setMessage("portalblock.command.notexist", "&cPortal block &4%point%&c does not exist!");
		setMessage("portalblock.command.teleport", "&dTeleported to... &8%point%");
		
		setMessage("portalblock.console.create", "&a%player% created new portal block! ID: %name%");
		setMessage("portalblock.console.break", "&c%player% removed portal block &4%name%");
		setMessage("portalblock.console.teleport", "&a%player% teleported to &8%point%");
	}
	
	private void readConfig(){
		try{
			denyTime = config.getInt("cancel.time");
			denyDistance = config.getInt("cancel.distance");
			sendConsoleMessages = config.getBoolean("consolemessages");
			
			if(config.getBoolean("recipe.userecipe")){
				ShapedRecipe recipe = new ShapedRecipe(new ItemStack(Material.ENDER_PORTAL_FRAME));
				String rawMap = config.getString("recipe.map");
				recipe.shape(rawMap.split(", "));
		
				char c = 0;
				Material m = null;
				for(String key: config.getConfigurationSection("recipe.ingredients").getKeys(true)){
					if(key.contains("key") || key.contains("material")){
						key = "recipe.ingredients." + key;
						if(key.contains("key")){
							if(config.getString(key).length() == 1){
								c = config.getString(key).charAt(0);
							}else{
								throw new IllegalArgumentException("Illegal key identifier: " + config.getString(key));
							}
						}else if(key.contains("material")){
							m = Material.valueOf(config.getString(key));
							if(rawMap.contains(String.valueOf(c)))
								recipe.setIngredient(c, m);
						}
					}
				}
				Bukkit.getServer().addRecipe(recipe);
			}
		}catch (IllegalArgumentException e){
			getConsole().sendMessage(formatString("[QuickChat] " + "&cError loading config.yml!"
					+ " Unparsable value in config! Caused by: " + e.getMessage()));
		}
	}
	
	private void setMessage(String name, String message){
		File f = new File(getDataFolder() + File.separator + "messages.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		if(!config.isSet(name)){
			config.set(name, message);
			try{
				config.save(f);
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public static String formatString(String string){
		return string.replace("&", "§");
	}

}
