package com.shunyank.split_kar.ui.home;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.shunyank.split_kar.models.GroupModel;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentListFetchListener;
import com.shunyank.split_kar.network.callbacks.ErrorListener;
import com.shunyank.split_kar.network.utils.DatabaseUtils;

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
    public HomeViewModel() {
      groupList = new MutableLiveData<>();
      friendsGroupList = new MutableLiveData<>();
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
}