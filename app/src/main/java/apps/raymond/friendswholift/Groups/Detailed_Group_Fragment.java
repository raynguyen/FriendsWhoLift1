package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;

import apps.raymond.friendswholift.Core_Activity;
import apps.raymond.friendswholift.R;

public class Detailed_Group_Fragment extends Fragment {
    private static final String TAG = "Detailed_Group_Fragment";
    private static final String TRANSITION_NAME = "transition_name";
    private static final String GROUP_BASE = "group_base";

    TransitionScheduler transitionScheduler;
    public interface TransitionScheduler{
        void scheduleStartTransition(View sharedView);
    }

    private GroupsViewModel mGroupViewModel;

    public Detailed_Group_Fragment(){
    }


    public static Detailed_Group_Fragment newInstance(GroupBase groupBase, String transitionName){
        Detailed_Group_Fragment detailed_group_fragment = new Detailed_Group_Fragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(GROUP_BASE, groupBase);
        bundle.putString(TRANSITION_NAME,transitionName);
        detailed_group_fragment.setArguments(bundle);
        return detailed_group_fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroupViewModel = ViewModelProviders.of(requireActivity()).get(GroupsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.group_detail_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        final GroupBase groupBase = getArguments().getParcelable(GROUP_BASE);
        String transitionName = getArguments().getString(TRANSITION_NAME);

        final TextView name = view.findViewById(R.id.detail_group_name_txt);

        name.setText(transitionName);
        name.setTransitionName("hungry");
        transitionScheduler.scheduleStartTransition(name);

        TextView desc = view.findViewById(R.id.group_desc_txt);
        desc.setText(groupBase.getDescription());

        final ImageView image = view.findViewById(R.id.group_image);
        if(groupBase.getImageURI()!=null && groupBase.getBytes()==null){
            Log.i(TAG,"Fetching the photo for this group.");
            mGroupViewModel.getImage(groupBase.getImageURI())
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            //We need to get an instance of the Class that will execute our interface method.
            transitionScheduler = (TransitionScheduler) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG,"Class cast exception." + e.getMessage());
        }
    }
}
