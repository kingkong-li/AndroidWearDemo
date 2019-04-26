package com.emt.fatri.wearbaidusdkdemo.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.datamodel.NodeInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * description:历史报警数据adpter
 * Created by kingkong on 2018/5/11 0011.
 * changed by kingkong on 2018/5/11 0011.
 */

public class NodeRecycleViewAdapter extends RecyclerView.Adapter<NodeRecycleViewAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<NodeInfo> mData;

    public NodeRecycleViewAdapter(Context context, ArrayList<NodeInfo> data ){
        mContext=context;
        mData=data;
    }
    public void setData(ArrayList<NodeInfo> data)
    {
        mData=data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.node_item_layout,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.mNodeCreateTime.setText(String.valueOf(dateformat.format(mData.get(position).createTimeInMills))+"-报警-");
        holder.mNodeState.setText(String.valueOf(mData.get(position).stateCode));

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyHolder extends ViewHolder {
        TextView mNodeCreateTime;
        TextView mNodeState;
        MyHolder(View itemView) {
            super(itemView);
            mNodeCreateTime=itemView.findViewById(R.id.node_create_time_TextView);
            mNodeState=itemView.findViewById(R.id.node_state_TextView);

        }

    }


}
