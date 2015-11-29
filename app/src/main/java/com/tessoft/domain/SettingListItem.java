package com.tessoft.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingListItem {

	private String settingName;
	private String settingValue;
	public String getSettingName() {
		return settingName;
	}
	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}
	public String getSettingValue() {
		return settingValue;
	}
	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}

}
