package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModelProviders;
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

import com.google.android.gms.tasks.OnSuccessListener;

import apps.raymond.friendswholift.R;

public class Detailed_Group_Fragment extends Fragment {
    private static final String TAG = "Detailed_Group_Fragment";

    private GroupBase groupBase;
    private GroupsViewModel mGroupViewModel;

    public Detailed_Group_Fragment(){
    }

    public static Detailed_Group_Fragment newInstance(){
        return new Detailed_Group_Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            this.groupBase = bundle.getParcelable("GroupObject");
        }
        mGroupViewModel = ViewModelProviders.of(requireActivity()).get(GroupsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.group_focus_fragment,container,false);
    }

    ImageView image;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView name = view.findViewById(R.id.group_name_txt);
        TextView desc = view.findViewById(R.id.group_desc_txt);
        image = view.findViewById(R.id.group_image);
        name.setText(groupBase.getName());
        desc.setText(groupBase.getDescription());

        if(groupBase.getImageURI()!=null && groupBase.getBytes()==null){
            Log.i(TAG,"Fetching the photo for this group.");
            mGroupViewModel.getImage(groupBase.getImageURI())
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.i(TAG,"Adding fetched byte[] to " +groupBase.getName());
                            groupBase.setBytes(bytes);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            image.setImageBitmap(bitmap);
                        }
                    });
        }
        if(groupBase.getBytes()!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(groupBase.getBytes(),0,groupBase.getBytes().length);
            image.setImageBitmap(bitmap);
        }
    }

}
