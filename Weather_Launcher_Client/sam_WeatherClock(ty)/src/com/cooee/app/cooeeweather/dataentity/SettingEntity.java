
package com.cooee.app.cooeeweather.dataentity;

public class SettingEntity {
    // field name
    public static final String UPDATE_WHEN_OPEN = "updateWhenOpen";
    public static final String UPDATE_REGULARLY = "updateRegularly";
    public static final String UPDATE_INTERVAL = "updateInterval";
    public static final String SOUND_ENABLE = "soundEnable";

    // projection
    public static final String[] projection = new String[] {
            UPDATE_WHEN_OPEN,
            UPDATE_REGULARLY, UPDATE_INTERVAL, SOUND_ENABLE
    };

    // filed
    private Integer updateWhenOpen;
    private Integer updateRegularly;
    private Integer updateInterval;
    private Integer soundEnable;

    public Integer getUpdateWhenOpen() {
        return updateWhenOpen;
    }

    public void setUpdateWhenOpen(Integer updateWhenOpen) {
        this.updateWhenOpen = updateWhenOpen;
    }

    public Integer getUpdateRegularly() {
        return updateRegularly;
    }

    public void setUpdateRegularly(Integer updateRegularly) {
        this.updateRegularly = updateRegularly;
    }

    public Integer getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(Integer updateInterval) {
        this.updateInterval = updateInterval;
    }

    public Integer getSoundEnable() {
        return soundEnable;
    }

    public void setSoundEnable(Integer soundEnable) {
        this.soundEnable = soundEnable;
    }
}
