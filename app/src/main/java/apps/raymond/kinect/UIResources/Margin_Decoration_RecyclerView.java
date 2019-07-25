package apps.raymond.kinect.UIResources;

import android.content.Context;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.View;

public class Margin_Decoration_RecyclerView extends RecyclerView.ItemDecoration {

    private Context context;
    public Margin_Decoration_RecyclerView(Context context){
        this.context = context;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {

        DisplayMetrics metrics = new DisplayMetrics();
        ((FragmentActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int dp = (int) Math.ceil(6 * metrics.density);

        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = dp;
        }
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = 24;
        } else {
            outRect.bottom = dp;
        }

        outRect.left = dp;
        outRect.right = dp;

    }
}
