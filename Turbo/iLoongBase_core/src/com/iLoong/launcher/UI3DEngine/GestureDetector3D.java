package com.iLoong.launcher.UI3DEngine;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.Log;


public class GestureDetector3D
{
	
	public static interface GestureListener3D
	{
		
		public boolean tap(
				int x ,
				int y ,
				int count );
		
		public boolean longPress(
				int x ,
				int y );
		
		public boolean fling(
				float velocityX ,
				float velocityY );
		
		public boolean pan(
				int x ,
				int y ,
				int deltaX ,
				int deltaY );
		
		public boolean zoom(
				float originalDistance ,
				float currentDistance );
		
		public boolean pinch(
				Vector2 initialFirstPointer ,
				Vector2 initialSecondPointer ,
				Vector2 firstPointer ,
				Vector2 secondPointer );
		
		public boolean onTouchDown(
				int x ,
				int y ,
				int pointer );
		
		public boolean onTouchDragged(
				int x ,
				int y ,
				int pointer );
		
		public boolean onTouchUp(
				int x ,
				int y ,
				int pointer );
	}
	
	private final int tapSquareSize;
	private final long tapCountInterval;
	private final long longPressDuration;
	private long maxFlingDelay;
	private boolean inTapSquare;
	private int tapCount;
	private long lastTapTime;
	private boolean longPressFired;
	private boolean pinching;
	private boolean panning;
	private final VelocityTracker3D tracker = new VelocityTracker3D();
	private int tapSquareCenterX;
	private int tapSquareCenterY;
	private long gestureStartTime;
	private Vector2 firstPointer = new Vector2();
	private Vector2 secondPointer = new Vector2();
	private Vector2 initialFirstPointer = new Vector2();
	private Vector2 initialSecondPointer = new Vector2();
	private int px = 0 , py = 0;
	private boolean longPressReturn = false;
	private boolean[] isPressDown = new boolean[2];
	private final GestureListener3D listener;
	
	public GestureDetector3D(
			GestureListener3D listener )
	{
		this( ConfigBase.halfTapSquareSize , 0.5f , 0.35f , 0.15f , listener );
	}
	
	public GestureDetector3D(
			int halfTapSquareSize ,
			float tapCountInterval ,
			float longPressDuration ,
			float maxFlingDelay ,
			GestureListener3D listener )
	{
		this.tapSquareSize = halfTapSquareSize;
		this.tapCountInterval = (long)( tapCountInterval * 1000000000l );
		this.longPressDuration = (long)( longPressDuration * 1000000000l );
		this.maxFlingDelay = (long)( maxFlingDelay * 1000000000l );
		this.listener = listener;
	}
	
	public boolean touchDown(
			int x ,
			int y ,
			int pointer ,
			int button )
	{
		//		Log.v("jbc", "touchDown x="+x+" y="+y+" pointer="+pointer);
		if( pointer > 1 )
			return false;
		px = x;
		py = y;
		isPressDown[pointer] = true;
		if( pointer == 0 )
		{
			firstPointer.set( x , y );
			gestureStartTime = Gdx.input.getCurrentEventTime();
			tracker.start( x , y , gestureStartTime );
			// we are still touching with the second finger -> pinch mode
			if( isPressDown[1] )
			{
				inTapSquare = false;
				pinching = true;
				initialFirstPointer.set( firstPointer );
				initialSecondPointer.set( secondPointer );
			}
			else
			{
				inTapSquare = true;
				pinching = false;
				longPressFired = false;
				tapSquareCenterX = x;
				tapSquareCenterY = y;
			}
		}
		else
		{
			secondPointer.set( x , y );
			inTapSquare = false;
			pinching = true;
			initialFirstPointer.set( firstPointer );
			initialSecondPointer.set( secondPointer );
		}
		return listener.onTouchDown( x , y , pointer );
	}
	
