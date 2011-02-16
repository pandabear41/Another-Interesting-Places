package net.onlyway.AIP;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AnotherInterest extends JavaPlugin {
	
    public static final String DATA_FILE = "places.dat";

    private final AnotherInterestPlayer player = new AnotherInterestPlayer(this);
    private final AnotherInterestVehicle vehicle = new AnotherInterestVehicle(this);
    private final Places places = new Places(this);
    
    private HashMap<Player,Place> current = new HashMap<Player,Place>();
    private HashMap<Player,Long> times = new HashMap<Player,Long>();

    public AnotherInterest(PluginLoader loader, Server server, PluginDescriptionFile pdf, File dir, File plugin, ClassLoader classLoader)
    {
        super( loader, server, pdf, dir, plugin, classLoader );
    	places.updateData();
        if (places.convertOld("places.txt", server))
            System.out.println("An old interesting places file was loaded.");

    }

    @Override
    public void onEnable()
    {
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdfFile = this.getDescription();

        pm.registerEvent(Event.Type.PLAYER_JOIN,    player,  Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT,    player,  Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE,    player,  Priority.Normal, this);
        pm.registerEvent(Event.Type.VEHICLE_MOVE,   vehicle, Priority.Normal, this);

        // Thanks Zoot. 
        getDataFolder().mkdirs(); // Make sure dir exists
        File config_file = new File(getDataFolder(), "config.yml");
        if (!config_file.isFile()) {
            extractResourceTo("/config.yml", config_file.getPath());
            System.out.println("A default config file was created for " + pdfFile + ". Please restart the server to ensure that the config is loaded.");
        }
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " has been loaded.");
    }

    /**
     *
     * @author Zoot
     */
    public static void extractResourceTo(String resource, String path) {
        FileWriter output;
        try {
            output = new FileWriter(path);
        } catch (IOException ex) {
            System.err.println("Error: Could not create file " + path);
            return;
        }

        InputStream input = AnotherInterest.class.getResourceAsStream(resource);
        try {
            int c = input.read();
            while (c > 0) {
                output.write(c);
                c = input.read();
            }
        } catch (IOException ex) {
            System.err.println("Error while writing file: " + path);
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException ex) {
                // Just give up
            }
        }
    }

    @Override
    public void onDisable()
    {
        places.updateData();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (command.getName().equalsIgnoreCase("aip")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command");
                return false;
            }

            Player player = (Player) sender;
            if (args.length < 1 || !((args[0].equalsIgnoreCase("mark") && args.length > 1) || args[0].equalsIgnoreCase("unmark") || args[0].equalsIgnoreCase("nearest") || args[0].equalsIgnoreCase("who"))) {
                player.sendMessage(ChatColor.RED + "/aip <unmark,nearest,who>");
                player.sendMessage(ChatColor.RED + "OR /aip mark [Name]:[Radius]");
                player.sendMessage(ChatColor.RED + "OR /aip mark [Name]:[Radius],[Y Radius]");
                player.sendMessage(ChatColor.RED + "OR /aip who [Player Name]");
                player.sendMessage(ChatColor.RED + "mark - Mark a position on the map with a name.");
                player.sendMessage(ChatColor.RED + "unmark - Unmark the nearest marked position");
                player.sendMessage(ChatColor.RED + "nearest - Displays the nearest marked position");
                player.sendMessage(ChatColor.RED + "who - List connected players and the areas they are in");
                return false;
            }
           
            if (args[0].equalsIgnoreCase("mark")) {
                String[] sstring = arrayToString(args, " ", 1).split(":");
                if (sstring.length < 2) {
                    player.sendMessage(ChatColor.RED + "Invalid Command Syntax!");
                    player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius]");
                    return false;
                }

                String name = sstring[0];
                String lprms = sstring[1];
                String[] parms = lprms.split(",");
                if (parms.length == 1) {  //Only Radius entered.
                    try {
                        markPlace(player, name, Integer.parseInt(parms[0]), true);
                    } catch ( NumberFormatException e ) {
                        player.sendMessage(ChatColor.RED + "Error in radius entry!");
                        player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius]");
                        return false;
                    }
                } else if (parms.length == 2) { //Radius and depth entered.
                    if (parms[1].contains("-")) {
                        String[] oparms = parms[1].split("-");
                        if (oparms.length == 2) {
                            try {
                                 markPlace(player, name, Integer.parseInt(parms[0]), Integer.parseInt(oparms[0]), Integer.parseInt(oparms[1]));
                            } catch ( NumberFormatException e ) {
                                player.sendMessage(ChatColor.RED + "Error in data entry!");
                                player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius],[Y Start]-[Y End]");
                                return false;
                            }
                        }
                    } else {
                        try {
                             markPlace(player, name, Integer.parseInt(parms[0]), Integer.parseInt(parms[1]));
                        } catch ( NumberFormatException e ) {
                            player.sendMessage(ChatColor.RED + "Error in data entry!");
                            player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius],[Y Radius]");
                            return false;
                        }
                    }

                }

                
            } else if (args[0].equalsIgnoreCase("unmark")) {
                unmarkPlace(player);
            } else if (args[0].equalsIgnoreCase("nearest")) {
                sendNearest(player);
            }
        }
        return true;
    }

    public static String arrayToString(String[] a, String separator) {
        return arrayToString(a, separator, 0);
    }
    public static String arrayToString(String[] a, String separator, int init) {
        StringBuilder result = new StringBuilder();
        if (a.length > init) {
            result.append(a[init]);
            for (int i=init+1; i<a.length; i++) {
                result.append(separator);
                result.append(a[i]);
            }
        }
        return result.toString();
    }

    private Place nearestPlace(Player player)
    {
    	if (places == null)
    		return null;
    	return places.getNearest(player.getLocation(), (int) player.getWorld().getId());
    }

    private Place nearestPlaceInRange(Player player)
    {
    	if (places == null)
    		return null;
    	return places.getNearestRadius(player.getLocation(), (int) player.getWorld().getId());
    }

    public void updateCurrent(Player player)
    {
    	if (times.containsKey(player) && System.currentTimeMillis() - times.get( player ) < 3000 )
    		return;
    	
    	Place place = nearestPlaceInRange(player);
        if (place != null && getConfiguration().getBoolean("debug",false)) player.sendMessage(ChatColor.WHITE + "Distance from:" + place.distance(player.getLocation()) + ". Y Axis:" + player.getLocation().getY() );
        Place old = null;
        if (current.containsKey(player))
            old = current.get(player);

        if ((place != old || !current.containsKey(player)) && !(old != null && place != null && old.getName().equals(place.getName()))) {
            if ( place == null ) {
                if ( old != null && getConfiguration().getBoolean("no-zone-name", false))
                    player.sendMessage(ChatColor.DARK_AQUA + getConfiguration().getString("no-zone-text") + ChatColor.WHITE + old.getName());
                else
                    player.sendMessage(ChatColor.DARK_AQUA + getConfiguration().getString("no-zone-text"));
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + getConfiguration().getString("entered-zone-text") + ChatColor.WHITE + " " + place.getName());
            }
        }
	times.put(player, System.currentTimeMillis());
        current.put(player, place);
    }
    
    public void removeCurrent(Player player)
    {
    	current.remove(player);
    	times.remove(player);
    }
    
