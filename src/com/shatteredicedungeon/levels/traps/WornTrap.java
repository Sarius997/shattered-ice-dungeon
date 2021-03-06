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
package com.shatteredicedungeon.levels.traps;

import com.shatteredicedungeon.sprites.TrapSprite;
import com.shatteredicedungeon.utils.GLog;

public class WornTrap extends Trap {

	{
		name = "Worn out trap";
		color = TrapSprite.BLACK;
		shape = TrapSprite.DOTS;
	}

	@Override
	public Trap hide() {
		//this one can't be hidden
		return reveal();
	}

	@Override
	public void activate() {
		GLog.i("nothing happens..");
	}

	@Override
	public String desc() {
		return "Due to age and possibly poor workmanship, " +
				"it looks like this trap has worn to the point where it won't do anything when triggered.";
	}
}
