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
package com.shatteredicedungeon.levels;

import com.shatteredicedungeon.Assets;
import com.shatteredicedungeon.Dungeon;
import com.shatteredicedungeon.DungeonTilemap;
import com.shatteredicedungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredicedungeon.levels.Room.Type;
import com.shatteredicedungeon.levels.painters.Painter;
import com.shatteredicedungeon.levels.traps.ConfusionTrap;
import com.shatteredicedungeon.levels.traps.ExplosiveTrap;
import com.shatteredicedungeon.levels.traps.FireTrap;
import com.shatteredicedungeon.levels.traps.FlashingTrap;
import com.shatteredicedungeon.levels.traps.FlockTrap;
import com.shatteredicedungeon.levels.traps.FrostTrap;
import com.shatteredicedungeon.levels.traps.GrippingTrap;
import com.shatteredicedungeon.levels.traps.GuardianTrap;
import com.shatteredicedungeon.levels.traps.LightningTrap;
import com.shatteredicedungeon.levels.traps.OozeTrap;
import com.shatteredicedungeon.levels.traps.ParalyticTrap;
import com.shatteredicedungeon.levels.traps.PitfallTrap;
import com.shatteredicedungeon.levels.traps.PoisonTrap;
import com.shatteredicedungeon.levels.traps.RockfallTrap;
import com.shatteredicedungeon.levels.traps.SpearTrap;
import com.shatteredicedungeon.levels.traps.SummoningTrap;
import com.shatteredicedungeon.levels.traps.TeleportationTrap;
import com.shatteredicedungeon.levels.traps.VenomTrap;
import com.shatteredicedungeon.levels.traps.WarpingTrap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class CavesLevel extends RegularLevel {

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
		
		viewDistance = 6;
	}
	
	@Override
	public String tilesTex() {
		return Assets.TILES_CAVES;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_CAVES;
	}
	
	protected boolean[] water() {
		return Patch.generate( feeling == Feeling.WATER ? 0.60f : 0.45f, 6 );
	}
	
	protected boolean[] grass() {
		return Patch.generate( feeling == Feeling.GRASS ? 0.55f : 0.35f, 3 );
	}

	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{ FireTrap.class, FrostTrap.class, PoisonTrap.class, SpearTrap.class, VenomTrap.class,
				ExplosiveTrap.class, FlashingTrap.class, GrippingTrap.class, ParalyticTrap.class, LightningTrap.class, RockfallTrap.class, OozeTrap.class,
				ConfusionTrap.class, FlockTrap.class, GuardianTrap.class, PitfallTrap.class, SummoningTrap.class, TeleportationTrap.class,
				WarpingTrap.class};
	}

	@Override
	protected float[] trapChances() {
		return new float[]{ 8, 8, 8, 8, 8,
				4, 4, 4, 4, 4, 4, 4,
				2, 2, 2, 2, 2, 2,
				1 };
	}
	
	@Override
	protected boolean assignRoomType() {
		super.assignRoomType();

		if (!Blacksmith.Quest.spawn( rooms ) && Dungeon.depth == 14)
			return false;

		return true;
	}
	
	@Override
	protected void decorate() {
		
		for (Room room : rooms) {
			if (room.type != Room.Type.STANDARD) {
				continue;
			}
			
			if (room.width() <= 3 || room.height() <= 3) {
				continue;
			}
			
			int s = room.square();
			
			if (Random.Int( s ) > 8) {
				int corner = (room.left + 1) + (room.top + 1) * WIDTH;
				if (map[corner - 1] == Terrain.WALL && map[corner - WIDTH] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
					traps.remove(corner);
				}
			}
			
			if (Random.Int( s ) > 8) {
				int corner = (room.right - 1) + (room.top + 1) * WIDTH;
				if (map[corner + 1] == Terrain.WALL && map[corner - WIDTH] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
					traps.remove(corner);
				}
			}
			
			if (Random.Int( s ) > 8) {
				int corner = (room.left + 1) + (room.bottom - 1) * WIDTH;
				if (map[corner - 1] == Terrain.WALL && map[corner + WIDTH] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
					traps.remove(corner);
				}
			}
			
			if (Random.Int( s ) > 8) {
				int corner = (room.right - 1) + (room.bottom - 1) * WIDTH;
				if (map[corner + 1] == Terrain.WALL && map[corner + WIDTH] == Terrain.WALL) {
					map[corner] = Terrain.WALL;
					traps.remove(corner);
				}
			}

			for (Room n : room.connected.keySet()) {
				if ((n.type == Room.Type.STANDARD || n.type == Room.Type.TUNNEL) && Random.Int( 3 ) == 0) {
					Painter.set( this, room.connected.get( n ), Terrain.EMPTY_DECO );
				}
			}
		}
		
		for (int i=WIDTH + 1; i < LENGTH - WIDTH; i++) {
			if (map[i] == Terrain.EMPTY) {
				int n = 0;
				if (map[i+1] == Terrain.WALL) {
					n++;
				}
				if (map[i-1] == Terrain.WALL) {
					n++;
				}
				if (map[i+WIDTH] == Terrain.WALL) {
					n++;
				}
				if (map[i-WIDTH] == Terrain.WALL) {
					n++;
				}
				if (Random.Int( 6 ) <= n) {
					map[i] = Terrain.EMPTY_DECO;
				}
			}
		}
		
		for (int i=0; i < LENGTH; i++) {
			if (map[i] == Terrain.WALL && Random.Int( 12 ) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		placeSign();
		
		if (Dungeon.bossLevel( Dungeon.depth + 1 )) {
			return;
		}
		
		for (Room r : rooms) {
			if (r.type == Type.STANDARD) {
				for (Room n : r.neigbours) {
					if (n.type == Type.STANDARD && !r.connected.containsKey( n )) {
						Rect w = r.intersect( n );
						if (w.left == w.right && w.bottom - w.top >= 5) {
							
							w.top += 2;
							w.bottom -= 1;
							
							w.right++;
							
							Painter.fill( this, w.left, w.top, 1, w.height(), Terrain.CHASM );
							
						} else if (w.top == w.bottom && w.right - w.left >= 5) {
							
							w.left += 2;
							w.right -= 1;
							
							w.bottom++;
							
							Painter.fill( this, w.left, w.top, w.width(), 1, Terrain.CHASM );
						}
					}
				}
			}
		}
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
		case Terrain.GRASS:
			return "Fluorescent moss";
		case Terrain.HIGH_GRASS:
			return "Fluorescent mushrooms";
		case Terrain.WATER:
			return "Freezing cold water.";
		default:
			return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
		case Terrain.ENTRANCE:
			return "The ladder leads up to the upper depth.";
		case Terrain.EXIT:
			return "The ladder leads down to the lower depth.";
		case Terrain.HIGH_GRASS:
			return "Huge mushrooms block the view.";
		case Terrain.WALL_DECO:
			return "A vein of some ore is visible on the wall. Gold?";
		case Terrain.BOOKSHELF:
			return "Who would need a bookshelf in a cave?";
		default:
			return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addCavesVisuals( this, visuals );
		return visuals;
	}
	
	public static void addCavesVisuals( Level level, Group group ) {
		for (int i=0; i < LENGTH; i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Vein( i ) );
			}
		}
	}
	
	private static class Vein extends Group {
		
		private int pos;
		
		private float delay;
		
		public Vein( int pos ) {
			super();
			
			this.pos = pos;
			
			delay = Random.Float( 2 );
		}
		
		@Override
		public void update() {
			
			if (visible = Dungeon.visible[pos]) {
				
				super.update();

				if ((delay -= Game.elapsed) <= 0) {

					//pickaxe can remove the ore, should remove the sparkling too.
					if (Dungeon.level.map[pos] != Terrain.WALL_DECO){
						kill();
						return;
					}
					
					delay = Random.Float();
					
					PointF p = DungeonTilemap.tileToWorld( pos );
					((Sparkle)recycle( Sparkle.class )).reset(
						p.x + Random.Float( DungeonTilemap.SIZE ),
						p.y + Random.Float( DungeonTilemap.SIZE ) );
				}
			}
		}
	}
	
	public static final class Sparkle extends PixelParticle {
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			left = lifespan = 0.5f;
		}
		
		@Override
		public void update() {
			super.update();
			
			float p = left / lifespan;
			size( (am = p < 0.5f ? p * 2 : (1 - p) * 2) * 2 );
		}
	}
}