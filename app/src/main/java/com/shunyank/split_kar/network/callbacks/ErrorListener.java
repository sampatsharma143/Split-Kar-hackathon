package com.shunyank.split_kar.network.callbacks;

import kotlin.Result;

public interface ErrorListener {
    void onFailedError(Result.Failure failure);
    void onException(Exception e);
}
