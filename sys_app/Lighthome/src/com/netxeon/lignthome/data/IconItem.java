package com.netxeon.lignthome.data;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class IconItem {
	private String lable;
	private Drawable icon;
	private int visibility;
	private ComponentName componentName;
	
	public String getLable() {
		return lable;
	}
	public void setLable(String lable) {
		this.lable = lable;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public int getVisibility() {
		return visibility;
	}
	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}
	public ComponentName getComponentName() {
		return componentName;
	}
	public void setComponentName(ComponentName componentName) {
		this.componentName = componentName;
	}

}
