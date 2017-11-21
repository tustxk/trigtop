package com.netxeon.lignthome;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.GridView;

public class CustomizedGridView extends GridView {

	public CustomizedGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomizedGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomizedGridView(Context context) {
		super(context);
	}



	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {  
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);  
       
        if (!gainFocus) {  
            setSelection(-1);
        }  
    }  
}
