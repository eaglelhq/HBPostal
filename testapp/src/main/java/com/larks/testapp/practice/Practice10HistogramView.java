package com.larks.testapp.practice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class Practice10HistogramView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();

    public Practice10HistogramView(Context context) {
        super(context);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        综合练习
//        练习内容：使用各种 Canvas.drawXXX() 方法画直方图
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        path.moveTo(80,50);
        path.lineTo(80, 500); // 由当前位置 (0, 0) 向 (100, 100) 画一条直线
        path.rLineTo(600, 0); // 由当前位置 (100, 100) 向正右方 100 像素的位置画一条直线
        canvas.drawPath(path,paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(16);
        canvas.drawText("Froyo", 100, 525, paint);
        canvas.drawText("GB", 180, 525, paint);
        canvas.drawText("ICS", 230, 525, paint);
        canvas.drawText("JB", 290, 525, paint);
        canvas.drawText("Kitkat", 350, 525, paint);
        canvas.drawText("L", 420, 525, paint);
        canvas.drawText("M", 480, 525, paint);

        canvas.drawText("直方图", 320, 550, paint);

        paint.setColor(Color.GREEN);
        canvas.drawRect(100,510,150,500,paint);
        canvas.drawRect(160,480,210,500,paint);
        canvas.drawRect(220,460,270,500,paint);
        canvas.drawRect(280,360,330,500,paint);
        canvas.drawRect(340,260,390,500,paint);
        canvas.drawRect(400,160,450,500,paint);


        canvas.drawRect(460,320,510,500,paint);
    }
}
