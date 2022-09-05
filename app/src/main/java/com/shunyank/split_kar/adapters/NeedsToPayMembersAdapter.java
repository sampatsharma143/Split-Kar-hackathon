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
import com.shunyank.split_kar.activities.BillSettlementActivity;
import com.shunyank.split_kar.activities.GroupActivity;

import java.util.ArrayList;

//
public class NeedsToPayMembersAdapter extends RecyclerView.Adapter<NeedsToPayMembersAdapter.AdapterViewHolder> {

    ArrayList<BillSettlementActivity.SimpleBillMemberModel> dataList;
    Context context;


    AdapterClickListener clickListener;

    public void setClickListener(AdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDataList(  ArrayList<BillSettlementActivity.SimpleBillMemberModel> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // reusing split_between layout for paid members adapter /
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.split_between_layout,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        int pos = position;
        BillSettlementActivity.SimpleBillMemberModel gmcm = dataList.get(pos);
        holder.userName.setText(gmcm.getMember_name());
//
        if(gmcm.isMember_is_on_app()){
            GroupActivity.UserModelForGroupMember userModelForGroupMember  = new Gson().fromJson(gmcm.getUser_data(), GroupActivity.UserModelForGroupMember.class);
            Glide.with(context).load(userModelForGroupMember.getAvatar_url()).into(holder.profile);
        }else {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.profile);
        }

        // using total Paid as split amount
        holder.SplitAmount.setText(gmcm.getTotal_paid());




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
        TextView SplitAmount;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            SplitAmount = itemView.findViewById(R.id.willget);
            userName = itemView.findViewById(R.id.name_tv);
            itemView.findViewById(R.id.willpay).setVisibility(View.GONE);
        }
    }
}
