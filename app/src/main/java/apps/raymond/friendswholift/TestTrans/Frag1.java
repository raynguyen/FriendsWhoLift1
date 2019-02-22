package apps.raymond.friendswholift.TestTrans;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Interfaces.TestInterface;
import apps.raymond.friendswholift.R;

public class Frag1 extends Fragment implements TestInterface {
    private static final String TAG = "FRAGMENT 1";
    public Frag1(){}

    public List<String> testList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testList = new ArrayList<>();
        testList.add("Yes");
        testList.add("fyeayeayeary");
        testList.add("fyeayeayeary");
        testList.add("fyeayeayeary");
        testList.add("fyeayeayeary");
        testList.add("fyeayeayeary");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_frag1,container,false);
        return view;
    }

    TextView textView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.test_recycler);
        TestAdapter mAdapter = new TestAdapter(testList,this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter.setData(testList);


        textView = view.findViewById(R.id.frag1_txt);
        Log.i(TAG,"Fragment 1 TextView with TransitionName: "+textView.getTransitionName());
        /*
        textView.setTransitionName("transition");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Clicked on fRAgment 1 text view to try and launch frag 2.");
                Frag2 frag2 = new Frag2();

                frag2.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));

                getFragmentManager().beginTransaction()
                        .replace(R.id.test_frame,frag2)
                        .addToBackStack(null)
                        .addSharedElement(textView,textView.getTransitionName())
                        .commit();
            }
        });*/
    }


    @Override
    public void onItemClick() {
        Log.i(TAG,"IN THE INTERFACE FOR FRAG1 WHERE THE CLICKED VIEW HAS TRANSITION NAME: "+textView.getTransitionName());

        Frag2 frag2 = new Frag2();
        frag2.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));

        getFragmentManager().beginTransaction()
                .replace(R.id.test_frame,frag2)
                .addToBackStack(null)
                .addSharedElement(textView,textView.getTransitionName())
                .commit();
    }

    public void setData(List<String> testList){
        this.testList = testList;
    }

}
