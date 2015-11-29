package com.tessoft.domain;

public class MainMenuItem {

	private String menuName;
	private int notiCount = 0;
	
	public MainMenuItem( String menuName )
	{
		setMenuName(menuName);
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public int getNotiCount() {
		return notiCount;
	}

	public void setNotiCount(int notiCount) {
		this.notiCount = notiCount;
	}
}
