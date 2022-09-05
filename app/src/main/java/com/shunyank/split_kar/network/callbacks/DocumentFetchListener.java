package com.shunyank.split_kar.network.callbacks;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public interface DocumentFetchListener {
    void onFetchSuccessfully(Document document);
    void onFailed(Result.Failure failure);
    void onException(AppwriteException exception);
}
