package com.shunyank.split_kar.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.activities.CreateGroupActivity;
import com.shunyank.split_kar.activities.GroupActivity;
import com.shunyank.split_kar.adapters.GroupListAdapter;
import com.shunyank.split_kar.adapters.listeners.AdapterClickListener;
import com.shunyank.split_kar.databinding.FragmentHomeBinding;
import com.shunyank.split_kar.models.GroupModel;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.callbacks.ErrorListener;
import com.shunyank.split_kar.network.utils.DatabaseUtils;
import com.shunyank.split_kar.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

import io.appwrite.Query;
import io.appwrite.services.Databases;
import kotlin.Result;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    String userId;
    Databases database;
    GroupListAdapter groupListAdapter;
    GroupListAdapter friendsGroupListAdapter;
    private HomeViewModel homeViewModel;
    private CountDownTimer countdown;
    private boolean paused=false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // init views
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        RecyclerView.LayoutManager fgLayoutManager = new LinearLayoutManager(requireContext());
        binding.groupsListRecyclerview.setLayoutManager(layoutManager);
        binding.friendsGroupsListRecyclerview.setLayoutManager(fgLayoutManager);


        groupListAdapter = new GroupListAdapter();

        binding.groupsListRecyclerview.setAdapter(groupListAdapter);

        friendsGroupListAdapter = new GroupListAdapter();
        binding.friendsGroupsListRecyclerview.setAdapter(friendsGroupListAdapter);

        friendsGroupListAdapter.setAdapterClickListener(new AdapterClickListener() {
            @Override
            public void onItemClick(Object item) {
                GroupModel groupModel = (GroupModel) item;
                Intent intent = new Intent(requireActivity(), GroupActivity.class);
                intent.putExtra("group_id",groupModel.getId());
                intent.putExtra("group_name",groupModel.getGroupName());
                intent.putExtra("group_members",groupModel.getMembersAsString());
                startActivity(intent);
                Log.e("clicked",new Gson().toJson(groupModel));
            }
        });
        groupListAdapter.setAdapterClickListener(new AdapterClickListener() {
            @Override
            public void onItemClick(Object item) {
                GroupModel groupModel = (GroupModel) item;
                Intent intent = new Intent(requireActivity(), GroupActivity.class);
                intent.putExtra("group_id",groupModel.getId());
                intent.putExtra("group_name",groupModel.getGroupName());
                intent.putExtra("group_members",groupModel.getMembersAsString());
                startActivity(intent);
                Log.e("clicked",new Gson().toJson(groupModel));
            }
        });



        binding.createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
        binding.addBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBill();
            }
        });

        userId = new SharedPref(getContext()).getUserModel().getId();
        database = DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(getContext()));







        homeViewModel.groupList.observe(requireActivity(), new Observer<ArrayList<GroupModel>>() {
            @Override
            public void onChanged(ArrayList<GroupModel> groupModels) {

                    if(groupModels.size()>0){





                        binding.noGroupsLayout.setVisibility(View.GONE);
                    }else {
                        binding.noGroupsLayout.setVisibility(View.VISIBLE);

                    }

                    groupListAdapter.setData(groupModels);

            }
        });
        homeViewModel.friendsGroupList.observe(requireActivity(), new Observer<ArrayList<GroupModel>>() {
            @Override
            public void onChanged(ArrayList<GroupModel> groupModels) {
                Log.e("Friend   groupModels",new Gson().toJson(groupModels));
                if(groupModels.size()==0) {
                    binding.friendsGroupsTitle.setVisibility(View.GONE);
                }else {
                    binding.friendsGroupsTitle.setVisibility(View.VISIBLE);

                }

                friendsGroupListAdapter.setData(groupModels);
                friendsGroupListAdapter.notifyDataSetChanged();
            }
        });

        homeViewModel.youNeedToPay.observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.e("YOu need to pay ", s);
                binding.youNeedToPay.setText("₹"+s);
            }
        });
        homeViewModel.youWillGet.observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.e("YOu will get ", s);
                binding.youwillGet.setText("₹"+s);
            }
        });

        binding.addBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });

        getMyGroups();
        getFriendsGroups();

        return root;
    }

    private void getFriendsGroups() {
        if(homeViewModel!=null) {
            List<Object> searchQuery = new ArrayList<>();
            searchQuery.add(Query.Companion.equal("is_admin", false));
            searchQuery.add(Query.Companion.equal("member_app_id", userId));
            List<Object> searchQuery2 = new ArrayList<>();
            searchQuery2.add(Query.Companion.equal("member_app_id", userId));

            homeViewModel.listMyAllGroups(getActivity(), database, searchQuery2, new ErrorListener() {
                @Override
                public void onFailedError(Result.Failure failure) {

                }

                @Override
                public void onException(Exception e) {

                }
            });

            homeViewModel.listFriendsGroups(getActivity(), database, searchQuery, new ErrorListener() {
                @Override
                public void onFailedError(Result.Failure failure) {
                    failure.exception.printStackTrace();
                }

                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

    void getMyGroups(){
        if(homeViewModel!=null) {
            List<Object> searchQuery = new ArrayList<>();
            searchQuery.add(Query.Companion.equal("admin", userId));
            homeViewModel.fetchMyGroups(getActivity(), database, searchQuery, new ErrorListener() {
                @Override
                public void onFailedError(Result.Failure failure) {
                    failure.exception.printStackTrace();
                }

                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void addNewBill() {

    }

    private void createGroup(){
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();


        View view  = LayoutInflater.from(getContext()).inflate(R.layout.create_group_dialog,binding.getRoot(),false);
        dialog.setView(view);
        ImageButton closeDialog = view.findViewById(R.id.close_dialog)  ;
        EditText editText = view.findViewById(R.id.group_name);
        Button nextButton = view.findViewById(R.id.next_button);
        AppCompatButton cancelButton = view.findViewById(R.id.cancel_text);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group_name = editText.getText().toString();

                if(group_name.isEmpty()){
                    Toast.makeText(getContext(),"Enter New Group Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent =new Intent(getActivity(), CreateGroupActivity.class);
                intent.putExtra("name",group_name);
                startActivity(intent);
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyGroups();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}