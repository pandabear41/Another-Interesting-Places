package net.onlyway.AIP;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class AnotherInterestPlayer extends PlayerListener {

    private final AnotherInterest plugin;

    public AnotherInterestPlayer( final AnotherInterest plugin )
    {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerJoin( PlayerEvent event )
    {
        plugin.updateCurrent( event.getPlayer() );
    }
    
    @Override
    public void onPlayerQuit( PlayerEvent event )
    {
    	plugin.removeCurrent( event.getPlayer() );
    }

    @Override
    public void onPlayerCommand( PlayerChatEvent event )
    {
    	if ( event.isCancelled() )
    		return;
    	String[] args = event.getMessage().split( " ", 2 );
    	boolean b = false;
    	
    	if ( ( args[ 0 ].equalsIgnoreCase( "/who" ) && !plugin.getConfig().disableWho() ) || args[ 0 ].equalsIgnoreCase( "/where" ) ) {
    		if ( args.length <= 1 )
    			plugin.sendWho( event.getPlayer() );
    		else
    			plugin.sendWho( event.getPlayer(), args[ 1 ] );
    		b = true;
    	}
    	
    	if ( args[ 0 ].equalsIgnoreCase( "/nearest" ) ) {
    		plugin.sendNearest( event.getPlayer() );
    		b = true;
    	}
    	
    	if ( args[ 0 ].equalsIgnoreCase( "/mark" ) ) {
    		if ( args.length <= 1 )
    			plugin.markPlace( event.getPlayer(), null, -1, -1, -1, -1 );
    		else {
    			String[] split = args[ 1 ].split( " " );
    			int r = -1;
    			int x = -1;
    			int y = -1;
    			int z = -1;
    			boolean a = true;
    			int n = 0;
    			while ( a ) {
    				a = false;
    				if ( split[ n ].length() >= 3 && split[ n ].substring( 0, 2 ).equals( "r:" ) ) {
        				try {
        					r = Integer.parseInt( split[ n ].substring( 2 ) );
        					a = true;
        					n++;
        				}
        				catch ( NumberFormatException e ) {
        				}
    				}
    				if ( split[ n ].length() >= 3 && split[ n ].substring( 0, 2 ).equals( "x:" ) ) {
        				try {
        					x = Integer.parseInt( split[ n ].substring( 2 ) );
        					a = true;
        					n++;
        				}
        				catch ( NumberFormatException e ) {
        				}
    				}
    				if ( split[ n ].length() >= 3 && split[ n ].substring( 0, 2 ).equals( "y:" ) ) {
        				try {
        					y = Integer.parseInt( split[ n ].substring( 2 ) );
        					a = true;
        					n++;
        				}
        				catch ( NumberFormatException e ) {
        				}
    				}
    				if ( split[ n ].length() >= 3 && split[ n ].substring( 0, 2 ).equals( "z:" ) ) {
        				try {
        					z = Integer.parseInt( split[ n ].substring( 2 ) );
        					a = true;
        					n++;
        				}
        				catch ( NumberFormatException e ) {
        				}
    				}
    			}
    			
    			plugin.markPlace( event.getPlayer(), AnotherInterest.safeMessage( args[ 1 ].split( " ", n + 1 )[ n ].replaceAll( "##", "§" ) ), r, x, y, z );
    		}
    		b = true;
    	}
    	
    	if ( args[ 0 ].equalsIgnoreCase( "/unmark" ) ) {
    		plugin.unmarkPlace( event.getPlayer() );
    		b = true;
    	}
    	
    	event.setCancelled( b );
    }
    
    @Override
    public void onPlayerMove( PlayerMoveEvent event )
    {
    	plugin.updateCurrent( event.getPlayer() );
    }

}