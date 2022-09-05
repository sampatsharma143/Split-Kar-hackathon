package com.shunyank.split_kar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.activities.GroupActivity;
import com.shunyank.split_kar.adapters.listeners.AdapterClickListener;
import com.shunyank.split_kar.adapters.listeners.SettlementButtonClickListener;
import com.shunyank.split_kar.models.SettlementModel;
import com.shunyank.split_kar.utils.SettleType;
import com.shunyank.split_kar.utils.SharedPref;

import java.util.ArrayList;

public class BillSettlementAdapter extends RecyclerView.Adapter<BillSettlementAdapter.GroupViewHolder> {
    ArrayList<SettlementModel> data;
    Context context;
    public void setData(ArrayList<SettlementModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setAdapterClickListener(SettlementButtonClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
    }


    SettlementButtonClickListener adapterClickListener;
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.who_pay_whom_layout,parent,false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        SettlementModel model = data.get(position);

        holder.requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapterClickListener!=null){
                    adapterClickListener.onClick(SettleType.NONE,model);
                }
            }
        });
        holder.settleByCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapterClickListener!=null){
                    adapterClickListener.onClick(SettleType.SETTLE_BY_CASH,model);
                }

            }
        });

        holder.settleByUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapterClickListener!=null){
                    adapterClickListener.onClick(SettleType.SETTLE_BY_UPI,model);
                }

            }
        });


        holder.settleAsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapterClickListener!=null){
                    adapterClickListener.onClick(SettleType.SETTLE_AS_ADMIN,model);
                }

            }
        });

        if(model.getPayer_member_number().contentEquals(SharedPref.getMyPhoneNumber(context))){
            // You are the payer
            holder.payer_name.setText(SharedPref.getMyName(context)+"\n(you)");
            holder.receiver_name.setText(model.getReceiver_member_name());
            holder.amount.setTextColor(context.getResources().getColor(R.color.red));

            holder.requestBtn.setVisibility(View.GONE);
            holder.settleByUpi.setVisibility(View.VISIBLE);
            holder.settleByCash.setVisibility(View.VISIBLE);
            holder.settleAsAdmin.setVisibility(View.GONE);

        }else if(model.getReceiver_member_number().contentEquals(SharedPref.getMyPhoneNumber(context))) {
            // You are receiver
            holder.payer_name.setText(model.getPayer_member_name());
            holder.receiver_name.setText(SharedPref.getMyName(context)+"\n(you)");
            holder.amount.setTextColor(context.getResources().getColor(R.color.green));
            holder.requestBtn.setVisibility(View.VISIBLE);
            holder.settleByUpi.setVisibility(View.GONE);
            holder.settleByCash.setVisibility(View.VISIBLE);
            holder.settleAsAdmin.setVisibility(View.GONE);

        }else {

            // you are not included in the settlement
            holder.payer_name.setText(model.getPayer_member_name());
            holder.receiver_name.setText(model.getReceiver_member_name());
            holder.amount.setTextColor(context.getResources().getColor(R.color.black));

            holder.requestBtn.setVisibility(View.GONE);
            holder.settleByUpi.setVisibility(View.GONE);
            holder.settleByCash.setVisibility(View.GONE);
            holder.settleAsAdmin.setVisibility(View.VISIBLE);

        }
        if(model.isReceiver_member_is_on_app()){
            GroupActivity.UserModelForGroupMember userModelForGroupMember  = new Gson().fromJson(model.getReceiver_user_data(),
                    GroupActivity.UserModelForGroupMember.class);
            String url = userModelForGroupMember.getAvatar_url();
            if(url!=null){
                Glide.with(context).load(url).into(holder.receiverImage);

            }else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.receiverImage);
            }
        }else {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.receiverImage);
        }

        // payer profile image
        if(model.isPayer_member_is_on_app()){
            GroupActivity.UserModelForGroupMember userModelForGroupMember  = new Gson().fromJson(model.getPayer_user_data(),
                    GroupActivity.UserModelForGroupMember.class);
            String url = userModelForGroupMember.getAvatar_url();
            if(url!=null){
                Glide.with(context).load(url).into(holder.payerImage);

            }else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.payerImage);
            }
        }else {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.payerImage);
        }



        holder.amount.setText(String.valueOf("₹"+model.getPayable_amount()));
    }

    private String appendMembersCount(String convertTimeStampToDate, int size) {

      return   convertTimeStampToDate+" • ( members "+size+" )";
    }

    @Override
    public int getItemCount() {
        if(data!=null){
            return data.size();
        }
        return 0;
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView payer_name,receiver_name,amount;
        ImageView payerImage,receiverImage;
        Button settleByCash,settleByUpi,requestBtn,cleared,settleAsAdmin;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            payer_name = itemView.findViewById(R.id.payer_name);
            receiver_name = itemView.findViewById(R.id.receiver_name);
            amount = itemView.findViewById(R.id.amount);
            payerImage = itemView.findViewById(R.id.payer_profile);
            receiverImage = itemView.findViewById(R.id.receiver_profile);

            settleByCash = itemView.findViewById(R.id.settle_cash_btn);
            settleByUpi = itemView.findViewById(R.id.settle_upi_btn);
            requestBtn = itemView.findViewById(R.id.reminder_btn);
            cleared = itemView.findViewById(R.id.cleared);
            settleAsAdmin = itemView.findViewById(R.id.settle_as_admin);
        }
    }
}
