package net.onlyway.AIP;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;


public class Place implements JSONStreamAware {
	
    protected int x;
    protected int y;
    protected int z;
    protected int radius;
    protected int xDist;
    protected int yDist;
    protected int zDist;
    protected String worldname;
    protected String name;
    protected String ownername;
    
    public Place(JSONObject place) {
        if (place.containsKey("x")) x = ((Long) place.get("x")).intValue();
        if (place.containsKey("y")) y = ((Long) place.get("y")).intValue();
        if (place.containsKey("z")) z = ((Long) place.get("z")).intValue();
        if (place.containsKey("radius")) radius = ((Long) place.get("radius")).intValue();
        if (place.containsKey("xDist")) xDist = ((Long) place.get("xDist")).intValue();
        if (place.containsKey("yDist")) yDist = ((Long) place.get("yDist")).intValue();
        if (place.containsKey("zDist")) zDist = ((Long) place.get("zDist")).intValue();
        if (place.containsKey("worldname")) worldname = (String) place.get("worldname");
        if (place.containsKey("name")) name = (String) place.get("name");
        if (place.containsKey("ownername")) ownername = (String) place.get("ownername");
    }

    public Place(Location loc, int radius, int ignoreY, String world, String name, String ownername)
    {
        this.setPlace(loc, radius, ignoreY, radius, radius, world, name, ownername);
    }

    public Place(Location loc, int rx, int ry, int rz, String world, String name, String ownername)
    {
        this.setPlace(loc, rx, ry, rz, -1, world, name, ownername);
    }

    private void setPlace(Location loc, int rx, int ry, int rz, int radius, String world, String name, String ownername) {
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
        xDist = rx;
        yDist = ry;
        zDist = rz;
        this.radius = radius;
        this.name = name;
        this.worldname = world;
        this.ownername = ownername;
    }

    // Get the distance from a location.
    public double distance(Location loc)
    {
        double r = 0;
        r += (xDist == -1) ? 0 : ( x - loc.getBlockX()) * ( x - loc.getBlockX());
        r += (yDist == -1) ? 0 : ( y - loc.getBlockY()) * ( y - loc.getBlockY());
        r += (zDist == -1) ? 0 : ( z - loc.getBlockZ()) * ( z - loc.getBlockZ());
        return Math.sqrt(r);
    }

    // Determine weather the location is in range.
    public boolean inRange(Location loc)
    {
        if (radius != -1)
            return (distance(loc) <= radius && (yDist == -1) ? true : ((y - yDist) <= loc.getBlockY() && (y + yDist) >= loc.getBlockY()));
        else {
            boolean rangeX, rangeY, rangeZ;
            rangeX = (xDist == -1) ? true : ((x - xDist) <= loc.getBlockX() && (x + xDist) >= loc.getBlockX());
            rangeY = (yDist == -1) ? true : ((y - yDist) <= loc.getBlockY() && (y + yDist) >= loc.getBlockY());
            rangeZ = (zDist == -1) ? true : ((z - zDist) <= loc.getBlockZ() && (z + zDist) >= loc.getBlockZ());
            return (rangeX && rangeY && rangeZ && (worldname.equals(loc.getWorld().getName())));
        }
    }

//    public void setX(int x) {
//        this.x = x;
//    }

    public int getX() {
        return x;
    }

//    public void setY(int y) {
//        this.y = y;
//    }

    public int getY() {
        return y;
    }

//    public void setZ(int z) {
//        this.z = z;
//    }

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

    public String getWorld()
    {
        return worldname;
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

    public void writeJSONString(Writer writer) throws IOException {
        LinkedHashMap obj = new LinkedHashMap();
        obj.put("x", x);
        obj.put("y", y);
        obj.put("z", z);
        obj.put("radius", radius);
        obj.put("xDist", xDist);
        obj.put("yDist", yDist);
        obj.put("zDist", zDist);
        obj.put("worldname", worldname);
        obj.put("name", name);
        obj.put("ownername", ownername);
        JSONValue.writeJSONString(obj, writer);
    }
	
}