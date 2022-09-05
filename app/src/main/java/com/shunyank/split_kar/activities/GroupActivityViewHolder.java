package com.shunyank.split_kar.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.shunyank.split_kar.adapters.GroupBillsAdapter;
import com.shunyank.split_kar.models.BillModel;
import com.shunyank.split_kar.models.PayerModel;
import com.shunyank.split_kar.models.PhoneContact;
import com.shunyank.split_kar.models.ReceiverModel;
import com.shunyank.split_kar.models.SettlementModel;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentListFetchListener;
import com.shunyank.split_kar.network.callbacks.FriendFetchListener;
import com.shunyank.split_kar.network.model.UserModel;
import com.shunyank.split_kar.network.utils.DatabaseUtils;
import com.shunyank.split_kar.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.services.Databases;
import kotlin.Result;


public class GroupActivityViewHolder extends ViewModel {

//    public MutableLiveData<ArrayList<PhoneContact>> myContacts;
    public MutableLiveData<Integer> count;
    public MutableLiveData<Boolean> willGetOrNeedToPay;
    public MutableLiveData<String> balance;
    public MutableLiveData<Float> paidAlready;
    public float amount;
    public float paidAlreadyAmount;
    public float receivedAlreadyAmount;

    float needToPayTemp =0;
    float willGetTemp =0;
    float expense =0;
    public MutableLiveData<ArrayList<BillModel>> bills;


    private ArrayList<SettlementModel> settlementModels;

    public GroupActivityViewHolder() {
        count = new MutableLiveData<>();
        bills = new MutableLiveData<>();
        balance = new MutableLiveData<>();
        willGetOrNeedToPay = new MutableLiveData<>();
    }




