package com.iLoong.launcher.Desktop3D.APageEase;


import com.badlogic.gdx.graphics.Color;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;


public class GS4
{
	
	final public static float DEFAULT_ANGLE = -60;
	
	public static void updateEffect(
			ViewGroup3D pre_view ,
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float scroll_degree ,
			float this_width ,
			boolean is_Thumbnail )
	{
		Color color;
		float distance;
		if( is_Thumbnail )
		{
			distance = this_width;
		}
		else
		{
			distance = this_width * 5 / 8;
			ViewGroup3D parent = cur_view.getParent();
			if( parent == next_view.getParent() )
			{
				if( cur_view.getIndexInParent() < next_view.getIndexInParent() )
				{
					parent.addViewBefore( cur_view , next_view );
				}
			}
			if( parent == pre_view.getParent() )
			{
				if( cur_view.getIndexInParent() < pre_view.getIndexInParent() )
				{
					parent.addViewBefore( cur_view , pre_view );
				}
			}
		}
		float degree = scroll_degree - (int)scroll_degree;
		if( degree < 0.5 )
		{
			cur_view.setPosition( degree * -distance , 0 );
			//teapotXu add start
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
				cur_view.originX = this_width / 2;
			else
				//teapotXu add end
				cur_view.originX = 0;
			cur_view.setRotationY( degree * -DEFAULT_ANGLE );
			color = cur_view.getColor();
			if( is_Thumbnail )
				color.a = 1.0f;
			else
				color.a = 1f - Math.abs( degree );
			cur_view.setColor( color );
			if( cur_view instanceof CellLayout3D )
			{
				( (CellLayout3D)cur_view ).gs4_alpha = color.a;
				( (CellLayout3D)cur_view ).hl_x = degree * this_width;
				( (CellLayout3D)cur_view ).hl_w = ( 1 - degree ) * this_width;
				( (CellLayout3D)cur_view ).hl_u0 = 0.0f;
				( (CellLayout3D)cur_view ).hl_u1 = 1 - degree;
			}
			if( degree == 0.0f && scroll_degree != 0.0f )
			{
				next_view.setPosition( cur_view.x + distance , 0 );
				next_view.originX = this_width;
				next_view.setRotationY( ( 1 - degree ) * DEFAULT_ANGLE );
				color = next_view.getColor();
				if( is_Thumbnail )
					color.a = 1.0f;
				else
					color.a = ( 0.5f + Math.abs( degree ) ) / 2;
				next_view.setColor( color );
				if( next_view instanceof CellLayout3D )
				{
					( (CellLayout3D)next_view ).gs4_alpha = color.a;
					( (CellLayout3D)next_view ).hl_x = 0.0f;
					( (CellLayout3D)next_view ).hl_w = degree * this_width;
					( (CellLayout3D)next_view ).hl_u0 = 1 - degree;
					( (CellLayout3D)next_view ).hl_u1 = 1.0f;
				}
				pre_view.setPosition( cur_view.x - distance , 0 );
				pre_view.originX = 0;
				pre_view.setRotationY( ( degree + 1 ) * -DEFAULT_ANGLE );
				color = pre_view.getColor();
				if( is_Thumbnail )
					color.a = 1.0f;
				else
					color.a = ( 0.5f - Math.abs( degree ) ) / 2;
				pre_view.setColor( color );
				if( pre_view instanceof CellLayout3D )
				{
					( (CellLayout3D)pre_view ).gs4_alpha = color.a;
					( (CellLayout3D)pre_view ).hl_x = this_width;
					( (CellLayout3D)pre_view ).hl_w = 0.0f;
					( (CellLayout3D)pre_view ).hl_u0 = 0.0f;
					( (CellLayout3D)pre_view ).hl_u1 = 0.0f;
				}
			}
			else
			{
				pre_view.setPosition( cur_view.x - distance , 0 );
				pre_view.originX = 0;
				pre_view.setRotationY( ( degree + 1 ) * -DEFAULT_ANGLE );
				color = pre_view.getColor();
				if( is_Thumbnail )
					color.a = 1.0f;
				else
					color.a = ( 0.5f - Math.abs( degree ) ) / 2;
				pre_view.setColor( color );
				if( pre_view instanceof CellLayout3D )
				{
					( (CellLayout3D)pre_view ).gs4_alpha = color.a;
					( (CellLayout3D)pre_view ).hl_x = this_width;
					( (CellLayout3D)pre_view ).hl_w = 0.0f;
					( (CellLayout3D)pre_view ).hl_u0 = 0.0f;
					( (CellLayout3D)pre_view ).hl_u1 = 0.0f;
				}
				next_view.setPosition( cur_view.x + distance , 0 );
				next_view.originX = this_width;
				next_view.setRotationY( ( 1 - degree ) * DEFAULT_ANGLE );
				color = next_view.getColor();
				if( is_Thumbnail )
					color.a = 1.0f;
				else
					color.a = ( 0.5f + Math.abs( degree ) ) / 2;
				next_view.setColor( color );
				if( next_view instanceof CellLayout3D )
				{
					( (CellLayout3D)next_view ).gs4_alpha = color.a;
					( (CellLayout3D)next_view ).hl_x = 0.0f;
					( (CellLayout3D)next_view ).hl_w = degree * this_width;
					( (CellLayout3D)next_view ).hl_u0 = 1 - degree;
					( (CellLayout3D)next_view ).hl_u1 = 1.0f;
				}
			}
		}
		else
		{
			cur_view.setPosition( ( 1 - degree ) * distance , 0 );
			cur_view.originX = this_width;
			cur_view.setRotationY( ( 1 - degree ) * DEFAULT_ANGLE );
			color = cur_view.getColor();
			if( is_Thumbnail )
				color.a = 1.0f;
			else
				color.a = Math.abs( degree );
			cur_view.setColor( color );
			if( cur_view instanceof CellLayout3D )
			{
				( (CellLayout3D)cur_view ).gs4_alpha = color.a;
				( (CellLayout3D)cur_view ).hl_x = 0.0f;
				( (CellLayout3D)cur_view ).hl_w = degree * this_width;
				( (CellLayout3D)cur_view ).hl_u0 = 1 - degree;
				( (CellLayout3D)cur_view ).hl_u1 = 1.0f;
			}
			next_view.setPosition( cur_view.x + distance , 0 );
			next_view.originX = this_width;
			next_view.setRotationY( ( degree - 2 ) * -DEFAULT_ANGLE );
			color = next_view.getColor();
			if( is_Thumbnail )
				color.a = 1.0f;
			else
				color.a = ( Math.abs( degree ) - 0.5f ) / 2;
			next_view.setColor( color );
			if( next_view instanceof CellLayout3D )
			{
				( (CellLayout3D)next_view ).gs4_alpha = color.a;
				( (CellLayout3D)next_view ).hl_x = 0.0f;
				( (CellLayout3D)next_view ).hl_w = 0.0f;
				( (CellLayout3D)next_view ).hl_u0 = 1.0f;
				( (CellLayout3D)next_view ).hl_u1 = 1.0f;
			}
			pre_view.setPosition( cur_view.x - distance , 0 );
			pre_view.originX = 0;
			pre_view.setRotationY( degree * -DEFAULT_ANGLE );
			color = pre_view.getColor();
			if( is_Thumbnail )
				color.a = 1.0f;
			else
				color.a = ( 1.5f - Math.abs( degree ) ) / 2;
			pre_view.setColor( color );
			if( pre_view instanceof CellLayout3D )
			{
				( (CellLayout3D)pre_view ).gs4_alpha = color.a;
				( (CellLayout3D)pre_view ).hl_x = degree * this_width;
				( (CellLayout3D)pre_view ).hl_w = ( 1 - degree ) * this_width;
				( (CellLayout3D)pre_view ).hl_u0 = 0.0f;
				( (CellLayout3D)pre_view ).hl_u1 = 1 - degree;
			}
		}
		//		color.a= 1f;
		//    	cur_view.setColor(color);
		//    	pre_view.setColor(color);
		//    	next_view.setColor(color);
	}
}
