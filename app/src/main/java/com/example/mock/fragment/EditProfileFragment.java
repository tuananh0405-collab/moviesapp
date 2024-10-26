package com.example.mock.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.mock.R;
import com.example.mock.model.User;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class EditProfileFragment extends Fragment {

    private Button btnCancel, btnDone;
    private AppCompatButton btnDOBPicker;
    private ImageView imgAvt;
    private EditText edtName, edtEmail;
    private TextView txtDOB;
    private RadioGroup radioGroup;
    private RadioButton rbtnMale, rbtnFemale;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    public EditProfileFragment(User user) {
    }

    public interface OnProdileEditedListener {
        void onProfileEdited(User user);
    }

    private OnProdileEditedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnProdileEditedListener) {
            listener = (OnProdileEditedListener) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement OnProfileEditedListener");
        }
    }

    public static EditProfileFragment newInstance(User user) {
        EditProfileFragment fragment = new EditProfileFragment(user);
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                imgAvt.setImageBitmap(photo);
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                imgAvt.setImageURI(uri);
            }
        });

        btnCancel = view.findViewById(R.id.btnCancel);
        btnDone = view.findViewById(R.id.btnDone);

        imgAvt = view.findViewById(R.id.imgAvt);
        edtName = view.findViewById(R.id.edtName);
        edtEmail = view.findViewById(R.id.edtEmail);
        txtDOB = view.findViewById(R.id.txtDOB);

        radioGroup = view.findViewById(R.id.radioGroup);
        rbtnMale = view.findViewById(R.id.rbtnMale);
        rbtnFemale = view.findViewById(R.id.rbtnFemale);
        btnDOBPicker = view.findViewById(R.id.btnDOBPicker);

        User user = (User) getArguments().getParcelable("user");

        if (user != null) {
            edtName.setText(user.getName());
            edtEmail.setText(user.getEmail());
            txtDOB.setText(user.getDob());
            if (user.isMale()) {
                rbtnMale.setChecked(true);
            } else {
                rbtnFemale.setChecked(true);
            }
            imgAvt.setImageBitmap(user.getAvt());
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onProfileEdited(user);
                Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().remove(EditProfileFragment.this).commit();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedName = edtName.getText().toString();
                String updatedEmail = edtEmail.getText().toString();
                String updatedDOB = txtDOB.getText().toString();
                boolean isMale = rbtnMale.isChecked();

                imgAvt.setDrawingCacheEnabled(true);
                Bitmap avatarBitmap = Bitmap.createBitmap(imgAvt.getDrawingCache());
                imgAvt.setDrawingCacheEnabled(false);

                String avatarBase64 = bitmapToBase64(avatarBitmap);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", updatedName);
                editor.putString("email", updatedEmail);
                editor.putString("dob", updatedDOB);
                editor.putBoolean("isMale", isMale);
                editor.putString("avatar", avatarBase64);
                editor.apply();

                user.setName(updatedName);
                user.setEmail(updatedEmail);
                user.setDob(updatedDOB);
                user.setMale(isMale);
                user.setAvt(avatarBitmap);

                listener.onProfileEdited(user);

                getActivity().getSupportFragmentManager().beginTransaction().remove(EditProfileFragment.this).commit();
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        imgAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), imgAvt);
                popup.getMenuInflater().inflate(R.menu.avatar_options_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_camera) {
                            openCamera();
                        } else if (item.getItemId() == R.id.menu_gallery) {
                            openGallery();
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        btnDOBPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
                        txtDOB.setText(selectedDate);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });
        return view;
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 1);
            return;
        }
        cameraLauncher.launch(cameraIntent);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}