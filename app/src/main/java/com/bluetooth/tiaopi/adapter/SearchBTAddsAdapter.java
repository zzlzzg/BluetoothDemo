package com.bluetooth.tiaopi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bluetooth.tiaopi.R;
import com.bluetooth.tiaopi.model.BTAddsModel;

import java.util.ArrayList;

/**
 * Created by TiaoPi on 2017/3/29.
 */

public class SearchBTAddsAdapter extends RecyclerView.Adapter<SearchBTAddsAdapter.MyViewHolder> {

    Context context;
    private ArrayList<BTAddsModel> arrayList;

    public SearchBTAddsAdapter(Context context){
        this.context = context;
        arrayList = new ArrayList<>();
    }

    public void addBT(BTAddsModel btAddsModel) {
        arrayList.add(btAddsModel);
        notifyDataSetChanged();
    }

    public void clearBT() {
        arrayList.clear();
        notifyDataSetChanged();
    }

    public ArrayList<BTAddsModel> getData(){
        return arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.bt_adds_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv.setText(arrayList.get(position).getName() + " -- " +
                arrayList.get(position).getMacAdds());

        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textClick.onClickCall(position);
            }
        });

    }

    TextClick textClick;

    public void setTextClick(TextClick textClick){
        this.textClick = textClick;
    }

    public interface TextClick{
        void onClickCall(int position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.bt_adds_text_view);
        }
    }
}
