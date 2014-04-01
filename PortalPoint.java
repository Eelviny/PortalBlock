package portalBlock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class PortalPoint {
	
	private int X;
	private int Y;
	private int Z;
	
	private World world;
	private String name;
	
	public PortalPoint(){
		
		X = 0;
		Y = 0;
		Z = 0;
		world = Bukkit.getWorld("world");	
		name = "DEFAULT";
	}
	
	public PortalPoint(int X, int Y, int Z, World world, String name){
		
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.world = world;
		this.name = name;
	}
	
	public Integer getX(){
		return this.X;
	}
	
	public double getDoubleX(){
		return (double) this.X;
	}
	
	public Integer getY(){
		return this.Y;
	}
	
	public double getDoubleY(){
		return (double) this.Y;
	}
	
	public Integer getZ(){
		return this.Z;
	}
	
	public double getDoubleZ(){
		return (double) this.Z;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Location getLocation(){
		return new Location(this.getWorld(), this.getDoubleX(), this.getDoubleY(), this.getDoubleZ());
	}
	
	public Location getHeadLocation(){
		return new Location(this.getWorld(), this.getDoubleX(), this.getDoubleY() + 1.0, this.getDoubleZ());
	}
	
	public void setX(int X){
		this.X = X;
	}
	
	public void setY(int Y){
		this.Y = Y;
	}
	
	public void setZ(int Z){
		this.Z = Z;
	}
	
	public void setWorld(World world){
		this.world = world;
	}
	
	public void setName(String name){
		this.name = name;
	}

}
