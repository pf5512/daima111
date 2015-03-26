package com.cooee.widget3D.JewelWeather.Common;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation {
	public final TextureRegion[] keyFrames;
	public float frameDuration;

	public Animation(float frameDuration, List<TextureRegion> keyFrames) {
		this.frameDuration = frameDuration;
		this.keyFrames = new TextureRegion[keyFrames.size()];
		for (int i = 0, n = keyFrames.size(); i < n; i++) {
			this.keyFrames[i] = (TextureRegion) keyFrames.get(i);
		}
	}

	public Animation(float frameDuration, TextureRegion... keyFrames) {
		this.frameDuration = frameDuration;
		this.keyFrames = keyFrames;
	}

	public TextureRegion getKeyFrame(float stateTime, boolean looping) {
		int frameNumber = (int) (stateTime / frameDuration);
		if (!looping) {
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
		} else {
			frameNumber = frameNumber % keyFrames.length;
		}
		// Log.v("robot", "frameNumber:" + frameNumber);
		return keyFrames[frameNumber];
	}
	 
	public TextureRegion getKeyFrame(int frameNumber, boolean looping) {
		// if (!looping) {
		// frameNumber = Math.min(keyFrames.length - 1, frameNumber);
		// } else {
		frameNumber = frameNumber % keyFrames.length;
		// }
		// Log.v("robot", "frameNumber:" + frameNumber);
		return keyFrames[frameNumber];
	}
}