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
package com.shatteredicedungeon.items.scrolls;

import com.shatteredicedungeon.Assets;
import com.shatteredicedungeon.Dungeon;
import com.shatteredicedungeon.ResultDescriptions;
import com.shatteredicedungeon.actors.buffs.Blindness;
import com.shatteredicedungeon.actors.buffs.Buff;
import com.shatteredicedungeon.actors.buffs.Invisibility;
import com.shatteredicedungeon.actors.buffs.Paralysis;
import com.shatteredicedungeon.actors.mobs.Mob;
import com.shatteredicedungeon.levels.Level;
import com.shatteredicedungeon.scenes.GameScene;
import com.shatteredicedungeon.utils.GLog;
import com.shatteredicedungeon.utils.Utils;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ScrollOfPsionicBlast extends Scroll {

	{
		name = "Scroll of Psionic Blast";
		initials = "PB";

		bones = true;
	}
	
	@Override
	protected void doRead() {
		
		GameScene.flash( 0xFFFFFF );
		
		Sample.INSTANCE.play( Assets.SND_BLAST );
		Invisibility.dispel();
		
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (Level.fieldOfView[mob.pos]) {
				mob.damage(mob.HT, this );
			}
		}

		curUser.damage(Math.max(curUser.HT/5, curUser.HP/2), this);
		Buff.prolong( curUser, Paralysis.class, Random.Int( 4, 6 ) );
		Buff.prolong( curUser, Blindness.class, Random.Int( 6, 9 ) );
		Dungeon.observe();
		
		setKnown();
		
		curUser.spendAndNext( TIME_TO_READ );

		if (!curUser.isAlive()) {
			Dungeon.fail( Utils.format(ResultDescriptions.ITEM, name ));
			GLog.n("The Psionic Blast tears your mind apart...");
		}
	}
	
	@Override
	public String desc() {
		return
			"This scroll contains destructive energy that can be psionically channeled to tear apart " +
			"the minds of all visible creatures. The power unleashed by the scroll will also temporarily " +
			"blind, stun, and seriously harm the reader.";
	}
	
	@Override
	public int price() {
		return isKnown() ? 80 * quantity : super.price();
	}
}
