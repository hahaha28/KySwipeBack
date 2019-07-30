package com.zoe.example.kyswipeback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;


public class SwipeBackLayout extends FrameLayout {

    private FrameLayout decorView;
    private float xDown;
    private float xLastMove;
    private float xMove;
    private long downTime;
    private long upTime;
    private float mTouchSlop;
    private Activity activity;
    private Paint mPaint ;
    private int statusBarHeight;    //statusBar高度
    private int statusBarColor;     //statusBar的颜色
    private boolean swipeStatusBar = true;    //是否滑动statusBar，默认为true
    private int maxAlpha = 60;      //阴影的变化范围，最大为255

    public SwipeBackLayout(Context context) {
        this(context,null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setShadowLayer(1, -10, 0, Color.BLACK);
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTouchSlop = 10;

        Log.e("MyDebug","TouchSlop="+mTouchSlop);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        //绘制阴影
        setLayerType(LAYER_TYPE_SOFTWARE, null);   //禁用硬件加速
        mPaint.setShadowLayer(150,0,0,0xff000000);
        mPaint.setColor(0xff000000);
        canvas.drawRect(0,0,getWidth(),getHeight(),mPaint);

        //绘制背景由暗变亮效果
        float rate = 1 - (-1.0f * getScrollX() / getWidth());
        int alpha = (int) (maxAlpha * rate);
        mPaint.clearShadowLayer();
        mPaint.setColor(Color.argb(alpha, 0, 0, 0));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        Rect rect = new Rect(getScrollX(), 0, 0, getHeight());
        canvas.drawRect(rect, mPaint);
    }

    public void attachToActivity(Activity activity) {
        this.activity = activity;
        Window window = activity.getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);

        FrameLayout decorView = (FrameLayout) window.getDecorView();
        this.decorView = decorView;
        decorView.setBackgroundColor(0x00ffffff);
        View decorChildView = decorView.getChildAt(0);
        this.setBackgroundResource(android.R.color.transparent);

        decorView.findViewById(android.R.id.content).setBackgroundColor(0xffffffff);
        decorView.removeViewAt(0);
        this.addView(decorChildView);
        decorChildView.setPadding(0,0,0,0);
        this.setPadding(0,0,0,0);
        decorView.addView(this);
        decorChildView.setClickable(true);

        if(swipeStatusBar){
            initStatusBarInfo();
            decorChildView.setBackgroundColor(statusBarColor);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = ev.getRawX();
                downTime = System.currentTimeMillis();
                xLastMove = xDown;
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = ev.getRawX();
                float diffX = Math.abs(xMove - xLastMove);
                xLastMove = xMove;
                if (diffX > mTouchSlop ) {
                    return true;
                }
                break;

        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_MOVE:
                xMove = event.getRawX();
                int dx = (int) (xLastMove - xMove);
                xLastMove = xMove;
                if ((dx + getScrollX()) > 0) {
                    scrollTo(0, 0);
                    return true;
                }
                scrollBy(dx, 0);
                invalidate();
                break;
            case ACTION_UP:
                upTime = System.currentTimeMillis();
                int scrolled = getScrollX();
                float speed = (event.getRawX() - xDown) / (upTime - downTime);
//                Log.d("MyDebug", "speed=" + speed);
                if (speed > 1) {
                    scrollOut();
                    break;
                }
                if (scrolled < -getWidth() / 4) {
                    scrollOut();
                } else {
                    scrollRecover();
                }
                break;

        }
        return true;
    }

    /**
     * 设置背景由暗变亮的范围，最大为255（最暗），最小为0
     * @param alpha 取值为0-255
     */
    public void setBackgroundAlphaRange(int alpha){
        this.maxAlpha=alpha;
    }

    /**
     * 设置状态栏是否跟随滑动，默认为true
     * @param flag true为跟随滑动
     */
    public void swipeStatusBar(boolean flag){
        this.swipeStatusBar =flag;
    }

    private void scrollRecover() {
        int scrolled = getScrollX();
        ValueAnimator animator = ValueAnimator.ofInt(scrolled, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                scrollTo(x, 0);
            }
        });

        animator.start();
    }

    private void scrollOut() {
        int scrolled = getScrollX();
        ValueAnimator animator = ValueAnimator.ofInt(scrolled, -getWidth());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int x = (int) animation.getAnimatedValue();
                scrollTo(x, 0);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finishActivity();
            }
        });
        animator.start();
    }

    private void finishActivity() {
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    private void initStatusBarInfo(){
        statusBarHeight = 0;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        statusBarColor = typedValue.data;
        setStatusBarFullTransparent();
    }

    private void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

}
