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
import com.shunyank.split_kar.adapters.listeners.AdapterClickListener;
import com.shunyank.split_kar.models.GroupModel;
import com.shunyank.split_kar.utils.Helper;

import java.util.ArrayList;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {
    ArrayList<GroupModel> data;

    public void setData(ArrayList<GroupModel> data) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_layout,parent,false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupModel gm = data.get(position);
        holder.groupName.setText(gm.getGroupName());
        holder.date.setText(
                appendMembersCount(Helper.ConvertTimeStampToDate(gm.getCreatedAt()),
                gm.getMembers().size()
                ));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterClickListener.onItemClick(gm);
            }
        });
        holder.clickButton.setOnClickListener(v->{
            adapterClickListener.onItemClick(gm);


        });
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
        ConstraintLayout mainLayout;
        TextView groupName;
        TextView date;
        ImageView clickButton;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.main_layout);
            groupName = itemView.findViewById(R.id.group_name_tv);
            date = itemView.findViewById(R.id.date_tv);
            clickButton = itemView.findViewById(R.id.click_button);
        }
    }
}
