package apps.raymond.kinect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

public class Add_Users_RecyclerView extends RecyclerView {

    public Add_Users_RecyclerView(Context context){
        super(context);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = MeasureSpec.makeMeasureSpec(240,MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
