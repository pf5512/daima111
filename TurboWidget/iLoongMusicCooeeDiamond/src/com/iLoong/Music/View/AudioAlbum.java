package com.iLoong.Music.View;

import java.util.ArrayList;

public class AudioAlbum {
	public int albumId = -1;
	public String albumName;
	public String artist;
	public String subTitle;
	public ArrayList<AudioItem> audios;

	public AudioAlbum() {
		audios = new ArrayList<AudioItem>();
	}
}
