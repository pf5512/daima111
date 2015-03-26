
package com.example.icsdk_app;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cool.sdk.Category.CaregoryReqCallBack;
import cool.sdk.Category.CaregoyReqType;
import cool.sdk.Category.CategoryHelper;
import cool.sdk.Category.RecommendApkInfo;
import cool.sdk.Category.RecommendInfo;
import cool.sdk.update.UpdateManagerImpl;

public class MainActivity extends Activity
{

    // Button butUpdateGetCfg = null;
    Button butUpdateFore = null; // 前台分类请求 ALL 安装的 APK
    Button butShowMapData = null;
    Button butShowDbData = null;
    Button butClearAllData = null;

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        UpdateManagerImpl.Resume(this);
    }

    @Override
    protected void onCreate(
            Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CategoryHelper.getInstance(getApplicationContext()).UpdateMapData();
        butUpdateFore = (Button) findViewById(R.id.butUpdateFore);
        butUpdateFore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(
                    View arg0)
            {
                new Thread(new Runnable() {

                    @Override
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        try
                        {
                            JSONArray appList = CategoryHelper.getInstance(getApplicationContext())
                                    .GetAllInstallAppList();
                            CategoryHelper.getInstance(getApplicationContext())
                                    .doCategoryRequestForeground(appList,
                                            new CaregoryReqCallBack() {

                                                @Override
                                                public void ReqSucess(
                                                        CaregoyReqType type,
                                                        List<String> appList)
                                                {
                                                    // TODO Auto-generated
                                                    // method stub
                                                    Log.v("COOL ", type + "SUCESS");
                                                    Log.v("COOL ", appList.size() + "**");
                                                    // Log.v( "COOL" ,
                                                    // "dictMap: " +
                                                    // CategoryHelper.dictMap );
                                                    // Log.v( "COOL" ,
                                                    // "treeMap: " +
                                                    // CategoryHelper.treeMap );
                                                    // Log.v( "COOL" ,
                                                    // "cateinfoMap: " +
                                                    // CategoryHelper.cateinfoMap
                                                    // );
                                                }

                                                @Override
                                                public void ReqFailed(
                                                        CaregoyReqType type,
                                                        String Msg)
                                                {
                                                    // TODO Auto-generated
                                                    // method stub
                                                    Log.v("COOL ", type + "Failed");
                                                    // Log.v( "COOL" ,
                                                    // "dictMap: " +
                                                    // CategoryHelper.dictMap );
                                                    // Log.v( "COOL" ,
                                                    // "treeMap: " +
                                                    // CategoryHelper.treeMap );
                                                    // Log.v( "COOL" ,
                                                    // "cateinfoMap: " +
                                                    // CategoryHelper.cateinfoMap
                                                    // );
                                                }
                                            });
                        }
                        catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                    }
                }).start();
                // TODO Auto-generated method stub
            }
        });
        butShowMapData = (Button) findViewById(R.id.butShowMapData);
        butShowMapData.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(
                    View arg0)
            {
                // TODO Auto-generated method stub
                Log.v("COOL", "dictMap: " + CategoryHelper.dictMap);
                Log.v("COOL", "treeMap: " + CategoryHelper.treeMap);
                Log.v("COOL", "cateinfoMap: " + CategoryHelper.cateinfoMap);
                String strJsonArry = CategoryHelper.getInstance(getApplicationContext()).getString(
                        "cateinfoFloderIDJSONArray", null);
                if (null != strJsonArry && strJsonArry.length() > 1)
                {
                    try
                    {
                        JSONArray cateinfoFloderIDJSONArray = new JSONArray(strJsonArry);
                        for (int i = 0; i < cateinfoFloderIDJSONArray.length(); i++)
                        {
                            int FloderID = cateinfoFloderIDJSONArray.getInt(i);
                            Log.v("COOL", "get  cateinfoFloderIDJSONArray : [" + i + "]" + FloderID);
                        }
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                    }
                }
                for (RecommendInfo RInfo : CategoryHelper.RecommendInfoMap.values())
                {
                    Map<String, RecommendApkInfo> apkinfoMap = RInfo.getApkinfoMap();
                    Log.v(
                            "COOL",
                            "    FolderID:" + RInfo.getFolderID() + "   FolderType:"
                                    + RInfo.getFolderType() + "    FolderEN:" + RInfo.getFolderEN()
                                    + "   FolderCN:" + RInfo.getFolderCN() + "  FolderFN:" + RInfo
                                            .getFolderFN());
                    for (RecommendApkInfo apkInfo : apkinfoMap.values())
                    {
                        Log.v(
                                "COOL",
                                "   pkgName:" + apkInfo.getPkgName() + "   ApkType:"
                                        + apkInfo.getApkType() + "   ApkSize:"
                                        + apkInfo.getApkSize() + "   ApkDLInfo:"
                                        + apkInfo.getApkDLInfo() + "   ApkVersionCode:" + apkInfo
                                                .getApkVersionCode() + "   ApkVersionName:"
                                        + apkInfo.getApkVersionName() + "   ApkEN:"
                                        + apkInfo.getApkEN() + "   ApkCN:" + apkInfo.getApkCN()
                                        + "   ApkFN:" + apkInfo
                                                .getApkFN() + "   ApkIconpath:"
                                        + apkInfo.getApkIconpath());
                    }
                }
            }
        });
        butShowDbData = (Button) findViewById(R.id.butShowDBData);
        butShowDbData.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(
                    View arg0)
            {
                // TODO Auto-generated method stub
                CategoryHelper.getInstance(getApplicationContext()).UpdateMapData();
                Log.v("COOL", "dictMap: " + CategoryHelper.dictMap);
                Log.v("COOL", "treeMap: " + CategoryHelper.treeMap);
                Log.v("COOL", "cateinfoMap: " + CategoryHelper.cateinfoMap);
            }
        });
        butClearAllData = (Button) findViewById(R.id.butClearAllData);
        butClearAllData.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(
                    View arg0)
            {
                // TODO Auto-generated method stub
                CategoryHelper.dictMap.clear();
                CategoryHelper.treeMap.clear();
                CategoryHelper.cateinfoMap.clear();
                CategoryHelper.RecommendInfoMap.clear();
                CategoryHelper.getInstance(getApplicationContext()).ClearAllData();
            }
        });
    }
}
