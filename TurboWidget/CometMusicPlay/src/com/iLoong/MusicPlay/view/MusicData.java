package com.iLoong.MusicPlay.view;

public class MusicData {
	public long id = -1;
	public long albumid;
	public String album;
	public String album_artist;
	public boolean playing;
	public long position;
	public long duration;
	public String track;
	public long songId;
	public String argist;
	public int listSize;
	public String title;

	@Override
	public String toString() {
		return "id:" + id + " album:" + album + " albumid:" + albumid
				+ " album_artist:" + album_artist + " argist:" + argist
				+ " songId:" + songId + " track:" + track + " title:" + title;
	}
}
