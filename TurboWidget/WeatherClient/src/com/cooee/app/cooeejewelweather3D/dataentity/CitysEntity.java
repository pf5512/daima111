
package com.cooee.app.cooeejewelweather3D.dataentity;

public class CitysEntity {
    // field name
    public static final String PROVINCE_ID = "province_id";
    public static final String NAME = "name";
    public static final String CITY_NUM = "city_num";
    public static final String PINYIN_NAME = "pinyin_name";
    public static final String PINYIN_ABB = "pinyin_abb";

    // projection
    public static final String[] projection = new String[] {
            PROVINCE_ID, NAME, CITY_NUM, PINYIN_NAME, PINYIN_ABB
    };

    // filed
    private Integer province_id;
    private String name;
    private String city_num;
    private String pinyin_name;
    private String pinyin_abb; // 城市拼音�?��

    /**
     * @param 字符�?     * @return 前缀
     */
    public static String getPrefix(String name) {
        int index = name.indexOf(".");
        if (index == -1) {
            return name;
        } else {
            return name.substring(0, index);
        }
    }

    /**
     * @param 字符�?     * @return 后缀
     */
    public static String getSuffix(String name) {
        int index = name.indexOf(".");
        if (index == -1) {
            return name;
        } else {
            return name.substring(index + 1, name.length());
        }
    }

    public void setProvinceId(Integer province_id) {
        this.province_id = province_id;
    }

    public Integer getProvinceId() {
        return province_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCityNum(String city_num) {
        this.city_num = city_num;
    }

    public String getCityNum() {
        return city_num;
    }

    public void setPinyinName(String pinyin_name) {
        this.pinyin_name = pinyin_name;
    }

    public String getPinyinName() {
        return pinyin_name;
    }

    public void setPinyinAbb(String pinyin_abb) {
        this.pinyin_abb = pinyin_abb;
    }

    public String getPinyinAbb() {
        return pinyin_abb;
    }
}
