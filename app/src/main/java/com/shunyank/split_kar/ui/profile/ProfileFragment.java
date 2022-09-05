package com.shunyank.split_kar.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.shunyank.split_kar.activities.LoginActivity;
import com.shunyank.split_kar.databinding.FragmentProfileBinding;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.utils.SharedPref;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Account;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPref sharedPref = new SharedPref(requireContext());
        String name = sharedPref.getUserModel().getUser_name();
        String phone = sharedPref.getUserModel().getPhone_number();
        String url = sharedPref.getUserModel().getAvatar_url();

        binding.nameTv.setText(name);
        binding.phoneNumber.setText("+"+phone);

        Glide.with(root).load(url).into(binding.profileImage);

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client client = AppWriteHelper.GetAppWriteClient(getContext());

                Account account = new Account(client);
                try {
                    account.deleteSessions(new Continuation<Object>() {
                        @Override
                        public void resumeWith(@NonNull Object o) {

                            SharedPref sharedPref = new SharedPref(getActivity());
                            sharedPref.clearUser();
                            startActivity(new Intent( getActivity(), LoginActivity.class));
                            getParentFragment().getActivity().finish();
                        }

                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }
                    });
                } catch (AppwriteException e) {
                    e.printStackTrace();
                }


            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}