package cool.sdk.Category;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;


public class CateBloom extends CateBloomJni
{
	
	static CateBloom _instance = null;
	private long blocks = 0;
	Context context;
	
	private CateBloom(
			Context context )
	{
		this.context = context;
	}
	
	public static CateBloom getInstince(
			Context context )
	{
		synchronized( CateBloom.class )
		{
			if( _instance == null )
			{
				_instance = new CateBloom( context );
				if( !_instance.init() )
				{
					_instance = null;
				}
			}
			return _instance;
		}
	}
	
	protected void finalize()
	{
		this.free( this.blocks );
	}
	
	private Boolean initByPath(
			String block_path )
	{
		long blocks = 0;
		if( 0 < ( blocks = this.initpath( block_path ) ) )
		{
			this.blocks = blocks;
			return true;
		}
		return false;
	}
	
	private Boolean init()
	{
		return this.initByAssetPath( "cate_bloom_" + version + ".bin" );
	}
	
	private Boolean initByAssetPath(
			String asset_path )
	{
		InputStream inStream = null;
		try
		{
			inStream = context.getAssets().open( asset_path );
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return false;
		}
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		try
		{
			while( ( rc = inStream.read( buff , 0 , 100 ) ) > 0 )
			{
				swapStream.write( buff , 0 , rc );
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return false;
		}
		if( !this.initByBuffer( swapStream.toByteArray() , swapStream.size() ) )
		{
			return false;
		}
		return true;
	}
	
	private Boolean initByBuffer(
			byte[] buffer ,
			int len )
	{
		long blocks = 0;
		if( 0 < ( blocks = this.initbytes( buffer , len ) ) )
		{
			this.blocks = blocks;
			return true;
		}
		return false;
	}
	
	public int pnameToCaid(
			String pname )
	{
		if( 0 == this.blocks )
		{
			return -1;
		}
		int caid = -1;
		if( 0 <= ( caid = this.pname2caid( this.blocks , pname ) ) )
		{
			return caid;
		}
		return -1;
	}
	
	static String[][] cate_frame_array = {
			{ "-1" , "-2" , "更多应用" , "More App" , "10" } ,
			{ "0" , "-2" , "系统自带" , "System" , "9" } ,
			{ "800" , "-2" , "生活" , "Daily life" , "5" } ,
			{ "801" , "-2" , "新闻" , "News" , "3" } ,
			{ "802" , "-2" , "沟通" , "Communicate" , "1" } ,
			{ "803" , "-2" , "影音" , "Audio and video" , "2" } ,
			{ "804" , "-2" , "阅读" , "Read" , "4" } ,
			{ "805" , "-2" , "工具" , "Tools" , "7" } ,
			{ "806" , "-2" , "美化" , "Phone Beautify" , "8" } ,
			{ "807" , "-2" , "金融" , "Finance" , "6" } ,
			{ "808" , "-2" , "游戏" , "Game" , "0" } ,
			{ "900" , "800" , "便捷生活" , "Daily life" , "0" } ,
			{ "901" , "801" , "新闻资讯" , "News" , "0" } ,
			{ "902" , "802" , "通信聊天" , "Chat" , "0" } ,
			{ "903" , "800" , "网上购物" , "shopping" , "0" } ,
			{ "904" , "803" , "影音图像" , "Audio and video" , "0" } ,
			{ "905" , "800" , "出行必用" , "Travel" , "0" } ,
			{ "906" , "804" , "阅读学习" , "Read" , "0" } ,
			{ "907" , "802" , "社交网络" , "Social network" , "0" } ,
			{ "908" , "805" , "常用工具" , "Tools" , "0" } ,
			{ "909" , "805" , "性能优化" , "Phone optimization" , "0" } ,
			{ "910" , "806" , "美化手机" , "Phone Beautify" , "0" } ,
			{ "911" , "805" , "办公软件" , "Office software" , "0" } ,
			{ "912" , "800" , "育儿母婴" , "Yo infant" , "0" } ,
			{ "913" , "807" , "金融理财" , "Financial" , "0" } ,
			{ "914" , "808" , "休闲时间" , "Games" , "0" } ,
			{ "915" , "808" , "跑酷竞速" , "Games" , "0" } ,
			{ "916" , "808" , "宝石消除" , "Games" , "0" } ,
			{ "917" , "808" , "网络游戏" , "Games" , "0" } ,
			{ "918" , "808" , "动作射击" , "Games" , "0" } ,
			{ "919" , "808" , "扑克棋牌" , "Games" , "0" } ,
			{ "920" , "808" , "儿童益智" , "Games" , "0" } ,
			{ "921" , "808" , "塔防守卫" , "Games" , "0" } ,
			{ "922" , "808" , "体育格斗" , "Games" , "0" } ,
			{ "923" , "808" , "角色扮演" , "Games" , "0" } ,
			{ "924" , "808" , "经营策略" , "Games" , "0" } ,
			{ "950" , "805" , "应用市场" , "App Market" , "0" } ,
			{ "948" , "808" , "游戏市场" , "Game Market" , "0" } ,
			{ "949" , "808" , "游戏攻略" , "Youxi Gonglue" , "0" } ,
			{ "951" , "800" , "美食烹饪" , "Foods" , "0" } , };
	
	public CateFrameItem[] getCateFrameItemList()
	{
		String[][] frames = cate_frame_array;
		CateFrameItem[] items = new CateFrameItem[frames.length];
		for( int i = 0 ; i < frames.length ; i++ )
		{
			CateFrameItem item = new CateFrameItem();
			items[i] = item;
			String[] itemStr = frames[i];
			item.id = Integer.parseInt( itemStr[0] );
			item.pid = Integer.parseInt( itemStr[1] );
			item.cn = itemStr[2];
			item.en = itemStr[3];
			item.od = Integer.parseInt( itemStr[4] );
		}
		return items;
	}
}
