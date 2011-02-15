package net.onlyway.AIP;

import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class AnotherInterestVehicle extends VehicleListener {

    private final AnotherInterest plugin;

    public AnotherInterestVehicle(final AnotherInterest plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public void onVehicleMove(VehicleMoveEvent event)
    {
    	if (event.getVehicle().getPassenger() instanceof Player)
    		plugin.updateCurrent((Player)event.getVehicle().getPassenger());
    }
    
}