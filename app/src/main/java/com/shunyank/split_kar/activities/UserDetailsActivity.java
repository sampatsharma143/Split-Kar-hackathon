package com.shunyank.split_kar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.shunyank.split_kar.MainActivity;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.SplashActivity;
import com.shunyank.split_kar.adapters.AdapterClickListener;
import com.shunyank.split_kar.adapters.AvatarAdapter;
import com.shunyank.split_kar.databinding.ActivityUserDetailsBinding;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentUpdateListener;
import com.shunyank.split_kar.network.callbacks.storage.FilesFetchListener;
import com.shunyank.split_kar.network.model.UserModel;
import com.shunyank.split_kar.network.utils.DatabaseUtils;
import com.shunyank.split_kar.network.utils.StorageUtils;
import com.shunyank.split_kar.utils.SharedPref;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.models.File;
import io.appwrite.models.Session;
import io.appwrite.models.SessionList;
import io.appwrite.models.User;
import io.appwrite.services.Account;
import io.appwrite.services.Databases;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class UserDetailsActivity extends AppCompatActivity {

    private ActivityUserDetailsBinding binding;
    private String phone;
    private String uuid;
    private String country;
    private String location;
    String avatar_url = "";
    String documentId;
    AvatarAdapter avatarAdapter;
    ArrayList<UserAvatarModel> avatarList;
    public class UserAvatarModel{
        String avatarUrl;
        boolean isSelected;

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public UserAvatarModel(String avatarUrl, boolean isSelected) {
            this.avatarUrl = avatarUrl;
            this.isSelected = isSelected;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public boolean isSelected() {
            return isSelected;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        documentId = intent.getStringExtra("document_id");
        UUID temp_uuid=UUID.randomUUID(); //Generates random UUID.
        uuid = temp_uuid.toString();
        country = "IN";
        //Country
        // Location
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        binding.avatarRecyclerview.setLayoutManager(layoutManager);

        avatarAdapter = new AvatarAdapter(this) ;
        binding.avatarRecyclerview.setAdapter(avatarAdapter);
        avatarAdapter.setClickListener(new AdapterClickListener() {
            @Override
            public void onItemClick(Object item) {
                int pos = (int) item;
                for(int i=0;i<avatarList.size();i++){
                    avatarList.get(i).setSelected(false);
                }
                avatar_url = avatarList.get(pos).getAvatarUrl();
                avatarList.get(pos).setSelected(true);
                avatarAdapter.setAvatarFiles(avatarList);
            }
        });
        fetchAvatar();

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.edtName.getText().toString().isEmpty()){
                    Toast.makeText(UserDetailsActivity.this, getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(avatar_url.isEmpty()){
                    Toast.makeText(UserDetailsActivity.this, "Please select avatar", Toast.LENGTH_SHORT).show();
                    return;
                }

                    updateUser(AppWriteHelper.GetAppWriteClient(UserDetailsActivity.this));

            }
        });
    }

    private void fetchAvatar(){
        //fetch avatar and show them
        avatarList = new ArrayList<>();
        StorageUtils.getFilesList(UserDetailsActivity.this, StorageUtils.getStorage(AppWriteHelper.GetAppWriteClient(UserDetailsActivity.this)),
                Constants.avatarBucketId, new FilesFetchListener() {
                    @Override
                    public void onFetched(List<File> files) {
                        Log.e("files",new Gson().toJson(files));
                        for(File file:files){
                            String imageUrl = "https://superapp.megamind.app/v1/storage/buckets/"+file.getBucketId()+"/files/"+file.getId()+"/view?project="+Constants.getProjectId();

                            UserAvatarModel userAvatarModel = new UserAvatarModel(imageUrl,false);
                            avatarList.add(userAvatarModel);
                            Log.e("image url",imageUrl);
                        }
                        avatarAdapter.setAvatarFiles(avatarList);


                    }

                    @Override
                    public void onFailed(Result.Failure failure) {
                        failure.exception.printStackTrace();;
                    }

                    @Override
                    public void onException(Exception exception) {

                    }
                });

    }

    void updateUser(Client client){

        Databases database = DatabaseUtils.getDatabase(client);


        UserModel userModel = new UserModel(
                binding.edtName.getText().toString(),
                phone,
                "INR",
                "",
                avatar_url,
                true,
                false
        );
//        UserModel userModel = new UserModel(binding.edtName.getText().toString().trim(),phone,country,location,"https://superapp.megamind.app/v1/storage/buckets/629870747bb6787e359e/files/6299041ca11a2c74fe6c/view?project=62978e0077e9bc29e92c","1","0","0","0",true);

        HashMap<Object,Object> data = DatabaseUtils.convertToHashmap(userModel);
            DatabaseUtils.updateDocument(UserDetailsActivity.this,
                    database,
                    AppWriteHelper.getCollectionId(Constants.UserCollectionId),
                    documentId,
                    data,
                    new DocumentUpdateListener() {
                        @Override
                        public void onUpdatedSuccessfully(Document document) {
                            SharedPref sharedPref = new SharedPref(UserDetailsActivity.this);
                               UserModel userModel1 = DatabaseUtils.convertToModel(document,UserModel.class);
                                sharedPref.saveUser(userModel1);
                                Toast.makeText(UserDetailsActivity.this,getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show();

                            Intent intent=   new Intent(UserDetailsActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailed(Result.Failure failure) {
                            Toast.makeText(UserDetailsActivity.this,getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            failure.exception.printStackTrace();
                        }

                        @Override
                        public void onException(AppwriteException exception) {
                            Toast.makeText(UserDetailsActivity.this,getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            exception.printStackTrace();
                        }
                    }
            );


            // change it to databaseUtils
//            database.createDocument(AppWriteHelper.getCollectionId(Constants.UserCollectionId), "unique()",
//                    new Gson().toJson(userModel).toString(), new Continuation<Document>() {
//                @NonNull
//                @Override
//                public CoroutineContext getContext() {
//                    return EmptyCoroutineContext.INSTANCE;
//                }
//
//                @Override
//                public void resumeWith(@NonNull Object o) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.e("CreateUserResponse", new Gson().toJson(o).toString());
//
//                            if (o instanceof Result.Failure) {
//                                Result.Failure failure = (Result.Failure) o;
//                                Log.e("failure", failure.exception.getLocalizedMessage().toString());
//                                Toast.makeText(UserDetailsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//
//                            } else {
//
//                            }
//                        }
//                    });
//                }
//            });

    }
}


