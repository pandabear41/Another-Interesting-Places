package net.onlyway.AIP;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AnotherInterestPlayer extends PlayerListener {

    private final AnotherInterest plugin;

    public AnotherInterestPlayer(final AnotherInterest plugin)
    {
        this.plugin = plugin;
    }
    
	
	@Override
    public void onPlayerJoin( PlayerJoinEvent event )
    {
        plugin.updateCurrent( event.getPlayer() );
    }

    @Override
    public void onPlayerQuit( PlayerQuitEvent event )
    {
        plugin.removeCurrent( event.getPlayer() );
    }
    
    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {
    	plugin.updateCurrent(event.getPlayer());
    }

}