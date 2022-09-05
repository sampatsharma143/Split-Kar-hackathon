package com.shunyank.split_kar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.adapters.GroupMembersAdapter;
import com.shunyank.split_kar.adapters.PaidMembersAdapter;
import com.shunyank.split_kar.adapters.RemainingPaymentMembersAdapter;
import com.shunyank.split_kar.adapters.SplitMembersAdapter;
import com.shunyank.split_kar.databinding.ActivityBillDetailsBinding;
import com.shunyank.split_kar.models.BillModel;
import com.shunyank.split_kar.models.GroupMemberModel;
import com.shunyank.split_kar.models.SplitAmountModel;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentListFetchListener;
import com.shunyank.split_kar.network.model.GroupMemberCollectionModel;
import com.shunyank.split_kar.network.utils.DatabaseUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public class BillDetailsActivity extends AppCompatActivity {

    String groupId;
    String billId;
    String eventName;
    ActivityBillDetailsBinding binding;
    BillModel billModel;
    ArrayList<GroupMemberCollectionModel> membersModels;
    ArrayList<BillSettlementActivity.SimpleBillMemberModel> paidMembersModels;
    ArrayList<BillSettlementActivity.SimpleBillMemberModel> splitMembersModels;
    ArrayList<BillSettlementActivity.SimpleBillMemberModel> remainingPaymentModels;

    GroupMembersAdapter membersAdapter;
    PaidMembersAdapter paidMembersAdapter;
    SplitMembersAdapter splitMembersAdapter;
    RemainingPaymentMembersAdapter remainingPaymentMembersAdapter;
    private double totalExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillDetailsBinding.inflate(getLayoutInflater());
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        setContentView(binding.getRoot());



      String  billModelJson = getIntent().getStringExtra("bill_model_json");

      billModel = new Gson().fromJson(billModelJson,BillModel.class);

      groupId = billModel.getGroup_id();
      billId = billModel.getId();
      eventName = billModel.getEvent_name();
      binding.activityTitle.setText( eventName+" Bill Details");
      totalExpense = billModel.getTotal_expense();
      binding.totalExpense.setText( "₹"+totalExpense);


      // setting up members list
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
      binding.groupMembersRyc.setLayoutManager(layoutManager);
      membersAdapter = new GroupMembersAdapter();
      membersAdapter.setContext(this);
      binding.groupMembersRyc.setAdapter(membersAdapter);

      // setting up paid members list

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
        binding.paidMembersRyc.setLayoutManager(layoutManager1);
        paidMembersAdapter = new PaidMembersAdapter();
        paidMembersAdapter.setContext(this);
        binding.paidMembersRyc.setAdapter(paidMembersAdapter);


        // setting up split members list



        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
        binding.splitMembersRyc.setLayoutManager(layoutManager2);
        splitMembersAdapter = new SplitMembersAdapter();
        splitMembersAdapter.setContext(this);
        binding.splitMembersRyc.setAdapter(splitMembersAdapter);




        // setting up split members list



        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
        binding.remainingPaymentRyc.setLayoutManager(layoutManager3);
        remainingPaymentMembersAdapter = new RemainingPaymentMembersAdapter();
        remainingPaymentMembersAdapter.setContext(this);
        binding.remainingPaymentRyc.setAdapter(remainingPaymentMembersAdapter);




        fetchBillsData(this,billModel,groupId);



    binding.backButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BillDetailsActivity.super.onBackPressed();
        }
    });
    }


    void fetchBillsData(Activity activity, BillModel billModel, String groupid){
        membersModels = new ArrayList<>();
        paidMembersModels = new ArrayList<>();
        splitMembersModels = new ArrayList<>();
        remainingPaymentModels = new ArrayList<>();
        List<Object> searchQuery = new ArrayList<>();
        searchQuery.add(Query.Companion.equal("bill_id",billModel.getId()));
        searchQuery.add(Query.Companion.equal("group_id",groupid));
        DatabaseUtils.fetchDocuments(activity,
                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(activity)),
                AppWriteHelper.getCollectionId(Constants.BillsCollectionId),
                searchQuery,
                new DocumentListFetchListener() {
                    @Override
                    public void onFetchSuccessfully(List<Document> documents) {

                        ArrayList<BillSettlementActivity.SimpleBillMemberModel> models =DatabaseUtils.convertToModelList(documents, BillSettlementActivity.SimpleBillMemberModel.class);
                        remainingPaymentModels = models;
                        for (BillSettlementActivity.SimpleBillMemberModel model :models){

                            float totalPaid = Float.valueOf(model.getTotal_paid());
                            if(totalPaid>0){
                                Log.e("paid",""+totalPaid);
                                BillSettlementActivity.SimpleBillMemberModel paidModel = new BillSettlementActivity.SimpleBillMemberModel();
                                paidModel.setMember_name(model.getMember_name());
                                paidModel.setMember_number(model.getMember_number());
                                paidModel.setGroup_id(model.getGroup_id());
                                paidModel.setIs_admin(model.isIs_admin());
                                if(model.isMember_is_on_app()){
                                    paidModel.setMember_is_on_app(model.isMember_is_on_app());
                                    paidModel.setMember_app_id(model.getMember_app_id());
                                    paidModel.setUser_data(model.getUser_data());
                                }
                                paidModel.setTotal_paid(String.valueOf(totalPaid));
                                paidMembersModels.add(paidModel);
                            }
                            splitMembersModels.add(model);




                            GroupMemberCollectionModel gMCModel = new GroupMemberCollectionModel();
                            gMCModel.setMember_name(model.getMember_name());
                            gMCModel.setMember_number(model.getMember_number());
                            gMCModel.setGroup_id(model.getGroup_id());
                            gMCModel.setIs_admin(model.isIs_admin());

                            if(model.isMember_is_on_app()){
                                gMCModel.setMember_is_on_app(model.isMember_is_on_app());
                                gMCModel.setMember_app_id(model.getMember_app_id());
                                gMCModel.setUser_data(model.getUser_data());
                            }
                                membersModels.add(gMCModel);
                        }

                        membersAdapter.setDataList(membersModels);
                        paidMembersAdapter.setDataList(paidMembersModels);
                        remainingPaymentMembersAdapter.setDataList(remainingPaymentModels);
                        float split = (float) (totalExpense / membersModels.size());
                        for ( BillSettlementActivity.SimpleBillMemberModel model:splitMembersModels){
                            model.setTotal_paid(getFloatValue(split));
                        }
                        splitMembersAdapter.setDataList(splitMembersModels);

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
    String getFloatValue(float value){
        DecimalFormat df = new DecimalFormat("#.00");

        return  df.format(value);

    }
}