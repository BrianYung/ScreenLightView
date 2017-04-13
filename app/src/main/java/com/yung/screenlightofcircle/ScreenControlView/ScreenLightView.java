package com.yung.screenlightofcircle.ScreenControlView;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yung.screenlightofcircle.R;

/**
 * Created by Brian on 2017-04-13.
 * 绘画一个内部圆形，外部为虚线进度的空心扇形
 */
public class ScreenLightView extends View {
    /**
     * 虚线进度之间间隔
     */
    private final static int spacearc = 20;
    /**
     * 每个圆弧点度数
     */
    private int avgarc = 16;
    /**
     * 刻度个数
     */
    private int procount = 0;
    /**
     * 控件宽度，设置与高相同，为正方形展示
     */
    private int width = 0;

    /**
     * 最小宽度
     */
    private int minwidth = 200;
    /**
     * 内边距
     */
    private int padding = 10;
    /**
     * 控件高度
     */
    private int height = 0;
    /**
     * 控件填充颜色
     */
    private int solidcolor = 0;
    /**
     * 控件未填充颜色
     */
    private int unsolidcolor = 0;
    /**
     * 亮度值虚线宽度
     */
    private int solidwidth = 0;

    /**
     * 设置宽度比距离在控件宽度的比例
     */
    private final float solid_widthandspace_percent = 1 / 13f;


    /**
     * 画圆
     */
    private Paint circlepaint;
    /**
     * 内部圆形半径
     */
    private int radius = 0;
    /**
     * 亮度值绘画
     */
    private Paint propaint;
    private Paint painttext;
    private RectF prorect;
    /**
     * 开始角度
     */
    private float startarc = 100f;

    /**
     * 当前刻度
     */
    private int curscale = 3;

    private Window window;
    private WindowManager.LayoutParams lp;

    public ScreenLightView(Context context) {
        this(context, null);
    }

    public ScreenLightView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ScreenLightViewStyle);
    }

    public ScreenLightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //进行控件自定义属性值的获取
        ini(context.obtainStyledAttributes(attrs, R.styleable.ScreenLightView, defStyleAttr, R.style.ScreenLightViewDefault));
        procount = 360 / (avgarc + spacearc);
    }

    private void ini(TypedArray ta) {
        solidcolor = ta.getColor(R.styleable.ScreenLightView_solidcolor, Color.GREEN);
        unsolidcolor = ta.getColor(R.styleable.ScreenLightView_unsolidcolor, Color.GREEN);
        //此处必须释放资源
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);
        //精确的控件宽度
        if (widthmode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            width = minwidth;
        }
        if (heightmode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = minwidth;
        }
        width = Math.min(width, height);
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (circlepaint == null)
            inipaint();
        setBrightness(curscale*0.1f);
        //绘画中心圆形
        canvas.drawCircle(width / 2, width / 2, radius, circlepaint);
        canvas.drawText(radian + "", 50, 50, painttext);
        startarc = 100f;
        for (int i = 0; i < procount; i++) {
            if (i < curscale) {
                propaint.setColor(solidcolor);
                canvas.drawArc(prorect, startarc, avgarc, false, propaint);
            } else {
                propaint.setColor(unsolidcolor);
                canvas.drawArc(prorect, startarc, avgarc, false, propaint);
            }
            startarc += 360 / procount;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.e("sdasd", action + "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("sdasd", action + "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("sdasd", action + "ACTION_UP");
                break;
        }
        //手指触摸屏幕时，进行初始角度重置
        getCurcaleForXY(event.getX(), event.getY());
        return true;
    }

    /**
     * 根据当前坐标返回当前刻度值
     *
     * @param x
     * @param y
     */
    int radian;

    private int getCurcaleForXY(float x, float y) {
        //根据手指当前坐标转换为对应的坐标系坐标
        float touchx = x - (width / 2);
        float touchy = (width / 2) - y;
        //当前对应锐角度数
        if (touchx >= 0 && touchy >= 0) {
            radian = 180 +Math.round((float) Math.toDegrees(Math.atan(x / y)));
        } else if (touchx >= 0 && touchy < 0) {
            radian = 270 +Math.round((float) Math.toDegrees(Math.atan(y / x)));
        } else if (touchx < 0 && touchy < 0) {
            radian = Math.round((float) Math.toDegrees(Math.atan(x / y)));
        } else if (touchx < 0 && touchy >= 0) {
            radian = 90 +Math.round((float) Math.toDegrees(Math.atan(y / x)));
        }
        curscale = radian / (avgarc+spacearc)+1;
        invalidate();

        return curscale;
    }

    private void setBrightness(float brightness) {
        if(window == null)
        {
            window = ((Activity) getContext()).getWindow();
            lp = window.getAttributes();
        }
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }

    /**
     *
     */
    private void inipaint() {
        painttext = new Paint();
        // 消除锯齿
        painttext.setAntiAlias(true);
        // 颜色
        painttext.setColor(solidcolor);
        painttext.setTextSize(30);

        circlepaint = new Paint();
        // 填充
        circlepaint.setStyle(Paint.Style.FILL);
        // 消除锯齿
        circlepaint.setAntiAlias(true);
        // 颜色
        circlepaint.setColor(solidcolor);
        propaint = new Paint();
        // 空心
        propaint.setStyle(Paint.Style.STROKE);
        // 颜色
        propaint.setColor(solidcolor);
        // 消除锯齿
        propaint.setAntiAlias(true);
        //断点形状为圆头
        propaint.setStrokeCap(Paint.Cap.ROUND);
        //绘画宽度（占一个百分比）
        solidwidth = Math.round(width * solid_widthandspace_percent);
        //设置圆环绘画宽度
        propaint.setStrokeWidth(solidwidth);
        //内部圆形半径计算（(界面宽度- - 环形图画与中心园距离(4个百分比) - 环宽度(一个百分比))/2）-绘画宽度/2（往内部偏移一半虚线宽度）
        radius = Math.round(width * (1 - solid_widthandspace_percent * 5)) / 2 - solidwidth / 2;
        //外部圆形绘画区域（起点外部虚线中心点的一半，保证虚线显示完全，否则虚线只显示一半）
        prorect = new RectF();
        prorect.left = solidwidth / 2;
        prorect.top = solidwidth / 2;
        prorect.right = width - solidwidth / 2;
        prorect.bottom = width - solidwidth / 2;
    }

}
