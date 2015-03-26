package com.iLoong.launcher.SetupMenu.Actions;


import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iLoong.RR;


public class DialogSeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener
{
	
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private static final String settingattr = "http://schemas.android.com/apk/res/com.iLoong.launcher.SetupMenu";
	private SeekBar mSeekBar;
	private TextView mValueText;
	private String mSuffix;
	private int mMax , mMin , mValue = 0;
	private int mScale = 100;
	
	public DialogSeekBarPreference(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		setPersistent( true );
		mSuffix = attrs.getAttributeValue( androidns , "text" );
		mMin = attrs.getAttributeIntValue( settingattr , "min" , 0 );
		mMax = attrs.getAttributeIntValue( androidns , "max" , 100 );
		setDialogLayoutResource( RR.layout.seekbar_preference );
	}
	
	@Override
	protected void onBindDialogView(
			View v )
	{
		super.onBindDialogView( v );
		TextView dialogMessage = (TextView)v.findViewById( RR.id.dialogMessage );
		dialogMessage.setText( getDialogMessage() );
		mValueText = (TextView)v.findViewById( RR.id.actualValue );
		mSeekBar = (SeekBar)v.findViewById( RR.id.myBar );
		mSeekBar.setOnSeekBarChangeListener( this );
		mSeekBar.setMax( mMax - mMin );
		mSeekBar.setProgress( mValue - mMin );
		String t = String.valueOf( mValue * mScale );
		mValueText.setText( mSuffix == null ? t : t.concat( mSuffix ) );
	}
	
	@Override
	protected Object onGetDefaultValue(
			TypedArray a ,
			int index )
	{
		return a.getInt( index , mMin );
	}
	
	@Override
	protected void onSetInitialValue(
			boolean restore ,
			Object defaultValue )
	{
		if( defaultValue == null || ( (Integer)defaultValue ).intValue() == 0 )
			mValue = mMin;
		else
			mValue = getPersistedInt( (Integer)defaultValue );
	}
	
	@Override
	protected void onDialogClosed(
			boolean positiveResult )
	{
		super.onDialogClosed( positiveResult );
		if( positiveResult )
		{
			int value = mSeekBar.getProgress() + mMin;
			if( callChangeListener( value ) )
			{
				setValue( value );
			}
		}
	}
	
	public void setValue(
			int value )
	{
		if( value > mMax )
		{
			value = mMax;
		}
		else if( value < mMin )
		{
			value = mMin;
		}
		mValue = value;
		persistInt( value );
	}
	
	public void setMax(
			int max )
	{
		mMax = max;
		if( mValue > mMax )
		{
			setValue( mMax );
		}
	}
	
	public void setMin(
			int min )
	{
		if( min < mMax )
		{
			mMin = min;
		}
	}
	
	public void onProgressChanged(
			SeekBar seek ,
			int value ,
			boolean fromTouch )
	{
		String t = String.valueOf( value * mScale + mMin * mScale );
		mValueText.setText( mSuffix == null ? t : t.concat( mSuffix ) );
	}
	
	public void onStartTrackingTouch(
			SeekBar seek )
	{
	}
	
	public void onStopTrackingTouch(
			SeekBar seek )
	{
	}
}
