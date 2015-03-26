package com.iLoong.MusicPlay.view;

public interface MusicListener {
	public void onNext(MusicData music);

	public void onPrevious(MusicData music);

	public void onPause(MusicData music);

	public void onPlay(MusicData music);

	public void onMetaChanged(MusicData music);
}
