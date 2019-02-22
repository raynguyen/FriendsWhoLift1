package apps.raymond.friendswholift.TestTrans;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.raymond.friendswholift.Interfaces.TestInterface;
import apps.raymond.friendswholift.R;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {
    private static final String TAG = "TESTADAPTER";
    private List<String> items;

    private TestInterface testInterface;
    public TestAdapter(List<String> itemList, TestInterface testInterface){
        this.items = itemList;
        this.testInterface = testInterface;
    }

    static class TestViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private TestViewHolder(View testItemView){
            super(testItemView);
            textView = testItemView.findViewById(R.id.test_item_txt);
        }
    }

    @NonNull
    @Override
    public TestAdapter.TestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.test_frag_item,viewGroup,false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TestAdapter.TestViewHolder viewHolder, int i) {
        viewHolder.textView.setText(items.get(i));
        viewHolder.textView.setTransitionName("transition"+i);
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"clicked on item at position: "+ viewHolder.getAdapterPosition() + " and with transition name: "+viewHolder.textView.getTransitionName());
                testInterface.onItemClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<String> items){
        this.items = items;
    }
}
