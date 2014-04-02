package portalBlock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class IOportals {
	
	public static synchronized void writePlayerFile(ArrayList<PortalPoint> portalPoints){
		
		File worldFile = createPlayerFile();
		worldFile.delete();
		File newworldFile = createPlayerFile();
		
		int counter = 0;
			for(PortalPoint portalPoint : portalPoints){
				
				setMessage(newworldFile, "portalpoint" + counter +".x", portalPoint.getX().toString());
				setMessage(newworldFile, "portalpoint" + counter +".y", portalPoint.getY().toString());
				setMessage(newworldFile, "portalpoint" + counter +".z", portalPoint.getZ().toString());
				setMessage(newworldFile, "portalpoint" + counter +".name", portalPoint.getName());
				setMessage(newworldFile, "portalpoint" + counter +".world", portalPoint.getWorld().getName());
				
				counter++;
			}
			
		}
	
public static synchronized ArrayList<PortalPoint> readPlayerFile(){
		
		File worldFile = createPlayerFile();
		 FileConfiguration config = YamlConfiguration.loadConfiguration(worldFile);
	
		 ArrayList<PortalPoint> portalPoints = new ArrayList<PortalPoint>();
		 PortalPoint portalPoint = new PortalPoint();
		 
		 for (String message :  config.getConfigurationSection("").getKeys(true)) {
			 
			 if(message.contains(".x")){
				 portalPoint.setX(Integer.parseInt(config.getString(message)));
			 }else if(message.contains(".y")){
				 portalPoint.setY(Integer.parseInt(config.getString(message)));
			 }else if(message.contains(".z")){
				 portalPoint.setZ(Integer.parseInt(config.getString(message)));
			 }else if(message.contains(".world")){
				 portalPoint.setWorld(Bukkit.getWorld(config.getString(message)));
				 portalPoints.add(portalPoint);
				 portalPoint = new  PortalPoint();
			 }else if(message.contains(".name")){
				 String name = config.getString(message);
				 portalPoint.setName(name);
			 }
		 }

		 return portalPoints;
	}
	
	private static File createPlayerFile(){
		try {
			
			File dir = new File(PortalBlock.getInstance().getDataFolder()+File.separator+"Points"+File.separator);
			Files.createDirectories(dir.toPath());
			File WorldFile = new File( dir, "points.yml");
			
			if(!WorldFile.exists()){
				WorldFile.createNewFile();
			}
			
			return WorldFile;
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void setMessage(File file, String name, String message) {
		
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(name, message);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}
