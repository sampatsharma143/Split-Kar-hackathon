package com.shunyank.split_kar.adapters;

import android.content.Context;
import android.util.Log;
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
import com.shunyank.split_kar.models.SplitAmountModel;
import com.shunyank.split_kar.network.model.GroupMemberCollectionModel;

import java.util.ArrayList;

//
public class PaidMembersAdapter extends RecyclerView.Adapter<PaidMembersAdapter.AdapterViewHolder> {

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
        Log.e("total padi",gmcm.getTotal_paid());
        holder.total_paid.setText(gmcm.getTotal_paid());



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
        TextView total_paid;
        TextView willpay;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            total_paid = itemView.findViewById(R.id.willget);
            willpay = itemView.findViewById(R.id.willpay);
            userName = itemView.findViewById(R.id.name_tv);
            willpay.setVisibility(View.GONE);
        }
    }
}
