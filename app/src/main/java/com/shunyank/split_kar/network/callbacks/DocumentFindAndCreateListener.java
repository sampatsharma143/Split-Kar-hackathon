package com.shunyank.split_kar.network.callbacks;

import java.util.List;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public interface DocumentFindAndCreateListener {
    void onFindSuccessfully(List<Document> documents);
    void onCreatedSuccessfully(Document document);
    void onFailed(Result.Failure failure);
    void onException(AppwriteException exception);
}
