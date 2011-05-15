Another Interesting Places <Fork of Interesting Places>
Copyright (c) 2011 Pandabear41 <http://onlywaysoftware.com/>

Parts of the Code was taken from: grandseiken @ 
<http://forums.bukkit.org/threads/info-interesting-places-1-2-name-areas-in-the-world.1463/>

Another Interesting Places allows you to mark a point in the world and give it
a name. So that when travelers are passing by they will see that someones 
creation it nearby.

To use, type "/mark", "/unmark", "/nearest", or "/who" in chat
to use the marking feature.


--COMMAND SYNTAX--

*******************************************************************************
COMMAND:
/mark [Name]
<Makes the point the default radius>
or
/mark [Name]:[Radius]
or
/mark [Name]:[Radius],[Y Radius]
or 
/mark [Name]:[Radius],[Y Start]-[Y End]

DESCRIPTION:
Mark sets a point and radius on the map that other players will see.
*******************************************************************************
COMMAND:
/unmark

DESCRIPTION:
Unmark removes the nearest point.
*******************************************************************************
COMMAND:
/nearest

DESCRIPTION:
Nearest displays the nerest point to the player and the coordinates of the 
marked point.
*******************************************************************************
COMMAND:
/who
OR
/where

DESCRIPTION:
Who displays the logged in players and the zones that they are in.
*******************************************************************************
-- TODO --

Add 
/aip mark [Name]:[X],[Y],[Z],[Dx],[Dy],[Dz]

Add 
/aip who [Name]


-- CHANGELOG --

v0.8 - 5/14/2011
    * [CHANGE] Now using the world name rather than the ID.

v0.7 - 4/28/2011
    * [ADD] Who command.
    * [ADD] New data conversion for v1.2
    * [CHANGE] The commands don't need /aip, just use the original commands.
    * [FIX] Breaks caused by Bukkit update.
    * [FIX] Changed config file generation.

v0.6 - 2/26/2011
    * [FIX] Problems from a recent Bukkit update.

v0.5 - 2/20/2011
    * [ADD] If there is no radius entered it defaults to one in the config.
    * [ADD] Config to allow only ops to use the commands.
    * [FIX] It now checks if the radius is negative or out of range (specified in config)
    * [FIX] Calculations in determining the y distance.

v0.4 - 2/16/2011
    * Initial release.


-- LICENSE --
Except where otherwise stated, this work is licensed under the Creative Commons
Attribution-NonCommercial-ShareAlike 3.0 Unported License. To view a copy of
this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send a
letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
California, 94105, USA.
