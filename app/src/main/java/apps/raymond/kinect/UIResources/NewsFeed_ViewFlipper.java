package apps.raymond.kinect.UIResources;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

import apps.raymond.kinect.R;

public class NewsFeed_ViewFlipper extends ViewFlipper {

    private Paint paint = new Paint();
    public NewsFeed_ViewFlipper(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int width = getWidth();

        float margin = 15; //Variable to calculate horizontal spacing between dots.
        float radius = 8; //Radius of the dot.
        float cx = (width / 2) - ((radius + margin) * 2 * getChildCount() / 2);
        float cy = getHeight() - 20;

        canvas.save();

        for (int i = 0; i < getChildCount(); i++)
        {
            if (i == getDisplayedChild())
            {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.blueish));
                canvas.drawCircle(cx, cy, radius, paint);

            } else
            {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.gray));
                canvas.drawCircle(cx, cy, radius, paint);
            }
            cx += 2 * (radius + margin);
        }
        canvas.restore();
    }

}
