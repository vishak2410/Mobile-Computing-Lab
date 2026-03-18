package com.example.graphics_primitives;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;

public class MyClass extends View {

    Paint paint;

    public MyClass(Context context) {
        super(context);
        paint = new Paint();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Draw Line
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(20);
        canvas.drawLine(50, 50, 400, 50, paint);

        // Draw Rectangle
        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(50, 100, 400, 250, paint);

        // Draw Circle (Green)
        paint.setColor(Color.GREEN);
        canvas.drawCircle(200, 400, 100, paint);

        // Draw Circle (Blue)
        paint.setColor(Color.BLUE);
        canvas.drawCircle(500, 600, 100, paint);

        // Draw Oval
        paint.setColor(Color.MAGENTA);
        canvas.drawOval(50, 550, 400, 700, paint);

        // Draw Text
        paint.setColor(Color.GRAY);
        paint.setTextSize(40);
        canvas.drawText("Graphic Primitives", 80, 800, paint);
    }
}