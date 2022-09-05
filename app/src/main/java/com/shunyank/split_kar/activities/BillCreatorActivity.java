package com.shunyank.split_kar.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.adapters.listeners.AmountChangeListener;
import com.shunyank.split_kar.adapters.EnterAmountAdapter;
import com.shunyank.split_kar.adapters.SplitBetweenMembersAdapter;
import com.shunyank.split_kar.databinding.ActivityBillCreatorBinding;
import com.shunyank.split_kar.models.SplitAmountModel;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentCreateListener;
import com.shunyank.split_kar.network.model.GroupMemberCollectionModel;
import com.shunyank.split_kar.network.utils.DatabaseUtils;
import com.shunyank.split_kar.utils.SharedPref;
import com.skydoves.powerspinner.DefaultSpinnerAdapter;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.services.Databases;
import kotlin.Result;

public class BillCreatorActivity extends AppCompatActivity {

    String groupId;
    String membersListString;
    ActivityBillCreatorBinding binding;
    private Databases database;
    ArrayList<GroupMemberCollectionModel> groupMembersList;
    ArrayList<SplitAmountModel> splitAmountModelsList = new ArrayList<>();
    ArrayList<String> membersName=new ArrayList<>();
    private SharedPref sharedPref;
    SplitBetweenMembersAdapter betweenMembersAdapter;
    int participantSize =0;
    public static int SINGLE_PERSON =0;
    public static int MULTI_PERSON_PERSON =1;
    int paymentMode =SINGLE_PERSON;
    float totalExpense = 0;
    private SplitAmountModel selectedMember;
    EnterAmountAdapter enterAmountAdapter;
    private float equalAmount=0;
    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        binding = ActivityBillCreatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPref = new SharedPref(this);
        database = DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(this));
        groupId = getIntent().getStringExtra("group_id");
        groupMembersList = new Gson().fromJson(getIntent().getStringExtra("group_members_list"),new TypeToken<ArrayList<GroupMemberCollectionModel>>() {
        }.getType());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);

        binding.splitRecyclerview.setLayoutManager(layoutManager);
        betweenMembersAdapter = new SplitBetweenMembersAdapter();
        betweenMembersAdapter.setContext(this);
        binding.splitRecyclerview.setAdapter(betweenMembersAdapter);
        participantSize = groupMembersList.size();

        // setting up recyclerview for multiperson paying

        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        binding.multiPersonRyc.setLayoutManager(layoutManager1);
        enterAmountAdapter = new EnterAmountAdapter();
        enterAmountAdapter.setContext(this);
        binding.multiPersonRyc.setAdapter(enterAmountAdapter);

        enterAmountAdapter.setAmountChangeListener(new AmountChangeListener() {
            @Override
            public void onAmountChange(ArrayList<SplitAmountModel> splitAmountModels) {

                ArrayList<SplitAmountModel> newSplitModelList = (ArrayList<SplitAmountModel>) splitAmountModels.clone();

                 totalExpense = 0;

                for (SplitAmountModel model:newSplitModelList){
                    model.setNeedToPay("0");
                    model.setWillGet("0");
                    if(model.isPaidByMe()) {
                        totalExpense =   totalExpense + Float.parseFloat(model.getTotalPaid());
                    }
                }
                 equalAmount = totalExpense/participantSize;
                updateAmounts();
                for (SplitAmountModel model:newSplitModelList){


                    float remaining = Float.parseFloat(model.getTotalPaid()) - equalAmount;
                    if(remaining>0){
                        model.setWillGet(getFloatValue(remaining));

                    }
                    if(remaining<0){
                        model.setNeedToPay(getFloatValue(Math.abs(remaining)));

                    }
                    if(Math.abs(remaining)==equalAmount){
                        model.setPaidByMe(false);
                    }else {
                        model.setPaidByMe(true);

                    }
                    model.setTotalPaid(getFloatValue(Float.parseFloat(model.getTotalPaid())));



                }

                betweenMembersAdapter.setDataList(newSplitModelList);




            }
        });



        // for single person paying
        binding.amountEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float value =0;
                if(s.length()>0){
                    value= Float.parseFloat(String.valueOf(s));
                    Log.e("value",""+value);
                }else {
                    value =0;
                }
                if(selectedMember!=null){
                    calculateSplit(value, selectedMember);

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        binding.singleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSingle();
            }
        });

        binding.multipleLayout.setOnClickListener(v -> {
            selectMulti();
        });

        for (GroupMemberCollectionModel model :groupMembersList){

            splitAmountModelsList.add(new SplitAmountModel(model,"0","0"));
            if(sharedPref.getMyPhoneNumber().contentEquals(model.getMember_number())){
                membersName.add(0,"(You) "+model.getMember_name());

            }else {
                membersName.add(model.getMember_name());

            }
        }
        betweenMembersAdapter.setDataList(splitAmountModelsList);
        enterAmountAdapter.setDataList(splitAmountModelsList);

