package com.netxeon.lignthome;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.netxeon.lignthome.util.AnimEffectFactory;
import com.netxeon.lignthome.util.Logger;

//重写RelativeLayout
public class CustomizedView extends RelativeLayout implements View.OnFocusChangeListener {

	// 先重写几个构造函数
	public CustomizedView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnFocusChangeListener(this);
	}

	public CustomizedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnFocusChangeListener(this);
	}

	public CustomizedView(Context context) {
		super(context);
		setOnFocusChangeListener(this);
	}

	// 重写焦点改变事件
	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (hasFocus) {
			// 动画函数
			showOnFocusAnimation(view);
			// 有焦点设置一种颜色
			view.setBackgroundResource(R.drawable.hot_icon_focused_bg);
		} else {
			showLooseFocusAinimation(view);
			// 没焦点设置另一种颜色
			view.setBackgroundResource(R.drawable.shortcut_bg);
		}

	}

	private void showLooseFocusAinimation(final View view) {
		Logger.log(Logger.TAG_FOCUS, "----CustemizedImageView.showLooseocusAnimation() w: " + view.getWidth() + " h:" + view.getHeight());

		Animation scaleAnimation = null;

		if (view.getWidth() > 500) {
			scaleAnimation = AnimEffectFactory.createScaleAnimation(1.05F, 1.0F, 1.05F, 1.0F, 100L);
		} else {
			scaleAnimation = AnimEffectFactory.createScaleAnimation(1.1F, 1.0F, 1.1F, 1.0F, 100L);
		}

		view.startAnimation(scaleAnimation);
	}

	private void showOnFocusAnimation(final View view) {
		Logger.log(Logger.TAG_FOCUS, "----CustemizedImageView.showOnFocusAnimation() w: " + view.getWidth() + " h:" + view.getHeight());
		view.bringToFront();
		Animation scaleAnimation = null;
		if (view.getWidth() > 500) {
			scaleAnimation = AnimEffectFactory.createScaleAnimation(1.0F, 1.05F, 1.0F, 1.05F, 100L);
		} else {
			scaleAnimation = AnimEffectFactory.createScaleAnimation(1.0F, 1.1F, 1.0F, 1.1F, 100L);
		}

		view.startAnimation(scaleAnimation);

	}

}