    void fetchBillsData(Activity activity, BillModel billModel, String groupid, String member_app_id, ArrayList<BillModel> billModels, GroupBillsAdapter groupBillsAdapter){
        needToPayTemp = 0;
        willGetTemp =0;
        List<Object> searchQuery = new ArrayList<>();
        searchQuery.add(Query.Companion.equal("bill_id",billModel.getId()));
        searchQuery.add(Query.Companion.equal("group_id",groupid));
        searchQuery.add(Query.Companion.equal("member_app_id",member_app_id));
        DatabaseUtils.fetchDocuments(activity,
                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(activity)),
                AppWriteHelper.getCollectionId(Constants.BillsCollectionId),
                searchQuery,
                new DocumentListFetchListener() {
                    @Override
                    public void onFetchSuccessfully(List<Document> documents) {

                        for (Document document:documents){

//                            Log.e(" id",document.getId());
//                            Log.e("bill id",billModel.getId());
//                            Log.e("group id",groupid);
//                            Log.e("name",document.getData().get("member_name").toString());
//                            Log.e("need to pay",document.getData().get("need_to_pay").toString());
//                            Log.e("will get",document.getData().get("will_get").toString());
//                            Log.e("space","----------------------------");
                            String needToPay = document.getData().get("need_to_pay").toString();
                            String willGet = document.getData().get("will_get").toString();
//
//                            needToPayTemp = needToPayTemp + Float.parseFloat(needToPay);
//                            willGetTemp = willGetTemp + Float.parseFloat(willGet);

                            for (BillModel model :billModels){
                                if (model.getId().contentEquals(billModel.getId())){
                                    if(!needToPay.isEmpty()&&!needToPay.contentEquals("0"))
                                    {
                                        billModel.setGetOrPay("₹"+needToPay);
                                        billModel.setNeedToPay(true);
                                    }
                                    else if(!willGet.isEmpty()&&!willGet.contentEquals("0"))
                                    {
                                        billModel.setGetOrPay("₹"+willGet);
                                        billModel.setNeedToPay(false);

                                    }
                                }
                            }


                        }
                        bills.postValue(billModels);


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



    void fetchMyBalance(Activity activity, String groupid, String member_app_id){
        needToPayTemp = 0;
        willGetTemp =0;
        List<Object> searchQuery = new ArrayList<>();

        searchQuery.add(Query.Companion.equal("group_id",groupid));
        searchQuery.add(Query.Companion.equal("member_app_id",member_app_id));
        DatabaseUtils.fetchDocuments(activity,
                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(activity)),
                AppWriteHelper.getCollectionId(Constants.BillsCollectionId),
                searchQuery,
                new DocumentListFetchListener() {
                    @Override
                    public void onFetchSuccessfully(List<Document> documents) {

                        for (Document document:documents){

//                            Log.e(" id",document.getId());
//                            Log.e("bill id",billModel.getId());
//                            Log.e("group id",groupid);
//                            Log.e("name",document.getData().get("member_name").toString());
//                            Log.e("need to pay",document.getData().get("need_to_pay").toString());
//                            Log.e("will get",document.getData().get("will_get").toString());
//                            Log.e("space","----------------------------");
                            String needToPay = document.getData().get("need_to_pay").toString();
                            String willGet = document.getData().get("will_get").toString();
                            String paid = document.getData().get("total_paid").toString();

                            needToPayTemp = needToPayTemp + Float.parseFloat(needToPay);
                            willGetTemp = willGetTemp + Float.parseFloat(willGet);
                            expense = expense + Float.parseFloat(paid);

                        }

                        fetchMyGroupSettlement(activity,groupid, SharedPref.getUserId(activity));





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


    void fetchMyGroupSettlement(Activity activity,String groupId,String userid){


        List<Object> searchQuery = new ArrayList<>();
        searchQuery.add(Query.Companion.equal("payer_member_app_id",userid));
        searchQuery.add(Query.Companion.equal("group_id",groupId));

        List<Object> searchQuery2 = new ArrayList<>();
        searchQuery2.add(Query.Companion.equal("receiver_member_app_id",userid));
        searchQuery2.add(Query.Companion.equal("group_id",groupId));

        fetchSettlementHistory(activity,searchQuery,searchQuery2);
    }
    void fetchSettlementHistory(Activity activity, List<Object> searchQuery, List<Object> searchQuery2){
        paidAlreadyAmount =0;
        receivedAlreadyAmount =0;
        DatabaseUtils.fetchDocuments(activity,
                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(activity)),
                AppWriteHelper.getCollectionId(Constants.GroupsSettlementCollectionId),
                searchQuery,
                new DocumentListFetchListener(){


                    @Override
                    public void onFetchSuccessfully(List<Document> documents) {
                        settlementModels = DatabaseUtils.convertToModelList(documents, SettlementModel.class);
                        for (SettlementModel model:settlementModels){
                            paidAlreadyAmount =paidAlreadyAmount+ Float.parseFloat(model.getPayable_amount());
                        }
                        // need to pay - paid

                        DatabaseUtils.fetchDocuments(activity,
                                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(activity)),
                                AppWriteHelper.getCollectionId(Constants.GroupsSettlementCollectionId),
                                searchQuery2,
                                new DocumentListFetchListener(){


                                    @Override
                                    public void onFetchSuccessfully(List<Document> documents) {
                                        settlementModels = DatabaseUtils.convertToModelList(documents, SettlementModel.class);
                                        for (SettlementModel model:settlementModels){
                                            receivedAlreadyAmount =receivedAlreadyAmount+ Float.parseFloat(model.getPayable_amount());
                                        }

                                        Log.e("already paid","->"+paidAlreadyAmount);
                                        Log.e("already received","->"+receivedAlreadyAmount);



                                        // need to pay - paid
                                        boolean needtopay = false;
                                        float calculation = (willGetTemp-receivedAlreadyAmount) - (needToPayTemp-paidAlreadyAmount);
//                        Log.e("will GetTemp",""+willGetTemp);
//                        Log.e("need to py temp",""+needToPayTemp);
                        Log.e("calculation",""+calculation);
//                        Log.e("expense",""+expense);
                                        if(calculation>0){

                                            amount = calculation;
                                            needToPayTemp = 0;
                                            needtopay = false;
                                        }
                                       else if(calculation==0){
                                            amount = calculation;
                                            needToPayTemp = 0;
                                            needtopay = false;

                                        }
                                        else {
                                            amount = Math.abs(calculation);
                                            willGetTemp = 0;
                                            needtopay = true;
                                        }

                                        willGetOrNeedToPay.postValue(needtopay);
                                        balance.postValue(String.valueOf(amount));



                                    }

                                    @Override
                                    public void onFailed(Result.Failure failure) {

                                    }

                                    @Override
                                    public void onException(AppwriteException exception) {

                                    }
                                });
                    }

                    @Override
                    public void onFailed(Result.Failure failure) {

                    }

                    @Override
                    public void onException(AppwriteException exception) {

                    }
                });

    }




}
