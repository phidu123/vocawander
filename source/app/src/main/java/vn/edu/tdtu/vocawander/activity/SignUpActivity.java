package vn.edu.tdtu.vocawander.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.model.User;
import vn.edu.tdtu.vocawander.util.PasswordUtil;

public class SignUpActivity extends AppCompatActivity {
    private Button btnLogin, btnSignUp;

    private EditText etName, etPassword, etRePassword, etEmail;

    private final FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);

        btnLogin.setOnClickListener(view -> {
            returnToLogin();
        });

        btnSignUp.setOnClickListener(view -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String rePassword = etRePassword.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(SignUpActivity.this, "Some fields are empty", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(
                        SignUpActivity.this,
                        "Password length must be more than 6",
                        Toast.LENGTH_SHORT).show();
            } else if (!TextUtils.equals(password, rePassword)) {
                Toast.makeText(SignUpActivity.this, "The password and password confirmation do not match.", Toast.LENGTH_SHORT).show();
            } else {
                signUp(name, email, password);
            }
        });
    }

    private void signUp(String name, String email, String password) {
        String hashedPassword = PasswordUtil.getHashing(password);
        User userData = new User(name, email, hashedPassword, "");

        // Store user data in Firestore
        mDatabase.collection("user")
                .add(userData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SignUpActivity.this, "Registration successful. Login now", Toast.LENGTH_SHORT).show();
                    returnToLogin();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUpActivity.this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void returnToLogin() {
        Intent intentLogin = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
    }
}
