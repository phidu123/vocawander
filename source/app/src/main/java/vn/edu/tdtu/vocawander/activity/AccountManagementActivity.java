package vn.edu.tdtu.vocawander.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.model.Vocabulary;

public class AccountManagementActivity extends AppCompatActivity {
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 124;

    private final FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef;

    private EditText etName, etPhone, etEmail;

    private TextView tvName, btnSave, btnChangePassword, btnDeleteAccount;

    private String imgUrl;

    private ImageView ivAvatar;

    private String userId;

    private static final String PREF_NAME = "user_credentials";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        tvName = findViewById(R.id.tvName);
        ivAvatar = findViewById(R.id.ivAvatar);

        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        btnDeleteAccount.setOnClickListener(view -> {
            if (userId != null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete your account?");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    deleteAccount(userId);
                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();
            }
        });

        btnChangePassword.setOnClickListener(view -> {
            Intent changePasswordIntent = new Intent(AccountManagementActivity.this, ChangePasswordActivity.class);
            changePasswordIntent.putExtra("userId", userId);
            startActivity(changePasswordIntent);
        });

        btnSave.setOnClickListener(view -> {
            saveData();
        });

        ivAvatar.setOnClickListener(view -> {
            openGalleryIntent();
        });

        Intent accountIntent = getIntent();

        if (accountIntent != null) {
            userId = accountIntent.getStringExtra("userId");
            mDatabase.collection("user").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> data = documentSnapshot.getData();

                                String email = (String) data.get("email");
                                String name = (String) data.get("name");
                                String phone = (String) data.get("phone");

                                String avatar = (String) data.get("avatar");

                                email = email != null ? email : "";
                                name = name != null ? name : "";
                                phone = phone != null ? phone : "";
                                avatar = avatar != null ? avatar : "";

                                if (avatar.equals("")) {
                                    ivAvatar.setImageResource(R.mipmap.app_icon);
                                } else {
                                    imgUrl = avatar;
                                    Glide.with(AccountManagementActivity.this).load(avatar).circleCrop().into(ivAvatar);
                                }

                                tvName.setText(name);
                                etName.setText(name);
                                etPhone.setText(phone);
                                etEmail.setText(email);
                            }
                        }
                    });
        }
    }

    private void deleteAccount(String userId) {
        DocumentReference userRef = mDatabase.collection("user").document(userId);

        userRef.delete()
                .addOnSuccessListener(aVoid -> {
                    showToast("Account deleted successfully");

                    clearUserCredentials();
                    navigateToLoginPage();
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to delete account: " + e.getMessage());
                });
    }

    private void clearUserCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Remove the keys related to user credentials
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_REMEMBER_ME);

        // Apply the changes
        editor.apply();
    }
    private void navigateToLoginPage() {
        // You can use Intent to navigate to the login page or perform any other navigation logic
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish(); // Finish the current activity to prevent going back to it
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveData() {
        if (userId != null) {
            DocumentReference userRef = mDatabase.collection("user").document(userId);

            String updatedName = etName.getText().toString();
            String updatedPhone = etPhone.getText().toString();

            String updatedAvatar = (imgUrl != null) ? imgUrl : "";


            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("name", updatedName);
            updatedData.put("phone", updatedPhone);
            updatedData.put("avatar", updatedAvatar);

            // Update the document
            userRef.update(updatedData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update successful
                            Toast.makeText(getApplicationContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                            reloadActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                            Toast.makeText(getApplicationContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void reloadActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void openGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (galleryIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(galleryIntent, GALLERY_PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No gallery app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            uploadFile(imageUri);
        }
    }

    private void uploadFile(Uri imgUri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(imgUri != null){
            StorageReference storageReference = mStorageRef.child(UUID.randomUUID().toString());
            storageReference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Upload success
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imgUrl = uri.toString();
                                    Glide.with(AccountManagementActivity.this)
                                            .load(imgUrl)
                                            .circleCrop()
                                            .into(ivAvatar);
                                }
                            });
                            // Dismiss the ProgressDialog once upload is successful
                            progressDialog.setMessage("Uploading 100%");
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Upload failure
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        // Show a progress dialog or update a ProgressBar here
                        int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading " + progress + "%");
                    });
        }else {
            Toast.makeText(getApplicationContext(), "No image chosen" , Toast.LENGTH_LONG).show();
        }
    }
}