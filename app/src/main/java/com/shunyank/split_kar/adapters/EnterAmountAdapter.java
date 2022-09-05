package com.shunyank.split_kar.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class EnterAmountAdapter extends RecyclerView.Adapter<EnterAmountAdapter.AdapterViewHolder> {
    ArrayList<SplitAmountModel> dataList;
    Context context;


    AmountChangeListener amountChangeListener;

    public void setAmountChangeListener(AmountChangeListener amountChangeListener) {
        this.amountChangeListener = amountChangeListener;
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
    public EnterAmountAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_person_layout,parent,false);
        return new EnterAmountAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnterAmountAdapter.AdapterViewHolder holder, int position) {
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


        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float amount = 0;
                if(s.length()>0){
                        amount = Float.parseFloat(s.toString());
                        dataList.get(pos).setPaidByMe(true);

                }
                else {
                        amount =0;
                    dataList.get(pos).setPaidByMe(false);
                }
                dataList.get(pos).setTotalPaid(String.valueOf(amount));
                amountChangeListener.onAmountChange(dataList);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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
        EditText editText;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            editText = itemView.findViewById(R.id.amount_edt);
            userName = itemView.findViewById(R.id.name_tv);
        }
    }
}
