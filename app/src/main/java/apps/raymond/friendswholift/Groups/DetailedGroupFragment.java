package apps.raymond.friendswholift.Groups;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import apps.raymond.friendswholift.R;

public class DetailedGroupFragment extends Fragment {
    private static final String TAG = "DetailedGroupFragment";

    private GroupBase groupBase;

    public DetailedGroupFragment(){
    }

    public static DetailedGroupFragment newInstance(){
        Log.i(TAG,"Created a new instance of the DetailedGroupFragment.");
        return new DetailedGroupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            this.groupBase = bundle.getParcelable("GroupObject");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.group_focus_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView name = view.findViewById(R.id.group_name_txt);
        TextView desc = view.findViewById(R.id.group_desc_txt);
        ImageView image = view.findViewById(R.id.group_image);
        name.setText(groupBase.getName());
        desc.setText(groupBase.getDescription());

        byte[] bytes = groupBase.getByteArray();
        if(bytes !=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            image.setImageBitmap(bitmap);
        }
    }



}