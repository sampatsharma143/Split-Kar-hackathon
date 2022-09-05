package com.shunyank.split_kar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.window.SplashScreen;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.shunyank.split_kar.activities.LoginActivity;
import com.shunyank.split_kar.activities.UserDetailsActivity;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.CompleteCallback;
import com.shunyank.split_kar.network.callbacks.storage.FilesFetchListener;
import com.shunyank.split_kar.network.utils.StorageUtils;
import com.shunyank.split_kar.utils.SharedPref;

import java.util.List;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.File;
import io.appwrite.services.Account;
import io.appwrite.services.Storage;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public class SplashActivity extends AppCompatActivity {
    CompleteCallback callback;
    boolean notOpened = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LottieAnimationView view = findViewById(R.id.lottieAnimationView);
        view.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            callback.onComplete();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AppWriteHelper.createSessionIfRevoked(this,
                new Account(AppWriteHelper.GetAppWriteClient(this)),
                new CompleteCallback() {
                    @Override
                    public void onComplete() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new CountDownTimer(3000,1000)
                                {
                                    @Override
                                    public void onFinish() {
                                        SharedPref sharedPref = new SharedPref(getBaseContext());
                                        String user = sharedPref.getUser();

                                        if(!notOpened)
                                            {
                                                if(user.isEmpty()){
                                                // start the login activity
                                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                                                 }else {

                                                startActivity(new Intent(SplashActivity.this, MainActivity.class));

                                                 }
                                                notOpened = true;
                                        }
                                        finish();
                                    }
                                    @Override
                                    public void onTick(long l) {


                                    }
                                }.start();
                            }
                        });
                    }
                }
        );

    }


}