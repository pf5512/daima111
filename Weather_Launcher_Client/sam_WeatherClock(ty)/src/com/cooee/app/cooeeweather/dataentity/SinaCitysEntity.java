
package com.cooee.app.cooeeweather.dataentity;

public class SinaCitysEntity {
    // field name
    
	//新浪城市数据库
    public static final String NAME = "city";

    // projection
    public static final String[] projection = new String[] {
             NAME    };

    // filed
    private String name;

    /**
     * @param 字符串
     * @return 前缀
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
     * @param 字符串
     * @return 后缀
     */
    public static String getSuffix(String name) {
        int index = name.indexOf(".");
        if (index == -1) {
            return name;
        } else {
            return name.substring(index + 1, name.length());
        }
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
