package com.iLoong.launcher.SetupMenu.Actions;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.base.themebox.R;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.media.BottomBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ShowDesktop extends Action {
    private Toast toast=null;
    ShareHandler shareHandler =new ShareHandler();
    public ShowDesktop(int actionid, String action){
        super(actionid, action);
    }
    
    public static void Init(){
        SetupMenuActions.getInstance().RegisterAction(ActionSetting.ACTION_SHOW_DESKTOP,
                new ShowDesktop(ActionSetting.ACTION_SHOW_DESKTOP, ShowDesktop.class.getName()));
    }
    
    public void delayShare(){
        File fileDir =iLoongLauncher.getInstance().getFilesDir();
        Log.v("pckCls", "dir");
        Uri data =Uri.fromFile(new File(Utils3D.getScreenShotPath()));
       // Uri data =Uri.fromFile(new File(fileDir.getAbsolutePath()+File.separator+Utils3D.getScreenShotPath()));
        Intent shareIntent =new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);//Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putExtra(Intent.EXTRA_STREAM, data);
      //  shareIntent.setType("text/plain");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("image/*");
        shareIntent.putExtra("sms_body", SetupMenu.getContext().getResources().getString(RR.string.desktop_download_info));
        SendMsgToAndroid.startActivity(Intent.createChooser(shareIntent,iLoongLauncher.getInstance().getResources().getString(RR.string.media_share)));
       
    }
    @Override
    protected void OnRunAction() {
        if(Utils3D.getScreenShotPath()==null){
            if(toast!=null)
                toast.cancel();
            toast=Toast.makeText(iLoongLauncher.getInstance(), RR.string.insert_sd_card,
                    Toast.LENGTH_SHORT);
            toast.show();
            return; 
        }
        delayShare();
        
//        iLoongLauncher.getInstance().postRunnable(new Runnable() {
//            public void run() {
//                Message message = shareHandler.obtainMessage();
//                message.obj=Utils3D.getScreenShotPath();
//                message.what=ShareHandler.ON_SHARE;
//                shareHandler.sendMessage(message);
//            }
//        });
     
        
    
        
        
    }
    
    public static boolean saveBmpToSystem(Bitmap thumbnail,String name){
        
        File fileDir = iLoongLauncher.getInstance().getFilesDir();
        File file =new File(fileDir.getAbsolutePath()+File.separator+Utils3D.getScreenShotPath());
        FileOutputStream os ;
        FileInputStream is;
        try{
            os =new FileOutputStream(file);
            //os=iLoongLauncher.getInstance().openFileOutput(name,Activity.MODE_PRIVATE);
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
          
        }
        catch(IOException e1){
            
        }
        
        return true;
                
    }
    public static boolean saveBmp(Bitmap thumbnail,String path){

       
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        if(fOut == null) return false;
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return true;
    }
    @Override
    protected void OnActionFinish() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void OnPutValue(String key) {
        // TODO Auto-generated method stub
        
    }
    
    private class ShareHandler extends Handler{
        
        public static  final int ON_SHARE =1;
       
        
        @Override
        public void handleMessage(Message msg) {
             int wpOffsetX;
            super.handleMessage(msg);
            int what =msg.what;
            String path =msg.obj.toString();
            switch(what){
                
                case ON_SHARE:{
                    
                }break;
            }
            
        }
        
        
        
    }

}
