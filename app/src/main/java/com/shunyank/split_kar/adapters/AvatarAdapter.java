package com.shunyank.split_kar.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.activities.UserDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import io.appwrite.models.File;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AdapterViewHolder> {

    ArrayList<UserDetailsActivity.UserAvatarModel> avatarFiles;
    Context context;
    public AvatarAdapter(Context context) {
        this.context  = context;
    }

    public void setAvatarFiles(ArrayList<UserDetailsActivity.UserAvatarModel> avatarFiles) {
        this.avatarFiles = avatarFiles;
        notifyDataSetChanged();
    }
    AdapterClickListener clickListener;

    public void setClickListener(AdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar_layout,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        int pos = position;
        Glide.with(context).load(avatarFiles.get(pos).getAvatarUrl()).into(holder.profile);

        if(avatarFiles.get(pos).isSelected()){
            holder.check.setVisibility(View.VISIBLE);
        }else{
            holder.check.setVisibility(View.GONE);
        }

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (avatarFiles != null) {
            return avatarFiles.size();
        }else {
            return 0;
        }
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView profile,check;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            check = itemView.findViewById(R.id.checked);
        }
    }
}