	public boolean touchDragged(
			int x ,
			int y ,
			int pointer )
	{
		//		Log.v("jbc", "touchDragged x="+x+" y="+y+" pointer="+pointer+" pinching="+pinching);
		if( pointer > 1 || gestureStartTime == 0 || !isPressDown[pointer] )
			return false;
		//		Log.v("touch","drag x:" + x + " y:" + y + " p:" + pointer + " height:"+Utils3D.getScreenHeight());
		if( y < 0 || y > UtilsBase.getScreenHeight() - 1 )
		{//超出屏幕区域
			y = y < 0 ? 0 : UtilsBase.getScreenHeight() - 1;
		}
		if( x < 0 || x > UtilsBase.getScreenWidth() - 1 )
		{
			x = x < 0 ? 0 : UtilsBase.getScreenWidth() - 1;
		}
		//		if(y < 0 || y > Utils3D.getScreenHeight()){//超出屏幕区域
		//			if(isPressDown[pointer]){
		////				Log.v("touch", "uppppppppppppppppppppppp");
		//				return touchUp(x, y < 0 ? 0 : Utils3D.getScreenHeight() - 1,pointer,0);
		//			}		
		//			else
		//				return false;
		//			
		//		}
		px = x;
		py = y;
		boolean r = false;
		// handle pinch zoom
		if( pinching )
		{
			if( pointer == 0 )
				firstPointer.set( x , y );
			else
				secondPointer.set( x , y );
			if( listener != null )
			{
				boolean result = listener.pinch( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer );
				r = listener.zoom( initialFirstPointer.dst( initialSecondPointer ) , firstPointer.dst( secondPointer ) ) || result || r;
			}
			return listener.onTouchDragged( x , y , pointer ) || r;
		}
		// update tracker
		tracker.update( x , y , Gdx.input.getCurrentEventTime() );
		// check if we are still tapping.
		if( !( inTapSquare && Math.abs( x - tapSquareCenterX ) < tapSquareSize && Math.abs( y - tapSquareCenterY ) < tapSquareSize ) )
		{
			///Log.v("touch","handle scroll1 : " + inTapSquare);
			inTapSquare = false;
		}
		if( !inTapSquare && pointer == 0 )
		{
			// handle scroll
			//Log.v("touch","handle scroll2");
			inTapSquare = false;
			panning = true;
			r = listener.pan( tracker.lastX , tracker.lastY , tracker.deltaX , tracker.deltaY ) || r;
		}
		//		else {
		//			// handle longpress
		//			if (!longPressFired && Gdx.input.getCurrentEventTime() - gestureStartTime > longPressDuration) {
		//				longPressFired = true;
		//				r = r|| listener.longPress(x, y);
		//			}
		//		}
		return listener.onTouchDragged( x , y , pointer ) || r;
	}
	
	public boolean touchUp(
			int x ,
			int y ,
			int pointer ,
			int button )
	{
		if( pointer > 1 )
			return false;
		if( y < 0 || y > UtilsBase.getScreenHeight() )
		{//超出屏幕区域
			y = y < 0 ? 0 : UtilsBase.getScreenHeight() - 1;
		}
		if( x < 0 || x > UtilsBase.getScreenWidth() - 1 )
		{
			x = x < 0 ? 0 : UtilsBase.getScreenWidth() - 1;
		}
		//Log.v("widget","up x:" + x + " y:" + y + " p:" + pointer);
		isPressDown[pointer] = false;
		//isPressDown[1] = false;
		panning = false;
		boolean r = false;
		if( inTapSquare & !longPressFired )
		{
			// handle taps
			if( System.nanoTime() - lastTapTime > tapCountInterval )
			{
				Log.v( "tap" , "reset" );
				tapCount = 0;
			}
			tapCount++;
			lastTapTime = System.nanoTime();
			gestureStartTime = 0;
			//			if(pointer == 0)
			r = listener.tap( tapSquareCenterX , tapSquareCenterY , tapCount ) || r;
		}
		else if( pinching )
		{
			// handle pinch end
			pinching = false;
			panning = true;
			// we are basically in pan/scroll mode again, reset velocity tracker
			if( pointer == 0 )
			{
				// first pointer has lifted off, set up panning to use the second pointer...
				tracker.start( (int)secondPointer.x , (int)secondPointer.y , Gdx.input.getCurrentEventTime() );
			}
			else
			{
				// second pointer has lifted off, set up panning to use the first pointer...
				tracker.start( (int)firstPointer.x , (int)firstPointer.y , Gdx.input.getCurrentEventTime() );
			}
		}
		else
		{
			gestureStartTime = 0;
			// handle fling
			long time = Gdx.input.getCurrentEventTime();
			if( time - tracker.lastTime < maxFlingDelay )
			{
				tracker.update( x , y , time );
				//				if(pointer == 0)
				r = listener.fling( tracker.getVelocityX() , tracker.getVelocityY() ) || r;
			}
		}
		return listener.onTouchUp( x , y , pointer ) || r;
	}
	
