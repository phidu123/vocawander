package vn.edu.tdtu.vocawander.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.util.PasswordUtil;

public class ChangePasswordActivity extends AppCompatActivity {

    private final FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    private EditText etOldPass, etNewPass, etConfirm;
    private Button btnUpdate;

    private TextView tvStatus;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userId");
        }

        etOldPass = findViewById(R.id.etOldPass);
        etNewPass = findViewById(R.id.etNewPass);
        etConfirm = findViewById(R.id.etConfirm);
        btnUpdate = findViewById(R.id.btnUpdate);
        tvStatus = findViewById(R.id.tvStatus);

        btnUpdate.setOnClickListener(view -> {
            String oldPassword = etOldPass.getText().toString();
            String newPassword = etNewPass.getText().toString();
            String confirmPassword = etConfirm.getText().toString();

            if (!newPassword.equals(confirmPassword)) {
                tvStatus.setText("Password and confirm password does not match");
            } else if (newPassword.length() < 6) {
                tvStatus.setText("Password must be more than 6 ");
            } else if (userId != null){
                // query current password in the firestore with the collection "user"
                // check does it equal to the oldPassword and show messge.
                // if it equals, call the method changePassword()

                DocumentReference userRef = mDatabase.collection("user").document(userId);

                userRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String currentPassword = document.getString("password");
                            if (currentPassword != null && PasswordUtil.checkPassword(oldPassword, currentPassword)) {
                                changePassword(newPassword);
                            } else {
                                tvStatus.setText("Old password doesn't match");
                            }
                        } else {
                            // Document does not exist
                            Toast.makeText(getApplicationContext(), "User document not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Task failed with an exception
                        Toast.makeText(getApplicationContext(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        setupPasswordToggle(etOldPass);
        setupPasswordToggle(etNewPass);
        setupPasswordToggle(etConfirm);
    }

    private void changePassword(String password) {
        if (userId != null) {
            DocumentReference userRef = mDatabase.collection("user").document(userId);

            Map<String, Object> updatedData = new HashMap<>();;
            updatedData.put("password", PasswordUtil.getHashing(password));

            // Update the document
            userRef.update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Your password has been updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed to update new password", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void setupPasswordToggle(EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePassword(editText);
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePassword(EditText etPasswordShow) {
        int inputType = etPasswordShow.getInputType();

        if (inputType == 1) {
            etPasswordShow.setInputType(129); // 129 = hide password
            etPasswordShow.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0,
                    R.drawable.hide_password, 0
            );
        } else {
            etPasswordShow.setInputType(1); // 1 = show password
            etPasswordShow.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0,
                    R.drawable.show_password, 0
            );
        }
        etPasswordShow.setSelection(etPasswordShow.getText().length());
    }
}