package aurelienribon.tweenengine;


public final class Timeline extends BaseTween<Timeline>
{
	
	public OriginalTimeline origTimeline;
	
	public boolean isFinished()
	{
		return origTimeline.isFinished();
	}
	
	public void free()
	{
		synchronized( OriginalBaseTween.lock )
		{
			origTimeline.free( id );
		}
	}
	
	//	public OriginalTimeline getOrigTimeline(){
	//		return origTimeline;
	//	}
	public static Timeline createSequence()
	{
		synchronized( OriginalBaseTween.lock )
		{
			OriginalTimeline origTimeline = OriginalTimeline.createSequence();
			Timeline holder = new Timeline();
			long id = System.nanoTime();
			holder.id = id;
			origTimeline.id = id;
			origTimeline.holder = holder;
			holder.origTimeline = origTimeline;
			return holder;
		}
	}
	
	public static Timeline createParallel()
	{
		synchronized( OriginalBaseTween.lock )
		{
			OriginalTimeline origTimeline = OriginalTimeline.createParallel();
			Timeline holder = new Timeline();
			long id = System.nanoTime();
			holder.id = id;
			origTimeline.id = id;
			origTimeline.holder = holder;
			holder.origTimeline = origTimeline;
			return holder;
		}
	}
	
	public Timeline push(
			Tween tween )
	{
		origTimeline.push( tween.origTween );
		return this;
	}
	
	public Timeline push(
			Timeline timeline )
	{
		origTimeline.push( timeline.origTimeline );
		return this;
	}
	
	public Timeline start(
			TweenManager manager )
	{
		origTimeline.start( manager );
		return this;
	}
	
	public Timeline setCallback(
			TweenCallback callback )
	{
		origTimeline.setCallback( callback );
		return this;
	}
	
	public Object getUserData()
	{
		return origTimeline.getUserData();
	}
	
	public Timeline setUserData(
			Object data )
	{
		origTimeline.setUserData( data );
		return this;
	}
	
	public boolean isStarted()
	{
		return origTimeline.isStarted();
	}
	
	public Timeline build()
	{
		origTimeline.build();
		return this;
	}
	
	public Timeline repeatYoyo(
			int count ,
			float delay )
	{
		origTimeline.repeatYoyo( count , delay );
		return this;
	}
	
	public Timeline repeat(
			int count ,
			float delay )
	{
		origTimeline.repeat( count , delay );
		return this;
	}
	
	public void kill()
	{
		origTimeline.kill();
	}
	
	public void pause()
	{
		origTimeline.pause();
	}
	
	public void resume()
	{
		origTimeline.resume();
	}
	
	public boolean isPaused()
	{
		return origTimeline.isPaused();
	}
}
