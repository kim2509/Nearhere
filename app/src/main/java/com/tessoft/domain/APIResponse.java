package com.tessoft.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class APIResponse {

	private String resCode = "0000";
	private String resMsg = "SUCCESS";
	
	private Object data = null;
	private Object data2 = null;
	private Object data3 = null;

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getResMsg() {
		return resMsg;
	}

	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData2() {
		return data2;
	}

	public void setData2(Object data2) {
		this.data2 = data2;
	}

	public Object getData3() {
		return data3;
	}

	public void setData3(Object data3) {
		this.data3 = data3;
	}
}
