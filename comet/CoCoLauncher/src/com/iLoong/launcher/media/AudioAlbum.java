/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 */

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iLoong.launcher.media;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.media.PhotoItem.PhotoView;
import com.iLoong.launcher.theme.ThemeManager;

public class AudioAlbum {
	public static final int VISIBLE_AUDIO_NUM = 1;
	public static final int EVENT_AUDIO_ALBUM_CLICK = 0;
	public static final int EVENT_AUDIO_ALBUM_LONGCLICK = 1;

	public static final int TYPE_ALBUM = 0;
	public static final int TYPE_ARTIST = 1;
	public static final int TYPE_FOLDER = 2;
	public int type;
	public String title;
	public String artist;
	public String folder;
	public String thumbnailPath;
	public MediaCache mediaCache;
	public ArrayList<AudioItem> audios;
	public AudioAlbumView vg;
	public String albumId;
	public int visibleAudioIndex = -1;
	public TextureRegion defaultRegion;
	public static TextureRegion bgRegion;
	private float bgWidth;
	private float bgHeight;
	private float titleHeight;
	static {
		bgRegion = R3D.findRegion("default-audio-bg");
	}

	public AudioAlbum() {
		audios = new ArrayList<AudioItem>();
		bgWidth = R3D.audio_width + R3D.audio_bottom_padding
				+ R3D.audio_left_padding;
		bgHeight = R3D.audio_height + 2 * R3D.audio_bottom_padding;
		titleHeight = Utils3D.getTitleHeight(R3D.photo_title_size, 1);
	}

	public View3D obtainView() {
		if (vg == null) {
			vg = new AudioAlbumView(getLabel());
			vg.album = this;
		}
		visibleAudioIndex = getVisibleAudio();
		if (visibleAudioIndex != -1) {
			View3D view = audios.get(visibleAudioIndex).obtainView();
			vg.addView(view);
			view.setPosition(0, 0);
		}
		return vg;
	}

	private String getLabel() {
		switch (type) {
		case TYPE_ALBUM:
			if (title == null)
				title = "";
			return title;
		case TYPE_ARTIST:
			if (artist == null)
				artist = "";
			return artist;
		case TYPE_FOLDER:
			if (folder == null) {
				folder = "";
				return "";
			}
			int index = folder.lastIndexOf('/');
			if (index + 1 >= folder.length())
				return folder;
			return folder.substring(index + 1);
		default:
			return title;
		}
	}

	private int getVisibleAudio() {
		for (int i = 0; i < audios.size(); i++) {
			if (audios.get(i).thumbnailPath == null
					|| audios.get(i).thumbnailPath.equals(""))
				continue;
			return i;
		}
		return -1;
	}

	public void prepare(int priority) {
		int i = getVisibleAudio();
		if (i != visibleAudioIndex) {
			visibleAudioIndex = i;
			vg.removeAllViews();
			View3D view = audios.get(visibleAudioIndex).obtainView();
			vg.addView(view);
			view.setPosition(0, 0);
		}
		if (visibleAudioIndex != -1)
			audios.get(visibleAudioIndex).prepare(priority);
	}

	public void free() {
		if (visibleAudioIndex != -1 && audios.size() > 0
				&& visibleAudioIndex < audios.size())
			audios.get(visibleAudioIndex).free();
	}

	public void onDelete() {
		for (int i = 0; i < audios.size(); i++) {
			audios.get(i).onDelete();
		}
		mediaCache.deleteAudioAlbum(this);
	}

	public class AudioAlbumView extends ViewGroup3D implements MediaView {

		public AudioAlbum album;
		public boolean selected = false;

		public AudioAlbumView(String name) {
			super(name);
		}

		@Override
		public boolean onClick(float x, float y) {
			if (AppHost3D.selectState) {
				selected = !selected;
			} else
				super.onCtrlEvent(this, EVENT_AUDIO_ALBUM_CLICK);
			return true;
		}

