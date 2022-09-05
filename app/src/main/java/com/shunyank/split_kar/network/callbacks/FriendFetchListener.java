package com.shunyank.split_kar.network.callbacks;

import com.shunyank.split_kar.models.PhoneContact;

import java.util.List;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public interface FriendFetchListener {


    void onFetchSuccessfully(List<Document> documents, PhoneContact phoneContact);
    void onFailed(Result.Failure failure);
    void onException(AppwriteException exception);
}
