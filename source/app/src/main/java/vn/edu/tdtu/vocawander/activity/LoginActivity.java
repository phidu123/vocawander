package vn.edu.tdtu.vocawander.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.model.SharedViewModel;
import vn.edu.tdtu.vocawander.model.User;
import vn.edu.tdtu.vocawander.util.PasswordUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText etPassword, etEmail;
    private Button btnSignUp, btnLogin;

    private CheckBox cbRemember;
    private TextView tvForgotPassword;
    private final FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    private static final String PREF_NAME = "user_credentials";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        cbRemember = findViewById(R.id.cbRemember);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        if (getRememberMeStatus()) {
            restoreUserCredentials();
        }

        tvForgotPassword.setOnClickListener(view -> {
            if (etEmail.getText().toString().isEmpty()) {
                Toast.makeText(
                        LoginActivity.this,
                        "Please enter your email first",
                        Toast.LENGTH_SHORT).show();
            } else {
                forgotPassword(etEmail.getText().toString());
            }
        });


        etPassword.setOnTouchListener((v, event) -> {
            int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePassword(etPassword);
                    return true;
                }
            }
            return false;
        });

        btnSignUp.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
            finish();
        });

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(
                        LoginActivity.this,
                        "Please enter your email and password",
                        Toast.LENGTH_SHORT).show();
            } else {
                userLogin(email, password);
            }
        });
    }

    private void forgotPassword(String email) {
        mDatabase.collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            User user = document.toObject(User.class);

                            if (user != null) {
                                String phone = user.getPhone();
                                Intent otpIntent = new Intent(LoginActivity.this, VerifyOTP_Activity.class);
                                otpIntent.putExtra("phone", user.getPhone());
                                otpIntent.putExtra("userId", document.getId());
                                startActivity(otpIntent);
                            }
                        } else {
                            // No account
                            Toast.makeText(LoginActivity.this, "The account not existed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle authentication error
                        Toast.makeText(LoginActivity.this, "Firestore authentication error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void restoreUserCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        String password = sharedPreferences.getString(KEY_PASSWORD, "");
        etEmail.setText(email);
        etPassword.setText(password);
        cbRemember.setChecked(true);
        userLogin(email, password);
    }

    private boolean getRememberMeStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
    }

    private void userLogin(String email, String password) {
        // Perform user authentication using Firestore
        mDatabase.collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            User user = document.toObject(User.class);

                            if (user != null) {
                                // Check if the entered password matches the stored hashed password

//                                if (user.getPassword().equals(password)) {
                                if (PasswordUtil.checkPassword(password, user.getPassword())) {
                                    // User is authenticated successfully
                                    saveRememberMeStatus(cbRemember.isChecked());
                                    if (cbRemember.isChecked()) {
                                        saveUserCredentials(etEmail.getText().toString(), etPassword.getText().toString());
                                    }
                                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                    homeIntent.putExtra("ID", document.getId());
                                    homeIntent.putExtra("username", user.getName());
                                    homeIntent.putExtra("email", user.getEmail());
                                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(homeIntent);
                                    finish();
                                } else {
                                    // Authentication failed
                                    Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // No account
                            Toast.makeText(LoginActivity.this, "The account not existed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle authentication error
                        Toast.makeText(LoginActivity.this, "Firestore authentication error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserCredentials(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    private void saveRememberMeStatus(boolean isChecked) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_REMEMBER_ME, isChecked);
        editor.apply();
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
