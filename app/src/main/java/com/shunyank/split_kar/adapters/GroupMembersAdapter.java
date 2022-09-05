package com.shunyank.split_kar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.activities.GroupActivity;
import com.shunyank.split_kar.models.GroupMemberModel;
import com.shunyank.split_kar.models.PhoneContact;
import com.shunyank.split_kar.network.model.GroupMemberCollectionModel;
import com.shunyank.split_kar.utils.SharedPref;

import java.util.ArrayList;

//
public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.AdapterViewHolder> {

    ArrayList<GroupMemberCollectionModel> dataList;
    Context context;


    AdapterClickListener clickListener;

    public void setClickListener(AdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDataList(ArrayList<GroupMemberCollectionModel> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.group_members_layout,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        int pos = position;
        GroupMemberCollectionModel gmcm = dataList.get(pos);
        // checking if it is me
        if(dataList.get(pos).getMember_number().contentEquals(SharedPref.getMyPhoneNumber(context))){
            // its me show my name according to me

            holder.userName.setText(SharedPref.getMyName(context));

        }else {
            holder.userName.setText(dataList .get(pos).getMember_name());

        }
        if(gmcm.isMember_is_on_app()){
            GroupActivity.UserModelForGroupMember userModelForGroupMember  = new Gson().fromJson(gmcm.getUser_data(), GroupActivity.UserModelForGroupMember.class);
            String url = userModelForGroupMember.getAvatar_url();
            if(url!=null){
                Glide.with(context).load(url).into(holder.profile);

            }else {
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.profile);
            }
        }else {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.profile);
        }

        //        holder.remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(clickListener!=null){
//                    clickListener.onItemClick(getItem(pos));
//                }
//            }
//        });


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
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
        }
    }
}
