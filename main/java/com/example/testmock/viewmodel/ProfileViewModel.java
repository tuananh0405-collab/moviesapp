package com.example.testmock.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testmock.model.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

public class ProfileViewModel extends ViewModel {

    private static final String TAG = "ProfileViewModel";
    private final DatabaseReference databaseReference;
    private final MutableLiveData<UserProfile> userProfileLiveData;

    public ProfileViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userProfileLiveData = new MutableLiveData<>();
        fetchUserProfile();
    }

    public LiveData<UserProfile> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    private void fetchUserProfile() {
        String userId = "khanh";
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userProfile = snapshot.getValue(UserProfile.class);
                userProfileLiveData.postValue(userProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read user profile", error.toException());
            }
        });
    }

    public void saveUserProfile(UserProfile userProfile) {
        String userId = "khanh";
        userProfileLiveData.setValue(userProfile);
        databaseReference.child(userId).setValue(userProfile)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User profile updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update user profile", e));
    }

    public String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap convertBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
