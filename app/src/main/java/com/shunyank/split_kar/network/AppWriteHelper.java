package com.shunyank.split_kar.network;

import static com.shunyank.split_kar.network.Constants.getAppUrl;
import static com.shunyank.split_kar.network.Constants.getProjectId;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.network.callbacks.CompleteCallback;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Session;
import io.appwrite.models.SessionList;
import io.appwrite.services.Account;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class AppWriteHelper {
    private static Client client = null;
     ;
    public AppWriteHelper(Context context){


    }
    public static Client GetAppWriteClient(Context context)
    {
        if (client == null) {

            client = new Client(context)
                    .setEndpoint(getAppUrl()) // Your API Endpoint
                    .setProject(getProjectId())
                    .setSelfSigned(true);// Your project ID
        }
        return client;
    }

    public static void createSessionIfRevoked(Activity activity, Account account, CompleteCallback callback){

        try {
            account.getSessions(new Continuation<SessionList>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {

                    if(o instanceof Result.Failure){

                        Log.e("session error",o.toString());
                        createSession(activity,account,callback);
                    }else {

                        callback.onComplete();
                    }
                }
            });
        } catch (AppwriteException e) {
            e.printStackTrace();
        }


    }

    public static void createSession(Activity activity, Account account, CompleteCallback callback){
        try {
            account.createAnonymousSession(new Continuation<Session>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {


                    if (o instanceof Result.Failure) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Result.Failure failure = (Result.Failure) o;
                                Log.e("Session failure", failure.exception.getLocalizedMessage());
                                failure.exception.printStackTrace();
                                Toast.makeText(activity, activity.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        if(callback!=null) {
                            callback.onComplete();
                            Log.e("Session", new Gson().toJson(o));

                        }
                    }






                }
            });
        } catch (AppwriteException e) {
            e.printStackTrace();
        }
    }

    public static String getCollectionId(String collectionName){
        return collectionName+"_ct";
    }


}
