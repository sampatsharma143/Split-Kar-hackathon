package com.shunyank.split_kar.network.utils;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.shunyank.split_kar.network.callbacks.storage.FilesFetchListener;

import java.util.List;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.models.File;
import io.appwrite.models.FileList;
import io.appwrite.services.Databases;
import io.appwrite.services.Storage;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class StorageUtils {


    private static Storage storage;

    public static Storage getStorage(Client client){

        if(storage==null){
            storage =  new Storage(client);
        }
        return storage;
    }

    public static void getFilesList(Activity activity, Storage storage, String bucketId, FilesFetchListener filesFetchListener){

        try {
            storage.listFiles(bucketId, new Continuation<FileList>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(o instanceof Result.Failure){

                                filesFetchListener.onFailed((Result.Failure) o);
                            }else {

                                filesFetchListener.onFetched(((FileList)o).getFiles());

                            }
                        }
                    });

                }
            });
        } catch (AppwriteException e) {
            e.printStackTrace();
            filesFetchListener.onException(e);
        }


    }

}
