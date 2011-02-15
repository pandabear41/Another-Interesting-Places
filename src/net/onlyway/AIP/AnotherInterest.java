package net.onlyway.AIP;

import java.io.File;
import java.util.HashMap;
import net.omnivr.olib.Util;
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
    public static final String CONFIG_FILE = "config.txt";

    private final AnotherInterestPlayer player = new AnotherInterestPlayer(this);
    private final AnotherInterestVehicle vehicle = new AnotherInterestVehicle(this);
    private final Places places = new Places(this);
    
    private HashMap<Player,Place> current = new HashMap<Player,Place>();
    private HashMap<Player,Long> times = new HashMap<Player,Long>();

    public AnotherInterest(PluginLoader loader, Server server, PluginDescriptionFile pdf, File dir, File plugin, ClassLoader classLoader)
    {
        super( loader, server, pdf, dir, plugin, classLoader );
    	places.updateData();
    }

    @Override
    public void onEnable()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN,    player,  Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT,    player,  Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE,    player,  Priority.Normal, this);
        pm.registerEvent(Event.Type.VEHICLE_MOVE,   vehicle, Priority.Normal, this);

        // Thanks Zoot. 
        getDataFolder().mkdirs(); // Make sure dir exists
        File config_file = new File(getDataFolder(), "config.yml");
        if (!config_file.isFile()) {
            Util.extractResourceTo("/config.yml", config_file.getPath());
        }

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " has been loaded.");
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
                //player.sendMessage(ChatColor.RED + "OR /aip mark [Name]:[Radius],[Height]");
                player.sendMessage(ChatColor.RED + "OR /aip who [Player Name]");
                player.sendMessage(ChatColor.RED + "mark - Mark a position on the map with a name.");
                player.sendMessage(ChatColor.RED + "unmark - Unmark the nearest marked position");
                player.sendMessage(ChatColor.RED + "nearest - Displays the nearest marked position");
                player.sendMessage(ChatColor.RED + "who - List connected players and the areas they are in");
                return false;
            }
           
            if (args[0].equalsIgnoreCase("mark")) {
                int r = -1;
                int d = -1;
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
                        r=Integer.parseInt(parms[0]);
                    } catch ( NumberFormatException e ) {
                        player.sendMessage(ChatColor.RED + "Error in radius entry!");
                        player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius]");
                        return false;
                    }
                } else if (parms.length == 2) { //Radius and depth entered.
                    try {
                        r=Integer.parseInt(parms[0]);
                        d=Integer.parseInt(parms[1]);
                    } catch ( NumberFormatException e ) {
                        player.sendMessage(ChatColor.RED + "Error in data entry!");
                        player.sendMessage(ChatColor.RED + "USE /aip mark [Name]:[Radius],[Depth Radius]");
                        return false;
                    }
                }

                markPlace(player, name, r, d);
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
        //if (place != null) player.sendMessage(ChatColor.WHITE + "Distance from:" + place.distance(player.getLocation()));
        if (place != null) player.sendMessage(ChatColor.WHITE + "Distance from:" + place.distance(player.getLocation()) + ". Y Axis:" + player.getLocation().getY() + ". Delta y:" + place.getDepth() );
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
         markPlace(player, name, radius, -1);
    }

    public void markPlace(Player player, String name, int radius, int depth)
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
    	if ( radius > 0 )
    		mark = new Place(player.getLocation(), radius, (int) player.getWorld().getId(), name, player.getDisplayName());
    	else
    		player.sendMessage(ChatColor.RED + "Error: Feature not implemented");
        mark.setDepth(depth);
        
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
    	if (player.isOp() || player.getDisplayName().equals(nearest.getOwner())) {
            places.getPlaces().remove(nearest);
            places.updateData();
            player.sendMessage(ChatColor.RED + "Unmarked " + nearest.toString());

            for (Player p : getServer().getOnlinePlayers())
                    updateCurrent(p);
        }
    }
    
    
    
}