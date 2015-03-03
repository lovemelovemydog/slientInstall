package com.example.anzhuo.studiodemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.anzhuo.studiodemo.R;

/**
 * Created by fangzhu on 2015/2/27.
 */
public class FaceImageView extends View {
    public FaceImageView(Context context) {
        super(context);
    }

    public FaceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.face_first);
//        bitmap = bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), 600, false);//修改高宽 失真
//        canvas.drawBitmap(bitmap, 0, 0, null);


        //修改高宽 失真 使用9patch 不失真
        Bitmap bitmap9patch = BitmapFactory.decodeResource(getResources(), R.drawable.close_nor);
        NinePatch ninePatch = new NinePatch(bitmap9patch, bitmap9patch.getNinePatchChunk(), null);
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 100;
        rect.right = bitmap9patch.getWidth();
        rect.bottom = 600;
//        canvas.drawRect(rect, paint);
        ninePatch.draw(canvas, rect);




    }
}
