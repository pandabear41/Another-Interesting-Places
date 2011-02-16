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
            FileInputStream reader = new FileInputStream(plugin.getDataFolder() + File.separator + AnotherInterest.DATA_FILE);
            ObjectInputStream oin = new ObjectInputStream(reader);


            Object obj = null ;
            try {
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

    public boolean convertOld(String filename, Server server) {
        File old_file = new File(plugin.getDataFolder(), filename);
        if (!old_file.isFile()) {
            return false;
        }
        try {
            BufferedReader reader = new BufferedReader( new FileReader( plugin.getDataFolder() + File.separator + filename ) );
            boolean b = true;
            boolean v1_1 = false;
            String s = reader.readLine();
            if ( s.equals( "version1.1" ) ) {
                v1_1 = true;
                s = reader.readLine();
            }
            while ( b ) {
                try {
                    addOld( s, v1_1, server );
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
        return true;
    }

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
                myPlace = new Place(loc, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), (int) world.getId(), args[6].replaceAll( "##", "§" ), "[none]");
            } else {
                String[] args = s.split( " ", 5 );
                loc =  new Location(world, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                myPlace = new Place(loc, Integer.parseInt(args[3]), -1, (int) world.getId(), args[4].replaceAll( "##", "§" ), "[none]");
            }
        } else {
            String[] args = s.split( " ", 4 );
// TODO: Add a place with default radius.
//            x = Integer.parseInt( args[ 0 ] );
//            y = Integer.parseInt( args[ 1 ] );
//            z = Integer.parseInt( args[ 2 ] );
//            name = args[ 3 ].replaceAll( "##", "§" );
        }
        places.add(myPlace);

    }

    public ArrayList<Place> getPlaces()
    {
        return places;
    }
    
    public Place getNearestRadius(Location loc, int world) {
        Iterator placs = places.iterator();

        //Lazy mode activated.
        Place nearest = null;
        Place current = null;
        if (placs.hasNext()) {
            nearest = (Place) placs.next();

        }
        while(placs.hasNext()) {
            current = (Place) placs.next();
            if (nearest.distance(loc) > current.distance(loc) && current.inRange(loc) && current.getWorld() == world) {
                nearest = current;
            }
        }

        if (nearest == null )
            return null;
        else 
            return (nearest.inRange(loc) && nearest.getWorld() == world) ? nearest : null ;

    }

    public Place getNearest(Location loc, int world) {
        Iterator placs = places.iterator();

        //Lazy mode activated.
        Place nearest = null;
        Place current = null;
        if (placs.hasNext()) {
            nearest = (Place) placs.next();
        }
        while(placs.hasNext()) {
            current = (Place) placs.next();
            if (nearest.distance(loc) > current.distance(loc) && current.getWorld() == world) {
                nearest = current;
            }
        }
        if (nearest == null)
            return null;
        else
            return (nearest.getWorld() == world) ? nearest : null ;
    }

    void updateData()
    {
        try {
            FileOutputStream writer = new FileOutputStream(plugin.getDataFolder() + File.separator + AnotherInterest.DATA_FILE);

            ObjectOutputStream oout = new ObjectOutputStream (writer);
            for (Place p : places) {
                oout.writeObject(p);
            }
            writer.close();
        }
        catch (IOException e) {
        }
    }
	
}
