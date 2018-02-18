package uk.co.peterandrew.ws2812patternsequencer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by pete on 17/02/2018.
 */

public class LedColourView extends View {

    int color = Color.rgb(0, 0, 0);

    public LedColourView(Context context) {
        super(context);
    }

    public LedColourView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LedColourView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(50, 50);
    }

    public void setColor(int red, int green, int blue) {
        color = Color.rgb(red, green, blue);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(color);

        canvas.drawCircle(25, 25, 25, paint);
    }

}
