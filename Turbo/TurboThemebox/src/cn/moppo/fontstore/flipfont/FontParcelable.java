package cn.moppo.fontstore.flipfont;


import android.os.Parcel;
import android.os.Parcelable;


public class FontParcelable implements Parcelable
{
	
	public String mStr;
	
	public FontParcelable(
			String str )
	{
		mStr = str;
	}
	
	public int describeContents()
	{
		return 0;
	}
	
	public void writeToParcel(
			Parcel out ,
			int flags )
	{
		out.writeString( mStr );
	}
	
	public static final Parcelable.Creator<FontParcelable> CREATOR = new Parcelable.Creator<FontParcelable>() {
		
		public FontParcelable createFromParcel(
				Parcel in )
		{
			return new FontParcelable( in );
		}
		
		public FontParcelable[] newArray(
				int size )
		{
			return new FontParcelable[size];
		}
	};
	
	private FontParcelable(
			Parcel in )
	{
		mStr = in.readString();
	}
}
