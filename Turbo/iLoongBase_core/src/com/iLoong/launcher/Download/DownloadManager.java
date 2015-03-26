// package com.iLoong.launcher.Download;
//
//
// import android.app.AlertDialog;
// import android.app.AlertDialog.Builder;
// import android.content.Context;
// import android.content.DialogInterface;
// import android.content.DialogInterface.OnCancelListener;
// import android.content.DialogInterface.OnClickListener;
// import android.graphics.Bitmap;
// import android.graphics.drawable.BitmapDrawable;
//
// import com.cooee.download.BaseDownloadHelper;
// import com.iLoong.base.R;
// import com.iLoong.launcher.UI3DEngine.Messenger;
//
//
// /**
// * Represents an item in the launcher.
// */
// public class DownloadManager implements BaseDownloadHelper.DownloadListener
// {
//
// private static AlertDialog alertDialog;
//
// @Override
// public void setProxy(
// Object obj )
// {
// // TODO Auto-generated method stub
// }
//
// @Override
// public void onDownloadSuccess()
// {
// // TODO Auto-generated method stub
// }
//
// @Override
// public void onDownloadFail()
// {
// // TODO Auto-generated method stub
// }
//
// @Override
// public void onDownloadProgress(
// int progress )
// {
// // TODO Auto-generated method stub
// }
//
// @Override
// public void onInstallSuccess(
// String packageName )
// {
// // TODO Auto-generated method stub
// }
//
// @Override
// public void showMessage(
// String message )
// {
// // TODO Auto-generated method stub
// Messenger.sendMsg( Messenger.MSG_TOAST , message );
// }
//
// public static void ToDownloadApkDialog(
// final Context mContext ,
// Bitmap icon ,
// final String mTitle ,
// final String mPackageName ,
// final boolean mIsInstallAfterDownloadComplete )
// {
// AlertDialog.Builder builder = new Builder( mContext );
// if( icon != null )
// {
// builder.setIcon( new BitmapDrawable( mContext.getResources() , icon ) );
// }
// builder.setTitle( mTitle );
// builder.setMessage( "\"" + mTitle + "\"" + mContext.getResources().getString( R.string.to_download_content ) );
// builder.setPositiveButton( mContext.getResources().getString( R.string.to_download_ok ) , new OnClickListener() {
//
// @Override
// public void onClick(
// DialogInterface dialog ,
// int which )
// {
// Messenger.sendDownloadPackageMessage( mTitle , mPackageName , mIsInstallAfterDownloadComplete );
// dialog.dismiss();
// alertDialog = null;
// }
// } );
// builder.setNegativeButton( mContext.getResources().getString( R.string.to_download_cancel ) , new OnClickListener() {
//
// @Override
// public void onClick(
// DialogInterface dialog ,
// int which )
// {
// dialog.dismiss();
// alertDialog = null;
// }
// } );
// builder.setOnCancelListener( new OnCancelListener() {
//
// @Override
// public void onCancel(
// DialogInterface dialog )
// {
// // TODO Auto-generated method stub
// alertDialog = null;
// }
// } );
// // builder.create().show();
// alertDialog = builder.create();
// alertDialog.show();
// }
//
// public static void closeAlertDialog()
// {
// if( alertDialog != null )
// {
// alertDialog.dismiss();
// alertDialog = null;
// }
// }
//}
