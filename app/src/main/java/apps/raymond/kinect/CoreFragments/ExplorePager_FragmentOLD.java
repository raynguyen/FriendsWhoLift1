package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UIResources.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class ExplorePager_FragmentOLD extends Fragment implements Events_Adapter.EventClickListener{
    private static final String TAG = "ExplorePagerFrag";
    private static final String INSTANCE = "instance";
    public static final int SUGGESTED = 0;
    public static final int POPULAR = 1;
    private Core_ViewModel mViewModel;
    private ExploreEventListener mEventClickListener;

    public interface ExploreEventListener{
        void onEventDetails(Event_Model event);
    }

    public static ExplorePager_FragmentOLD newInstance(int instanceType){
        ExplorePager_FragmentOLD fragment = new ExplorePager_FragmentOLD();
        Bundle args = new Bundle();
        args.putInt(INSTANCE, instanceType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        Fragment mParentFragment = getParentFragment();
        try{
            mEventClickListener = (ExploreEventListener) mParentFragment;
        } catch (ClassCastException e){
            Log.w(TAG,e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_pager_old, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressBar progressBar = view.findViewById(R.id.progress_explore_suggested);
        TextView textNullData = view.findViewById(R.id.text_explore_suggested_null);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_explore_suggested);
        Events_Adapter adapter = new Events_Adapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new Margin_Decoration_RecyclerView());
        int instanceType = getArguments().getInt(INSTANCE);
        if(instanceType == SUGGESTED){
            mViewModel.getSuggestedEvents().observe(requireActivity(), (List<Event_Model> events) -> {
                progressBar.setVisibility(View.GONE);
                if(events != null){
                    adapter.setData(events);
                    if(events.size()==0){
                        textNullData.setVisibility(View.VISIBLE);
                    } else {
                        textNullData.setVisibility(View.GONE);
                    }
                }
            });
        } else if (instanceType == POPULAR){
            mViewModel.getPopularEvents().observe(requireActivity(), (List<Event_Model> events) -> {
                progressBar.setVisibility(View.GONE);
                if(events != null && events.size() !=0){
                    adapter.setData(events);
                } else {
                    textNullData.setVisibility(View.GONE);
                }
            });
        }


    }

    @Override
    public void onEventClick(Event_Model event) {
        if(mEventClickListener != null){
            mEventClickListener.onEventDetails(event);
        }
    }
}
