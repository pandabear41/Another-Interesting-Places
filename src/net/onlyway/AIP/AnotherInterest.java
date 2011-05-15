package net.onlyway.AIP;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AnotherInterest extends JavaPlugin {
	
    public static final String DATA_FILE = "places.dat";

    private final AnotherInterestPlayer player = new AnotherInterestPlayer(this);
    private final AnotherInterestVehicle vehicle = new AnotherInterestVehicle(this);
    private Places places;
	
	private static final Logger log = Logger.getLogger("Minecraft");
    
    private HashMap<Player,Place> current = new HashMap<Player,Place>();
    private HashMap<Player,Long> times = new HashMap<Player,Long>();
    
    PluginDescriptionFile pdfFile;

    @Override
    public void onEnable()
    {
        PluginManager pm = getServer().getPluginManager();
        pdfFile = this.getDescription();

        // Odd problem with bukkit.
        places = new Places(this);

		initFiles();
        // Load the old config if it exists.
        if (places.convertOld("places.txt", getServer()))
            log.log(Level.INFO,"[AIP] An old interesting places file was loaded.");

        // Register out events.
        pm.registerEvent(Event.Type.PLAYER_JOIN,    player,  Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT,    player,  Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE,    player,  Priority.Normal, this);
        pm.registerEvent(Event.Type.VEHICLE_MOVE,   vehicle, Priority.Normal, this);
		
        log.log(Level.INFO, pdfFile.getName() + " version " + pdfFile.getVersion() + " has been loaded.");
    }
	
	
    public void initFiles() {
        File folder = this.getDataFolder();
        boolean result = true;
        if(!folder.exists()){
            if(!folder.mkdir()){
                result = false;
                log.log(Level.SEVERE, "[AIP] Could not create data folder!");
            }
        }
        File configFile = new File(getDataFolder(), "config.yml");
        log.log(Level.INFO, "[AIP] Config file: "+ getDataFolder() + "\\" + "config.yml" );
        try {
            if( !configFile.exists() )
                extractResourceTo("/config.yml", configFile.getPath());

            if( !configFile.canRead()) {
                result = false;
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "[AIP] Error creating data files: {0}", e.getMessage());
        }

        if(!result)
            log.log(Level.INFO, "[AIP] Failed to initialize data files!");
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
        // Save when the plugin is disabled.
        places.updateData();
    }
    
    public World getFirstWorld()
    {
    	return this.getServer().getWorlds().get(0);
    }
    
    public World getWorld(String worldname)
    {
    	return this.getServer().getWorld(worldname);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        // Set up the player.
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return false;
        }
        Player player = (Player) sender;
            
        if (command.getName().equalsIgnoreCase("mark")) { // The mark command.
            // Help to be displayed if no input
            if (args.length == 0) {
                player.sendMessage(ChatColor.WHITE + "Syntax is:");
                return false;
            }
            String[] sstring = arrayToString(args, " ", 0).split(":");
            if (sstring.length == 1) { // We want to mark a point even if there is no radius specified.
                // Mark the point.
                markPlace(player, sstring[0], getConfiguration().getInt("radius-default", 25), true);
                return true;
            }

            // Some var stuff.
            String name = sstring[0];
            String lprms = sstring[1];
            String[] parms = lprms.split(",");

            int rlimit = getConfiguration().getInt("radius-limit", 1000);
            int r = 0;
            if (parms.length > 0) { // Make sure there is a radius first.
                try {
                    r = Integer.parseInt(parms[0]);
                    // Check the radius.
                    if (r < 0 || r > rlimit) {
                        player.sendMessage(ChatColor.RED + "The radius must be between 0 and " + Integer.toString(rlimit) + "!");
                        return true;
                    }
                } catch ( NumberFormatException e ) {
                    player.sendMessage(ChatColor.RED + "Error in radius entry!");
                    player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius]");
                    player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius],[Y Start]-[Y End]");
                    player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius],[Y Radius]");
                    return true;
                }
            }
            if (parms.length == 1) {  //Only Radius entered.
                try {
                    // Mark the point.
                    markPlace(player, name, r, true);
                } catch ( NumberFormatException e ) {
                    player.sendMessage(ChatColor.RED + "Error in radius entry!");
                    player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius]");
                    return true;
                }
            } else if (parms.length == 2) {
                if (parms[1].contains("-")) { // Radius, Y begining, Y end entered.
                    String[] oparms = parms[1].split("-");
                    if (oparms.length == 2) {
                            try {
                                // Mark the point.
                                markPlace(player, name, r, Integer.parseInt(oparms[0]), Integer.parseInt(oparms[1]));
                            } catch ( NumberFormatException e ) {
                                player.sendMessage(ChatColor.RED + "Error in data entry!");
                                player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius],[Y Start]-[Y End]");
                                return true;
                            }
                    }
                } else { // Radius and Y radius entered.
                    try {
                        // Mark the point.
                        markPlace(player, name, r, Integer.parseInt(parms[1]));
                    } catch ( NumberFormatException e ) {
                        player.sendMessage(ChatColor.RED + "Error in data entry!");
                        player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius],[Y Radius]");
                        return true;
                    }
                }
            }			
        } else if (command.getName().equalsIgnoreCase("unmark")) { // The unmark command.
            // Run the command.
            unmarkPlace(player);
        } else if (command.getName().equalsIgnoreCase("nearest")) { // The nearest command.
            // Run the command.
            sendNearest(player);
        } else if ((command.getName().equalsIgnoreCase("who") || command.getName().equalsIgnoreCase("where")) & !getConfiguration().getBoolean("no-who",false) ) { // The who and where command.		
            // Run the command.
            sendWho(player);
        } else if (command.getName().equalsIgnoreCase("aip")) {
            // Version information
            player.sendMessage(ChatColor.GREEN + "This server is using " + pdfFile.getName() + " version " + pdfFile.getVersion());
        }
        return true;
    }

    // A function to convert a array to a string.
    public static String arrayToString(String[] a, String separator) {
        return arrayToString(a, separator, 0);
    }

    // A function to convert a array to a string.
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

    // Get the nearest place to the player.
    private Place nearestPlace(Player player)
    {
    	if (places == null)
    		return null;
    	return places.getNearest(player.getLocation(), player.getWorld().getName());
    }

    // Get the nearest place to the player that is in the radius range.
    private Place nearestPlaceInRange(Player player)
    {
    	if (places == null)
    		return null;
    	return places.getNearestRadius(player.getLocation(), player.getWorld().getName());
    }

    // Update the players location and send out the text if needed.
    public void updateCurrent(Player player)
    {
        // We want to check every 3 seconds.
    	if (times.containsKey(player) && System.currentTimeMillis() - times.get( player ) < 3000 )
    		return;

        // Grab the nearest place that is in range of the player.
    	Place place = nearestPlaceInRange(player);
        
        // DEBUG stuff.
        if (place != null && getConfiguration().getBoolean("debug",false)) player.sendMessage(ChatColor.WHITE + "Distance from:" + place.distance(player.getLocation()) + ". Y Axis:" + player.getLocation().getY() );

        // Grab the old location.
        Place old = null;
        if (current.containsKey(player))
            old = current.get(player);

        // Check if the player has entered a new location or the wilderness.
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
        // Put the players delay and his current place.
	times.put(player, System.currentTimeMillis());
        current.put(player, place);
    }

    // Remove the current place and time.
    public void removeCurrent(Player player)
    {
    	current.remove(player);
    	times.remove(player);
    }


    public void sendWho(Player player)
    {
    	Player[] players = getServer().getOnlinePlayers();
    	player.sendMessage(ChatColor.YELLOW + Integer.toString(players.length) + " " + getConfiguration().getString("who-text"));
    	for (Player p : players) {
            Place place = nearestPlaceInRange(p);

            if (place == null)
                player.sendMessage(ChatColor.WHITE + p.getName()+ " - wilderness");
            else
                player.sendMessage(ChatColor.WHITE + p.getName() + " - " + place.getName());
    	}
    }

