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

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;

import com.shatteredicedungeon.Assets;
import com.shatteredicedungeon.ShatteredIceDungeon;
import com.shatteredicedungeon.effects.BannerSprites;
import com.shatteredicedungeon.effects.Fireball;
import com.shatteredicedungeon.ui.Archs;
import com.shatteredicedungeon.ui.ChangesButton;
import com.shatteredicedungeon.ui.ExitButton;
import com.shatteredicedungeon.ui.PrefsButton;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

public class TitleScene extends PixelScene {

	private static final String TXT_PLAY		= "Play";
	private static final String TXT_HIGHSCORES	= "Rankings";
	private static final String TXT_BADGES		= "Badges";
	private static final String TXT_ABOUT		= "About";
	
	@Override
	public void create() {
		
		super.create();


		Music.INSTANCE.play( Assets.THEME, true );
		Music.INSTANCE.volume( ShatteredIceDungeon.musicVol() / 10f );

		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;
		
		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );
		
		Image title = BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON );
		add( title );

		float height = title.height
				+ (ShatteredIceDungeon.landscape() ? DashboardItem.SIZE
						: DashboardItem.SIZE * 2);

		title.x = (w - title.width()) / 2;
		title.y = (h - height) / 2;
		
		placeTorch(title.x + 18, title.y + 20);
		placeTorch(title.x + title.width - 18, title.y + 20);

		Image signs = new Image( BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON_SIGNS ) ) {
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = (float)Math.sin( -(time += Game.elapsed) );
			}
			@Override
			public void draw() {
				GLES20.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE );
				super.draw();
				GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			}
		};
		signs.x = title.x;
		signs.y = title.y;
		add( signs );
		
		DashboardItem btnBadges = new DashboardItem( TXT_BADGES, 3 ) {
			@Override
			protected void onClick() {
				ShatteredIceDungeon.switchNoFade(BadgesScene.class);
			}
		};
		add(btnBadges);
		
		DashboardItem btnAbout = new DashboardItem( TXT_ABOUT, 1 ) {
			@Override
			protected void onClick() {
				ShatteredIceDungeon.switchNoFade(AboutScene.class);
			}
		};
		add( btnAbout );
		
		DashboardItem btnPlay = new DashboardItem( TXT_PLAY, 0 ) {
			@Override
			protected void onClick() {
				ShatteredIceDungeon.switchNoFade(StartScene.class);
			}
		};
		add( btnPlay );
		
		DashboardItem btnHighscores = new DashboardItem( TXT_HIGHSCORES, 2 ) {
			@Override
			protected void onClick() {
				ShatteredIceDungeon.switchNoFade(RankingsScene.class);
			}
		};
		add( btnHighscores );

		if (ShatteredIceDungeon.landscape()) {
			float y = (h + height) / 2 - DashboardItem.SIZE;
			btnHighscores    .setPos( w / 2 - btnHighscores.width(), y );
			btnBadges        .setPos( w / 2, y );
			btnPlay            .setPos( btnHighscores.left() - btnPlay.width(), y );
			btnAbout        .setPos( btnBadges.right(), y );
		} else {
			btnBadges.setPos( w / 2 - btnBadges.width(), (h + height) / 2 - DashboardItem.SIZE );
			btnAbout.setPos( w / 2, (h + height) / 2 - DashboardItem.SIZE );
			btnPlay.setPos( w / 2 - btnPlay.width(), btnAbout.top() - DashboardItem.SIZE );
			btnHighscores.setPos( w / 2, btnPlay.top() );
		}

		BitmapText source = new BitmapText("PD v 1.7.5", pixelFont);
		source.measure();
		source.hardlight(0x444444);
		source.x = w - source.width();
		source.y = h - source.height();
		add(source);

		BitmapText sourceShattered = new BitmapText("Shattered PD v 0.3.2b",
				pixelFont);
		sourceShattered.measure();
		sourceShattered.hardlight(0x00FF00);
		sourceShattered.x = w - sourceShattered.width();
		sourceShattered.y = h - sourceShattered.height() - source.height();
		add(sourceShattered);

		BitmapText version = new BitmapText("ice PD v " + Game.version + "",
				pixelFont);
		version.measure();
		// version.hardlight( 0xCCCCCC );
		version.hardlight(0x00FFFF);
		version.x = w - version.width();
		version.y = h - version.height() - sourceShattered.height()
				- source.height();

		add(version);

		Button changes = new ChangesButton();
		changes.setPos( w-changes.width(), h - version.height() - sourceShattered.height() - source.height() - changes.height());
		add( changes );
		
		PrefsButton btnPrefs = new PrefsButton();
		btnPrefs.setPos( 0, 0 );
		add( btnPrefs );

		ExitButton btnExit = new ExitButton();
		btnExit.setPos(w - btnExit.width(), 0);
		add(btnExit);

		fadeIn();
	}
	
	private void placeTorch( float x, float y ) {
		Fireball fb = new Fireball();
		fb.setPos( x, y );
		add( fb );
	}
	
	private static class DashboardItem extends Button {
		
		public static final float SIZE	= 48;
		
		private static final int IMAGE_SIZE	= 32;
		
		private Image image;
		private BitmapText label;
		
		public DashboardItem( String text, int index ) {
			super();
			
			image.frame( image.texture.uvRect( index * IMAGE_SIZE, 0, (index + 1) * IMAGE_SIZE, IMAGE_SIZE ) );
			this.label.text( text );
			this.label.measure();
			
			setSize( SIZE, SIZE );
		}
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			image = new Image( Assets.DASHBOARD );
			add( image );
			
			label = createText( 9 );
			add( label );
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			image.x = x + (width - image.width()) / 2;
			image.y = y;
			
			label.x = x + (width - label.width()) / 2;
			label.y = image.y + image.height() +2;
		}
		
		@Override
		protected void onTouchDown() {
			image.brightness( 1.5f );
			Sample.INSTANCE.play( Assets.SND_CLICK, 1, 1, 0.8f );
		}
		
		@Override
		protected void onTouchUp() {
			image.resetColor();
		}
	}
}
