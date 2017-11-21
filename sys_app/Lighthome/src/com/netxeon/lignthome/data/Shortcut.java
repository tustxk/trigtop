package com.netxeon.lignthome.data;

import android.util.Log;

public class Shortcut {
	private String category;
	private String componentName;
	private boolean persistent;
	
	public boolean isPersistent() {
		return persistent;
	}
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Shortcut) {   
			Shortcut shortcut= (Shortcut) o;   
			Log.v("temp", "-------Util.recordIt() need record: " + shortcut.componentName);
            return this.componentName.equals(shortcut.componentName); 
        }  
		return super.equals(o);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return componentName;
	}

}
