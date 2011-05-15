package net.onlyway.AIP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Server;
import org.bukkit.World.Environment;

public class Places {

    private final AnotherInterest plugin;
    private ArrayList<Place> places = new ArrayList<Place>();

    public Places(final AnotherInterest plugin)
    {
        this.plugin = plugin;
        try {
            // Open up the file.
            FileInputStream reader = new FileInputStream(plugin.getDataFolder() + File.separator + AnotherInterest.DATA_FILE);
            ObjectInputStream oin = new ObjectInputStream(reader);

            Object obj = null ;
            try {
                // Read the place object and put it into the array.
                while ((obj = oin.readObject()) != null) {
                    if (obj instanceof Place) {
                        Place plac = (Place) obj;
                        places.add(plac);
                    }
                }
            } catch (ClassNotFoundException ex) {
            }
        } catch (IOException e) {
        }
    }

    // Converter for the old Interesting Places format.
    public boolean convertOld(String filename, Server server) {
        File old_file = new File(plugin.getDataFolder(), filename);
        if (!old_file.isFile()) {
            return false;
        }
        try {
            BufferedReader reader = new BufferedReader( new FileReader( plugin.getDataFolder() + File.separator + filename ) );
            boolean b = true;
            int version = 0;
            String s = reader.readLine();
            if ( s.equals( "version1.1" ) ) {
                version = 1;
                s = reader.readLine();
            } else if ( s.equals( "version1.2" )) {
                version = 2;
            }
            while ( b ) {
                try {
                    if (version == 2) {
                       addv1_2( s, server); 
                    } else {
                       addOld( s, (version==1 ? true : false), server ); 
                    }
                }
                catch ( Exception e ) {
                }
                s = reader.readLine();
                b = s != null;
            }
            reader.close();
        }
        catch ( IOException e ) {
        }
        old_file.delete();
        this.updateData();
        return true;
    }
    
    // Had to add this because the programmer added another version.
    public void addv1_2(String s, Server server) {
        Place myPlace = null;
        int x, y, z, radius;
        String name;
        String[] xyz = s.split(" ", 2);
        if (xyz[0].equalsIgnoreCase("xyz")) {
                String[] args = xyz[1].split(" ", 8);
                String worldname = args[0];
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
                int xDist = Integer.parseInt(args[4]);
                int yDist = Integer.parseInt(args[5]);
                int zDist = Integer.parseInt(args[6]);
                name = args[7].replaceAll("##", "§");
                myPlace = new Place(new Location(plugin.getWorld(worldname), x, y, z), xDist, yDist, zDist, worldname, name, "[None]");
        } else {
                String[] args = s.split(" ", 6);
                String worldname = args[0];
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
                radius = Integer.parseInt(args[4]);
                name = args[5].replaceAll("##", "§");
                myPlace = new Place(new Location(plugin.getWorld(worldname), x, y, z), radius, -1, worldname, name,  "[None]");
        }
        places.add(myPlace);
    }

    // Parses the old format.
    public void addOld(String s, boolean v1_1, Server server) {
        Place myPlace = null;
        Location loc;
        World world;

        List<World> worlds = server.getWorlds();
        world = server.getWorlds().get(0);
        for (int i = 0; i < worlds.size(); i++)
        {
            world = server.getWorlds().get(i);
            if (world.getEnvironment() == Environment.NORMAL) break;
        }

        if ( v1_1 ) {
            String[] xyz = s.split( " ", 2 );
            if ( xyz[ 0 ].equalsIgnoreCase( "xyz" ) ) {
                String[] args = xyz[ 1 ].split( " ", 7 );
                loc =  new Location(world, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                myPlace = new Place(loc, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), world.getName(), args[6].replaceAll( "##", "§" ), "[none]");
            } else {
                String[] args = s.split( " ", 5 );
                loc =  new Location(world, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                myPlace = new Place(loc, Integer.parseInt(args[3]), -1, world.getName(), args[4].replaceAll( "##", "§" ), "[none]");
            }
        } else {
            String[] args = s.split( " ", 4 );
            loc =  new Location(world, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            myPlace = new Place(loc, plugin.getConfiguration().getInt("radius-default", 25), -1, world.getName(), args[3].replaceAll( "##", "§" ), "[none]");
        }
        places.add(myPlace);
    }

    // Get the array of the places.
    public ArrayList<Place> getPlaces()
    {
        return places;
    }

    // Get the nearest place in the radius circle.
    public Place getNearestRadius(Location loc, String world) {
        Iterator placs = places.iterator();

        //Lazy mode activated.
        Place nearest = null;
        Place current = null;
        if (placs.hasNext()) {
            nearest = (Place) placs.next();

        }
        while(placs.hasNext()) {
            current = (Place) placs.next();
            if (nearest.distance(loc) > current.distance(loc) && current.inRange(loc) && current.getWorld().equals(world)) {
                nearest = current;
            }
        }

        if (nearest == null )
            return null;
        else 
            return (nearest.inRange(loc) && nearest.getWorld().equals(world)) ? nearest : null ;

    }

    // Get the nearest place.
    public Place getNearest(Location loc, String world) {
        Iterator placs = places.iterator();

        //Lazy mode activated.
        Place nearest = null;
        Place current = null;
        if (placs.hasNext()) {
            nearest = (Place) placs.next();
        }
        while(placs.hasNext()) {
            current = (Place) placs.next();
            if (nearest.distance(loc) > current.distance(loc) && current.getWorld().equals(world)) {
                nearest = current;
            }
        }
        if (nearest == null)
            return null;
        else
            return (nearest.getWorld().equals(world)) ? nearest : null ;
    }

    void updateData()
    {
        try {
            // Open up the file.
            FileOutputStream writer = new FileOutputStream(plugin.getDataFolder() + File.separator + AnotherInterest.DATA_FILE);
            ObjectOutputStream oout = new ObjectOutputStream (writer);

            // Output all of the places to the file.
            for (Place p : places) {
                oout.writeObject(p);
            }
            writer.close();
        }
        catch (IOException e) {
        }
    }
	
}
