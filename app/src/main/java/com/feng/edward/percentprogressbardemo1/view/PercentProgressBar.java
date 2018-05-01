package com.feng.edward.percentprogressbardemo1.view;

import com.feng.edward.percentprogressbardemo1.R;
import com.feng.edward.percentprogressbardemo1.util.DimensionUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 功能描述：带滚动百分比的圆形进度条
 *
 * @author (作者) edward（冯丰枫）
 * @link http://www.jianshu.com/u/f7176d6d53d2
 * 创建时间： 2018/4/16 0016
 */
public class PercentProgressBar extends View {
    private Paint
            progressBackPaint, //进度条背景画笔
            progressFrontPaint,//进度条前景画笔
            percentTextPaint;  //百分比文字画笔

    private RectF
            progressRectF,//进度条圆弧所在的矩形
            percentTextRectF;//百分比文字所在的矩形

    private Path percentTextPath;   //百分比文字所在的路径
    private float percentTextRadius;  //百分比文字圆弧的半径
    private float progressPaintWidth; //进度条画笔的宽度

    private int percentProgress;//百分比进度（0 ~ 100）


    public PercentProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PercentProgressBar, defStyleAttr, 0);

        //初始化进度条画笔的宽度
        progressPaintWidth = attributes.getDimension(R.styleable.PercentProgressBar_progressWidth, DimensionUtils.dip2px(context, 10));
        //初始化百分比文字的大小
        float percentTextPaintSize = attributes.getDimensionPixelSize(R.styleable.PercentProgressBar_percentTextSize, DimensionUtils.sp2px(context, 10));
        int progressBackColor = attributes.getColor(R.styleable.PercentProgressBar_progressBackColor, 0xffaaaaaa);
        int progressFrontColor = attributes.getColor(R.styleable.PercentProgressBar_progressFrontColor, 0xffFF4081);
        int percentTextColor = attributes.getColor(R.styleable.PercentProgressBar_percentTextColor, 0xffff0077);
        attributes.recycle();
        //初始化进度条背景画笔
        progressBackPaint = new Paint();
        progressBackPaint.setColor(progressBackColor);
        progressBackPaint.setStrokeWidth(progressPaintWidth);
        progressBackPaint.setAntiAlias(true);
        progressBackPaint.setStyle(Paint.Style.STROKE);

        //初始化进度条前景画笔
        progressFrontPaint = new Paint();
        progressFrontPaint.setColor(progressFrontColor);
        progressFrontPaint.setStrokeWidth(progressPaintWidth);
        progressFrontPaint.setAntiAlias(true);
        progressFrontPaint.setStyle(Paint.Style.STROKE);

        //初始化百分比文字画笔
        percentTextPaint = new Paint();
        percentTextPaint.setColor(percentTextColor);
        percentTextPaint.setTextSize(percentTextPaintSize);// 设置文字画笔的尺寸(px)
        percentTextPaint.setAntiAlias(true);
        percentTextPaint.setStyle(Paint.Style.STROKE);
        percentTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        //初始化百分比文字路径
        percentTextPath = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();// 获取控件的layout_width
        int height = getMeasuredHeight(); // 获取控件的layout_height
        //获取内切圆圆心坐标
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2;//获取控件内切圆的半径

        Rect rect = new Rect();
        percentTextPaint.getTextBounds("100%", 0, "100%".length(), rect);//获取最大百分比文字的高度
        int textHeight = rect.height();
        //比较进度条的宽度和百分比文字的高度，去两者中较大者，用以计算进度条的半径，保证精度条和百分比文字互为中心
        float radiusArc = radius - (progressPaintWidth > textHeight ? progressPaintWidth / 2 : textHeight / 2);

        //初始化进度条圆弧所在的矩形
        progressRectF = new RectF();
        progressRectF.left = centerX - radiusArc;
        progressRectF.top = centerY - radiusArc;
        progressRectF.right = centerX + radiusArc;
        progressRectF.bottom = centerY + radiusArc;

        percentTextRadius = radiusArc - textHeight / 2;//计算百分比文字圆弧的半径

        //初始化百分比文字路径所在的矩形
        percentTextRectF = new RectF();
        percentTextRectF.left = centerX - percentTextRadius;
        percentTextRectF.top = centerY - percentTextRadius;
        percentTextRectF.right = centerX + percentTextRadius;
        percentTextRectF.bottom = centerY + percentTextRadius;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制进度框的背景
        canvas.drawArc(progressRectF, 0, 360, false, progressBackPaint);
        //绘制进度框的前景（从圆形最高点中间，顺时针绘制）
        canvas.drawArc(progressRectF, -90, percentProgress / 100.0f * 360, false, progressFrontPaint);
        //百分比文字
        String text = percentProgress + "%";
        //计算百分比文字的弧跨度
        float sweepAngle = (float) (percentTextPaint.measureText(text) * 360 / (2 * Math.PI * percentTextRadius));
        //初始化百分比文字的弧路径
        percentTextPath.addArc(percentTextRectF, percentProgress * 3.6f - 90.0f - sweepAngle / 2.0f, sweepAngle);
        //绘制弧形百分比文字
        canvas.drawTextOnPath(text, percentTextPath, 0, 0, percentTextPaint);
        //路径重置
        percentTextPath.reset();
    }

    /**
     * 设置当前百分比进度
     */
    public void setPercentProgress(int percentProgress) {
        this.percentProgress = percentProgress;
        invalidate();
    }
}
