package com.netxeon.lignthome.util;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class AnimEffectFactory {

	//透明度动画AlphaAnimation
	public static Animation alphaAnimation(float paramFloat1, float paramFloat2, long paramLong1, long paramLong2) {
		
		//paramFloat1 to paramFloat2   参数1到0从完全的透明度，到完全的不透明
		AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
		// /设置动画时间
		localAlphaAnimation.setDuration(paramLong1);
		// 设置动画执行之前的等待时间
		localAlphaAnimation.setStartOffset(paramLong2);
		//设置动画加速器（AccelerateDecelerateInterpolator开始慢后续快）
		localAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		
		return localAlphaAnimation;
	}

	//渐变尺寸缩放
	public static Animation createScaleAnimation(float fromXScale, float toXScale, float fromYScale, float toYScale, long duration) {
		
		//参数1：x轴的初始值
        //参数2：x轴收缩后的值
        //参数3：y轴的初始值
        //参数4：y轴收缩后的值
        //参数5：确定x轴坐标的类型
        //参数6：x轴的值，0.5f表明是以自身这个控件的一半长度为x轴
        //参数7：确定y轴坐标的类型
        //参数8：y轴的值，0.5f表明是以自身这个控件的一半长度为x轴
		ScaleAnimation localScaleAnimation = new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale, 1, 0.5F, 1, 0.5F);
		//动画执行完被应用，即不还原
		localScaleAnimation.setFillAfter(true);
		
		localScaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		localScaleAnimation.setDuration(duration);
		return localScaleAnimation;
	}

}
