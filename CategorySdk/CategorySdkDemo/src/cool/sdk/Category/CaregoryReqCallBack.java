package cool.sdk.Category;


import java.util.List;


public interface CaregoryReqCallBack
{
	
	void ReqFailed(
			CaregoyReqType type ,
			String Msg );
	
	void ReqSucess(
			CaregoyReqType type ,
			List<String> appList );
}
