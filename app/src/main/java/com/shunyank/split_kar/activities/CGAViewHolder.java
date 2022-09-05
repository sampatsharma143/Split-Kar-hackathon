package com.shunyank.split_kar.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.shunyank.split_kar.models.PhoneContact;
import com.shunyank.split_kar.network.AppWriteHelper;
import com.shunyank.split_kar.network.Constants;
import com.shunyank.split_kar.network.callbacks.FriendFetchListener;
import com.shunyank.split_kar.network.model.UserModel;
import com.shunyank.split_kar.network.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;


public class CGAViewHolder extends ViewModel {

    public MutableLiveData<ArrayList<PhoneContact>> myContacts;
    public MutableLiveData<Object> userProfile;
    public MutableLiveData<Integer> checkAll;


    public CGAViewHolder() {
        myContacts = new MutableLiveData<>();
        userProfile = new MutableLiveData<>();
        checkAll = new MutableLiveData<>();
    }

    public void getContacts(Activity activity, String searchQuery) {
        Log.e("called","yes");
        ArrayList<PhoneContact> data= new ArrayList<>();
        Uri filterUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(searchQuery));
        String[] projection = new String[]{ ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
        Cursor phonesCursor;
//        if(searchQuery.isEmpty()){
//            phonesCursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
//
//        }else {
            phonesCursor = activity.getContentResolver().query(filterUri, projection, null, null, null);
//        }
        int i=1;
        while (phonesCursor.moveToNext()) {
//            @SuppressLint("Range") String id = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID));
            @SuppressLint("Range") String name = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phoneNumber = phoneNumber.replace(" ","");
            phoneNumber = phoneNumber.replace("-","");

            // for all numbers uncomment this
//            String patterns
//                    = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
//                    + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
//                    + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$"
//                    +"|^(\\+91)[6-9]\\d{9}$"
//                    +"|^[6-9]\\d{9}$"
//                    +"|^(91)\\d{10}$";

            // for indian numbers only
            String patterns
                    =
                    "^(\\+91)[6-9]\\d{9}$"
                    +"|^[6-9]\\d{9}$"
                    +"|^(91)\\d{10}$";
            boolean alreadyThere = false;
            if(Pattern.compile(patterns).matcher(phoneNumber).matches()){

                for(PhoneContact contact:data){

                    if(contact.getNumber().contentEquals(phoneNumber)){
                        alreadyThere = true;
                        break;
                    }

                }
                if(!alreadyThere) {
                    PhoneContact objContact = new PhoneContact(name, phoneNumber);
                    data.add(objContact);
                }
            }else {
                Log.e(i+" - Not a Valid Number ",phoneNumber);
                i++;
            }


        }

        myContacts.setValue(data);
    }

    public void getUser(Activity activity,PhoneContact p) {
        List<Object> data = new ArrayList<>();
        String phone = p.getNumber().replace("+","");
        data.add(Query.Companion.equal("phone_number",phone));
        DatabaseUtils.checkContactIsFriend(activity, DatabaseUtils.getDatabase(AppWriteHelper.GetAppWriteClient(activity.getBaseContext())),
                AppWriteHelper.getCollectionId(Constants.UserCollectionId), data, new FriendFetchListener() {



                    @Override
                    public void onFetchSuccessfully(List<Document> documents, PhoneContact phoneContact) {
                        if(documents.size()>0)
                        {
                            UserModel user = DatabaseUtils.convertToModel(documents.get(0),UserModel.class);
                            userProfile.postValue(user);
                        }else {
                            if(phoneContact!=null){
                                Log.e("on not in app",new Gson().toJson(phoneContact));
                                userProfile.postValue(phoneContact);

                            }

                        }
                    }

                    @Override
                    public void onFailed(Result.Failure failure) {

                    }

                    @Override
                    public void onException(AppwriteException exception) {

                    }
                },p
        );

    }

    public void updateCheckAll(Integer value){
        checkAll.postValue(value);
    }
}