		@Override
		public boolean onLongClick(float x, float y) {
			if (AppHost3D.selectState)
				return true;
			else {
				super.onCtrlEvent(this, EVENT_AUDIO_ALBUM_LONGCLICK);
				selected = true;
			}
			return true;
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			if (bgRegion != null) {
				batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
				batch.draw(bgRegion, x + (width - bgWidth) / 2, y + titleHeight
						+ (height - titleHeight - bgHeight) / 2, bgWidth,
						bgHeight);
			}
			if (defaultRegion != null && visibleAudioIndex == -1) {
				batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
				batch.draw(defaultRegion, x + (width - bgWidth) / 2
						+ R3D.audio_left_padding, y + titleHeight
						+ (height - titleHeight - bgHeight) / 2
						+ R3D.audio_bottom_padding, R3D.audio_width,
						R3D.audio_height);
			}
			super.draw(batch, parentAlpha);
			if (region.getTexture() != null) {
				float numberWidth = 0;// Utils3D.getNumberWidth(audios.size());
				float titleX = (width - region.getRegionWidth()) / 2;
				batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
				batch.draw(region, x + titleX, y);
				// titleX += region.getRegionWidth();
				// batch.draw(PhotoBucket.leftBracket, x+titleX, y);
				// titleX += PhotoBucket.leftBracket.getRegionWidth();
				// TextureRegion number;
				// String s = audios.size()+"";
				// char c;
				// for(int i = 0;i < s.length();i++){
				// c = s.charAt(i);
				// number = R3D.findRegion("photo-"+c);
				// batch.draw(number, x+titleX, y);
				// titleX += number.getRegionWidth();
				// }
				// batch.draw(PhotoBucket.rightBracket, x+titleX, y);
			}
			if (AppHost3D.selectState) {
				if (selected)
					batch.draw(R3D.selectedRegion, x + (width + bgWidth)
							/ 2 - R3D.selectedRegion.getRegionWidth(), y
							+ titleHeight + (height - titleHeight - bgHeight)
							/ 2);
				else
					batch.draw(R3D.unselectRegion, x + (width + bgHeight)
							/ 2 - R3D.unselectRegion.getRegionWidth(), y
							+ titleHeight + (height - titleHeight - bgHeight)
							/ 2);
			}
		}

		@Override
		public void setSize(float w, float h) {
			super.setSize(w, h);
			this.setOrigin(w / 2, h / 2);
			if (visibleAudioIndex != -1) {
				View3D view = audios.get(visibleAudioIndex).obtainView();
				view.setSize(R3D.audio_width, R3D.audio_height);
				view.y = titleHeight + (height - titleHeight - bgHeight) / 2
						+ R3D.audio_bottom_padding;
				view.x = (width - bgWidth) / 2 + R3D.audio_left_padding;
			}
			if (region.getTexture() == null) {
				float numberWidth = 0;// Utils3D.getTitleWidth("(0000)",R3D.photo_title_size);
				region.setRegion(new BitmapTexture((AppBar3D
						.titleToPixmapWidthLimit(
								getLabel() + "(" + audios.size() + ")",
								(int) (w - numberWidth - 4),
								R3D.photo_title_size, Color.WHITE, true))));
			}
			if (defaultRegion == null)
				defaultRegion = AudioItem.defaultRegions[Math.abs(getLabel()
						.hashCode()) % 6];
		}

		@Override
		public void prepare(int priority) {
			album.prepare(priority);
		}

		@Override
		public void free() {
			album.free();
		}

		@Override
		public void refresh() {
			// TODO Auto-generated method stub

		}

		@Override
		public void select() {
			selected = true;
		}

		@Override
		public void share(ArrayList<Uri> list) {
			if (!selected)
				return;
			for (int i = 0; i < album.audios.size(); i++) {
				AudioItem audio = album.audios.get(i);
				if (audio.data != null) {
					list.add(Uri.parse("file://" + audio.data));
				}
			}
		}

		@Override
		public void onDelete() {
			if (!selected)
				return;
			for (int i = 0; i < album.audios.size(); i++) {
				AudioItem audio = album.audios.get(i);
				audio.onDelete();
			}
			album.onDelete();
		}

		@Override
		public void clearSelect() {
			selected = false;
		}
	}
}
