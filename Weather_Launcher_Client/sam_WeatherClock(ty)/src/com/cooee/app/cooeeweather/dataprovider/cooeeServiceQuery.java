package com.cooee.app.cooeeweather.dataprovider;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import com.cooee.app.cooeeweather.dataentity.weatherdataentity;
import com.cooee.app.cooeeweather.dataentity.weatherforecastentity;
import com.cooee.app.cooeeweather.dataprovider.weatherwebservice.FLAG_UPDATE;
import com.cooee.app.cooeeweather.filehelp.Log;


public class cooeeServiceQuery {
	private static final String TAG = "com.cooee.weather.dataprovider.cooeeServiceQuery";
	// COOEE 预报URL
	public final static String COOEE_FORCAST_URL = "http://widget.coeeland.com/w2/tianqi2.ashx?city=%s&m=2603&l=320480&f=2424&s=B01_HVGA&imsi=460008623197253&sc=+8613800210500&iccid=898600810910f6287253";

	private static String Update_city = null;
	//weijie_20121210_01
	private static final int TIMEOUT_CONNECT = 10*1000;//设置请求超时10秒钟  
	private static final int TIMEOUT_SOCKET = 30*1000;  //设置等待数据超时时间10秒钟 

	public static weatherdataentity CooeeWeatherDataUpdate(String city_num) {
		weatherdataentity dataentity = null;
		// weather forcast data
		if (city_num == null) {
			Log.v(TAG, "No postalcode");
			return null;
		}
		Update_city = city_num;
		Reader responseReader = null;
		//weijie_20121210_01 
		BasicHttpParams httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECT);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);  
		HttpClient client = new DefaultHttpClient(httpParameters);
		//END
		//original 
		//HttpClient client = new DefaultHttpClient();

		try {
			String encode_city = URLEncoder.encode(city_num, "UTF-8");
			HttpGet request = new HttpGet(String.format(COOEE_FORCAST_URL,
					encode_city));
			HttpResponse response = client.execute(request);

			int status = response.getStatusLine().getStatusCode();
			Log.d(TAG, "Request returned status " + status);
			if (status == 200) {
				HttpEntity entity = response.getEntity();
				responseReader = new InputStreamReader(entity.getContent(),
						"GB2312");
				dataentity = CooeeWeatherParseWeatherData(responseReader);
				com.cooee.app.cooeeweather.dataprovider.weatherwebservice.Update_Result_Flag = FLAG_UPDATE.UPDATE_SUCCES;
			}else
			{
				//shanjie deal with other
				weatherwebservice.Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
			}
		} catch (Exception e) {
			Log.e(TAG, "HttpResponse: request error!");
			e.printStackTrace();
			com.cooee.app.cooeeweather.dataprovider.weatherwebservice.Update_Result_Flag = FLAG_UPDATE.WEBSERVICE_ERROR;
		}
		return dataentity;
	}

	/**
	 * prase data and insert data to object(weatherdataentity)
	 */
	public static weatherdataentity CooeeWeatherParseWeatherData(
			Reader responseReader) {
		weatherdataentity dataEntity = new weatherdataentity();
		weatherforecastentity forecastentity = null;

		char[] buffer = new char[1024];
		try {
			responseReader.read(buffer);//json 数据解析
			if (buffer.length > 100) {
				String buf = new String(buffer);
				String s[] = buf.split("</it>");
				queryCurWeatherData(s[0], dataEntity);
				for (int i = 0; i < 4; i++) {
					forecastentity = cooeeForcastDataQuery(s[i]);
					dataEntity.getDetails().add(forecastentity);
				}
			} else {
				com.cooee.app.cooeeweather.dataprovider.weatherwebservice.Update_Result_Flag = FLAG_UPDATE.INVILIDE_VALUE;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			com.cooee.app.cooeeweather.dataprovider.weatherwebservice.Update_Result_Flag = FLAG_UPDATE.DATAPROVIDER_ERROR;
		}
		return dataEntity;
	}

	public static void queryCurWeatherData(String s,
			weatherdataentity dataEntity) {
		dataEntity.setCity(Update_city);
		dataEntity.setPostalCode(Update_city);

		String c1[] = s.split("<wd>");
		String c2[] = c1[1].split("</wd>");
		CooeeCurFormatTemprature(dataEntity, c2[0]);

		String t1[] = s.split("<tq>");
		String t2[] = t1[1].split("</tq>");
		getCondition(t2[0], dataEntity);

		dataEntity.setTempC(CalTempC(dataEntity.getTempH(),
				dataEntity.getTempL()));
	}

	/**
	 * format the temprature of allday to low and high
	 */
	public static void CooeeCurFormatTemprature(weatherdataentity dataEntity,
			String temp) {
		String[] s = temp.split("\\,");

		String high = s[1];
		String low = s[0];
		dataEntity.setTempL(Integer.parseInt(low));
		dataEntity.setTempH(Integer.parseInt(high));
	}

	public static void getCondition(String s, weatherdataentity dataEntity) {
		String t[] = s.split("\\,");

		if (t[0].equals(s)) {
			dataEntity.setCondition(t[0]);
		} else {
			Date dates = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dates);
			int hour = cal.get(Calendar.HOUR_OF_DAY);

			if (hour >= 0 && hour < 18) {
				dataEntity.setCondition(t[0]);
			} else {
				dataEntity.setCondition(t[1]);
			}
		}
	}

	public static weatherforecastentity cooeeForcastDataQuery(String s) {
		weatherforecastentity forecastentity = new weatherforecastentity();

		String t1[] = s.split("<tq>");
		String t2[] = t1[1].split("</tq>");
		String t3[] = t2[0].split("\\,");
		if (t3[0].equals(t3[1])) {
			forecastentity.setCondition(t3[0]);
		} else {
			String res = t2[0].replaceAll(",", "转");
			forecastentity.setCondition(res);
		}

		String c1[] = s.split("<wd>");
		String c2[] = c1[1].split("</wd>");
		CooeeFormatTemprature(forecastentity, c2[0]);

		String w1[] = s.split("<xq>");
		String w2[] = w1[1].split("</xq>");
		int week = formatDayOfWeek(w2[0]);
		forecastentity.setDayOfWeek(week);
		return forecastentity;
	}

	/**
	 * format the temprature of allday to low and high
	 */
	public static void CooeeFormatTemprature(weatherforecastentity dataEntity,
			String temp) {
		String[] s = temp.split("\\,");

		String high = s[1];
		String low = s[0];
		dataEntity.setLow(Integer.parseInt(low));
		dataEntity.setHight(Integer.parseInt(high));
	}

	private static Integer CalTempC(Integer tempH, Integer tempL) {
		Integer tempC = 0;
		Calendar cal = Calendar.getInstance();
		Date date = null;
		// 获取小时
		try {
			URL url = new URL("http://www.bjtime.cn");// 取得资源对象
			URLConnection uc = url.openConnection();// 生成连接对象
			uc.connect(); // 发出连接
			long ld = uc.getDate(); // 取得网站日期时间
			date = new Date(ld);
		} catch (Exception e) {

		}
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		float f;
		if (hour >= 0 && hour < 6) {
			tempC = tempL;
		} else if (hour >= 6 && hour <= 14) {
			f = tempH - tempL;
			f = f / (14 - 5) * (hour - 5);
			tempC = tempL + (int) f;
		} else if (hour >= 15 && hour <= 20) {
			f = tempH - tempL;
			f = f / (20 - 14) * (hour - 14);
			tempC = tempH - (int) f;
		} else if (hour > 20 && hour < 24) {
			tempC = tempL;
		}
		return tempC;
	}
	
	public static int formatDayOfWeek(String week){
		int dayofweek = 0;
		if(week.equals("星期日")){
			dayofweek = 0;
		}else if(week.equals("星期一")){
			dayofweek = 1;
		}else if(week.equals("星期二")){
			dayofweek = 2;
		}else if(week.equals("星期三")){
			dayofweek = 3;
		}else if(week.equals("星期四")){
			dayofweek = 4;
		}else if(week.equals("星期五")){
			dayofweek = 5;
		}else if(week.equals("星期六")){
			dayofweek = 6;
		}
		return dayofweek;
	}
}
