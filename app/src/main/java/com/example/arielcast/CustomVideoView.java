package com.example.arielcast;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.widget.VideoView;



public class CustomVideoView extends VideoView {

    private int measuredWidth = 1450;
    private int measuredHeight = 650;

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context) {
        super(context);
    }

    public void setNewDimension(int width, int height) {
        this.measuredHeight = height;
        this.measuredWidth = width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(measuredWidth>1450)
         setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);


    }


}//end class
