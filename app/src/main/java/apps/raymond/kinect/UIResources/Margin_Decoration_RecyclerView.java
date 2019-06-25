package apps.raymond.kinect.UIResources;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class Margin_Decoration_RecyclerView extends RecyclerView.ItemDecoration {
    private static final int MARGIN = 12;

    public Margin_Decoration_RecyclerView(){
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view)==0){
            outRect.top = MARGIN;
        }
        outRect.left = 8;
        outRect.right = 8;
        outRect.bottom = 12;
    }
}
