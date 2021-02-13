package com.example.surfaceballs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

public class BallsSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {
    public BallsSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    DrawThread thread;
    float width, height, d = 30;
    Random r = new Random();
    float mouse_x, mouse_y;
    Paint paint = new Paint();
    ArrayList<Ball> balls;

    class DrawThread extends Thread{
        boolean runFlag = true;
        SurfaceHolder holder;
        public DrawThread(SurfaceHolder holder) {this.holder = holder;}


        @Override
        public void run() {
            super.run();

            while (runFlag){

                int count = 0;
                for (int i = 0; i< balls.size()-1; i++){
                    if (balls.get(i).currentColor == balls.get(i+1).currentColor)
                        count++;
                }
                if (count == balls.size()-1){
                    runFlag = false;
                    TextView tv = findViewById(R.id.message);
                    //Log.d("MSG", String.valueOf(tv));
                    //tv.setText("Вы выйграли!");
                }

                Canvas c = holder.lockCanvas();
                if (c!=null){
                    paint.setColor(Color.BLACK);
                    c.drawColor(Color.WHITE);
                    c.drawRect(mouse_x-d*2,mouse_y-d*2, mouse_x+d*2, mouse_y+d*2, paint);

                    for (int i = 0; i< balls.size(); i++)
                        balls.get(i).step();

                    for (int i = 0; i< balls.size(); i++)
                        c.drawCircle(balls.get(i).x,balls.get(i).y,d,balls.get(i).p);

                    for (int i = 0; i< balls.size(); i++)
                        balls.get(i).checkForWall();

                    for (int i = 0; i< balls.size(); i++)
                        balls.get(i).checkForSquare(mouse_x, mouse_y, d);

                    for (int i = 0; i< balls.size()-1; i++)
                        for (int j = i+1; j< balls.size(); j++)
                            balls.get(i).checkForBall(balls.get(j), d);
                    holder.unlockCanvasAndPost(c);
                    try {Thread.sleep(10);}
                    catch(InterruptedException e){}
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int m_x = (int) event.getX();
        int m_y = (int) event.getY();
        int count = 0;
        for (int i = 0; i< balls.size(); i++){
            if (balls.get(i).x<m_x-3*d || balls.get(i).x>m_x+3*d || balls.get(i).y<m_y-3*d || balls.get(i).y>m_y+3*d)
                count++;
        }

        if (count == balls.size()) {
            mouse_x = m_x;
            mouse_y = m_y;
        }
        return true;
    }

    @Override
    public void surfaceRedrawNeeded(@NonNull SurfaceHolder holder) {}

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread = new DrawThread(holder);
        thread.start();
        Log.d("mytag", "DrawThread is running");
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels - 250;
        balls = new ArrayList<>();
        balls.add(new Ball(width,height));
        balls.add(new Ball(width,height));
        balls.add(new Ball(width,height));
        mouse_x = r.nextFloat()*width;
        mouse_y = r.nextFloat()*height;

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        thread.runFlag = false;
        thread = new DrawThread(holder);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        thread.runFlag = false;
    }
}
