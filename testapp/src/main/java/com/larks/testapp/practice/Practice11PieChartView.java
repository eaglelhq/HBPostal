package com.larks.testapp.practice;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class Practice11PieChartView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Practice11PieChartView(Context context) {
        super(context);
    }

    public Practice11PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice11PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        综合练习
//        练习内容：使用各种 Canvas.drawXXX() 方法画饼图
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawArc(100,100,600,600,-180,110,true,paint);
        paint.setColor(Color.YELLOW);
        canvas.drawArc(100,100,600,600,-65,55,true,paint);
        paint.setColor(Color.MAGENTA);
        canvas.drawArc(100,100,600,600,-5,20,true,paint);
        paint.setColor(Color.GREEN);
        canvas.drawArc(100,100,600,600,20,100,true,paint);
        paint.setColor(Color.DKGRAY);
        canvas.drawArc(100,100,600,600,132,45,true,paint);

    }
}
