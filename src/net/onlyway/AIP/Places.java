package net.onlyway.AIP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Location;

public class Places {

    private final AnotherInterest plugin;
    private HashMap<String,Place> places = new HashMap<String,Place>();

    public Places( final AnotherInterest plugin )
    {
            this.plugin = plugin;
            try {
                FileInputStream reader = new FileInputStream( plugin.getDataFolder() + File.separator + AnotherInterest.DATA_FILE);
                ObjectInputStream oin = new ObjectInputStream (reader);


                Object obj = null ;
                try {
                    while ((obj = oin.readObject()) != null) {
                        if (obj instanceof Place) {
                            Place plac = (Place) obj;
                            places.put( plac.getName(),plac);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                }


            } catch ( IOException e ) {
            }
    }

    public Set getPlaces()
    {
            return places.entrySet();
    }

    public void addPlace(Place plac) {
        places.put(plac.getName(), plac);
    }

    public Place getNearestRadius(Location loc) {
        Iterator placs = places.entrySet().iterator();

        //Lazy mode activated.
        Place nearest = null;
        Place current = null;
        Entry entry = null;
        if (placs.hasNext()) {
            entry = (Entry) placs.next();
            nearest = (Place) entry.getValue();
        }
        while(placs.hasNext()) {
            entry = (Entry) placs.next();
            current = (Place) entry.getValue();
            if (nearest.distance(loc) > current.distance(loc) && current.inRange(loc)) {
                nearest = current;
            }
        }

        if (nearest == null)
            return null;
        else 
            return nearest.inRange(loc) ? nearest : null ;

    }

    public Place getNearest(Location loc) {
        Iterator placs = places.entrySet().iterator();

        //Lazy mode activated.
        Place nearest = null;
        Place current = null;
        Entry entry = null;
        if (placs.hasNext()) {
            entry = (Entry) placs.next();
            nearest = (Place) entry.getValue();
        }
        while(placs.hasNext()) {
            entry = (Entry) placs.next();
            current = (Place) entry.getValue();
            if (nearest.distance(loc) > current.distance(loc)) {
                nearest = current;
            }
        }

        return nearest;

    }

    void updateData()
    {
        
        try {
            plugin.getDataFolder().mkdir();
            FileOutputStream writer = new FileOutputStream(plugin.getDataFolder() + File.separator + AnotherInterest.DATA_FILE);

            ObjectOutputStream oout = new ObjectOutputStream (writer);
            Iterator placs = places.entrySet().iterator();
            Entry entry = null;
            while(placs.hasNext()) {
                entry = (Entry) placs.next();
                oout.writeObject((Place) entry.getValue());
            }
            writer.close();
        }
        catch ( IOException e ) {
        }
    }
	
}
