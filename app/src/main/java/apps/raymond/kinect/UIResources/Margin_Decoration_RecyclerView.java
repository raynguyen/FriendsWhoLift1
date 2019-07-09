package apps.raymond.kinect.UIResources;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class Margin_Decoration_RecyclerView extends RecyclerView.ItemDecoration {
    private int mTopMargin;

    public Margin_Decoration_RecyclerView(int topMargin){
        mTopMargin = topMargin;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {

        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = mTopMargin;
        }
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = 24;
        } else {
            outRect.bottom = 12;
        }

        outRect.left = 8;
        outRect.right = 8;

    }
}
