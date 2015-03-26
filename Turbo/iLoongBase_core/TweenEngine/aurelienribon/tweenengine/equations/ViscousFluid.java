package aurelienribon.tweenengine.equations;


import aurelienribon.tweenengine.TweenEquation;


/**
 * Viscous fluid equation based on android.widget.Scroller
 * @author Su Yu | suyu@cooee.cn
 */
public abstract class ViscousFluid extends TweenEquation
{
	
	public static final ViscousFluid INOUT = new ViscousFluid() {
		
		// This controls the viscous fluid effect (how much of it)
		final float mViscousFluidScale = 8.0f;
		// must be set to 1.0 (used in viscousFluid())
		float mViscousFluidNormalize = 1.0f;
		// init flag
		boolean init = false;
		
		float viscousFluid(
				float x )
		{
			x = x * mViscousFluidScale;
			if( x < 1.0f )
			{
				x -= ( 1.0f - (float)Math.exp( -x ) );
			}
			else
			{
				float start = 0.36787944117f;
				x = 1.0f - (float)Math.exp( 1.0f - x );
				x = start + x * ( 1.0f - start );
			}
			x *= mViscousFluidNormalize;
			return x;
		}
		
		@Override
		public float compute(
				float t ,
				float b ,
				float c ,
				float d )
		{
			if( !init )
			{
				mViscousFluidNormalize = 1.0f / viscousFluid( mViscousFluidNormalize );
				init = true;
			}
			float x = viscousFluid(t / d); 
			return c * x + b;
		}
		
		@Override
		public String toString()
		{
			return "ViscousFluid.INOUT";
		}
	};
}
