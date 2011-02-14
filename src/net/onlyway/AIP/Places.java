package net.onlyway.AIP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Places {
	
    private final AnotherInterest plugin;
    private ArrayList< Place > places = new ArrayList< Place >();

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
                            places.add(plac);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                }


            }
            catch ( IOException e ) {
            }
    }

    public ArrayList< Place > getPlaces()
    {
            return places;
    }

    void updateData()
    {
        try {
            plugin.getDataFolder().mkdir();
            FileOutputStream writer = new FileOutputStream(plugin.getDataFolder() + File.separator + AnotherInterest.DATA_FILE);

            ObjectOutputStream oout = new ObjectOutputStream (writer);;
            for ( Place p : places )
                oout.writeObject(p);
            writer.close();
        }
        catch ( IOException e ) {
        }
    }
	
}
