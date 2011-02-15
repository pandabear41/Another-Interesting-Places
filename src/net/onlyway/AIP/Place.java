package net.onlyway.AIP;

import java.io.Serializable;
import org.bukkit.ChatColor;
import org.bukkit.Location;


public class Place implements Serializable {
	
    private int x;
    private int y;
    private int z;
    private int radius;
    // I don't think this is even needed.
    //private int xDist;
    //private int yDist;
    //private int zDist;
    private int world;
    private String name;

    public Place(Location loc, int radius, int world, String name)
    {
            x = loc.getBlockX();
            y = loc.getBlockY();
            z = loc.getBlockZ();
            this.radius = radius;
            this.name = name;
            this.world = world;
    }

    public double distance(Location loc)
    {
            double r = 0;
            r += Math.pow( x - loc.getBlockX(), 2);
            r += Math.pow( y - loc.getBlockY(), 2);
            r += Math.pow( z - loc.getBlockZ(), 2);
            return Math.sqrt(r);
    }

    public boolean inRange(Location loc)
    {
        return distance(loc) <= radius;
    }

    public int getX()
    {
            return x;
    }

    public int getY()
    {
            return y;
    }

    public int getZ()
    {
            return z;
    }

    public float getRadius()
    {
            return radius * radius;
    }

    public boolean hasRadius()
    {
            return radius > 0;
    }

    public int getWorld()
    {
            return world;
    }

    public String getName()
    {
            return name;
    }

    @Override
    public String toString() {

        return ChatColor.WHITE + name + " [" + Integer.toString( x ) + ", " + Integer.toString( y ) + ", " + Integer.toString( z ) + ", Radius:" + Integer.toString( radius ) + "]";
    }
	
}