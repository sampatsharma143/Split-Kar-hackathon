package com.shunyank.split_kar.activities;

import static com.shunyank.split_kar.network.AppWriteHelper.getCollectionId;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.shunyank.split_kar.MainActivity;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.databinding.ActivityLoginBinding;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentCreateListener;
import com.shunyank.split_kar.network.callbacks.DocumentListFetchListener;
import com.shunyank.split_kar.network.model.UserModel;
import com.shunyank.split_kar.network.utils.DatabaseUtils;
import com.shunyank.split_kar.utils.Helper;
import com.shunyank.split_kar.utils.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.services.Databases;
import kotlin.Result;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    String phoneNumber = "";
    private String codeSent;
    PhoneAuthProvider.ForceResendingToken tokenone;
    private boolean canResend=false;
    private boolean canVerifyOtp=false;
    Databases databases;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databases = DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(this));
        binding.otpEdt.onEditorAction(EditorInfo.IME_ACTION_DONE);
        binding.verifyOtpBtn.setBackground(getResources().getDrawable(R.drawable.button_bg_grey));

        binding.otpEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.otpEdt.setLetterSpacing(0f);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0){

//                    Log.e("CountOtp",s.length(), new String[]{" otp :", s.toString()});
                    if(s.length()==6){
                        binding.verifyOtpBtn.setBackground(getResources().getDrawable(R.drawable.button_bg));
                        canVerifyOtp = true;
                    }else {
                        binding.verifyOtpBtn.setBackground(getResources().getDrawable(R.drawable.button_bg_grey));
                        canVerifyOtp = false;

                    }
                    binding.otpEdt.setLetterSpacing(0.6f);
                }
                else if(s.length()==0){
                    canVerifyOtp = false;
                    binding.otpEdt.setLetterSpacing(0f);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.requestOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.otpEdt.onEditorAction(EditorInfo.IME_ACTION_DONE);

                sendOTP(binding.phoneEdt);

            }
        });
        binding.changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLayout(false);

            }
        });
        binding.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canResend) {
                    resendVerificationCode("+"+phoneNumber,tokenone);
                    canResend = false;
                }
            }
        });

        binding.verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.otpEdt.onEditorAction(EditorInfo.IME_ACTION_DONE);

                if(codeSent.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Try Again Later", Toast.LENGTH_SHORT).show();

                }
                else if (canVerifyOtp) {
                    startVerifyLoading();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, binding.otpEdt.getText().toString());
                    signInWithPhoneAuthCredential(credential);
            }
                else {
                    Toast.makeText(LoginActivity.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void startVerifyLoading() {
        binding.verifyOtpBtn.setVisibility(View.GONE);
        binding.verifyOtpLoading.setVisibility(View.VISIBLE);
    }

    private void stopVerifyLoading() {
        binding.verifyOtpBtn.setVisibility(View.VISIBLE);
        binding.verifyOtpLoading.setVisibility(View.GONE);
    }

    private void switchLayout(boolean showOTP){
        if(showOTP){
            binding.otpEdt.setText("");
            Transition transition = new Slide(Gravity.LEFT);
            transition.setDuration(200);
            transition.addTarget(binding.loginLayout);

            ViewGroup viewGroup = binding.loginLayout;

            TransitionManager.beginDelayedTransition(viewGroup, transition);

            binding.loginLayout.setVisibility(View.GONE);
            new CountDownTimer(300,1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.otpLayout.setVisibility(View.VISIBLE);

                        }
                    });

                }
            }.start();
        }else {
            Transition transition = new Slide(Gravity.RIGHT);
            transition.setDuration(200);
            transition.addTarget(binding.otpLayout);

            ViewGroup viewGroup = binding.otpLayout;
            TransitionManager.beginDelayedTransition(viewGroup, transition);
            binding.otpLayout.setVisibility(View.GONE);
            binding.loginLayout.setVisibility(View.VISIBLE);

        }
    }

    String sendOTP( EditText phoneForLogin){
        String phone = phoneForLogin.getText().toString();
        HashMap<String,Boolean> phNumber = Helper.validatePhoneNumber(this,phoneForLogin.getText().toString());
        if(phNumber!=null) {

            boolean only10 = phNumber.get("only10");
            boolean withPlus91 = phNumber.get("withPlus91");
            boolean with91 = phNumber.get("with91");

            if (withPlus91) {
                phone = phone.replace("+91", "");

            } else if (with91) {
                phone = phone.replace("91", "");

            } else if (!only10 && !withPlus91 && !with91) {
                Toast.makeText(this, R.string.please_enter_correct_number, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "only indian numbers are allowed", Toast.LENGTH_SHORT).show();
                return phone;
            }
            phoneNumber = "91"+phone;
            phone =  "+91"+phone;
            sendOTPtoPhone(phone);

        }
        return phone;
    }


    public void sendOTPtoPhone(String phoneNumber){
        Log.e("Sending OTP TO",phoneNumber);
        startLoading();
        mAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber( phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private Thread t;
    private CountDownTimer countdown;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            Log.e("codeSend",s);
            tokenone=forceResendingToken;
            stopLoading();
            switchLayout(true);
            binding.sendTo.setText(String.format( getString( R.string.code_sent_to),"+"+phoneNumber));
            binding.resend.setText("");
            binding.resend.setVisibility(View.VISIBLE);
            binding.resend.setTextColor(getResources().getColor(R.color.grey_50));
            binding.resend.setClickable(false);
            if(countdown!=null){
                countdown.cancel();
                countdown = null;
            }
            countdown  = new CountDownTimer(30000,1000){


                @Override
                public void onTick(long millisUntilFinished) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                binding.resend.setText("Resend in "+millisUntilFinished/1000);
                                if ((millisUntilFinished/1000)==1){
                                    canResend = true;
                                    binding.resend.setText("Resend");
                                    binding.resend.setTextColor(getResources().getColor(R.color.black));
                                    binding.resend.setClickable(true);
                                }
                            });
                        }
                    });
                }

                @Override
                public void onFinish() {

                }
            }.start();
