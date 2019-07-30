package com.zoe.example.kyswipeback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SwipeBackActivity extends AppCompatActivity {

    private SwipeBackLayout swipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipeBackLayout = new SwipeBackLayout(this);
        swipeBackLayout.attachToActivity(this);
    }

    /**
     * 设置背景由暗变亮的范围，最大为255（最暗），最小为0
     * @param alpha 取值为0-255
     */
    public void setBackgroundAlphaRange(int alpha){
        swipeBackLayout.setBackgroundAlphaRange(alpha);
    }

    /**
     * 设置状态栏是否跟随滑动，默认为true
     * @param flag true为跟随滑动
     */
    public void swipeStatusBar(boolean flag){
        swipeBackLayout.swipeStatusBar(flag);
    }

}
