package com.shunyank.split_kar.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shunyank.split_kar.R;
import com.shunyank.split_kar.adapters.AdapterClickListener;
import com.shunyank.split_kar.adapters.FriendAndContactAdapter;
import com.shunyank.split_kar.adapters.SelectedParticipantsAdapter;
import com.shunyank.split_kar.databinding.ActivityCreateGroupBinding;
import com.shunyank.split_kar.models.FriendAndContactModel;
import com.shunyank.split_kar.models.GroupMemberModel;
import com.shunyank.split_kar.models.PhoneContact;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.DocumentCreateListener;
import com.shunyank.split_kar.network.callbacks.DocumentListFetchListener;
import com.shunyank.split_kar.network.callbacks.DocumentUpdateListener;
import com.shunyank.split_kar.network.model.GroupMemberCollectionModel;
import com.shunyank.split_kar.network.model.UserModel;
import com.shunyank.split_kar.network.utils.DatabaseUtils;
import com.shunyank.split_kar.utils.Helper;
import com.shunyank.split_kar.utils.SharedPref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.services.Databases;
import kotlin.Result;

public class CreateGroupActivity extends AppCompatActivity {
    ActivityCreateGroupBinding binding;
    boolean havePermission = false;
    boolean isSyncedFromServer = false;
    private CGAViewHolder cgaViewHolder;
    Databases databases;
    private String userId;
    ArrayList<PhoneContact> oldContactList;
    ArrayList<PhoneContact> currentContactList = new ArrayList<>();
    ArrayList<PhoneContact> groupMembersList = new ArrayList<>();
    ArrayList<PhoneContact> searchedList;
    final int[] i2 = {1};
    ArrayList<FriendAndContactModel> friendsList = new ArrayList<>();
    ArrayList<FriendAndContactModel> inviteFriendsList = new ArrayList<>();