// I will add this later
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

    // The /nearest command.
    public void sendNearest(Player player)
    {
    	Place nearest = nearestPlace(player);
    	if (nearest != null)
    		player.sendMessage(nearest.toString());
    }

    // One mark command that is not needed.
    public void markPlace(Player player, String name, int radius)
    {
         markPlace(player, name, radius, true);
    }

    // The mark command needed for /aip mark [Name]:[Radius]
    public void markPlace(Player player, String name, int radius, boolean ignoreY)
    {
        markPlace(player, player.getLocation(), name, radius, (ignoreY) ? -1 : radius, radius, radius);
    }

    // The mark command needed for /aip mark [Name]:[Radius],[Y Radius]
    public void markPlace(Player player, String name, int radius, int yRadius)
    {
        markPlace(player, player.getLocation(), name, radius, yRadius, radius, radius);
    }

    // The mark command needed for /aip mark [Name]:[Radius],[Y Start]-[Y End]
    public void markPlace(Player player, String name, int radius, int y, int yRadius)
    {
        Location loc = player.getLocation();
        loc.setY(y + (yRadius - y)/2);
        markPlace(player, loc, name, radius, (Math.abs(yRadius - y)/2), radius, radius);
    }

    // The mark command.
    public void markPlace(Player player, Location loc, String name, int rx, int ry, int rz, int radius)
    {
        // Check to see if only ops can use this command.
    	if (getConfiguration().getBoolean("ops-only-mark", false) && !player.isOp()) {
    		player.sendMessage(ChatColor.RED + "You must be a op to use this command!");
    		return;
    	}

        // Get the nearest point from the player to see if he is too close to another point.
    	Place nearest = nearestPlace(player);
    	if (nearest != null && nearest.distance( player.getLocation() ) < 5) {
    		player.sendMessage(ChatColor.RED + "Too close to " + nearest.toString() + "!");
    		return;
    	}

        // Make sure a name is supplied.
    	if (name == null || name.trim().equals( "" )) {
    		player.sendMessage(ChatColor.RED + "You must supply a name!");
    		return;
    	}

    	Place mark = null;
    	if ( radius != -1 )
                // Make a place with a radius.
    		mark = new Place(loc, radius, ry, player.getWorld().getName(), name, player.getDisplayName());
    	else
                // Make a place with a custom radius.
    		mark = new Place(loc, rx, ry, rz, player.getWorld().getName(), name, player.getDisplayName());

        // Add the place to the structure and save it.
    	places.getPlaces().add(mark);
        places.updateData();
    	player.sendMessage(ChatColor.BLUE + "marked " + mark.toString());

    	for (Player p : getServer().getOnlinePlayers())
    		updateCurrent(p);
    }

    public void unmarkPlace(Player player)
    {
        // Check to see if only ops can use this command.
    	if (getConfiguration().getBoolean("ops-only-unmark", false) && !player.isOp()) {
    		player.sendMessage(ChatColor.RED + "You must be a op to use this command!");
    		return;
    	}

        // Check to see if there is even a place to unmark.
    	Place nearest = nearestPlace(player);
        if (nearest == null) {
    		player.sendMessage(ChatColor.RED + "Nothing to unmark!");
    		return;
    	}

        // Make sure that the player is the owner so griefers don't remove other peoples places.
    	if (player.isOp() || player.getDisplayName().equals(nearest.getOwner()) || nearest.getOwner().equals("[none]") ) {
            // Remove the nearest place and save the file.
            places.getPlaces().remove(nearest);
            places.updateData();
            player.sendMessage(ChatColor.RED + "Unmarked " + nearest.toString());

            // Update all of the current players to reflect the change.
            for (Player p : getServer().getOnlinePlayers())
                    updateCurrent(p);
        } else {
            player.sendMessage(ChatColor.RED + "You can't unmark a point you don't own!");
        }
    }
    
}