
package com.cooee.app.cooeejewelweather3D.dataentity;

public class PostalCodeEntity {
    // field name
    public static final String POSTAL_CODE = "postalCode";
    public static final String USER_ID = "userid";
    public static final String CITY_NUM = "city_num";

    // projection
    public static final String[] projection = new String[] {
            POSTAL_CODE, USER_ID, CITY_NUM
    };

    // field
    private String postalCode;
    private String userId;
    private String city_num;

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCityNum() {
        return city_num;
    }

    public void setCityNum(String city_num) {
        this.city_num = city_num;
    }
}
