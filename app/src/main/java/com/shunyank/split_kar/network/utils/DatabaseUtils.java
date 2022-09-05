package com.shunyank.split_kar.network.utils;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.shunyank.split_kar.BuildConfig;
import com.shunyank.split_kar.models.PhoneContact;
import com.shunyank.split_kar.network.callbacks.DocumentCreateListener;
import com.shunyank.split_kar.network.callbacks.DocumentDeleteListener;
import com.shunyank.split_kar.network.callbacks.DocumentFetchListener;
import com.shunyank.split_kar.network.callbacks.DocumentFindAndCreateListener;
import com.shunyank.split_kar.network.callbacks.DocumentListFetchListener;
import com.shunyank.split_kar.network.callbacks.DocumentUpdateListener;
import com.shunyank.split_kar.network.callbacks.FriendFetchListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.models.DocumentList;
import io.appwrite.services.Databases;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class DatabaseUtils {

    private static Databases databases;

    public static Databases getDatabase(Client client){
        
        if(databases==null){
            databases =  new Databases(client,"default");
        }
        return databases;
    }

    public static void deleteDocument( Databases database, String collectionId, String documentId, DocumentDeleteListener documentDeleteListener){

        try {
            database.deleteDocument(collectionId, documentId, new Continuation<Object>() {
                @Override
                public void resumeWith(@NonNull Object o) {

                            if(o instanceof Result.Failure){
                                documentDeleteListener.onFailed((Result.Failure) o);
                            }else {
                                documentDeleteListener.onDeletedSuccessfully();
                            }


                }

                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }
            });
        } catch (AppwriteException e) {
            e.printStackTrace();
        }

    }
    public static void updateDocument(Databases database, String collectionId,
                                      String documentId,
                                      HashMap<Object,Object> data,
                                      DocumentUpdateListener documentUpdateListener){

        try {
            database.updateDocument(collectionId, documentId,data ,new Continuation<Document>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }
                @Override
                public void resumeWith(@NonNull Object o) {

                            if(o instanceof Result.Failure){
                                documentUpdateListener.onFailed((Result.Failure) o);
                            }else {

                                documentUpdateListener.onUpdatedSuccessfully((Document) o);

                            }
                        }
            });

        }catch (AppwriteException exception){
            documentUpdateListener.onException(exception);
        }

    }



    public static void fetchDocument( Databases database, String collectionId, String documentId, DocumentFetchListener documentFetchListener){

        try {
            database.getDocument(collectionId, documentId, new Continuation<Document>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {


                            if(o instanceof Result.Failure){
                                Log.e("Appwrite Failure","true");

                                documentFetchListener.onFailed((Result.Failure) o);
                            }else {

                                documentFetchListener.onFetchSuccessfully((Document) o);

                            }




                }
            });

        }catch (AppwriteException exception){
            if(BuildConfig.DEBUG){
                Log.e("Appwrite Exception","true");
            }
            documentFetchListener.onException(exception);
        }

    }

    public static void fetchDocument(Activity activity, Databases database, String collectionId, String documentId, DocumentFetchListener documentFetchListener){

        try {
            database.getDocument(collectionId, documentId, new Continuation<Document>() {
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
                                Log.e("Appwrite Failure","true");

                                documentFetchListener.onFailed((Result.Failure) o);
                            }else {

                                documentFetchListener.onFetchSuccessfully((Document) o);

                            }
                        }
                    });


                }
            });

        }catch (AppwriteException exception){
            if(BuildConfig.DEBUG){
                Log.e("Appwrite Exception","true");
            }
            documentFetchListener.onException(exception);
        }

    }


    public static void updateDocument(Activity activity, Databases database, String collectionId,
                                      String documentId,
                                      HashMap<Object,Object> data,
                                      DocumentUpdateListener documentUpdateListener){

        try {
            database.updateDocument(collectionId, documentId,data ,new Continuation<Document>() {
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
                                documentUpdateListener.onFailed((Result.Failure) o);
                            }else {

                                documentUpdateListener.onUpdatedSuccessfully((Document) o);

                            }
                        }
                    });


                }
            });

        }catch (AppwriteException exception){
            documentUpdateListener.onException(exception);
        }

    }

    public static void checkContactIsFriend(Activity activity, Databases databases, String collectionId, List<Object> searchQueries,
                                            FriendFetchListener friendFetchListener, PhoneContact contact){
        try {
            databases.listDocuments(collectionId, searchQueries, new Continuation<DocumentList>() {
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
                            if (o instanceof Result.Failure) {
                                Log.e("Appwrite Failure", "true");

                                friendFetchListener.onFailed((Result.Failure) o);
                            } else {


                                DocumentList documentList = (DocumentList) o;


                                friendFetchListener.onFetchSuccessfully(documentList.getDocuments(),contact);

                            }
                        }
                    });


                }
            });

        }catch (AppwriteException exception){
            if(BuildConfig.DEBUG){
                Log.e("AppWrite Exception","true");
            }
            friendFetchListener.onException(exception);
        }
    }


    public static void fetchDocuments(Activity activity, Databases databases, String collectionId, List<Object> searchQueries,
                                      DocumentListFetchListener documentListFetchListener){
        try {
            databases.listDocuments(collectionId, searchQueries, new Continuation<DocumentList>() {
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
                            if (o instanceof Result.Failure) {
                                Log.e("Appwrite Failure", "true");

                                documentListFetchListener.onFailed((Result.Failure) o);
                            } else {


                                DocumentList documentList = (DocumentList) o;


                                documentListFetchListener.onFetchSuccessfully(documentList.getDocuments());

                            }
                        }
                    });


                }
            });

        }catch (AppwriteException exception){
            if(BuildConfig.DEBUG){
                Log.e("AppWrite Exception","true");
            }
            documentListFetchListener.onException(exception);
        }
    }

    public static void createDocument(Activity activity, Databases database, String collectionId, HashMap<Object,Object> data, DocumentCreateListener documentCreateListener) {

        try {
            database.createDocument(collectionId, "unique()", data, new Continuation<Document>() {
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
                            if (o instanceof Result.Failure) {

                                documentCreateListener.onFailed((Result.Failure) o);

                            } else {

                                documentCreateListener.onCreatedSuccessfully((Document) o);

                            }
                        }
                    });


                }
            });


        } catch (AppwriteException e) {
            e.printStackTrace();
            documentCreateListener.onException(e);
        }
    }
    public static void createDocumentIfNotFound(Activity activity, Databases database, List<Object> searchQuery, String collectionId,
                                                HashMap<Object,Object> data,
                                                DocumentFindAndCreateListener documentFindAndCreateListener) {



        fetchDocuments(activity, database, collectionId, searchQuery, new DocumentListFetchListener() {
            @Override
            public void onFetchSuccessfully(List<Document> document) {
                Log.e("fetchedData",new Gson().toJson(document));
                if(document.size()>0){
                    // 0 for only first item
                    documentFindAndCreateListener.onFindSuccessfully(document);
                }else {

                    try {
                        database.createDocument(collectionId, "unique()", data, new Continuation<Document>() {
                            @NonNull
                            @Override
                            public CoroutineContext getContext() {
                                return EmptyCoroutineContext.INSTANCE;
                            }

                            @Override
                            public void resumeWith(@NonNull Object o) {

                                if(o instanceof Result.Failure){

                                    documentFindAndCreateListener.onFailed((Result.Failure) o);

                                }else {

                                    documentFindAndCreateListener.onCreatedSuccessfully((Document) o);

                                }

                            }
                        });
                    } catch (AppwriteException e) {
                        e.printStackTrace();
                        documentFindAndCreateListener.onException(e);
                    }

                }
            }
            @Override
            public void onFailed(Result.Failure failure) {
                documentFindAndCreateListener.onFailed(failure);

            }

            @Override
            public void onException(AppwriteException exception) {
                documentFindAndCreateListener.onException(exception);

            }
        });

    }

    public static <T> ArrayList<T> convertToModelList(List<Document> documents, Class<T> classOfT){
        Gson gson = new Gson();
        ArrayList<T> data = new ArrayList<>();
        for(Document document:documents){
            JsonElement jsonElement = gson.toJsonTree(document.getData());
            data.add( new Gson().fromJson(jsonElement,classOfT));
        }
        return data;
    }
    public static <T> T convertToModel(Document document, Class<T> classOfT){
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(document.getData());
        return new Gson().fromJson(jsonElement,classOfT);
    }

    public static HashMap<Object,Object> convertToHashmap(Object object){
        HashMap<Object, Object> data = new Gson().fromJson(
                new Gson().toJson(object), new TypeToken<HashMap<String, Object>>() {}.getType()
        );
        return data;
    }


}
