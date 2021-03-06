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
package com.shatteredicedungeon.scenes;

import java.io.IOException;
import java.util.ArrayList;

import com.shatteredicedungeon.Assets;
import com.shatteredicedungeon.Badges;
import com.shatteredicedungeon.Dungeon;
import com.shatteredicedungeon.DungeonTilemap;
import com.shatteredicedungeon.FogOfWar;
import com.shatteredicedungeon.ShatteredIceDungeon;
import com.shatteredicedungeon.Statistics;
import com.shatteredicedungeon.actors.Actor;
import com.shatteredicedungeon.actors.blobs.Blob;
import com.shatteredicedungeon.actors.mobs.Mob;
import com.shatteredicedungeon.effects.BannerSprites;
import com.shatteredicedungeon.effects.BlobEmitter;
import com.shatteredicedungeon.effects.EmoIcon;
import com.shatteredicedungeon.effects.Flare;
import com.shatteredicedungeon.effects.FloatingText;
import com.shatteredicedungeon.effects.Ripple;
import com.shatteredicedungeon.effects.SpellSprite;
import com.shatteredicedungeon.items.Heap;
import com.shatteredicedungeon.items.Honeypot;
import com.shatteredicedungeon.items.Item;
import com.shatteredicedungeon.items.bags.PotionBandolier;
import com.shatteredicedungeon.items.bags.ScrollHolder;
import com.shatteredicedungeon.items.bags.SeedPouch;
import com.shatteredicedungeon.items.bags.WandHolster;
import com.shatteredicedungeon.items.potions.Potion;
import com.shatteredicedungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredicedungeon.levels.Level;
import com.shatteredicedungeon.levels.RegularLevel;
import com.shatteredicedungeon.levels.features.Chasm;
import com.shatteredicedungeon.levels.traps.Trap;
import com.shatteredicedungeon.plants.Plant;
import com.shatteredicedungeon.sprites.CharSprite;
import com.shatteredicedungeon.sprites.DiscardedItemSprite;
import com.shatteredicedungeon.sprites.HeroSprite;
import com.shatteredicedungeon.sprites.ItemSprite;
import com.shatteredicedungeon.sprites.PlantSprite;
import com.shatteredicedungeon.sprites.TrapSprite;
import com.shatteredicedungeon.ui.AttackIndicator;
import com.shatteredicedungeon.ui.Banner;
import com.shatteredicedungeon.ui.BusyIndicator;
import com.shatteredicedungeon.ui.CustomTileVisual;
import com.shatteredicedungeon.ui.GameLog;
import com.shatteredicedungeon.ui.HealthIndicator;
import com.shatteredicedungeon.ui.LootIndicator;
import com.shatteredicedungeon.ui.QuickSlotButton;
import com.shatteredicedungeon.ui.ResumeIndicator;
import com.shatteredicedungeon.ui.StatusPane;
import com.shatteredicedungeon.ui.Toast;
import com.shatteredicedungeon.ui.Toolbar;
import com.shatteredicedungeon.ui.Window;
import com.shatteredicedungeon.utils.GLog;
import com.shatteredicedungeon.windows.WndBag;
import com.shatteredicedungeon.windows.WndBag.Mode;
import com.shatteredicedungeon.windows.WndGame;
import com.shatteredicedungeon.windows.WndHero;
import com.shatteredicedungeon.windows.WndInfoCell;
import com.shatteredicedungeon.windows.WndInfoItem;
import com.shatteredicedungeon.windows.WndInfoMob;
import com.shatteredicedungeon.windows.WndInfoPlant;
import com.shatteredicedungeon.windows.WndInfoTrap;
import com.shatteredicedungeon.windows.WndMessage;
import com.shatteredicedungeon.windows.WndStory;
import com.shatteredicedungeon.windows.WndTradeItem;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class GameScene extends PixelScene {
	
	private static final String TXT_WELCOME			= "Welcome to the level %d of Pixel Dungeon!";
	private static final String TXT_WELCOME_BACK	= "Welcome back to the level %d of Pixel Dungeon!";
	
	private static final String TXT_CHASM	= "Your steps echo across the dungeon.";
	private static final String TXT_WATER	= "You hear water splashing around you.";
	private static final String TXT_GRASS	= "The smell of vegetation is thick in the air.";
	private static final String TXT_DARK	= "You can hear enemies moving in the darkness...";
	private static final String TXT_SECRETS	= "The atmosphere hints that this floor hides many secrets.";
	
	static GameScene scene;

	private SkinnedBlock water;
	private SkinnedBlock snow;
	private SkinnedBlock snow2;
	private DungeonTilemap tiles;
	private FogOfWar fog;
	private HeroSprite hero;
	
	private GameLog log;
	
	private BusyIndicator busy;
	
	private static CellSelector cellSelector;
	
	private Group terrain;
	private Group customTiles;
	private Group levelVisuals;
	private Group ripples;
	private Group plants;
	private Group traps;
	private Group heaps;
	private Group mobs;
	private Group emitters;
	private Group effects;
	private Group gases;
	private Group spells;
	private Group statuses;
	private Group emoicons;
	
	private Toolbar toolbar;
	private Toast prompt;

	private AttackIndicator attack;
	private LootIndicator loot;
	private ResumeIndicator resume;
	
	@Override
	public void create() {
		
		Music.INSTANCE.play( Assets.TUNE, true );
		Music.INSTANCE.volume( ShatteredIceDungeon.musicVol()/10f );

		ShatteredIceDungeon.lastClass(Dungeon.hero.heroClass.ordinal());
		
		super.create();
		Camera.main.zoom( GameMath.gate(minZoom, defaultZoom + ShatteredIceDungeon.zoom(), maxZoom));
		
		scene = this;

		terrain = new Group();
		add( terrain );

		water = new SkinnedBlock(
			Level.WIDTH * DungeonTilemap.SIZE,
			Level.HEIGHT * DungeonTilemap.SIZE,
			Dungeon.level.waterTex() );
		terrain.add( water );
		

		if (Dungeon.depth <= 5) {
			snow = new SkinnedBlock(Level.WIDTH * DungeonTilemap.SIZE,
					Level.HEIGHT * DungeonTilemap.SIZE, Assets.SNOW_ALL);
			add(snow);
		
			snow2 = new SkinnedBlock(Level.WIDTH * DungeonTilemap.SIZE,
					Level.HEIGHT * DungeonTilemap.SIZE, Assets.SNOW_ALL);
			add(snow);
			add(snow2);
		}

		ripples = new Group();
		terrain.add( ripples );
		
		tiles = new DungeonTilemap();
		terrain.add( tiles );

		customTiles = new Group();
		terrain.add(customTiles);

		for( CustomTileVisual visual : Dungeon.level.customTiles){
			addCustomTile(visual.create());
		}
		
		levelVisuals = Dungeon.level.addVisuals();
		add(levelVisuals);

		traps = new Group();
		add(traps);

		int size = Dungeon.level.traps.size();
		for (int i=0; i < size; i++) {
			addTrapSprite( Dungeon.level.traps.valueAt( i ) );
		}
		
		plants = new Group();
		add( plants );
		
		size = Dungeon.level.plants.size();
		for (int i=0; i < size; i++) {
			addPlantSprite( Dungeon.level.plants.valueAt( i ) );
		}
		
		heaps = new Group();
		add( heaps );
		
		size = Dungeon.level.heaps.size();
		for (int i=0; i < size; i++) {
			addHeapSprite( Dungeon.level.heaps.valueAt( i ) );
		}
		
		emitters = new Group();
		effects = new Group();
		emoicons = new Group();
		
		mobs = new Group();
		add( mobs );
		
		for (Mob mob : Dungeon.level.mobs) {
			addMobSprite( mob );
			if (Statistics.amuletObtained) {
				mob.beckon( Dungeon.hero.pos );
			}
		}
		
		add( emitters );
		add( effects );
		
		gases = new Group();
		add( gases );
		
		for (Blob blob : Dungeon.level.blobs.values()) {
			blob.emitter = null;
			addBlobSprite( blob );
		}

		fog = new FogOfWar( Level.WIDTH, Level.HEIGHT );
		fog.updateVisibility( Dungeon.visible, Dungeon.level.visited, Dungeon.level.mapped );
		add( fog );

		brightness(ShatteredIceDungeon.brightness());

		spells = new Group();
		add( spells );
		
		statuses = new Group();
		add( statuses );

		add( emoicons );
		
		hero = new HeroSprite();
		hero.place( Dungeon.hero.pos );
		hero.updateArmor();
		mobs.add( hero );

		add( new HealthIndicator() );
		
		add( cellSelector = new CellSelector( tiles ) );
		
		StatusPane sb = new StatusPane();
		sb.camera = uiCamera;
		sb.setSize( uiCamera.width, 0 );
		add( sb );
		
		toolbar = new Toolbar();
		toolbar.camera = uiCamera;
		toolbar.setRect( 0,uiCamera.height - toolbar.height(), uiCamera.width, toolbar.height() );
		add( toolbar );
		
		attack = new AttackIndicator();
		attack.camera = uiCamera;
		add( attack );

		loot = new LootIndicator();
		loot.camera = uiCamera;
		add( loot );

		resume = new ResumeIndicator();
		resume.camera = uiCamera;
		add( resume );

		log = new GameLog();
		log.camera = uiCamera;
		add( log );

		layoutTags();
		
		if (Dungeon.depth < Statistics.deepestFloor) {
			GLog.i(TXT_WELCOME_BACK, Dungeon.depth);
		} else {
			GLog.i(TXT_WELCOME, Dungeon.depth);
			if (InterlevelScene.mode == InterlevelScene.Mode.DESCEND) Sample.INSTANCE.play(Assets.SND_DESCEND);
		}

		switch (Dungeon.level.feeling) {
		case CHASM:
			GLog.w( TXT_CHASM );
			break;
		case WATER:
			GLog.w( TXT_WATER );
			break;
		case GRASS:
			GLog.w( TXT_GRASS );
			break;
		case DARK:
			GLog.w( TXT_DARK );
			break;
		default:
		}
		if (Dungeon.level instanceof RegularLevel &&
			((RegularLevel)Dungeon.level).secretDoors > Random.IntRange( 3, 4 )) {
			GLog.w( TXT_SECRETS );
		}

		busy = new BusyIndicator();
		busy.camera = uiCamera;
		busy.x = 1;
		busy.y = sb.bottom() + 1;
		add( busy );
		
		switch (InterlevelScene.mode) {
		case RESURRECT:
			ScrollOfTeleportation.appear( Dungeon.hero, Dungeon.level.entrance );
			new Flare( 8, 32 ).color( 0xFFFF66, true ).show( hero, 2f ) ;
			break;
		case RETURN:
			ScrollOfTeleportation.appear(  Dungeon.hero, Dungeon.hero.pos );
			break;
		case FALL:
			Chasm.heroLand();
			break;
		case DESCEND:
		case WANDERING_DOWN: // should be shown here
			switch (Dungeon.depth) {
			case 1:
				WndStory.showChapter( WndStory.ID_SEWERS );
				break;
			case 6:
				WndStory.showChapter( WndStory.ID_PRISON );
				break;
			case 11:
				WndStory.showChapter( WndStory.ID_CAVES );
				break;
			case 16:
				WndStory.showChapter( WndStory.ID_METROPOLIS );
				break;
			case 22:
				WndStory.showChapter( WndStory.ID_HALLS );
				break;
			}
			if (Dungeon.hero.isAlive() && Dungeon.depth != 22) {
				Badges.validateNoKilling();
			}
			break;
		default:
		}
		InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;

		ArrayList<Item> dropped = Dungeon.droppedItems.get( Dungeon.depth );
		if (dropped != null) {
			for (Item item : dropped) {
				int pos = Dungeon.level.randomRespawnCell();
				if (item instanceof Potion) {
					((Potion)item).shatter( pos );
				} else if (item instanceof Plant.Seed) {
					Dungeon.level.plant( (Plant.Seed)item, pos );
				} else if (item instanceof Honeypot) {
					Dungeon.level.drop(((Honeypot) item).shatter(null, pos), pos);
				} else {
					Dungeon.level.drop( item, pos );
				}
			}
			Dungeon.droppedItems.remove( Dungeon.depth );
		}

		Dungeon.hero.next();

		Camera.main.target = hero;
		fadeIn();
	}
	
	public void destroy() {
		
		freezeEmitters = false;

		scene = null;
		Badges.saveGlobal();
		
		super.destroy();
	}
	
	@Override
	public synchronized void pause() {
		try {
			Dungeon.saveAll();
			Badges.saveGlobal();
		} catch (IOException e) {
			//
		}
	}

	@Override
	public synchronized void update() {
		if (Dungeon.hero == null) {
			return;
		}

		super.update();

		if (!freezeEmitters)
			water.offset(0, -5 * Game.elapsed);

		if (Dungeon.depth <= 5 && !freezeEmitters) {
//		if (!freezeEmitters) {
			snow.offset(1 * Game.elapsed, -6 * Game.elapsed);
			snow2.offset(2 * Game.elapsed, -10 * Game.elapsed);
		}
		
		Actor.process();
		
		if (Dungeon.hero.ready && Dungeon.hero.paralysed == 0) {
			log.newLine();
		}

		if (tagAttack != attack.active || tagLoot != loot.visible || tagResume != resume.visible) {

			boolean atkAppearing = attack.active && !tagAttack;
			boolean lootAppearing = loot.visible && !tagLoot;
			boolean resAppearing = resume.visible && !tagResume;

			tagAttack = attack.active;
			tagLoot = loot.visible;
			tagResume = resume.visible;

			if (atkAppearing || lootAppearing || resAppearing)
				layoutTags();
		}

		cellSelector.enable(Dungeon.hero.ready);
	}

	private boolean tagAttack    = false;
	private boolean tagLoot        = false;
	private boolean tagResume    = false;

	public static void layoutTags() {

		if (scene == null) return;

		float tagLeft = ShatteredIceDungeon.flipTags() ? 0 : uiCamera.width - scene.attack.width();

		if (ShatteredIceDungeon.flipTags()) {
			scene.log.setRect(scene.attack.width(), scene.toolbar.top(), uiCamera.width - scene.attack.width(), 0);
		} else {
			scene.log.setRect(0, scene.toolbar.top(), uiCamera.width - scene.attack.width(),  0 );
		}

		float pos = scene.toolbar.top();

		if (scene.tagAttack){
			scene.attack.setPos( tagLeft, pos - scene.attack.height());
			scene.attack.flip(tagLeft == 0);
			pos = scene.attack.top();
		}

		if (scene.tagLoot) {
			scene.loot.setPos( tagLeft, pos - scene.loot.height() );
			scene.loot.flip(tagLeft == 0);
			pos = scene.loot.top();
		}

		if (scene.tagResume) {
			scene.resume.setPos( tagLeft, pos - scene.resume.height() );
			scene.resume.flip(tagLeft == 0);
		}
	}
	
	@Override
	protected void onBackPressed() {
		if (!cancel()) {
			add( new WndGame() );
		}
	}
	
	@Override
	protected void onMenuPressed() {
		if (Dungeon.hero.ready) {
			selectItem( null, WndBag.Mode.ALL, null );
		}
	}
	
	public void brightness( int value ) {
		float shift;
		if (value >= 0)
			shift = value/2f;
		else
			shift = value/3f;

		fog.am = 1f + shift;
		fog.aa = 0f - shift;
	}

	public void addCustomTile( CustomTileVisual visual){
		customTiles.add( visual.create() );
	}
	
	private void addHeapSprite( Heap heap ) {
		ItemSprite sprite = heap.sprite = (ItemSprite)heaps.recycle( ItemSprite.class );
		sprite.revive();
		sprite.link( heap );
		heaps.add( sprite );
	}
	
	private void addDiscardedSprite( Heap heap ) {
		heap.sprite = (DiscardedItemSprite)heaps.recycle( DiscardedItemSprite.class );
		heap.sprite.revive();
		heap.sprite.link( heap );
		heaps.add( heap.sprite );
	}
	
	private void addPlantSprite( Plant plant ) {
		(plant.sprite = (PlantSprite)plants.recycle( PlantSprite.class )).reset( plant );
	}

	private void addTrapSprite( Trap trap ) {
		(trap.sprite = (TrapSprite)traps.recycle( TrapSprite.class )).reset( trap );
		trap.sprite.visible = trap.visible;
	}
	
	private void addBlobSprite( final Blob gas ) {
		if (gas.emitter == null) {
			gases.add( new BlobEmitter( gas ) );
		}
	}
	
	private void addMobSprite( Mob mob ) {
		CharSprite sprite = mob.sprite();
		sprite.visible = Dungeon.visible[mob.pos];
		mobs.add( sprite );
		sprite.link( mob );
	}
	
	private void prompt( String text ) {
		
		if (prompt != null) {
			prompt.killAndErase();
			prompt = null;
		}
		
		if (text != null) {
			prompt = new Toast( text ) {
				@Override
				protected void onClose() {
					cancel();
				}
			};
			prompt.camera = uiCamera;
			prompt.setPos( (uiCamera.width - prompt.width()) / 2, uiCamera.height - 60 );
			add( prompt );
		}
	}
	
	private void showBanner( Banner banner ) {
		banner.camera = uiCamera;
		banner.x = (uiCamera.width - banner.width) / 2 ;
		banner.y = (uiCamera.height - banner.height) / 3 ;
		add( banner );
	}
	
	// -------------------------------------------------------

	public static void add( Plant plant ) {
		if (scene != null) {
			scene.addPlantSprite( plant );
		}
	}

	public static void add( Trap trap ) {
		if (scene != null) {
			scene.addTrapSprite( trap );
		}
	}
	
	public static void add( Blob gas ) {
		Actor.add( gas );
		if (scene != null) {
			scene.addBlobSprite( gas );
		}
	}
	
	public static void add( Heap heap ) {
		if (scene != null) {
			scene.addHeapSprite( heap );
		}
	}
	
	public static void discard( Heap heap ) {
		if (scene != null) {
			scene.addDiscardedSprite( heap );
		}
	}
	
	public static void add( Mob mob ) {
		Dungeon.level.mobs.add( mob );
		Actor.add( mob );
		scene.addMobSprite( mob );
	}
	
	public static void add( Mob mob, float delay ) {
		Dungeon.level.mobs.add( mob );
		Actor.addDelayed( mob, delay );
		scene.addMobSprite( mob );
	}
	
	public static void add( EmoIcon icon ) {
		scene.emoicons.add( icon );
	}
	
	public static void effect( Visual effect ) {
		scene.effects.add( effect );
	}
	
	public static Ripple ripple( int pos ) {
		Ripple ripple = (Ripple)scene.ripples.recycle( Ripple.class );
		ripple.reset( pos );
		return ripple;
	}
	
	public static SpellSprite spellSprite() {
		return (SpellSprite)scene.spells.recycle( SpellSprite.class );
	}
	
	public static Emitter emitter() {
		if (scene != null) {
			Emitter emitter = (Emitter)scene.emitters.recycle( Emitter.class );
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}
	
	public static FloatingText status() {
		return scene != null ? (FloatingText)scene.statuses.recycle( FloatingText.class ) : null;
	}
	
	public static void pickUp( Item item ) {
		scene.toolbar.pickup( item );
	}

	public static void resetMap() {
		if (scene != null) {
			scene.tiles.map(Dungeon.level.map, Level.WIDTH );

		}
	}

	public static void updateMap() {
		if (scene != null) {
			scene.tiles.updated.set( 0, 0, Level.WIDTH, Level.HEIGHT );
		}
	}
	
	public static void updateMap( int cell ) {
		if (scene != null) {
			scene.tiles.updated.union( cell % Level.WIDTH, cell / Level.WIDTH );
		}
	}
	
	public static void discoverTile( int pos, int oldValue ) {
		if (scene != null) {
			scene.tiles.discover( pos, oldValue );
		}
	}
	
	public static void show( Window wnd ) {
		cancelCellSelector();
		scene.add( wnd );
	}
	
	public static void afterObserve() {
		if (scene != null) {
			scene.fog.updateVisibility( Dungeon.visible, Dungeon.level.visited, Dungeon.level.mapped );
			
			for (Mob mob : Dungeon.level.mobs) {
				mob.sprite.visible = Dungeon.visible[mob.pos];
			}
		}
	}
	
	public static void flash( int color ) {
		scene.fadeIn( 0xFF000000 | color, true );
	}
	
	public static void gameOver() {
		Banner gameOver = new Banner( BannerSprites.get( BannerSprites.Type.GAME_OVER ) );
		gameOver.show( 0x000000, 1f );
		scene.showBanner( gameOver );
		
		Sample.INSTANCE.play( Assets.SND_DEATH );
	}
	
	public static void bossSlain() {
		if (Dungeon.hero.isAlive()) {
			Banner bossSlain = new Banner( BannerSprites.get( BannerSprites.Type.BOSS_SLAIN ) );
			bossSlain.show( 0xFFFFFF, 0.3f, 5f );
			scene.showBanner( bossSlain );
			
			Sample.INSTANCE.play( Assets.SND_BOSS );
		}
	}
	
	public static void handleCell( int cell ) {
		cellSelector.select( cell );
	}
	
	public static void selectCell( CellSelector.Listener listener ) {
		cellSelector.listener = listener;
		scene.prompt( listener.prompt() );
	}
	
	private static boolean cancelCellSelector() {
		if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
			cellSelector.cancel();
			return true;
		} else {
			return false;
		}
	}
	
	public static WndBag selectItem( WndBag.Listener listener, WndBag.Mode mode, String title ) {
		cancelCellSelector();
		
		WndBag wnd =
				mode == Mode.SEED ?
					WndBag.getBag( SeedPouch.class, listener, mode, title ) :
				mode == Mode.SCROLL ?
					WndBag.getBag( ScrollHolder.class, listener, mode, title ) :
				mode == Mode.POTION ?
					WndBag.getBag( PotionBandolier.class, listener, mode, title ) :
				mode == Mode.WAND ?
					WndBag.getBag( WandHolster.class, listener, mode, title ) :
				WndBag.lastBag( listener, mode, title );

		scene.add( wnd );
		
		return wnd;
	}
	
	static boolean cancel() {
		if (Dungeon.hero.curAction != null || Dungeon.hero.resting) {
			
			Dungeon.hero.curAction = null;
			Dungeon.hero.resting = false;
			return true;
			
		} else {
			
			return cancelCellSelector();
			
		}
	}
	
	public static void ready() {
		selectCell( defaultCellListener );
		QuickSlotButton.cancel();
	}

	public static void examineCell( Integer cell ) {
		if (cell == null) {
			return;
		}

		if (cell < 0 || cell > Level.LENGTH || (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell])) {
			GameScene.show( new WndMessage( "You don't know what is there." ) ) ;
			return;
		}

		if (cell == Dungeon.hero.pos) {
			GameScene.show( new WndHero() );
			return;
		}

		if (Dungeon.visible[cell]) {
			Mob mob = (Mob) Actor.findChar(cell);
			if (mob != null) {
				GameScene.show(new WndInfoMob(mob));
				return;
			}
		}

		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap != null && heap.seen) {
			if (heap.type == Heap.Type.FOR_SALE && heap.size() == 1 && heap.peek().price() > 0) {
				GameScene.show(new WndTradeItem(heap, false));
			} else {
				GameScene.show(new WndInfoItem(heap));
			}
			return;
		}

		Plant plant = Dungeon.level.plants.get( cell );
		if (plant != null) {
			GameScene.show( new WndInfoPlant( plant ) );
			return;
		}

		Trap trap = Dungeon.level.traps.get( cell );
		if (trap != null && trap.visible) {
			GameScene.show( new WndInfoTrap( trap ));
			return;
		}

		GameScene.show( new WndInfoCell( cell ) );
	}

	
	private static final CellSelector.Listener defaultCellListener = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer cell ) {
			if (Dungeon.hero.handle( cell )) {
				Dungeon.hero.next();
			}
		}
		@Override
		public String prompt() {
			return null;
		}
	};
}
