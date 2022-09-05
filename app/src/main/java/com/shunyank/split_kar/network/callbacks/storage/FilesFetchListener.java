package com.shunyank.split_kar.network.callbacks.storage;

import java.util.List;

import io.appwrite.models.File;
import kotlin.Result;
public interface FilesFetchListener {
    void onFetched(List<File> files);
    void onFailed(Result.Failure failure);
    void onException(Exception exception);
}
