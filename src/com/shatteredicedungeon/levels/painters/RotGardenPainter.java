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
import com.shatteredicedungeon.actors.mobs.Mob;
import com.shatteredicedungeon.actors.mobs.RotHeart;
import com.shatteredicedungeon.actors.mobs.RotLasher;
import com.shatteredicedungeon.items.keys.IronKey;
import com.shatteredicedungeon.levels.Level;
import com.shatteredicedungeon.levels.Room;
import com.shatteredicedungeon.levels.Terrain;
import com.watabou.utils.Random;

public class RotGardenPainter extends Painter {

	public static void paint( Level level, Room room ) {

		Room.Door entrance = room.entrance();
		entrance.set(Room.Door.Type.LOCKED);
		level.addItemToSpawn(new IronKey(Dungeon.depth));

		fill(level, room, Terrain.WALL);
		fill(level, room, 1, Terrain.GRASS);


		int heartX = Random.IntRange(room.left+1, room.right-1);
		int heartY = Random.IntRange(room.top+1, room.bottom-1);

		if (entrance.x == room.left) {
			heartX = room.right - 1;
		} else if (entrance.x == room.right) {
			heartX = room.left + 1;
		} else if (entrance.y == room.top) {
			heartY = room.bottom - 1;
		} else if (entrance.y == room.bottom) {
			heartY = room.top + 1;
		}

		placePlant(level, heartX + heartY * Level.WIDTH, new RotHeart());

		int lashers = ((room.right-room.left-1)*(room.bottom-room.top-1))/8;

		for (int i = 1; i <= lashers; i++){
			int pos;
			do {
				pos = room.random();
			} while (!validPlantPos(level, pos));
			placePlant(level, pos, new RotLasher());
		}
	}

	private static boolean validPlantPos(Level level, int pos){
		if (level.map[pos] != Terrain.GRASS){
			return false;
		}

		for (int i : Level.NEIGHBOURS9){
			if (level.findMob(pos+i) != null){
				return false;
			}
		}

		return true;
	}

	private static void placePlant(Level level, int pos, Mob plant){
		plant.pos = pos;
		level.mobs.add( plant );

		for(int i : Level.NEIGHBOURS8) {
			if (level.map[pos + i] == Terrain.GRASS){
				set(level, pos + i, Terrain.HIGH_GRASS);
			}
		}
	}
}
