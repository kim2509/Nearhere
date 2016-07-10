package com.tessoft.common;

public interface AdapterDelegate {

	public void doAction(String actionName, Object param);

	public String getStringValueForKey( String keyName );
}
