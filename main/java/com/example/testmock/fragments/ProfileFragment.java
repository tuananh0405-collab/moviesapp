package com.example.testmock.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.testmock.databinding.FragmentProfileBinding;
import com.example.testmock.model.UserProfile;
import com.example.testmock.viewmodel.ProfileViewModel;

import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private ProfileViewModel viewModel;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private FragmentProfileBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    updateProfileImage(imageBitmap);
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri imageUri = data.getData();
                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                        updateProfileImage(imageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        binding.setUserProfile(viewModel.getUserProfileLiveData().getValue());

        binding.cameraBtn.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                cameraLauncher.launch(takePictureIntent);
            }
        });

        binding.galleryBtn.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(pickPhoto);
        });

        binding.doneButton.setOnClickListener(v -> {
            UserProfile userProfile = new UserProfile(
                    binding.fullNameEditText.getText().toString(),
                    binding.emailEditText.getText().toString(),
                    binding.birthdayEditText.getText().toString(),
                    binding.getUserProfile() != null ? binding.getUserProfile().getProfileImage() : null
            );
            viewModel.saveUserProfile(userProfile);
            Navigation.findNavController(requireView()).navigateUp();
        });

        binding.cancelButton.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigateUp();
        });

        viewModel.getUserProfileLiveData().observe(getViewLifecycleOwner(), userProfile -> {
            binding.setUserProfile(userProfile);
        });
    }


    private void updateProfileImage(Bitmap bitmap) {
        String base64Image = viewModel.convertBitmapToBase64(bitmap);
        UserProfile userProfile = binding.getUserProfile();
        if (userProfile != null) {
            userProfile.setProfileImage(base64Image);
            binding.setUserProfile(userProfile);
            binding.displayImageView.setImageBitmap(bitmap);
        }
    }
}
