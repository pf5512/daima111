package com.iLoong.launcher.UI3DEngine;


import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * @author zhenNan.ye
 *
 */
public class ParticleAnim
{
	
	private Object target;
	private ParticleEffect particleEffect;
	private String particleType;
	private ParticleCallback callback;
	public long particleStartTime;
	
	public ParticleAnim()
	{
		target = null;
		particleEffect = null;
		particleType = null;
		callback = null;
	}
	
	public ParticleAnim(
			Object target ,
			ParticleEffect effect ,
			String particleType )
	{
		this.target = target;
		this.particleEffect = effect;
		this.particleType = particleType;
		callback = null;
	}
	
	public void setTarget(
			Object target )
	{
		this.target = target;
	}
	
	public Object getTarget()
	{
		return this.target;
	}
	
	public void setParticleEffect(
			ParticleEffect effect )
	{
		this.particleEffect = effect;
	}
	
	public ParticleEffect getParticleEffect()
	{
		return this.particleEffect;
	}
	
	public void setParticleType(
			String particleType )
	{
		this.particleType = particleType;
	}
	
	public String getParticleType()
	{
		return this.particleType;
	}
	
	public void start()
	{
		this.particleEffect.start();
	}
	
	public void stop()
	{
		this.particleEffect.allowCompletion();
	}
	
	public void setPosition(
			float positionX ,
			float positionY )
	{
		this.particleEffect.setPosition( positionX , positionY );
	}
	
	public boolean isFinish()
	{
		if( this.particleEffect != null )
		{
			return this.particleEffect.isComplete();
		}
		return true;
	}
	
	public void draw(
			SpriteBatch batch ,
			float delta )
	{
		this.particleEffect.draw( batch , delta );
	}
	
	public static interface ParticleCallback
	{
		
		public static final int START = 0x01;
		public static final int END = 0x02;
		
		public void onParticleCallback(
				int type );
	}
	
	public void setCallback(
			ParticleCallback callback )
	{
		this.callback = callback;
	}
	
	public ParticleCallback getCallback()
	{
		return this.callback;
	}
	
	public void callCallback(
			int type )
	{
		ParticleManager.getParticleManager().clearParticleAnimList();
		if( callback != null )
		{
			callback.onParticleCallback( type );
		}
	}
}