	public void handleLongPress()
	{
		if( isPressDown[0] && inTapSquare )
		{
			//			Log.v("click2"," long press inTapSquare :"  + longPressFired +" x:" + px + " y:" + py + " cur:" + Gdx.input.getCurrentEventTime() + " start:" + gestureStartTime + "  11:" +(Gdx.input.getCurrentEventTime() - gestureStartTime > longPressDuration));
			if( !longPressFired && System.nanoTime() - gestureStartTime > longPressDuration )
			{
				longPressFired = true;
				listener.longPress( px , py );
				//Log.e("opt", "fireLongPress!!!");
				//				Log.v("click2"," long press x:" + px + " y:" + py + " dur:" + (Gdx.input.getCurrentEventTime() - gestureStartTime)/1000000000l);
			}
		}
	}
	
	/** @return whether the user touched the screen long enough to trigger a long press event. */
	public boolean isLongPressed()
	{
		return isLongPressed( longPressDuration );
	}
	
	/** @param duration
	 * @return whether the user touched the screen for as much or more than the given duration. */
	public boolean isLongPressed(
			float duration )
	{
		if( gestureStartTime == 0 )
			return false;
		return System.nanoTime() - gestureStartTime > (long)( duration * 1000000000l );
	}
	
	public boolean isPanning()
	{
		return panning;
	}
	
	public boolean[] getPointerPressDown()
	{
		return isPressDown;
	}
	
	public void reset()
	{
		gestureStartTime = 0;
		panning = false;
		inTapSquare = false;
		isPressDown[0] = false;
		isPressDown[1] = false;
	}
	
	static class VelocityTracker3D
	{
		
		int sampleSize = 10;
		int lastX;
		int lastY;
		int deltaX;
		int deltaY;
		long lastTime;
		int numSamples;
		float[] meanX = new float[sampleSize];
		float[] meanY = new float[sampleSize];
		long[] meanTime = new long[sampleSize];
		
		public void start(
				int x ,
				int y ,
				long timeStamp )
		{
			lastX = x;
			lastY = y;
			deltaX = 0;
			deltaY = 0;
			numSamples = 0;
			for( int i = 0 ; i < sampleSize ; i++ )
			{
				meanX[i] = 0;
				meanY[i] = 0;
				meanTime[i] = 0;
			}
			lastTime = timeStamp;
		}
		
		public void update(
				int x ,
				int y ,
				long timeStamp )
		{
			long currTime = timeStamp;
			deltaX = ( x - lastX );
			deltaY = ( y - lastY );
			lastX = x;
			lastY = y;
			long deltaTime = currTime - lastTime;
			lastTime = currTime;
			int index = numSamples % sampleSize;
			meanX[index] = deltaX;
			meanY[index] = deltaY;
			meanTime[index] = deltaTime;
			numSamples++;
		}
		
		public float getVelocityX()
		{
			float meanX = getAverage( this.meanX , numSamples );
			float meanTime = getAverage( this.meanTime , numSamples ) / 1000000000.0f;
			if( meanTime == 0 )
				return 0;
			return meanX / meanTime;
		}
		
		public float getVelocityY()
		{
			float meanY = getAverage( this.meanY , numSamples );
			float meanTime = getAverage( this.meanTime , numSamples ) / 1000000000.0f;
			if( meanTime == 0 )
				return 0;
			return meanY / meanTime;
		}
		
		private float getAverage(
				float[] values ,
				int numSamples )
		{
			numSamples = Math.min( sampleSize , numSamples );
			float sum = 0;
			for( int i = 0 ; i < numSamples ; i++ )
			{
				sum += values[i];
			}
			return sum / numSamples;
		}
		
		private long getAverage(
				long[] values ,
				int numSamples )
		{
			numSamples = Math.min( sampleSize , numSamples );
			long sum = 0;
			for( int i = 0 ; i < numSamples ; i++ )
			{
				sum += values[i];
			}
			if( numSamples == 0 )
				return 0;
			return sum / numSamples;
		}
		
		private float getSum(
				float[] values ,
				int numSamples )
		{
			numSamples = Math.min( sampleSize , numSamples );
			float sum = 0;
			for( int i = 0 ; i < numSamples ; i++ )
			{
				sum += values[i];
			}
			if( numSamples == 0 )
				return 0;
			return sum;
		}
	}
}
