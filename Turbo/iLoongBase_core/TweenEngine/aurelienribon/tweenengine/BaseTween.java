package aurelienribon.tweenengine;


public abstract class BaseTween<T>
{
	
	public long id;
	
	public void free()
	{
	}
	
	public T start(
			TweenManager manager )
	{
		return (T)this;
	}
	
	public T delay(
			float delay )
	{
		return (T)this;
	}
	
	public T setCallback(
			TweenCallback callback )
	{
		return (T)this;
	}
	
	public T setCallbackTriggers(
			int flags )
	{
		return (T)this;
	}
	
	public Object getUserData()
	{
		return null;
	}
	
	public T setUserData(
			Object data )
	{
		return (T)this;
	}
	
	public T build()
	{
		return (T)this;
	}
	
	public T repeatYoyo(
			int count ,
			float delay )
	{
		return (T)this;
	}
	
	public T repeat(
			int count ,
			float delay )
	{
		return (T)this;
	}
	
	public void kill()
	{
	}
	
	public void pause()
	{
	}
	
	public void resume()
	{
	}
	
	public boolean isPaused()
	{
		return false;
	}
}
