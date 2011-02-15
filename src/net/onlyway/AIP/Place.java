package net.onlyway.AIP;

import java.io.Serializable;
import org.bukkit.ChatColor;
import org.bukkit.Location;


public class Place implements Serializable {
	
    private int x;
    private int y;
    private int z;
    private int radius;
    private int xDist;
    private int yDist;
    private int zDist;
    private int world;
    private boolean ignoreY;
    private String name;
    private String ownername;

    public Place(Location loc, int radius, int world, String name, String ownername)
    {
        xDist = -1;
        yDist = -1;
        zDist = -1;
        ignoreY = false;
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
        this.radius = radius;
        this.name = name;
        this.world = world;
        this.ownername = ownername;
    }

    public Place(Location loc, int x, int y, int z, int world, String name, String ownername)
    {
        radius = -1;
        ignoreY = false;
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        xDist = x;
        yDist = y;
        zDist = z;
        this.name = name;
        this.world = world;
        this.ownername = ownername;
    }

    public double distance(Location loc)
    {
        double r = 0;
        r += Math.pow( x - loc.getBlockX(), 2);
        r += (yDist == -1 || ignoreY) ? (Math.pow( y - loc.getBlockY(), 2)) : 0; // If there is a depth we want to calculate it seperately.
        r += Math.pow( z - loc.getBlockZ(), 2);
        return Math.sqrt(r);
    }



    public boolean inRange(Location loc)
    {
        if (yDist == -1)
            return (distance(loc) <= radius);
        else
            return (distance(loc) <= radius) && ((y - yDist) < loc.getBlockY() && (y + yDist) > loc.getBlockY());
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getZ() {
        return z;
    }

    public void setXDist(int xDist) {
        this.xDist = xDist;
    }

    public int getXDist() {
        return xDist;
    }

    public void setYDist(int yDist) {
        this.yDist = yDist;
    }

    public int getYDist() {
        return yDist;
    }

    public void setZDist(int zDist) {
        this.zDist = zDist;
    }

    public int getZDist() {
        return zDist;
    }

    public void setIgnoreY(boolean ignore) {
        ignoreY = ignore;
    }

    public boolean getIgnoreY() {
        return ignoreY;
    }

    public float getRadius()
    {
        return radius;
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

    public String getOwner() {
        return ownername;
    }

    @Override
    public String toString() {

        return ChatColor.WHITE + name + " [" + Integer.toString( x ) + ", " + Integer.toString( y ) + ", " + Integer.toString( z ) + ", Radius:" + Integer.toString( radius ) + "]";
    }
	
}