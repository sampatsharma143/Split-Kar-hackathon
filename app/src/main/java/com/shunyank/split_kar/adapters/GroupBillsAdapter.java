package com.shunyank.split_kar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shunyank.split_kar.R;
import com.shunyank.split_kar.models.BillModel;
import com.shunyank.split_kar.models.SplitAmountModel;
import com.shunyank.split_kar.utils.Helper;

import java.util.ArrayList;

public class GroupBillsAdapter extends RecyclerView.Adapter<GroupBillsAdapter.ViewHolder> {

    ArrayList<BillModel> dataList;
    Context context;


    AdapterClickListener clickListener;

    public void setClickListener(AdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDataList(ArrayList<BillModel> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.group_bill_layout,parent,false);
        return new GroupBillsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BillModel model = dataList.get(position);
            holder.eventName.setText(model.getEvent_name());
            holder.totalExpense.setText("Expense : ₹"+model.getTotal_expense());
            holder.membersCount.setText("Members("+ model.getSplit_count()+")");
            holder.createdBy.setText(model.getCreated_by());
            holder.createAt.setText(Helper.ConvertTimeStampToDate(model.getCreatedAt()));
            if(model.getGetOrPay()!=null){

                if(model.isNeedToPay()){
                    holder.getOrPay.setTextColor(context.getResources().getColor(R.color.red));
                }else {
                    holder.getOrPay.setTextColor(context.getResources().getColor(R.color.green));

                }
                holder.getOrPay.setText(model.getGetOrPay());
            }


            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(model);
                }
            });
    }

    @Override
    public int getItemCount() {
        if(dataList!=null){
            return dataList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView mainLayout;
        TextView eventName,totalExpense,membersCount,createdBy,createAt,getOrPay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            totalExpense = itemView.findViewById(R.id.total_expense);
            membersCount = itemView.findViewById(R.id.members_count);
            createdBy = itemView.findViewById(R.id.created_by);
            createAt = itemView.findViewById(R.id.create_at);
            getOrPay = itemView.findViewById(R.id.get_or_pay);
            mainLayout = itemView.findViewById(R.id.main_layout);
        }
    }
}
