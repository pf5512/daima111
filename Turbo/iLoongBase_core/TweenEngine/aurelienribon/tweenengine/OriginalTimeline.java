package aurelienribon.tweenengine;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.iLoong.launcher.UI3DEngine.ConfigBase;


/**
 * A Timeline can be used to create complex animations made of sequences and
 * parallel sets of Tweens.
 * <p/>
 *
 * The following example will create an animation sequence composed of 5 parts:
 * <p/>
 *
 * 1. First, opacity and scale are set to 0 (with Tween.set() calls).<br/>
 * 2. Then, opacity and scale are animated in parallel.<br/>
 * 3. Then, the animation is paused for 1s.<br/>
 * 4. Then, position is animated to x=100.<br/>
 * 5. Then, rotation is animated to 360°.
 * <p/>
 *
 * This animation will be repeated 5 times, with a 500ms delay between each
 * iteration:
 * <br/><br/>
 *
 * <pre> {@code
 * Timeline.createSequence()
 *     .push(Tween.set(myObject, OPACITY).target(0))
 *     .push(Tween.set(myObject, SCALE).target(0, 0))
 *     .beginParallel()
 *          .push(Tween.to(myObject, OPACITY, 0.5f).target(1).ease(Quad.INOUT))
 *          .push(Tween.to(myObject, SCALE, 0.5f).target(1, 1).ease(Quad.INOUT))
 *     .end()
 *     .pushPause(1.0f)
 *     .push(Tween.to(myObject, POSITION_X, 0.5f).target(100).ease(Quad.INOUT))
 *     .push(Tween.to(myObject, ROTATION, 0.5f).target(360).ease(Quad.INOUT))
 *     .repeat(5, 0.5f)
 *     .start(myManager);
 * }</pre>
 *
 * @see OriginalTween
 * @see TweenManager
 * @see TweenCallback
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public final class OriginalTimeline extends OriginalBaseTween<OriginalTimeline>
{
	
	// -------------------------------------------------------------------------
	// Static -- pool
	// -------------------------------------------------------------------------
	private static final Pool.Callback<OriginalTimeline> poolCallback = new Pool.Callback<OriginalTimeline>() {
		
		@Override
		public void onPool(
				OriginalTimeline obj )
		{
			obj.reset();
		}
	};
	static final Pool<OriginalTimeline> pool = new Pool<OriginalTimeline>( 10 , poolCallback ) {
		
		@Override
		protected OriginalTimeline create()
		{
			return new OriginalTimeline();
		}
	};
	
	/**
	 * Used for debug purpose. Gets the current number of empty timelines that
	 * are waiting in the Timeline pool.
	 */
	public static int getPoolSize()
	{
		return pool.size();
	}
	
	/**
	 * Increases the minimum capacity of the pool. Capacity defaults to 10.
	 */
	public static void ensurePoolCapacity(
			int minCapacity )
	{
		pool.ensureCapacity( minCapacity );
	}
	
	// -------------------------------------------------------------------------
	// Static -- factories
	// -------------------------------------------------------------------------
	/**
	 * Creates a new timeline with a 'sequence' behavior. Its children will
	 * be delayed so that they are triggered one after the other.
	 */
	public static OriginalTimeline createSequence()
	{
		OriginalTimeline tl = pool.get();
		tl.setup( Modes.SEQUENCE );
		return tl;
	}
	
	/**
	 * Creates a new timeline with a 'parallel' behavior. Its children will be
	 * triggered all at once.
	 */
	public static OriginalTimeline createParallel()
	{
		OriginalTimeline tl = pool.get();
		tl.setup( Modes.PARALLEL );
		return tl;
	}
	
	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------
	private enum Modes
	{
		SEQUENCE , PARALLEL
	}
	
	private final List<OriginalBaseTween> children = new ArrayList<OriginalBaseTween>( 10 );
	private final List<BaseTween> holders = new ArrayList<BaseTween>( 10 );
	private OriginalTimeline current;
	private OriginalTimeline parent;
	private Modes mode;
	private boolean isBuilt;
	
	// -------------------------------------------------------------------------
	// Setup
	// -------------------------------------------------------------------------
	private OriginalTimeline()
	{
		reset();
	}
	
	@Override
	protected void reset()
	{
		super.reset();
		children.clear();
		holders.clear();
		current = parent = null;
		isBuilt = false;
	}
	
	private void setup(
			Modes mode )
	{
		this.mode = mode;
		this.current = this;
	}
	
	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------
	/**
	 * Adds a Tween to the current timeline.
	 *
	 * @return The current timeline, for chaining instructions.
	 */
	public OriginalTimeline push(
			OriginalTween tween )
	{
		if( isBuilt )
			throw new RuntimeException( "You can't push anything to a timeline once it is started" );
		//Log.e("tween", "push tween:"+tween+" "+this);
		current.children.add( tween );
		current.holders.add( tween.holder );
		return this;
	}
	
	/**
	 * Nests a Timeline in the current one.
	 *
	 * @return The current timeline, for chaining instructions.
	 */
	public OriginalTimeline push(
			OriginalTimeline timeline )
	{
		//Log.e("tween", "push timeline:"+timeline+" "+this);
		if( isBuilt )
			throw new RuntimeException( "You can't push anything to a timeline once it is started" );
		if( timeline.current != timeline )
			throw new RuntimeException( "You forgot to call a few 'end()' statements in your pushed timeline" );
		timeline.parent = current;
		current.children.add( timeline );
		current.holders.add( timeline.holder );
		return this;
	}
	
	/**
	 * Adds a pause to the timeline. The pause may be negative if you want to
	 * overlap the preceding and following children.
	 *
	 * @param time A positive or negative duration.
	 * @return The current timeline, for chaining instructions.
	 */
	public OriginalTimeline pushPause(
			float time )
	{
		if( isBuilt )
			throw new RuntimeException( "You can't push anything to a timeline once it is started" );
		current.children.add( OriginalTween.mark().delay( time ) );
		return this;
	}
	
	/**
	 * Starts a nested timeline with a 'sequence' behavior. Don't forget to
	 * call {@link end()} to close this nested timeline.
	 *
	 * @return The current timeline, for chaining instructions.
	 */
	//	public Timeline beginSequence() {
	//		if (isBuilt) throw new RuntimeException("You can't push anything to a timeline once it is started");
	//		Timeline tl = pool.get();
	//		tl.parent = current;
	//		tl.mode = Modes.SEQUENCE;
	//		current.children.add(tl);
	//		current = tl;
	//		return this;
	//	}
	/**
	 * Starts a nested timeline with a 'parallel' behavior. Don't forget to
	 * call {@link end()} to close this nested timeline.
	 *
	 * @return The current timeline, for chaining instructions.
	 */
	//	public Timeline beginParallel() {
	//		if (isBuilt) throw new RuntimeException("You can't push anything to a timeline once it is started");
	//		Timeline tl = pool.get();
	//		tl.parent = current;
	//		tl.mode = Modes.PARALLEL;
	//		current.children.add(tl);
	//		current = tl;
	//		return this;
	//	}
	/**
	 * Closes the last nested timeline.
	 *
	 * @return The current timeline, for chaining instructions.
	 */
	public OriginalTimeline end()
	{
		if( isBuilt )
			throw new RuntimeException( "You can't push anything to a timeline once it is started" );
		if( current == this )
			throw new RuntimeException( "Nothing to end..." );
		current = current.parent;
		return this;
	}
	
	/**
	 * Gets a list of the timeline children. If the timeline is started, the
	 * list will be immutable.
	 */
	public List<OriginalBaseTween> getChildren()
	{
		if( isBuilt )
			return Collections.unmodifiableList( current.children );
		else
			return current.children;
	}
	
	// -------------------------------------------------------------------------
	// Overrides
	// -------------------------------------------------------------------------
	private boolean isValid(
			OriginalBaseTween orig ,
			int position )
	{
		//Jone add:umeng-exception:id_1
		if(ConfigBase.net_version){
			if(position>=holders.size())
				return false;
		}
		//Jone end 
		if( holders.get( position ) == orig.holder )
		{
			if( holders.get( position ).id == orig.id )
				return true;
		}
		return false;
	}
	
	@Override
	public OriginalTimeline build()
	{
		if( isBuilt )
			return this;
		duration = 0;
		for( int i = 0 ; i < children.size() ; i++ )
		{
			OriginalBaseTween obj = children.get( i );
			if( !isValid( obj , i ) )
				continue;
			if( obj.getRepeatCount() < 0 )
				throw new RuntimeException( "You can't push an object with infinite repetitions in a timeline" );
			obj.build();
			switch( mode )
			{
				case SEQUENCE:
					float tDelay = duration;
					duration += obj.getFullDuration();
					obj.delay += tDelay;
					break;
				case PARALLEL:
					duration = Math.max( duration , obj.getFullDuration() );
					break;
			}
		}
		isBuilt = true;
		return this;
	}
	
	@Override
	public OriginalTimeline start()
	{
		super.start();
		for( int i = 0 ; i < children.size() ; i++ )
		{
			OriginalBaseTween obj = children.get( i );
			if( !isValid( obj , i ) )
				continue;
			obj.start();
		}
		return this;
	}
	
	@Override
	public void free(
			long id )
	{
		if( id != this.id )
		{
			//Log.e("tween", "free error!!!!!!");
			return;
		}
		free();
	}
	
	@Override
	public void free()
	{
		//Log.e("tween", "free timeline:"+this);
		if( !isFinished && isKilled )
		{
			isFinished = true;
			callCallback( TweenCallback.COMPLETE );
		}
		for( int i = holders.size() - 1 ; i >= 0 ; i-- )
		{
			BaseTween obj = holders.remove( i );
			children.remove( i );
			//Log.e("tween", "free tween:"+obj+" "+this);
			obj.free();
		}
		pool.free( this );
	}
	
	@Override
	protected void initializeOverride()
	{
	}
	
	@Override
	protected void computeOverride(
			int step ,
			int lastStep ,
			float delta )
	{
		float time;
		if( step > lastStep )
		{
			forceStartValues( step );
			time = isYoyo( step ) ? -currentTime : currentTime;
		}
		else if( step < lastStep )
		{
			forceEndValues( step );
			time = isYoyo( step ) ? duration - currentTime : currentTime - duration;
		}
		else
		{
			time = isYoyo( step ) ? -delta : delta;
		}
		if( delta >= 0 )
		{
			for( int i = 0 , n = children.size() ; i < n ; i++ )
			{
				if( !isValid( children.get( i ) , i ) )
					continue;
				children.get( i ).update( time );
			}
		}
		else
		{
			for( int i = children.size() - 1 ; i >= 0 ; i-- )
			{
				if( !isValid( children.get( i ) , i ) )
					continue;
				children.get( i ).update( time );
			}
		}
	}
	
	// -------------------------------------------------------------------------
	// BaseTween impl.
	// -------------------------------------------------------------------------
	@Override
	protected void forceStartValues()
	{
		for( int i = children.size() - 1 ; i >= 0 ; i-- )
		{
			OriginalBaseTween obj = children.get( i );
			if( !isValid( obj , i ) )
				continue;
			obj.forceToStart();
		}
	}
	
	@Override
	protected void forceEndValues()
	{
		for( int i = 0 , n = children.size() ; i < n ; i++ )
		{
			OriginalBaseTween obj = children.get( i );
			if( !isValid( obj , i ) )
				continue;
			obj.forceToEnd( duration );
		}
	}
	
	@Override
	protected void killTarget(
			Object target )
	{
		if( containsTarget( target ) )
			kill();
	}
	
	@Override
	protected void killTarget(
			Object target ,
			int tweenType )
	{
		if( containsTarget( target , tweenType ) )
			kill();
	}
	
	@Override
	protected boolean containsTarget(
			Object target )
	{
		for( int i = 0 , n = children.size() ; i < n ; i++ )
		{
			OriginalBaseTween obj = children.get( i );
			if( obj.containsTarget( target ) )
				return true;
		}
		return false;
	}
	
	@Override
	protected boolean containsTarget(
			Object target ,
			int tweenType )
	{
		for( int i = 0 , n = children.size() ; i < n ; i++ )
		{
			OriginalBaseTween obj = children.get( i );
			if( obj.containsTarget( target , tweenType ) )
				return true;
		}
		return false;
	}
}
