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
    private String name;
    private String ownername;

    public Place(Location loc, int radius, int ignoreY, int world, String name, String ownername)
    {
        xDist = radius;
        yDist = ignoreY;
        zDist = radius;
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
        this.radius = radius;
        this.name = name;
        this.world = world;
        this.ownername = ownername;
    }

    public Place(Location loc, int rx, int ry, int rz, int world, String name, String ownername)
    {
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
        xDist = rx;
        yDist = ry;
        zDist = rz;
        radius = -1;
        this.name = name;
        this.world = world;
        this.ownername = ownername;
    }

    public double distance(Location loc)
    {
        double r = 0;
        r += (xDist == -1) ? 0 : Math.pow( x - loc.getBlockX(), 2);
        r += (yDist == -1) ? 0 : Math.pow( y - loc.getBlockY(), 2); 
        r += (zDist == -1) ? 0 : Math.pow( z - loc.getBlockZ(), 2);
        return Math.sqrt(r);
    }

    public boolean inRange(Location loc)
    {
        if (radius != -1)
            return (distance(loc) <= radius && (yDist == -1) ? true : ((y - yDist) <= loc.getBlockY() && (y + yDist) >= loc.getBlockY()));
        else {
            boolean rangeX, rangeY, rangeZ;
            rangeX = (xDist == -1) ? true : ((x - xDist) <= loc.getBlockX() && (x + xDist) >= loc.getBlockX());
            rangeY = (yDist == -1) ? true : ((y - yDist) <= loc.getBlockY() && (y + yDist) >= loc.getBlockY());
            rangeZ = (zDist == -1) ? true : ((z - zDist) <= loc.getBlockZ() && (z + zDist) >= loc.getBlockZ());
            return (rangeX && rangeY && rangeZ);
        }
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

        return ChatColor.WHITE + name + " [" + Integer.toString(x) + ", " + Integer.toString(y) + ", " + Integer.toString(z) + "] Radius:[" + Integer.toString(xDist) + ", " + Integer.toString(yDist) + ", " + Integer.toString(zDist) + "]";
    }
	
}