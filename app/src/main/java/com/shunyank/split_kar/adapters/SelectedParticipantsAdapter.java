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
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.adapters.listeners.AdapterClickListener;
import com.shunyank.split_kar.models.PhoneContact;

import java.util.ArrayList;

//RecyclerView.Adapter<FriendAndContactAdapter.AdapterViewHolder>
public class SelectedParticipantsAdapter extends ListAdapter<PhoneContact, SelectedParticipantsAdapter.AdapterViewHolder> {

    ArrayList<PhoneContact> dataList;
    Context context;

    public SelectedParticipantsAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }
    public static final DiffUtil.ItemCallback<PhoneContact> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PhoneContact>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull PhoneContact oldPhoneContact, @NonNull PhoneContact newPhoneContact) {
                    // PhoneContact properties may have changed if reloaded from the DB, but ID is fixed
                    return oldPhoneContact==newPhoneContact;
                }
                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(
                        @NonNull PhoneContact oldPhoneContact, @NonNull PhoneContact newPhoneContact) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.


                    return oldPhoneContact.getNumber().contentEquals(newPhoneContact.getNumber());
                }
            };
    AdapterClickListener clickListener;

    public void setClickListener(AdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.added_participents_layout,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        int pos = position;
        holder.userName.setText(getItem(pos).getName());
        Glide.with(context).load(context.getResources().getDrawable(R.drawable.user_profile_default)).into(holder.profile);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener!=null){
                    clickListener.onItemClick(getItem(pos));
                }
            }
        });


    }



    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView userName;
        ImageButton remove;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            remove = itemView.findViewById(R.id.remove);

        }
    }
}
