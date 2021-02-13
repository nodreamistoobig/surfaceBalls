package com.example.surfaceballs;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

public class Ball {
    float x, y, x1, y1, x2, y2, xToY;
    Random r = new Random();
    int[] colors = {Color.rgb(3,192,60), Color.rgb(204,61,0), Color.rgb(59,176,143), Color.rgb(255,216,0), Color.rgb(119,221,231), Color.rgb(243,98,35), Color.rgb(186,60,89)};
    int currentColor;
    Paint p = new Paint();
    float width, height;
    int speed;

    public Ball(float width, float height){
        this.width = width;
        this.height = height;
        x1 = r.nextFloat()*width;
        y1 = r.nextFloat()*height;
        x2 = width;
        y2 = r.nextFloat()*height;
        x=x1; y=y1;
        xToY = (x2-x1)/(y2-y1);
        currentColor = (int)(r.nextFloat()*7) % 7;
        Log.d("next", String.valueOf(currentColor));
        p.setColor(colors[currentColor]);

        speed = (int) r.nextFloat()*3 * 50 + 50;
    }

    public void nextColor(){
        currentColor = (currentColor+1)%7;
        p.setColor(colors[currentColor]);
    }

    public void step(){
        x+=(x2-x1)/speed;
        y+=(y2-y1)/speed;
    }

    public void checkForWall(){
        if (x>=width || x<=0){
            bounce(true, true);
        }
        else if (y>=height || y<=0) {
            bounce(false, true);
        }
    }

    public void checkForSquare(float mouse_x, float mouse_y, float d){
        if (x+3*d>= mouse_x-5 && x-3*d<= mouse_x+5 && y>mouse_y-2*d && y<mouse_y+2*d){
            bounce(true, false);
            x+=(x2-x1)/(speed/2);
            y+=(y2-y1)/(speed/2);
        }
        else if (y+3*d>= mouse_y-5 && y-3*d<= mouse_y+5 && x>mouse_x-2*d && x<mouse_x+2*d) {
            bounce(false, false);
            x+=(x2-x1)/(speed/2);
            y+=(y2-y1)/(speed/2);
        }
    }

    public void bounce(boolean vertical, boolean wall){
        xToY = -xToY;
        x1 = x; y1 = y;
        if (vertical){
            if (wall){
                y2 = (width - 2*x) / xToY + y;
                x2 = width - x;
            }
            else{
                x2 = width/2 - ((x2-x1)/Math.abs(x2-x1))*(width/2);
                y2 = (x2-x) / xToY + y;
            }
        }
        else {
            if (wall){
                y2 = height - y;
                x2 = xToY*(y2 - y) + x;

            }
            else{
                y2 = height/2 - (y2-y1)/Math.abs(y2-y1) * (height/2);
                x2 = xToY*(y2-y) + x;
            }
        }
        nextColor();
    }

    public void checkForBall(Ball ball, float d){
        if (Math.abs(ball.x - x) < 5+2*d && Math.abs(ball.y - y) < 5+2*d){
            float buff_x = ball.x, buff_y = ball.y;
            ball.x = x; ball.y = y;
            x = buff_x; y = buff_y;
            nextColor();
            ball.nextColor();
        }
    }
}