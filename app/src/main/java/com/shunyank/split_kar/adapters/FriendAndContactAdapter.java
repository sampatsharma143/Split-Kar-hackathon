package com.shunyank.split_kar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.adapters.listeners.AdapterClickListener;
import com.shunyank.split_kar.models.PhoneContact;

import java.util.ArrayList;

//RecyclerView.Adapter<FriendAndContactAdapter.AdapterViewHolder>
public class FriendAndContactAdapter extends ListAdapter<PhoneContact,FriendAndContactAdapter.AdapterViewHolder> {

    ArrayList<PhoneContact> dataList;
    Context context;

    public FriendAndContactAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }
    public static final DiffUtil.ItemCallback<PhoneContact> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PhoneContact>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull PhoneContact oldPhoneContact, @NonNull PhoneContact newPhoneContact) {
                    // PhoneContact properties may have changed if reloaded from the DB, but ID is fixed
                    return oldPhoneContact.getNumber()==newPhoneContact.getNumber();
                }
                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(
                        @NonNull PhoneContact oldPhoneContact, @NonNull PhoneContact newPhoneContact) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.


                    return oldPhoneContact == newPhoneContact;
                }
            };
    AdapterClickListener clickListener;

    public void setClickListener(AdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_card_layout,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        int pos = position;
        holder.userName.setText(getItem(pos).getName());
        holder.phone.setText(getItem(pos).getNumber());
        Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.profile);
        holder.addButton.setVisibility(View.VISIBLE);
        holder.inviteButton.setVisibility(View.GONE);
        if(getItem(pos).isAdded){
            holder.addButton.setText("Remove");
        }else {
            holder.addButton.setText("ADD");

        }
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if already added that means its text is "Remove" and changing it to add
                if(getItem(pos).isAdded){
                    getItem(pos).isAdded = false;
                    holder.addButton.setText("ADD");


                }else {
                    holder.addButton.setText("Remove");
                    getItem(pos).isAdded = true;

                }
                clickListener.onItemClick(getItem(pos));

//                submitList(getCurrentList());

            }
        });


    }



    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView userName;
        TextView phone;
        Button addButton;
        AppCompatButton inviteButton;
        ConstraintLayout progressbar_layout;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_imageview);
            userName = itemView.findViewById(R.id.user_name);
            phone = itemView.findViewById(R.id.phone_number);
            addButton = itemView.findViewById(R.id.add_button);
            inviteButton = itemView.findViewById(R.id.invite_button);
            progressbar_layout = itemView.findViewById(R.id.progress_layout);
        }
    }
}
