/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2015 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.shatteredicedungeon.levels.painters;

import com.shatteredicedungeon.Dungeon;
import com.shatteredicedungeon.levels.Level;
import com.shatteredicedungeon.levels.Room;
import com.shatteredicedungeon.levels.Terrain;

public class ExitPainter extends Painter {

	public static void paint(Level level, Room room) {

		fill(level, room, Terrain.WALL);
		fill(level, room, 1, Terrain.EMPTY);

		for (Room.Door door : room.connected.values()) {
			door.set(Room.Door.Type.REGULAR);
		}
		if (room.type == Room.Type.EXIT) {
			level.exit = room.random(1);
			set(level, level.exit, Terrain.EXIT);
		} else if (room.type == Room.Type.EXIT2) {
			level.exit2 = room.random(1);
			set(level, level.exit2, Terrain.EXIT);
		}
		if (room.type == Room.Type.EXIT && Dungeon.depth == 21) {
			do {
				level.exit2 = room.random(1);
			} while (level.exit2 == level.exit
			// Modify this if the distance between the two exits should be
			// bigger
					|| Level.distance(level.exit, level.exit2) < 2);
			set(level, level.exit2, Terrain.EXIT);
		}
	}

}
