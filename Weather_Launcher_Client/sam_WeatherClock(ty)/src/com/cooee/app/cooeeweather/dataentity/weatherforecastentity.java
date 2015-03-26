package com.cooee.app.cooeeweather.dataentity;

import android.provider.BaseColumns;

public class weatherforecastentity {
	// field name
	public static final String DAYOFWEEK = "dayOfWeek";
	public static final String LOW = "low";
	public static final String HIGHT = "hight";
	public static final String ICON = "icon";
	public static final String CONDITION = "condition";
	public static final String CITY = "city";

	// projection
	public static final String[] forecastProjection = new String[] {
			BaseColumns._ID, CITY, DAYOFWEEK, LOW, HIGHT, ICON, CONDITION, };

	// field
	private Integer id;
	private String city;
	private Integer dayOfWeek;
	private Integer low;
	private Integer hight;
	private String icon;
	private String condition;
	private Integer widgetId;

	public void setTestData() {
		id = 0;
		city = "上海";
		dayOfWeek = 3;
		low = 26;
		hight = 32;
		icon = "/ig/images/weather/chance_of_storm.gif";
		condition = "多云";
		widgetId = 0;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the dayOfWeek
	 */
	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	/**
	 * @param dayOfWeek
	 *            the dayOfWeek to set
	 */
	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * @return the low
	 */
	public Integer getLow() {
		return low;
	}

	/**
	 * @param low
	 *            the low to set
	 */
	public void setLow(Integer low) {
		this.low = low;
	}

	/**
	 * @return the hight
	 */
	public Integer getHight() {
		return hight;
	}

	/**
	 * @param hight
	 *            the hight to set
	 */
	public void setHight(Integer hight) {
		this.hight = hight;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * @param condition
	 *            the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * @param widgetId
	 *            the widgetId to set
	 */
	public void setWidgetId(Integer widgetId) {
		this.widgetId = widgetId;
	}

	/**
	 * @return the widgetId
	 */
	public Integer getWidgetId() {
		return widgetId;
	}

	public String getDstailCity() {
		return city;
	}

	public void setDetailCity(String cityname) {
		this.city = cityname;
	}

}
