
package com.cooee.app.cooeejewelweather3D.dataentity;

public class SinaCitysEntity {
    // field name
    

	 public static final String NAME = "city";
    // projection
    public static final String[] projection = new String[] {
             NAME    };

    // filed
    private String name;

  
    public static String getPrefix(String name) {
        int index = name.indexOf(".");
        if (index == -1) {
            return name;
        } else {
            return name.substring(0, index);
        }
    }


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
