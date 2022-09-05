package com.shunyank.split_kar.network.callbacks;

import io.appwrite.exceptions.AppwriteException;
import kotlin.Result;

public interface DocumentDeleteListener {
    void onDeletedSuccessfully();
    void onFailed(Result.Failure failure);
    void onException(AppwriteException exception);
}