//    public void sendWho(Player player)
//    {
//    	Player[] players = getServer().getOnlinePlayers();
//    	player.sendMessage(config.whoHead(Integer.toString(players.length)));
//    	for (Player p : players) {
//            Place place = nearestPlace(p);
//
//            if (place == null)
//                player.sendMessage(config.whoLineNoPlace(p.getName()));
//            else
//                player.sendMessage(config.whoLinePlace(p.getName(), place.getName()));
//    	}
//    }
//
//    public void sendWho(Player player, String who)
//    {
//    	boolean b = false;
//    	Player[] players = getServer().getOnlinePlayers();
//    	for (Player p : players) {
//            if (p.getName().equalsIgnoreCase(who)) {
//                Location loc = p.getLocation();
//                Place place = nearestPlace(p);
//
//                if (place == null)
//                    player.sendMessage(config.whoLineNoPlace(p.getName()));
//                else
//                    player.sendMessage(config.whoLinePlace(p.getName(), place.getName()));
//                player.sendMessage("§f[" + Integer.toString( loc.getBlockX() ) + ", " + Integer.toString( loc.getBlockY() ) + ", " + Integer.toString( loc.getBlockZ() ) + "]");
//                b = true;
//            }
//    	}
//    	if (!b) {
//    		player.sendMessage(ChatColor.RED + "Player not found!");
//    	}
//    }
    
    public void sendNearest(Player player)
    {
    	Place nearest = nearestPlace(player);
    	if (nearest != null)
    		player.sendMessage(nearest.toString());
    }


    public void markPlace(Player player, String name, int radius)
    {
         markPlace(player, name, radius, true);
    }

    public void markPlace(Player player, String name, int radius, boolean ignoreY)
    {
        markPlace(player, player.getLocation(), name, radius, (ignoreY) ? -1 : radius, radius, radius);
    }

    public void markPlace(Player player, String name, int radius, int yRadius)
    {
        markPlace(player, player.getLocation(), name, radius, yRadius, radius, radius);
    }

    public void markPlace(Player player, String name, int radius, int y, int yRadius)
    {
        Location loc = player.getLocation();
        loc.setY(y + (yRadius - y)/2);
        markPlace(player, loc, name, radius, (Math.abs(yRadius - y)/2), radius, radius);
    }

    public void markPlace(Player player, Location loc, String name, int rx, int ry, int rz, int radius)
    {
//    	if (config.opsOnly() && !player.isOp()) {
//    		player.sendMessage(ChatColor.RED + "ops only!");
//    		return;
//    	}

    	Place nearest = nearestPlace(player);

    	if (nearest != null && nearest.distance( player.getLocation() ) < 5) {
    		player.sendMessage(ChatColor.RED + "Too close to " + nearest.toString() + "!");
    		return;
    	}

    	if (name == null || name.trim().equals( "" )) {
    		player.sendMessage(ChatColor.RED + "You must supply a name!");
    		return;
    	}

    	Place mark = null;
    	if ( radius != -1 )
    		mark = new Place(loc, radius, ry, (int) player.getWorld().getId(), name, player.getDisplayName());
    	else
    		mark = new Place(loc, rx, ry, rz, (int) player.getWorld().getId(), name, player.getDisplayName());

    	places.getPlaces().add(mark);
        places.updateData();
    	player.sendMessage(ChatColor.BLUE + "marked " + mark.toString());

    	for (Player p : getServer().getOnlinePlayers())
    		updateCurrent(p);
    }


    public void unmarkPlace(Player player)
    {
//    	if (config.opsOnly() && !player.isOp()) {
//    		player.sendMessage(ChatColor.RED + "ops only!");
//    		return;
//    	}
    	
    	Place nearest = nearestPlace(player);
        if (nearest == null) {
    		player.sendMessage(ChatColor.RED + "Nothing to unmark!");
    		return;
    	}


    	if (player.isOp() || player.getDisplayName().equals(nearest.getOwner()) || nearest.getOwner().equals("[none]") ) {
            places.getPlaces().remove(nearest);
            places.updateData();
            player.sendMessage(ChatColor.RED + "Unmarked " + nearest.toString());

            for (Player p : getServer().getOnlinePlayers())
                    updateCurrent(p);
        } else {
            player.sendMessage(ChatColor.RED + "You can't unmark a point you don't own!");
        }
    }
    
    
    
}