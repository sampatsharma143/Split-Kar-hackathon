package com.shunyank.split_kar.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.adapters.GroupBillsAdapter;
import com.shunyank.split_kar.adapters.WhoWillPayWhomAdapter;
import com.shunyank.split_kar.databinding.ActivityBillSettlementBinding;
import com.shunyank.split_kar.models.BillModel;
import com.shunyank.split_kar.models.SettlementModel;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentListFetchListener;
import com.shunyank.split_kar.network.utils.DatabaseUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public class BillSettlementActivity extends AppCompatActivity {

    ActivityBillSettlementBinding binding;

    ArrayList<BillModel> billModelArrayList;
    float totalExpense = 0;
    private float splitCount=0;
    float equalSplitAmount =0;
    ArrayList<SimpleBillMemberModel> needToPayUsers = new ArrayList<>();
    ArrayList<SimpleBillMemberModel> willGetUsers = new ArrayList<>();
    private ArrayList<SimpleBillMemberModel> simpleBillMemberModels = new ArrayList<>();
    int count =0;
    ArrayList<SettlementModel> transactions = new ArrayList<>();
    WhoWillPayWhomAdapter whoWillPayWhomAdapter;
    String groupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillSettlementBinding.inflate(getLayoutInflater());
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        setContentView(binding.getRoot());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.transactionRyc.setLayoutManager(layoutManager);
        whoWillPayWhomAdapter = new WhoWillPayWhomAdapter();
        binding.transactionRyc.setAdapter(whoWillPayWhomAdapter);
        binding.groupName.setText(getIntent().getStringExtra("group_name")+" Group Settlement");
        //        return new Gson().fromJson(members, );
        String billsJson = getIntent().getStringExtra("bills");
        if(billsJson!=null){

            billModelArrayList = new Gson().fromJson(billsJson,new TypeToken<ArrayList<BillModel>>() {}.getType());
//            Log.e("groupid",);
            groupId = billModelArrayList.get(0).getGroup_id();
            processArrayList(billModelArrayList);
        }else {
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            finish();
        }


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BillSettlementActivity.super.onBackPressed();
            }
        });

    }

    void processArrayList(ArrayList<BillModel> billModels){
        simpleBillMemberModels = new ArrayList<>();
        for (BillModel billModel:billModels){

            totalExpense = (float) (totalExpense+billModel.getTotal_expense());
            splitCount = billModel.getSplit_count();

            fetchBillsData(BillSettlementActivity.this,billModel, billModel.getGroup_id());
            count++;
        }
        equalSplitAmount = totalExpense/splitCount;


    }
    void fetchBillsData(Activity activity, BillModel billModel, String groupid){

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
                        // because the members will be the same for all the bills thats why we will only add members once and update their value with other models for same member

                        if(simpleBillMemberModels.size()==0) {
                            simpleBillMemberModels.addAll(DatabaseUtils.convertToModelList(documents, SimpleBillMemberModel.class));
                        }
                        ArrayList<SimpleBillMemberModel> models =DatabaseUtils.convertToModelList(documents,SimpleBillMemberModel.class);

                        if(simpleBillMemberModels.size()>=2) {
                            for (SimpleBillMemberModel billMemberModel : models) {


                                for (int i=0;i<simpleBillMemberModels.size();i++){

                                    String outerPhone =billMemberModel.getMember_number();
                                    String innerPhone =simpleBillMemberModels.get(i).getMember_number();
                                    String outerBillId = billMemberModel.getId();
                                    String innerBillId = simpleBillMemberModels.get(i).getId();
                                    if(outerPhone.contentEquals(innerPhone)&& !outerBillId.contentEquals(innerBillId)){
                                        float totalPaid = Float.parseFloat(billMemberModel.getTotal_paid()) +Float.parseFloat(simpleBillMemberModels.get(i).getTotal_paid());
                                        float need_to_pay = Float.parseFloat(billMemberModel.getNeed_to_pay()) +Float.parseFloat(simpleBillMemberModels.get(i).getNeed_to_pay());
                                        float willGet = Float.parseFloat(billMemberModel.getWill_get()) +Float.parseFloat(simpleBillMemberModels.get(i).getWill_get());



                                        simpleBillMemberModels.get(i).setTotal_paid(String.valueOf(totalPaid));
                                        simpleBillMemberModels.get(i).setNeed_to_pay(String.valueOf(need_to_pay));
                                        simpleBillMemberModels.get(i).setWill_get(String.valueOf(willGet));

                                    }

                                }
                            }
                        }
                        count = count-1;
                        Log.e("count",""+count);
                        if(count==0){

                           makeFurtherCalculation();
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

    public void makeFurtherCalculation(){
        Log.e("----------","--------------------------");
        Log.e("total expense",String.valueOf(totalExpense));
        Log.e("split amount",String.valueOf(equalSplitAmount));
        for (SimpleBillMemberModel model:simpleBillMemberModels){

            // add or remove settlement done on this group

            float need_to_pay = Float.parseFloat(model.getNeed_to_pay());
            float willGet = Float.parseFloat(model.getWill_get());
            float balance = willGet-need_to_pay;
            // if bigger then zero means user will get amount
            if(balance>0){

                model.setWill_get(String.valueOf(balance));
                model.setNeed_to_pay(String.valueOf(0));
                willGetUsers.add(model);

            }else {
                model.setWill_get(String.valueOf(0));
                model.setNeed_to_pay(String.valueOf( Math.abs( balance)));
                needToPayUsers.add(model);

            }


//            Log.e("memeber name",model.getMember_name());
//            Log.e("memeber total paid",model.total_paid);
//            Log.e("memeber Need To Pay",model.getNeed_to_pay());
//            Log.e("memeber Will Get",model.will_get);
//            Log.e("----------","--------------------------");
        }
        Log.e("will get user size",""+willGetUsers.size());
        Log.e("need to user size",""+needToPayUsers.size());
        createTransactionList();




    }
    void createTransactionList(){
//        outerscope:
//        for (SimpleBillMemberModel willGetModel:willGetUsers){
//
//            for (SimpleBillMemberModel needToPayModel:needToPayUsers){
//
//                float willGet = Float.parseFloat(willGetModel.getWill_get());
//                float needToPay = Float.parseFloat(needToPayModel.getNeed_to_pay());
////                Log.e("willget", ""+willGet);
////                Log.e("needToPay", ""+needToPay);
//                if( Math.abs(willGet-needToPay)<0.3){
//
//
//                    transactions.add(new WhoPayWhomModel(needToPayModel.getMember_name(),needToPay,willGetModel.getMember_name()));
//                    willGetUsers.remove(willGetModel);
//                    needToPayUsers.remove(needToPayModel);
//                    break outerscope;
//                }
//
//
//            }
//
//        }
//
//        // if == 0 not work then we will have to reduce
//        if(transactions.size()==0){
//
//
//
//
//
//        }
        checkOtherTransactionPos();

        whoWillPayWhomAdapter.setData(transactions);
        Log.e("transactions",new Gson().toJson(transactions));

    }

    void checkOtherTransactionPos(){
        for (int j=0;j<needToPayUsers.size();j++) {

            Log.e("needToPayUser",""+needToPayUsers.size());
            for (int i=0;i<willGetUsers.size();i++) {

                float willGet =  getFloatValue( Float.parseFloat(willGetUsers.get(i).getWill_get()));
                float needToPay = getFloatValue( Float.parseFloat(needToPayUsers.get(j).getNeed_to_pay()));
                Log.e("----------","--------------------------");

                Log.e("willget", "" + willGet);
                Log.e("needToPay", "" + needToPay);


                if (willGet>needToPay) {

                    float balance = willGet-needToPay;
                    SettlementModel settlementModel = new SettlementModel();

                    settlementModel.setGroup_id(groupId);
                    settlementModel.setIs_settled(false);
                    settlementModel.setPayable_amount(String.valueOf(needToPay));
                    settlementModel.setPayerDetails(
                            needToPayUsers.get(j).getMember_name(),
                            needToPayUsers.get(j).getMember_number(),
                            needToPayUsers.get(j).getMember_app_id(),
                            needToPayUsers.get(j).getUser_data(),
                            needToPayUsers.get(j).isMember_is_on_app(),
                            needToPayUsers.get(j).isIs_admin()
                    );

                    settlementModel.setReceiverDetails(
                            willGetUsers.get(i).getMember_name(),
                            willGetUsers.get(i).getMember_number(),
                            willGetUsers.get(i).getMember_app_id(),
                            willGetUsers.get(i).getUser_data(),
                            willGetUsers.get(i).isMember_is_on_app(),
                            willGetUsers.get(i).isIs_admin()
                    );



//                    transactions.add(new WhoPayWhomModel(needToPayUsers.get(j).getMember_name(), needToPay, willGetUsers.get(i).getMember_name()));
                    transactions.add(settlementModel);
                    willGetUsers.get(i).setWill_get(String.valueOf(balance));
                    needToPayUsers.remove(needToPayUsers.get(j));
                    checkOtherTransactionPos();
                }
                else if( Math.abs(willGet-needToPay)<0.3){
                    SettlementModel settlementModel = new SettlementModel();

                    settlementModel.setGroup_id(groupId);
                    settlementModel.setIs_settled(false);
                    settlementModel.setPayable_amount(String.valueOf(needToPay));
                    settlementModel.setPayerDetails(
                            needToPayUsers.get(j).getMember_name(),
                            needToPayUsers.get(j).getMember_number(),
                            needToPayUsers.get(j).getMember_app_id(),
                            needToPayUsers.get(j).getUser_data(),
                            needToPayUsers.get(j).isMember_is_on_app(),
                            needToPayUsers.get(j).isIs_admin()
                    );

                    settlementModel.setReceiverDetails(
                            willGetUsers.get(i).getMember_name(),
                            willGetUsers.get(i).getMember_number(),
                            willGetUsers.get(i).getMember_app_id(),
                            willGetUsers.get(i).getUser_data(),
                            willGetUsers.get(i).isMember_is_on_app(),
                            willGetUsers.get(i).isIs_admin()
                    );


                    Log.e("needToPay", "" + Math.abs(willGet-needToPay));
                    float balance = willGet-needToPay;
                    transactions.add(settlementModel);
                    willGetUsers.get(i).setWill_get(String.valueOf(balance));
                    needToPayUsers.remove(needToPayUsers.get(j));
                    checkOtherTransactionPos();
                }
            }

        }
    }

    public static class SimpleBillMemberModel{
        @SerializedName("$id")
        String id;
        boolean member_is_on_app,is_admin;
        String group_id,bill_id,need_to_pay,will_get,total_paid,member_name,member_number,member_app_id,user_data;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isMember_is_on_app() {
            return member_is_on_app;
        }

        public void setMember_is_on_app(boolean member_is_on_app) {
            this.member_is_on_app = member_is_on_app;
        }

        public boolean isIs_admin() {
            return is_admin;
        }

        public void setIs_admin(boolean is_admin) {
            this.is_admin = is_admin;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getBill_id() {
            return bill_id;
        }

        public void setBill_id(String bill_id) {
            this.bill_id = bill_id;
        }

        public String getNeed_to_pay() {
            return need_to_pay;
        }

        public void setNeed_to_pay(String need_to_pay) {
            this.need_to_pay = need_to_pay;
        }

        public String getWill_get() {
            return will_get;
        }

        public void setWill_get(String will_get) {
            this.will_get = will_get;
        }

        public String getTotal_paid() {
            return total_paid;
        }

        public void setTotal_paid(String total_paid) {
            this.total_paid = total_paid;
        }

        public String getMember_name() {
            return member_name;
        }

        public void setMember_name(String member_name) {
            this.member_name = member_name;
        }

        public String getMember_number() {
            return member_number;
        }

        public void setMember_number(String member_number) {
            this.member_number = member_number;
        }

        public String getMember_app_id() {
            return member_app_id;
        }

        public void setMember_app_id(String member_app_id) {
            this.member_app_id = member_app_id;
        }

        public String getUser_data() {
            return user_data;
        }

        public void setUser_data(String user_data) {
            this.user_data = user_data;
        }
    }
    public class WhoPayWhomModel{







        String whoWillPay;
        float howMuchPay;
        String toWhom;


        public WhoPayWhomModel(String whoWillPay, float howMuchPay, String toWhom) {
            this.whoWillPay = whoWillPay;
            this.howMuchPay = howMuchPay;
            this.toWhom = toWhom;
        }

        public String getWhoWillPay() {
            return whoWillPay;
        }

        public void setWhoWillPay(String whoWillPay) {
            this.whoWillPay = whoWillPay;
        }

        public float getHowMuchPay() {
            return howMuchPay;
        }

        public void setHowMuchPay(float howMuchPay) {
            this.howMuchPay = howMuchPay;
        }

        public String getToWhom() {
            return toWhom;
        }

        public void setToWhom(String toWhom) {
            this.toWhom = toWhom;
        }
    }

    float getFloatValue(float value){
        DecimalFormat df = new DecimalFormat("#.00");

        return Float.parseFloat( df.format(value));

    }
}