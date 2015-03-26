package com.cooee.uiengine.util.network;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cooee.uiengine.util.network.HttpDiscriptor.REQUEST_TYPE;

/**
 * 
 * @author Zhongqihong
 * 
 *         MD5网络访问方式实现
 * 
 */
public class HttpMD5 extends HttpAsyncBiz {

	public HttpMD5() {

	}

	@Override
	public void loadAsync(HttpDiscriptor httpDisc) {

		if (httpDisc.getReqType() == REQUEST_TYPE.REQUEST_POST) {
			post(httpDisc);
		} else
			super.loadAsync(httpDisc);

	}

	public void post(HttpDiscriptor httpDisc) {
		String urlContent = "";
		String finalString = "";
		String combineContent = httpDisc.getContent() + httpDisc.getMd5_key();
		MessageDigest messagedigest = null;
		try {
			messagedigest = MessageDigest.getInstance("MD5");
			messagedigest.update(combineContent.getBytes());
			urlContent = bufferToHex(messagedigest.digest());
			finalString = httpDisc.getContent() + urlContent;
			httpDisc.setResult(HttpHelper.post(httpDisc.getUrl(), finalString));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}

	}

	@Override
	public Object resolve(HttpDiscriptor httpDisc) {

		return super.resolve(httpDisc);

	}

	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4]; // 取字节中高 4 位的数字转换, >>>
												// 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
		char c1 = hexDigits[bt & 0xf]; // 取字节中低 4 位的数字转换
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

}