//             t = new Thread() {
//                public void run() {
//                    for (int i = 30; i > 0; i--) {
//                        try {
//                            final int a = i;
//                            int finalI = i;
//
//                            sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//            t.start();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getBaseContext(), "OTP Verification Completed!", Toast.LENGTH_SHORT).show();
//            Log.e("OTP: ",phoneAuthCredential.getSmsCode());
            String code =phoneAuthCredential.getSmsCode();

            if(!code.isEmpty())
            {
                binding.otpEdt.setText(code);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
                signInWithPhoneAuthCredential(credential);
            }else {
                stopVerifyLoading();
            }



        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getBaseContext(), "Verification Failed!", Toast.LENGTH_SHORT).show();
            stopLoading();
        }
    };

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
        // TODO: 8/28/2022 uncomment
        binding.resend.setVisibility(View.GONE);
        binding.sendTo.setText(String.format( getString( R.string.sending_otp),phoneNumber));
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // TODO: 8/3/2021 action after success0.
                            checkAccountExistence();
//                            if(actionLogin){
//                                logIn();
//                            }else {
//                                signUp();
//                            }

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Incorrect Verification code", Toast.LENGTH_SHORT).show();
                                // TODO: 8/3/2021 action after fail
//                                am.textView.setText("RETRY Please !");
                                stopVerifyLoading();
                            }
                        }
                    }
                });
    }

    private void checkAccountExistence() {
        List<Object> searchQueries = new ArrayList<Object>();
        searchQueries.add(Query.Companion.equal("phone_number",phoneNumber));
        DatabaseUtils.fetchDocuments(LoginActivity.this, databases,
                getCollectionId(Constants.UserCollectionId),
                searchQueries,
                new DocumentListFetchListener() {
                    @Override
                    public void onFetchSuccessfully(List<Document> documents) {
                        if(documents.size()>0){
                            // get first item - Logically we should only get 1 item in this list
                            Document document = documents.get(0);
                            UserModel userModel =  DatabaseUtils.convertToModel(document, UserModel.class);
                            // if user profile is completed or not
                            Log.e("usermodel",new Gson().toJson(userModel));
                            if(userModel.isIs_profile_completed()){
                                // start mainActivity by saving user data
                                SharedPref sharedPref = new SharedPref(LoginActivity.this)  ;
                                sharedPref.clearUser();
                                sharedPref.saveUser(userModel);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }else{
                                Intent intent = new Intent(LoginActivity.this,UserDetailsActivity.class);
                                intent.putExtra("phone",phoneNumber);
                                intent.putExtra("document_id",userModel.getId());
                                switchLayout(false);
                                stopLoading();
                                stopVerifyLoading();
                                startActivity(intent);

                            }


                        }else {
                            // add user to the database and set profile completed false
                            HashMap<Object,Object> data = new HashMap<>();
                            data.put("phone_number",phoneNumber);
                            data.put("is_profile_completed",false);
                            // start profile setup activity
                            DatabaseUtils.createDocument(LoginActivity.this, databases, AppWriteHelper.getCollectionId(Constants.UserCollectionId), data, new DocumentCreateListener() {
                                @Override
                                public void onCreatedSuccessfully(Document document) {
                                    stopVerifyLoading();
                                    Intent intent = new Intent(LoginActivity.this,UserDetailsActivity.class);
                                    intent.putExtra("phone",phoneNumber);
                                    intent.putExtra("document_id",document.getId());
                                    switchLayout(false);
                                    stopLoading();
                                    stopVerifyLoading();

                                    startActivity(intent);
                                }

                                @Override
                                public void onFailed(Result.Failure failure) {
                                    stopVerifyLoading();
                                    Toast.makeText(LoginActivity.this, "Something Went Wrong Try Again", Toast.LENGTH_SHORT).show();
                                    switchLayout(false);
                                }

                                @Override
                                public void onException(AppwriteException exception) {
                                    stopVerifyLoading();
                                    Toast.makeText(LoginActivity.this, "Something Went Wrong Try Again", Toast.LENGTH_SHORT).show();
                                    switchLayout(false);
                                }
                            });


                        }
                    }

                    @Override
                    public void onFailed(Result.Failure failure) {
                        failure.exception.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Something Went Wrong Try Again", Toast.LENGTH_SHORT).show();
                        switchLayout(false);
                    }

                    @Override
                    public void onException(AppwriteException exception) {
                        exception.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Something Went Wrong Try Again", Toast.LENGTH_SHORT).show();
                        switchLayout(false);
                    }
                }
        );

    }

    private void startLoading(){
      binding.requestOtpBtn.setVisibility(View.GONE);
        binding.verifyOtpLoading.setVisibility(View.VISIBLE);
    }
    private void stopLoading(){
        binding.requestOtpBtn.setVisibility(View.VISIBLE);
        binding.verifyOtpLoading.setVisibility(View.GONE);
    }

}