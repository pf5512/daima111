package com.iLoong.launcher.SetupMenu.Actions;

import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;

public class DesktopEffect extends Action {

    
    public DesktopEffect(int actionid, String action)
    {
        super( actionid,  action);
    }
    
    public static  void Init(){
        SetupMenuActions.getInstance().RegisterAction(ActionSetting.ACTION_DESKTOP_EFFECT,
                new DesktopEffect(ActionSetting.ACTION_DESKTOP_EFFECT, DesktopEffect.class.getName()));
    }
    @Override
    protected void OnRunAction() {
        SendMsgToAndroid.sendShowDeskEffectDialgMsg();
        
    }

    @Override
    protected void OnActionFinish() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void OnPutValue(String key) {
        // TODO Auto-generated method stub
        
    }

}
