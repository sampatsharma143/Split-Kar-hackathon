package com.shunyank.split_kar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.shunyank.split_kar.R;
import com.shunyank.split_kar.activities.BillSettlementActivity;
import com.shunyank.split_kar.models.GroupModel;
import com.shunyank.split_kar.utils.Helper;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WhoWillPayWhomAdapter extends RecyclerView.Adapter<WhoWillPayWhomAdapter.GroupViewHolder> {
    ArrayList<BillSettlementActivity.WhoPayWhomModel> data;

    public void setData(ArrayList<BillSettlementActivity.WhoPayWhomModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setAdapterClickListener(AdapterClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
    }


    AdapterClickListener adapterClickListener;
    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.who_pay_whom_layout,parent,false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        BillSettlementActivity.WhoPayWhomModel model = data.get(position);
        holder.payer.setText(model.getWhoWillPay());
        holder.receiver.setText(model.getToWhom());
        holder.amount.setText(String.valueOf(model.getHowMuchPay()));
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
        TextView payer,receiver,amount;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            payer = itemView.findViewById(R.id.payer);
            receiver = itemView.findViewById(R.id.receiver);
            amount = itemView.findViewById(R.id.amount);

        }
    }
}
