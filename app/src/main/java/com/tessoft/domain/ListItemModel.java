package com.tessoft.domain;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListItemModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String itemType = "";

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
}
