package com.shunyank.split_kar.network.callbacks;

import java.util.List;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public interface DocumentListFetchListener {
    void onFetchSuccessfully(List<Document> documents);
    void onFailed(Result.Failure failure);
    void onException(AppwriteException exception);
}
