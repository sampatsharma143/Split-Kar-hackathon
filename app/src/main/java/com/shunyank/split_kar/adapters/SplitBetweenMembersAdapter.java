package com.shunyank.split_kar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.activities.GroupActivity;
import com.shunyank.split_kar.models.SplitAmountModel;
import com.shunyank.split_kar.network.model.GroupMemberCollectionModel;

import java.util.ArrayList;

//
public class SplitBetweenMembersAdapter extends RecyclerView.Adapter<SplitBetweenMembersAdapter.AdapterViewHolder> {

    ArrayList<SplitAmountModel> dataList;
    Context context;


    AdapterClickListener clickListener;

    public void setClickListener(AdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDataList(ArrayList<SplitAmountModel> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.split_between_layout,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        int pos = position;
        GroupMemberCollectionModel gmcm = dataList.get(pos).getMemberDetails();
        holder.userName.setText(gmcm.getMember_name());
//
        if(gmcm.isMember_is_on_app()){
            GroupActivity.UserModelForGroupMember userModelForGroupMember  = new Gson().fromJson(gmcm.getUser_data(), GroupActivity.UserModelForGroupMember.class);
            Glide.with(context).load(userModelForGroupMember.getAvatar_url()).into(holder.profile);
        }else {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.profile);
        }

        holder.willget.setText(dataList.get(pos).getWillGet());
        holder.willpay.setText(dataList.get(pos).getNeedToPay());

//        if(!dataList.get(pos).getNeedToPay().isEmpty()){
//
//            holder.splitAmount.setText(dataList.get(pos).getNeedToPay());
//        }else if (!dataList.get(pos).getWillGet().isEmpty()){
//            holder.splitAmount.setText(dataList.get(pos).getWillGet());
//
//        }
//        else {
//            holder.splitAmount.setText("0");
//
//        }

    }

    @Override
    public int getItemCount() {
        if(dataList!=null)
            return dataList.size();
        return 0;
    }


    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView userName;
        TextView willget;
        TextView willpay;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            willget = itemView.findViewById(R.id.willget);
            willpay = itemView.findViewById(R.id.willpay);
            userName = itemView.findViewById(R.id.name_tv);
        }
    }
}
