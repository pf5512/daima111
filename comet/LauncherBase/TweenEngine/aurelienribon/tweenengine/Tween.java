package aurelienribon.tweenengine;


public final class Tween extends BaseTween<Tween>
{
	
	public OriginalTween origTween;
	
	public boolean isFinished()
	{
		return origTween.isFinished();
	}
	
	public void free()
	{
		synchronized( OriginalBaseTween.lock )
		{
			origTween.free( id );
		}
	}
	
	//	public OriginalTween getOrigTween(){
	//		return origTween;
	//	}
	public static void registerAccessor(
			Class someClass ,
			TweenAccessor defaultAccessor )
	{
		OriginalTween.registerAccessor( someClass , defaultAccessor );
	}
	
	public static Tween to(
			Object target ,
			int tweenType ,
			float duration )
	{
		synchronized( OriginalBaseTween.lock )
		{
			OriginalTween origTween = OriginalTween.to( target , tweenType , duration );
			Tween holder = new Tween();
			long id = System.nanoTime();
			holder.id = id;
			origTween.id = id;
			origTween.holder = holder;
			holder.origTween = origTween;
			return holder;
		}
	}
	
	public static Tween set(
			Object target ,
			int tweenType )
	{
		synchronized( OriginalBaseTween.lock )
		{
			OriginalTween origTween = OriginalTween.set( target , tweenType );
			Tween holder = new Tween();
			long id = System.nanoTime();
			holder.id = id;
			origTween.id = id;
			origTween.holder = holder;
			holder.origTween = origTween;
			return holder;
		}
	}
	
	public Tween target(
			float targetValue1 ,
			float targetValue2 ,
			float targetValue3 )
	{
		origTween.target( targetValue1 , targetValue2 , targetValue3 );
		return this;
	}
	
	public Tween target(
			float targetValue1 )
	{
		origTween.target( targetValue1 );
		return this;
	}
	
	public Tween target(
			float targetValue1 ,
			float targetValue2 )
	{
		origTween.target( targetValue1 , targetValue2 );
		return this;
	}
	
	public Tween ease(
			TweenEquation easeEquation )
	{
		origTween.ease( easeEquation );
		return this;
	}
	
	public Tween start(
			TweenManager manager )
	{
		origTween.start( manager );
		return this;
	}
	
	public Tween delay(
			float delay )
	{
		origTween.delay( delay );
		return this;
	}
	
	public Tween setCallback(
			TweenCallback callback )
	{
		origTween.setCallback( callback );
		return this;
	}
	
	public Object getUserData()
	{
		return origTween.getUserData();
	}
	
	public Tween setUserData(
			Object data )
	{
		origTween.setUserData( data );
		return this;
	}
	
	public boolean isStarted()
	{
		return origTween.isStarted();
	}
	
	public Object getTarget()
	{
		return origTween.getTarget();
	}
	
	public Tween repeatYoyo(
			int count ,
			float delay )
	{
		origTween.repeatYoyo( count , delay );
		return this;
	}
	
	public Tween repeat(
			int count ,
			float delay )
	{
		origTween.repeat( count , delay );
		return this;
	}
	
	public void kill()
	{
		origTween.kill();
	}
	
	public void pause()
	{
		origTween.pause();
	}
	
	public void resume()
	{
		origTween.resume();
	}
	
	public Tween targetRelative(
			float targetValue1 ,
			float targetValue2 )
	{
		origTween.targetRelative( targetValue1 , targetValue2 );
		return this;
	}
	
	public Tween build()
	{
		origTween.build();
		return this;
	}
	
	public int getType()
	{
		return origTween.getType();
	}
	
	public boolean isPaused()
	{
		return origTween.isPaused();
	}
	
	/************************ added by zhenNan.ye begin *************************/
	public float[] getTargetValues()
	{
		return origTween.getTargetValues();
	}
	/************************ added by zhenNan.ye end ***************************/
}
