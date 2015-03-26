package com.blueflash.interfaces;
interface IKingsReviveService2 {
	
	/*	
     *返回数组3组1维是：清理进程数，二组是清理内存大小(M)，三维是关闭自启个数
	 */
	float[] doKillProcessResult(boolean isShowToast);
} 