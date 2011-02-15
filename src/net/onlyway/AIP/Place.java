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
    private int depth;
    private String name;
    private String ownername;

    public Place(Location loc, int radius, int world, String name, String ownername)
    {
            depth = -1;
            x = loc.getBlockX();
            y = loc.getBlockY();
            z = loc.getBlockZ();
            this.radius = radius;
            this.name = name;
            this.world = world;
            this.ownername = ownername;
    }

    public double distance(Location loc)
    {
            double r = 0;
            r += Math.pow( x - loc.getBlockX(), 2);
            r += (depth == -1) ? (Math.pow( y - loc.getBlockY(), 2)) : 0; // If there is a depth we want to calculate it seperately.
            r += Math.pow( z - loc.getBlockZ(), 2);
            return Math.sqrt(r);
    }



    public boolean inRange(Location loc)
    {
        if (depth == -1)
            return (distance(loc) <= radius);
        else
            return (distance(loc) <= radius) && ((y - depth) < loc.getBlockY() && (y + depth) > loc.getBlockY());
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

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public String getOwner() {
        return ownername;
    }

    @Override
    public String toString() {

        return ChatColor.WHITE + name + " [" + Integer.toString( x ) + ", " + Integer.toString( y ) + ", " + Integer.toString( z ) + ", Radius:" + Integer.toString( radius ) + "]";
    }
	
}