    private FriendAndContactAdapter friendAndContactAdapter;
    private SelectedParticipantsAdapter selectedParticipantsAdapter;
    String groupName;
    UserModel userModel;
    private int membersSize=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         cgaViewHolder = new ViewModelProvider(this).get(CGAViewHolder.class);
        userModel = new SharedPref(this).getUserModel();
        userId = userModel.getId();
        groupName = getIntent().getStringExtra("name");
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));


        isSyncedFromServer = new SharedPref(this).getUserModel().isSynced();
        databases = DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(this));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager horizontallyLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.suggestedFriends.setLayoutManager(layoutManager);
        ((SimpleItemAnimator)binding.groupMembers.getItemAnimator()).setSupportsChangeAnimations(false);
        ((SimpleItemAnimator)binding.suggestedFriends.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.groupMembers.setLayoutManager(horizontallyLayoutManager);


        //SuggestedFriend list adapter init
        friendAndContactAdapter = new FriendAndContactAdapter(this);
        binding.suggestedFriends.setAdapter(friendAndContactAdapter);

        // selected group members / participants adapter init
        selectedParticipantsAdapter = new SelectedParticipantsAdapter(this);
        binding.groupMembers.setAdapter(selectedParticipantsAdapter);


        // add MySelf

        addMySelfToMemberList();


        cgaViewHolder.checkAll.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.e("members checked",""+integer);
                if(integer==0){
                    hideCreatingProgressbar();
                    finish();
                }
            }
        });

        friendAndContactAdapter.setClickListener(new AdapterClickListener() {
            @Override
            public void onItemClick(Object item) {
                PhoneContact phoneContact = (PhoneContact) item;
//                binding.friendSearchEdt.onEditorAction(EditorInfo.IME_ACTION_DONE);

                groupMembersList.add(phoneContact);
                updateGroupMembersCount(groupMembersList.size());
                selectedParticipantsAdapter.submitList(groupMembersList);
                selectedParticipantsAdapter.notifyDataSetChanged();
                binding.friendSearchEdt.setText("");
                cgaViewHolder.getContacts(CreateGroupActivity.this, "");
            }
        });

        // on remove button clicked
        selectedParticipantsAdapter.setClickListener(new AdapterClickListener() {
            @Override
            public void onItemClick(Object item) {
                // Remove Item From the list and update the Recyclerview
                String number = Helper.getRightPhoneNumber ( CreateGroupActivity.this, ((PhoneContact)item).getNumber());

                if(number.contentEquals(userModel.getPhone_number())){

                    Toast.makeText(CreateGroupActivity.this, "You can remove yourself", Toast.LENGTH_SHORT).show();
                    return;
                }

                groupMembersList.remove(item);
                updateGroupMembersCount(groupMembersList.size());
                selectedParticipantsAdapter.submitList(groupMembersList);
                selectedParticipantsAdapter.notifyDataSetChanged();
            }
        });



        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });



        // search
        binding.friendSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchQuery = s.toString();

                cgaViewHolder.getContacts(CreateGroupActivity.this,searchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            binding.noPermissionLayout.setVisibility(View.VISIBLE);
            havePermission = false;
        }else {
            syncContacts();
            binding.noPermissionLayout.setVisibility(View.GONE);
            havePermission =true;
        }

        binding.syncContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(havePermission){
                    syncContacts();
                }else {
                    ActivityCompat.requestPermissions(CreateGroupActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            110);
                }
            }
        });


        cgaViewHolder.myContacts.observe(this, new Observer<ArrayList<PhoneContact>>() {
            @Override
            public void onChanged(ArrayList<PhoneContact> phoneContacts) {
                Log.e("phone",new Gson().toJson(phoneContacts));
                if(phoneContacts.size()==0){
                    binding.message.setVisibility(View.VISIBLE);
                }else {
                    binding.message.setVisibility(View.GONE);
                }
                currentContactList = phoneContacts;
                Collections.sort(currentContactList, new Comparator<PhoneContact>() {
                    @Override
                    public int compare(PhoneContact o1, PhoneContact o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                  friendAndContactAdapter.submitList(currentContactList);

            }
        });


        binding.createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create group here and show progress bar
                showCreatingProgressbar();
                HashMap<Object,Object> data = new HashMap<>();

                /**
                 * admin -> userid
                 * members-> phone_numbers
                 * on_app_users-> using function ( only json)
                 *
                 *
                 * */
                List<GroupMemberModel> membersList = new ArrayList<>();
                List<String> empty = new ArrayList<>();
                for (PhoneContact p :
                        groupMembersList) {
                    String phoneNumber = Helper.getRightPhoneNumber(CreateGroupActivity.this,p.getNumber());
                    membersList.add(new GroupMemberModel(p.getName(),phoneNumber));
                }

                if(membersList.size()==0){
                    Toast.makeText(CreateGroupActivity.this, "minimum one member required!", Toast.LENGTH_SHORT).show();
                    hideCreatingProgressbar();
                    return;
                }

                membersSize = membersList.size();


                data.put("group_name",groupName);
                data.put("admin",userId);
                data.put("members",new Gson().toJson(membersList));
                data.put("on_app_members",new Gson().toJson(empty));



                DatabaseUtils.createDocument(CreateGroupActivity.this, databases,
                        AppWriteHelper.getCollectionId(Constants.GroupCollectionId),
                        data,
                        new DocumentCreateListener() {
                            @Override
                            public void onCreatedSuccessfully(Document document) {

                                for (GroupMemberModel memberModel:membersList){

                                    boolean isAdmin = false;
                                    boolean isOnApp = false;
                                    HashMap<Object,Object> data = new HashMap<>();
                                    if(userModel.getPhone_number().contentEquals(memberModel.getNumber())){
                                        data.put("member_app_id",userId);
                                        isOnApp = true;
                                        isAdmin = true;
                                    }else {
                                        isAdmin = false;
                                    }

                                    data.put("group_id",document.getId().toString());
                                    data.put("member_name",memberModel.getName());
                                    data.put("member_number",memberModel.getNumber());
                                    data.put("member_is_on_app",isOnApp);
                                    data.put("is_admin",isAdmin);

                                    DatabaseUtils.createDocument(CreateGroupActivity.this,databases,
                                            AppWriteHelper.getCollectionId(Constants.GroupMembersCollectionId),data,new DocumentCreateListener(){
                                                @Override
                                                public void onCreatedSuccessfully(Document document) {
                                                    GroupMemberCollectionModel model = DatabaseUtils.convertToModel(document,GroupMemberCollectionModel.class);
                                                    checkAndUpdate(model);
                                                }

                                                @Override
                                                public void onFailed(Result.Failure failure) {

                                                }

                                                @Override
                                                public void onException(AppwriteException exception) {

                                                }
                                            });
                                }



//                                Toast.makeText(CreateGroupActivity.this, "Group Created Successfully", Toast.LENGTH_SHORT).show();
                                // TODO: 8/30/2022 Create new Activity for group area

                            }

                            @Override
                            public void onFailed(Result.Failure failure) {
                                Toast.makeText(CreateGroupActivity.this, "Unable to create group. Try Again!", Toast.LENGTH_SHORT).show();
                                failure.exception.printStackTrace();
                            }

                            @Override
                            public void onException(AppwriteException exception) {
                                Toast.makeText(CreateGroupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                exception.printStackTrace();
                            }
                        }
                );


            }
        });

    }

    private void addMySelfToMemberList() {
        groupMembersList.add(new PhoneContact(userModel.getUser_name(),userModel.getPhone_number()));
        updateGroupMembersCount(groupMembersList.size());
        selectedParticipantsAdapter.submitList(groupMembersList);
        selectedParticipantsAdapter.notifyDataSetChanged();
    }

    void showCreatingProgressbar(){
        binding.createGroupButton.setVisibility(View.GONE);
        binding.creatingProgressbar.setVisibility(View.VISIBLE);
    }
    void hideCreatingProgressbar(){
        binding.createGroupButton.setVisibility(View.VISIBLE);
        binding.creatingProgressbar.setVisibility(View.GONE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==110){

            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(CreateGroupActivity.this, "Contacts Permission Granted", Toast.LENGTH_SHORT).show();
                syncContacts();
            } else {
                Toast.makeText(CreateGroupActivity.this, "Contacts Permission Denied", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void syncContacts() {
        oldContactList = new ArrayList<>();
        cgaViewHolder.getContacts(CreateGroupActivity.this, "");

    }


    public void updateGroupMembersCount(int count){
        binding.groupMembersText.setText("Group Members ("+count+")");
    }

    @Override
    public void onBackPressed() {
            showExitDialog();
    }

    public void showExitDialog(){
        if(groupMembersList.size()>0){
            AlertDialog dialog = new AlertDialog.Builder(CreateGroupActivity.this).create();
            View view = LayoutInflater.from(CreateGroupActivity.this).inflate(R.layout.group_for_layout,binding.getRoot(),false);
            dialog.setView(view);
            dialog.setMessage("You have added "+groupMembersList.size()+" member\nDo you really want to exit?");
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CreateGroupActivity.super.onBackPressed();
                    dialog.dismiss();
                }
            });
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }else {
            CreateGroupActivity.super.onBackPressed();
        }
    }



    void checkAndUpdate(GroupMemberCollectionModel gmcm){
        List<Object> searchQuery = new ArrayList<>();
        searchQuery.add(Query.Companion.equal("phone_number",gmcm.getMember_number()));
        DatabaseUtils.fetchDocuments(CreateGroupActivity.this,
                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(this)),
                AppWriteHelper.getCollectionId(Constants.UserCollectionId),
                searchQuery,
                new DocumentListFetchListener() {
                    @Override
                    public void onFetchSuccessfully(List<Document> documents) {
                        if(documents.size()>0)
                        {
                            // user id
                            UserModel userModel = DatabaseUtils.convertToModel(documents.get(0), UserModel.class);
                            GroupActivity.UserModelForGroupMember userModelForGroupMember = new GroupActivity.UserModelForGroupMember(userModel.getAvatar_url(),userModel.getUpi_id());

                            updateGroupMemberField(documents.get(0).getId(),gmcm.getId(),userModelForGroupMember);

                        }else {

                            membersSize = membersSize-1;
                            cgaViewHolder.updateCheckAll(membersSize);


                        }
                    }

                    @Override
                    public void onFailed(Result.Failure failure) {
                        failure.exception.printStackTrace();
                    }

                    @Override
                    public void onException(AppwriteException exception) {
                        exception.printStackTrace();

                    }
                }
        );

    }

    private void updateGroupMemberField(String userId, String groupMemberId, GroupActivity.UserModelForGroupMember userModelForGroupMember)
    {
        Log.e("Updating","members");
        Log.e("groupMemberId",""+groupMemberId);
        HashMap<Object,Object> updateField = new HashMap<>();
        updateField.put("member_is_on_app",true);
        updateField.put("member_app_id",userId);
        updateField.put("user_data",new Gson().toJson(userModelForGroupMember));

        DatabaseUtils.updateDocument(CreateGroupActivity.this,
                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(CreateGroupActivity.this)),
                AppWriteHelper.getCollectionId(Constants.GroupMembersCollectionId),
                groupMemberId,
                updateField,
                new DocumentUpdateListener(){
                    @Override
                    public void onUpdatedSuccessfully(Document document) {
                        membersSize = membersSize-1;
                        cgaViewHolder.updateCheckAll(membersSize);

                    }

                    @Override
                    public void onFailed(Result.Failure failure) {
                        failure.exception.printStackTrace();

                    }

                    @Override
                    public void onException(AppwriteException exception) {
                        exception.printStackTrace();
                    }
                }
        );

    }

    void getMembersListWithNoId(String groupId){
        List<Object> searchQuery = new ArrayList<>();

        searchQuery.add(Query.Companion.equal("group_id",groupId));
        searchQuery.add(Query.Companion.equal("user_data","no_user_data"));
        DatabaseUtils.fetchDocuments(this,
                DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(this)),
                AppWriteHelper.getCollectionId(Constants.GroupMembersCollectionId),
                searchQuery,
                new DocumentListFetchListener() {
                    @Override
                    public void onFetchSuccessfully(List<Document> documents) {

                        ArrayList<GroupMemberCollectionModel>  groupMembersList = DatabaseUtils.convertToModelList(documents, GroupMemberCollectionModel.class);
                        membersSize = documents.size();
                        Log.e("size",""+membersSize);

                            for (GroupMemberCollectionModel gmcm:groupMembersList){
                                checkAndUpdate(gmcm);
                            }
                        Log.e("data",new Gson().toJson(groupMembersList));
                    }

                    @Override
                    public void onFailed(Result.Failure failure) {

                        failure.exception.printStackTrace();
                    }

                    @Override
                    public void onException(AppwriteException exception) {
                        exception.printStackTrace();
                    }
                }

        );



    }

}