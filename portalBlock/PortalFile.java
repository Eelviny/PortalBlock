package portalBlock;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Location;


public class PortalFile {
	
	private ArrayList<PortalPoint> portalPoints;
	
	public PortalFile(){
		this.portalPoints = IOportals.readPlayerFile();
	}
	
	public synchronized void removePoint(String name){
		for(Iterator<PortalPoint> it = portalPoints.iterator(); it.hasNext();){
			PortalPoint portalPoint = it.next();
			if(portalPoint.getName().equalsIgnoreCase(name)){
				it.remove();
			}
		}
		saveChanges();
	}
	
	public synchronized void removePoint(Location loc){
		for(Iterator<PortalPoint> it = portalPoints.iterator(); it.hasNext();){
			PortalPoint portalPoint = it.next();
			if(portalPoint.getLocation().equals(loc)){
				it.remove();
			}
		}
		saveChanges();
	}
	
	public synchronized void addPoint(PortalPoint portalPoint){
		portalPoints.add(portalPoint);
		saveChanges();
	}
	
	public synchronized PortalPoint getPoint(String name){
		for(PortalPoint portalPoint: portalPoints){
			if(portalPoint.getName().equalsIgnoreCase(name)){
				return portalPoint;
			}
		}
		return null;
	}
	
	public synchronized PortalPoint getPoint(Location loc){
		for(PortalPoint portalPoint: portalPoints){
			if(portalPoint.getLocation().equals(loc)){
				return portalPoint;
			}
		}
		return null;
	}
	
	public synchronized boolean contains(String name){
		for(PortalPoint portalPoint: portalPoints){
			if(portalPoint.getName().equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean contains(Location loc){
		for(PortalPoint portalPoint: portalPoints){
			if(portalPoint.getLocation().equals(loc)){
				return true;
			}
		}
		return false;
	}
	
	public synchronized ArrayList<PortalPoint> getPoints(){
		return portalPoints;
	}
	
	private synchronized void saveChanges(){
		Thread save = new Thread(){
			public void run() {
				IOportals.writePlayerFile(portalPoints);
			}
		};
		save.start();
	}

}
