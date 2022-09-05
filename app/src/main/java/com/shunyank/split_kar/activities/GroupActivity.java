package com.shunyank.split_kar.activities;

import static com.shunyank.split_kar.utils.Helper.getFloatValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.adapters.listeners.AdapterClickListener;
import com.shunyank.split_kar.adapters.GroupBillsAdapter;
import com.shunyank.split_kar.adapters.GroupMembersAdapter;
import com.shunyank.split_kar.databinding.ActivityGroupBinding;
import com.shunyank.split_kar.models.BillModel;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentListFetchListener;
import com.shunyank.split_kar.network.model.GroupMemberCollectionModel;
import com.shunyank.split_kar.network.utils.DatabaseUtils;
import com.shunyank.split_kar.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public class GroupActivity extends AppCompatActivity {

    ActivityGroupBinding groupBinding;
    private GroupActivityViewHolder gavHolder;
    private String groupName;
    private String groupId;
    private String groupMembersString;
    public ArrayList<GroupMemberCollectionModel> groupMembersListForAdapter;
    GroupMembersAdapter groupMembersAdapter ;
    int count =1;
    private GroupActivityViewHolder groupActivityViewHolder;
    private int membersSize;
    GroupBillsAdapter groupBillsAdapter;
    private SharedPref sharedPref;
    boolean onPause = false;
    double totalExpense =0;
    String calculatedBillModels = "";
    private ArrayList<BillModel> billModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        groupBinding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(groupBinding.getRoot());

        sharedPref = new SharedPref(this);
        groupActivityViewHolder = new ViewModelProvider(this).get(GroupActivityViewHolder.class);

        groupName = getIntent().getStringExtra("group_name");
        //getting group members
        groupMembersString = getIntent().getStringExtra("group_members");

        groupId = getIntent().getStringExtra("group_id");
        gavHolder = new ViewModelProvider(this).get(GroupActivityViewHolder.class);
        groupBinding.groupName.setText(groupName);


        //setting up the recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);

        groupBinding.groupMembersRyc.setLayoutManager(layoutManager);
        groupMembersAdapter = new GroupMembersAdapter();
        groupMembersAdapter.setContext(this);
        groupBinding.groupMembersRyc.setAdapter(groupMembersAdapter);



        RecyclerView.LayoutManager verticalLayoutManager = new LinearLayoutManager(this);
        groupBinding.billsRyc.setLayoutManager(verticalLayoutManager);
        groupBillsAdapter = new GroupBillsAdapter();
        groupBillsAdapter.setContext(this);
        groupBinding.billsRyc.setAdapter(groupBillsAdapter);

        // hide balance section
        hideBalanceSection();







        // click listeneres
        groupBillsAdapter.setClickListener(new AdapterClickListener() {
            @Override
            public void onItemClick(Object item) {
                Intent intent = new Intent(GroupActivity.this,BillDetailsActivity.class);
                intent.putExtra("bill_model_json",new Gson().toJson(item));
                Log.e("clicked",new Gson().toJson(item));
                startActivity(intent);
            }
        });




        groupBinding.addBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(GroupActivity.this,BillCreatorActivity.class);
                intent.putExtra("group_id",groupId);
                intent.putExtra("group_members_list",new Gson().toJson(groupMembersListForAdapter));
                startActivity(intent);
            }
        });

        groupActivityViewHolder.balance.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                groupBinding.balanceProgressBar.setVisibility(View.GONE);
                groupBinding.getOrPay.setVisibility(View.VISIBLE);
                groupBinding.getOrPay.setText("₹"+ getFloatValue(Float.parseFloat(s)));

                // why here because we are posting balance data at one point of time
                groupBillsAdapter.setDataList(billModels);
            }
        });

        groupActivityViewHolder.bills.observe(this, new Observer<ArrayList<BillModel>>() {
            @Override
            public void onChanged(ArrayList<BillModel> billModels) {
                groupBillsAdapter.setDataList(billModels);
                calculatedBillModels =  new Gson().toJson(billModels).toString();
            }
        });


        groupBinding.balanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this,BillSettlementActivity.class);
                intent.putExtra("group_name",groupName);
                if(!calculatedBillModels.isEmpty()){
                    intent.putExtra("bills",calculatedBillModels);
                    startActivity(intent);
                }else {
                    Toast.makeText(GroupActivity.this, "Still Fetching Bills... ", Toast.LENGTH_SHORT).show();
                }
            }
        });


        groupActivityViewHolder.willGetOrNeedToPay.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                // aBoolean if true then we will set text Need to pay and set getOrPay Textveiw to red
                if(aBoolean){

                    groupBinding.amountTitle.setText("You Need To Pay");
                    groupBinding.getOrPay.setTextColor(getResources().getColor(R.color.red));

                }else {

                    groupBinding.amountTitle.setText("You Will Get");
                    groupBinding.getOrPay.setTextColor(getResources().getColor(R.color.green));

                }
            }
        });

        groupActivityViewHolder.count.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.e("on change size",""+integer);
                if(integer>=membersSize||integer==0){
                    groupBinding.settingUp.setVisibility(View.GONE);
                    Log.e("adapter data",new Gson().toJson(groupMembersListForAdapter));
//                    groupMembersAdapter.setDataList(groupMembersListForAdapter);
                }
            }
        });

        checkUserIsOnApp(groupId);

        groupBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupActivity.super.onBackPressed();
            }
        });




    }

    private void checkUserIsOnApp(String groupId) {
        groupBinding.settingUp.setVisibility(View.VISIBLE);
        getMembersList(groupId);
    }
    void getMembersList(String groupId){
        List<Object> searchQuery = new ArrayList<>();
        Log.e("called","get all member by group id");
        searchQuery.add(Query.Companion.equal("group_id",groupId));
        DatabaseUtils.fetchDocuments(this,
                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(this)),
                AppWriteHelper.getCollectionId(Constants.GroupMembersCollectionId),
                searchQuery,
                new DocumentListFetchListener() {
                    @Override
                    public void onFetchSuccessfully(List<Document> documents) {

                    ArrayList<GroupMemberCollectionModel>  groupMembersList = DatabaseUtils.convertToModelList(documents, GroupMemberCollectionModel.class);
                        groupMembersListForAdapter = groupMembersList;
                        groupMembersAdapter.setDataList(groupMembersListForAdapter);
//                        groupActivityViewHolder.updateCount(membersSize);
                        Log.e("member data default",new Gson().toJson(groupMembersList));
                        findBills(groupId);
                    }

                    @Override
                    public void onFailed(Result.Failure failure) {

                        failure.exception.printStackTrace();
                    }

                    @Override
                    public void onException(AppwriteException exception) {
                        exception.printStackTrace();
                    }
                }

        );



    }

    private void findBills(String groupId) {
        totalExpense = 0;
        billModels  = new ArrayList<>();
        List<Object> searchQuery = new ArrayList<>();
        searchQuery.add(Query.Companion.equal("group_id",groupId));
        DatabaseUtils.fetchDocuments(GroupActivity.this,
                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(GroupActivity.this)),
                AppWriteHelper.getCollectionId(Constants.GroupBillsCollectionId),
                searchQuery,
                new DocumentListFetchListener() {
                    @Override
                    public void onFetchSuccessfully(List<Document> documents) {

                        if(documents.size()>0){
                            hideNoBillLayout();
                            //Helper.ConvertTimeStampToDate(gm.getCreatedAt())
                            // setup adapter
                            billModels = DatabaseUtils.convertToModelList(documents,BillModel.class);
                            Log.e("billModels",new Gson().toJson(billModels));
                            for (BillModel billModel:billModels){
                                groupActivityViewHolder.fetchBillsData(GroupActivity.this,billModel,groupId,sharedPref.getUserId(),billModels,groupBillsAdapter);
                                totalExpense = totalExpense+billModel.getTotal_expense();
                            }
                            groupBinding.totalExpense.setText("₹"+ String.valueOf( totalExpense));
                            // now we know that there are some bills to calculate now we can show the balance section
                            groupActivityViewHolder.fetchMyBalance(GroupActivity.this,groupId,sharedPref.getUserId());
                            showBalanceSection();


                        }else {
                            showNoBillLayout();
                            //show no bills found

                        }
                        groupBinding.settingUp.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailed(Result.Failure failure) {

                    }

                    @Override
                    public void onException(AppwriteException exception) {

                    }
                }

        );


    }
    void hideNoBillLayout(){
        groupBinding.noBillLayout.setVisibility(View.GONE);
        groupBinding.noBillImageView.setVisibility(View.GONE);
    }
    void showNoBillLayout(){
        groupBinding.noBillLayout.setVisibility(View.VISIBLE);
        groupBinding.noBillImageView.setVisibility(View.VISIBLE);
    }

    void hideBalanceSection(){
        groupBinding.balanceTitle.setVisibility(View.GONE);
        groupBinding.balanceCard.setVisibility(View.GONE);

    }
    void showBalanceSection(){
        groupBinding.balanceTitle.setVisibility(View.VISIBLE);
        groupBinding.balanceCard.setVisibility(View.VISIBLE);
        groupBinding.balanceProgressBar.setVisibility(View.VISIBLE);

    }
    public static class UserModelForGroupMember{
        String avatar_url;
        String upi_id;
        public UserModelForGroupMember(){

        }
        public UserModelForGroupMember(String avatar_url, String upi_id) {
            this.avatar_url = avatar_url;
            this.upi_id = upi_id;
        }

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public String getUpi_id() {
            return upi_id;
        }

        public void setUpi_id(String upi_id) {
            this.upi_id = upi_id;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(groupId!=null){
            if(onPause) {
                onPause = false;
                findBills(groupId);
            }
        }
    }

}