//
//        ArrayList<String> data = new ArrayList<>();
//        data.add(" ");
//
//
        DefaultSpinnerAdapter spinnerAdapter = new DefaultSpinnerAdapter(binding.paidBySpinner);
        spinnerAdapter.setItems(membersName);
        binding.paidBySpinner.setSpinnerAdapter(spinnerAdapter);
        spinnerAdapter.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<CharSequence>() {
            @Override
            public void onItemSelected(int i, @Nullable CharSequence charSequence, int i1, CharSequence t1) {
                // resetting paid by me to false
                for (SplitAmountModel model :splitAmountModelsList){
                    model.setPaidByMe(false);
                    model.setWillGet("0");
                    model.setNeedToPay("0");
                    model.setTotalPaid("0");
                }
                selectedMember = splitAmountModelsList.get(i1);
                calculate(splitAmountModelsList.get(i1));

            }
        });
        binding.paidBySpinner.selectItemByIndex(0);

        binding.saveBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.eventEdt.getText().toString().trim().isEmpty()){
                    Toast.makeText(BillCreatorActivity.this, "Please enter event name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showSavingLoading();
                saveBillOnServer();
            }
        });


    }
    // multi payer calculation

    void saveBillOnServer(){

        HashMap<Object,Object> data = new HashMap<>();
        data.put("group_id",groupId);
        data.put("event_name",binding.eventEdt.getText().toString().trim());
        data.put("total_expense",totalExpense);
        data.put("paid_type",paymentMode);
        data.put("split_count",participantSize);
        data.put("created_by",sharedPref.getUserModel().getUser_name());
        data.put("is_completed",false);
        // Bill id (auto)
        // String group_id;
        // total_expense;
        // paid_type;
        // split_count=0;



//        data.put("")




        DatabaseUtils.createDocument(BillCreatorActivity.this,
                database,
                AppWriteHelper.getCollectionId(Constants.GroupBillsCollectionId),
                data,
                new DocumentCreateListener() {
                    @Override
                    public void onCreatedSuccessfully(Document document) {
                        String billId =document.getId();
                        for(SplitAmountModel model:splitAmountModelsList){

                            addBill(model,billId);

                        }
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
    void addBill(SplitAmountModel model,String billId){

        // needToPay
        // willGet
        // paidByMe
        // totalPaid
        // user_id;
        // member_name,
        // member_number,
        // member_app_id;
        // member_is_on_app;
        // is_admin;
        // user_data ->    avatar_url; upi_id;



        HashMap<Object,Object> data = new HashMap<>();

        data.put("group_id",groupId);
        data.put("bill_id",billId);
        data.put("need_to_pay",model.getNeedToPay());
        data.put("will_get",model.getWillGet());
        data.put("total_paid",model.getTotalPaid());
        data.put("member_name",model.getMemberDetails().getMember_name());
        data.put("member_number",model.getMemberDetails().getMember_number());
        data.put("member_app_id",model.getMemberDetails().getMember_app_id());
        data.put("member_is_on_app",model.getMemberDetails().isMember_is_on_app());
        data.put("is_admin",model.getMemberDetails().isIs_admin());
        data.put("user_data",model.getMemberDetails().getUser_data());
        DatabaseUtils.createDocument(BillCreatorActivity.this,
                database,
                AppWriteHelper.getCollectionId(Constants.BillsCollectionId),
                data,
                new DocumentCreateListener() {
                    @Override
                    public void onCreatedSuccessfully(Document document) {
                        count=count+1;
                        if(count==splitAmountModelsList.size()){
                            Log.e("bill saved "," we can finish the activity");

                            hideSavingLoading();
                            finish();
                        }
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

    void showSavingLoading(){
        binding.saveBill.setVisibility(View.GONE);
        binding.savingLoading.setVisibility(View.VISIBLE);
    }

    void hideSavingLoading(){
        binding.saveBill.setVisibility(View.VISIBLE);
        binding.savingLoading.setVisibility(View.GONE);
    }



    // for single payer calculation
    void calculate(SplitAmountModel paidBy){
        if(!binding.amountEdt.getText().toString().isEmpty()){
            float value= Float.parseFloat(String.valueOf(binding.amountEdt.getText().toString()));
            calculateSplit(value,paidBy);
        }

    }
    void  calculateSplit(float value, SplitAmountModel paidBy){
          equalAmount = value/participantSize;
          totalExpense = value;
            updateAmounts();
        for (SplitAmountModel model :splitAmountModelsList){
            if(model.getMemberDetails().getMember_number().contentEquals(paidBy.getMemberDetails().getMember_number())){

                // removing own share
                float willGet = value-equalAmount;
                model.setPaidByMe(true);
                model.setWillGet(getFloatValue(willGet));
                model.setNeedToPay("0");
                model.setTotalPaid(getFloatValue(value));
            }else {
                model.setPaidByMe(false);
                model.setWillGet("0");
                model.setNeedToPay(getFloatValue(equalAmount));
                model.setTotalPaid("0");

            }

            betweenMembersAdapter.setDataList(splitAmountModelsList);

        }


    }

    void selectSingle(){
        binding.singleView.setVisibility(View.VISIBLE);
        binding.multiView.setVisibility(View.GONE);
        binding.singlePayingLayout.setVisibility(View.VISIBLE);
        binding.multiPersonRyc.setVisibility(View.GONE);
        paymentMode = SINGLE_PERSON;

    }
    void selectMulti(){
        binding.singleView.setVisibility(View.GONE);
        binding.multiView.setVisibility(View.VISIBLE);
        binding.singlePayingLayout.setVisibility(View.GONE);
        binding.multiPersonRyc.setVisibility(View.VISIBLE);
        paymentMode = MULTI_PERSON_PERSON;
    }
    void updateAmounts(){
            binding.totalExpense.setText("Total Expense : "+getFloatValue(totalExpense));
            binding.splitAmount.setText("Split Amount : "+ getFloatValue(equalAmount));

    }

    String getFloatValue(float value){
        DecimalFormat df = new DecimalFormat("#.00");

        return df.format(value);

    }

}