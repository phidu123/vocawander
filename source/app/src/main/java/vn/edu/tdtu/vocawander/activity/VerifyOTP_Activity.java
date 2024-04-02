package vn.edu.tdtu.vocawander.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.util.PasswordUtil;

public class VerifyOTP_Activity extends AppCompatActivity {

    private String phone = "", maskedPhone = "", userId;

    private TextView tvPhone, tvResend;
    private final FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    private TextInputEditText etCode1, etCode2, etCode3, etCode4, etCode5, etCode6;

    private MaterialButton btnVerify;

    private ProgressBar progressBar;

    private String verificationOtp;

    private final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private final String DIGIT = "0123456789";
    private final String PASSWORD_ALLOW = CHAR_LOWER + CHAR_UPPER + DIGIT;

    private final SecureRandom random = new SecureRandom();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        progressBar = findViewById(R.id.progressBar);

        tvPhone = findViewById(R.id.tvPhone);
        tvResend = findViewById(R.id.tvResend);
        btnVerify = findViewById(R.id.btnVerify);

        etCode1 = findViewById(R.id.etCode1);
        etCode2 = findViewById(R.id.etCode2);
        etCode3 = findViewById(R.id.etCode3);
        etCode4 = findViewById(R.id.etCode4);
        etCode5 = findViewById(R.id.etCode5);
        etCode6 = findViewById(R.id.etCode6);
        otpInputScript();

        Intent intent = getIntent();
        if (intent != null) {
            phone = intent.getStringExtra("phone");
            userId = intent.getStringExtra("userId");
            String lastThreeCharacters = phone.substring(phone.length() - 3);
            maskedPhone = "*".repeat(phone.length() - 3) + lastThreeCharacters;
            tvPhone.setText(maskedPhone);
            sendOTP(phone);
        }

        tvResend.setOnClickListener(view -> {
            sendOTP(phone);
        });

        btnVerify.setOnClickListener(view -> {
            verifyOTP();
        });
    }

    private void verifyOTP() {
        String otpCode = etCode1.getText().toString() +
                etCode2.getText().toString() +
                etCode3.getText().toString() +
                etCode4.getText().toString() +
                etCode5.getText().toString() +
                etCode6.getText().toString();

        if (verificationOtp != null) {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationOtp, otpCode);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // show a dialog to notification your new password. sign in and change the password
                            changePassword(new PasswordChangeCallback() {
                                @Override
                                public void onPasswordChanged(String newPassword) {
                                    // Handle the new password
                                    showPasswordDialog(newPassword);
                                }

                                @Override
                                public void onPasswordChangeFailed(Exception e) {
                                    // Handle the failure
                                    System.out.println("Password change failed: " + e.getMessage());
                                }
                            });
                        } else {
                            Toast.makeText(this, "OTP is incorrect or invalid. Resend it", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "OTP is incorrect. Resend it", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPasswordDialog(String newPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Notification");

        builder.setMessage("Your new password is " + newPassword + ". Sign in and change password immediately!");

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle OK button click
            Intent intent = new Intent(VerifyOTP_Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        builder.setNeutralButton("Copy", (dialog, which) -> {
            // Handle Copy button click
            copyToClipboard(newPassword);
            Toast.makeText(getApplicationContext(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(VerifyOTP_Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Password", text);
        clipboardManager.setPrimaryClip(clipData);
    }


    private void changePassword(final PasswordChangeCallback callback) {
        if (userId != null) {
            DocumentReference userRef = mDatabase.collection("user").document(userId);

            Map<String, Object> updatedData = new HashMap<>();
            String password = generateRandomPassword();
            updatedData.put("password", PasswordUtil.getHashing(password));

            // Update the document
            userRef.update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        if (callback != null) {
                            callback.onPasswordChanged(password);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        if (callback != null) {
                            callback.onPasswordChangeFailed(e);
                        }
                        Toast.makeText(getApplicationContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String generateRandomPassword() {

        StringBuilder password = new StringBuilder(6);

        // At least one character from each character set
        password.append(getRandomChar(CHAR_LOWER));
        password.append(getRandomChar(CHAR_UPPER));
        password.append(getRandomChar(DIGIT));

        // Fill the remaining length with random characters
        for (int i = 3; i < 6; i++) {
            password.append(getRandomChar(PASSWORD_ALLOW));
        }
        // Shuffle the characters to ensure randomness
        return shuffleString(password.toString());
    }

    private char getRandomChar(String characterSet) {
        int randomIndex = random.nextInt(characterSet.length());
        return characterSet.charAt(randomIndex);
    }

    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }

    private void sendOTP(String phone) {
        // Show the ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(getCountryCodePhone(phone))
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificationOtp = verificationId;
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Hide the ProgressBar when the verification is complete
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(VerifyOTP_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private String getCountryCodePhone(String phone) {
        return "+84" + phone.substring(1);
    }

    private void otpInputScript() {
        etCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    etCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    etCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    etCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    etCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    etCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public interface PasswordChangeCallback {
        void onPasswordChanged(String newPassword);

        void onPasswordChangeFailed(Exception e);
    }
}