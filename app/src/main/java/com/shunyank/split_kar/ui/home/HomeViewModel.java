package com.shunyank.split_kar.ui.home;

import static com.shunyank.split_kar.utils.Helper.getValueInFloat;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.shunyank.split_kar.models.GroupModel;
import com.shunyank.split_kar.models.SettlementModel;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentListFetchListener;
import com.shunyank.split_kar.network.callbacks.ErrorListener;
import com.shunyank.split_kar.network.utils.DatabaseUtils;
import com.shunyank.split_kar.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.services.Databases;
import kotlin.Result;

public class HomeViewModel extends ViewModel {

    public final MutableLiveData<ArrayList<GroupModel>> groupList;
    public MutableLiveData<ArrayList<GroupModel>> friendsGroupList;
    public MutableLiveData<String> youWillGet;
    public MutableLiveData<String> youNeedToPay;
    float youWillGetTemp =0;

    public float amount;
    public float paidAlreadyAmount;
    public float receivedAlreadyAmount;

    float needToPayTemp =0;
    float willGetTemp =0;
    float expense =0;
    private ArrayList<SettlementModel> settlementModels;

    public HomeViewModel() {
      groupList = new MutableLiveData<>();
      friendsGroupList = new MutableLiveData<>();
        youWillGet = new MutableLiveData<>();
        youNeedToPay = new MutableLiveData<>();
    }


    public void fetchMyGroups(Activity activity, Databases databases, List<Object> searchQuery, ErrorListener errorListener){


       DatabaseUtils.fetchDocuments(activity, databases, AppWriteHelper.getCollectionId(Constants.GroupCollectionId), searchQuery, new DocumentListFetchListener() {
           @Override
           public void onFetchSuccessfully(List<Document> documents) {
               Log.e("groups",new Gson().toJson(documents));
               groupList.postValue(DatabaseUtils.convertToModelList(documents,GroupModel.class));




           }

           @Override
           public void onFailed(Result.Failure failure) {

               errorListener.onFailedError(failure);

           }

           @Override
           public void onException(AppwriteException exception) {
                    errorListener.onException(exception);
           }
       });

   }
    public void listMyAllGroups(Activity activity, Databases databases, List<Object> searchQuery, ErrorListener errorListener){
         amount=0;
         paidAlreadyAmount=0;
         receivedAlreadyAmount=0;

         needToPayTemp =0;
         willGetTemp =0;
         expense =0;
        settlementModels = new ArrayList<>();
        DatabaseUtils.fetchDocuments(activity, databases, AppWriteHelper.getCollectionId(Constants.GroupMembersCollectionId), searchQuery, new DocumentListFetchListener() {
            @Override
            public void onFetchSuccessfully(List<Document> documents) {
                List<String> values = new ArrayList<>();
                for (Document document:documents) {

                    values.add(document.getData().get("group_id").toString());


                }

                fetchMyBalance(activity,values,SharedPref.getUserId(activity));
                Log.e("listMyAllGroups ids ",new Gson().toJson(values));
//                List<Object> searchQuery = new ArrayList<>();
//                searchQuery.add(Query.Companion.equal("$id",values ));
//                fetchFriendsGroups(activity, databases, searchQuery, new ErrorListener() {
//                    @Override
//                    public void onFailedError(Result.Failure failure) {
//                        failure.exception.printStackTrace();
//                    }
//
//                    @Override
//                    public void onException(Exception e) {
//                        e.printStackTrace();
//                    }
//                });




            }

            @Override
            public void onFailed(Result.Failure failure) {

                errorListener.onFailedError(failure);

            }

            @Override
            public void onException(AppwriteException exception) {
                errorListener.onException(exception);
            }
        });

    }

   public void listFriendsGroups(Activity activity, Databases databases, List<Object> searchQuery, ErrorListener errorListener){
       DatabaseUtils.fetchDocuments(activity, databases, AppWriteHelper.getCollectionId(Constants.GroupMembersCollectionId), searchQuery, new DocumentListFetchListener() {
           @Override
           public void onFetchSuccessfully(List<Document> documents) {
                List<String> values = new ArrayList<>();
               for (Document document:documents) {

                   values.add(document.getData().get("group_id").toString());


               }
               Log.e("group ids ",new Gson().toJson(values));
                   List<Object> searchQuery = new ArrayList<>();
                   searchQuery.add(Query.Companion.equal("$id",values ));
                   fetchFriendsGroups(activity, databases, searchQuery, new ErrorListener() {
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

           @Override
           public void onFailed(Result.Failure failure) {

               errorListener.onFailedError(failure);

           }

           @Override
           public void onException(AppwriteException exception) {
               errorListener.onException(exception);
           }
       });

   }

    public void fetchFriendsGroups(Activity activity, Databases databases, List<Object> searchQuery, ErrorListener errorListener){

        DatabaseUtils.fetchDocuments(activity, databases, AppWriteHelper.getCollectionId(Constants.GroupCollectionId), searchQuery, new DocumentListFetchListener() {
            @Override
            public void onFetchSuccessfully(List<Document> documents) {
//                Log.e("friendsGroupList",new Gson().toJson(documents));
                friendsGroupList.postValue(DatabaseUtils.convertToModelList(documents,GroupModel.class));
            }

            @Override
            public void onFailed(Result.Failure failure) {

                errorListener.onFailedError(failure);

            }

            @Override
            public void onException(AppwriteException exception) {
                errorListener.onException(exception);
            }
        });

    }


    void fetchMyBalance(Activity activity,List<String> values, String member_app_id){
        needToPayTemp = 0;
        willGetTemp =0;
        List<Object> searchQuery = new ArrayList<>();

        searchQuery.add(Query.Companion.equal("group_id",values));
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
//                            Log.e("will GetTemp",""+willGetTemp);
//                            Log.e("need to py temp",""+needToPayTemp);
                        }

                        fetchMyGroupSettlement(activity,values, SharedPref.getUserId(activity));





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


    void fetchMyGroupSettlement(Activity activity,List<String > groupId,String userid){


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
        settlementModels = new ArrayList<>();
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
//                                        float calculation = getValueInFloat( (willGetTemp-receivedAlreadyAmount) )-getValueInFloat(  (needToPayTemp-paidAlreadyAmount));

//                                        Log.e("calculation",""+calculation);
//                        Log.e("expense",""+expense);


                                        willGetTemp = getValueInFloat(  willGetTemp-receivedAlreadyAmount);
                                        needToPayTemp = getValueInFloat(  needToPayTemp-paidAlreadyAmount);
                                        Log.e("will GetTemp",""+willGetTemp);
                                        Log.e("need to py temp",""+needToPayTemp);
                                        float calculation = willGetTemp -needToPayTemp;


                                        if(calculation>0.3){

                                            willGetTemp = calculation;
                                            needToPayTemp = 0;
                                            needtopay = false;
                                        }
                                        else if(calculation==0){
                                            willGetTemp = calculation;
                                            needToPayTemp = 0;
                                            needtopay = false;

                                        }
                                        else {
                                            needToPayTemp = Math.abs(calculation);
                                            willGetTemp = 0;
                                            needtopay = true;
                                        }
                                        youWillGet.postValue(String.valueOf(willGetTemp));
                                        youNeedToPay.postValue(String.valueOf(needToPayTemp));

//                                        willGetOrNeedToPay.postValue(needtopay);
//                                        balance.postValue(String.valueOf(amount));



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