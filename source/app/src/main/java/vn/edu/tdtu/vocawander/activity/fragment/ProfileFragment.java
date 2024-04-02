package vn.edu.tdtu.vocawander.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import javax.annotation.Nullable;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.AccountManagementActivity;
import vn.edu.tdtu.vocawander.activity.LoginActivity;
import vn.edu.tdtu.vocawander.model.SharedViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private SharedViewModel sharedViewModel;

    private final FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    private String userId;

    private TextView tvName, tvEmail, accountMng, permissionMng, reportMng,btnLogout,numWord,pointWord;

    private ImageView ivAvatar;

    private static final String PREF_NAME = "user_credentials";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_ME = "remember_me";

    public ProfileFragment() {
        // required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe the data
        sharedViewModel.getUsername().observe(this, username -> {
            if (tvName != null) {
                tvName.setText(username.toString());
            }
        });

        sharedViewModel.getEmail().observe(this, email -> {
            if (tvEmail != null) {
                tvEmail.setText(email.toString());
            }
        });

        sharedViewModel.getId().observe(this, id -> {
            userId = id;
        });

        if (userId != null) {
            System.out.println(userId);
            loadUserData(userId);
        } else {
            sharedViewModel.getId().observe(this, id -> {
                if (id != null) {
                    userId = id;
                    loadUserData(userId);
                }
            });
        }
    }

    private void loadUserData(String userId) {
        mDatabase.collection("user").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();

                        String email = (String) data.get("email");
                        String name = (String) data.get("name");
                        String avatar = (String) data.get("avatar");

                        name = name != null ? name : "";
                        email = email != null ? email : "";
                        avatar = avatar != null ? avatar : "";

                        if (avatar.equals("")) {
                            ivAvatar.setImageResource(R.mipmap.app_icon);
                        } else {
                            Glide.with(ProfileFragment.this).load(avatar).circleCrop().into(ivAvatar);
                        }

                        tvName.setText(name);
                        tvEmail.setText(email);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        accountMng = view.findViewById(R.id.accountMng);
        permissionMng = view.findViewById(R.id.permissionMng);
        reportMng = view.findViewById(R.id.reportMng);
        btnLogout = view.findViewById(R.id.btnLogout);
        numWord = view.findViewById(R.id.numberWord);
        getNumberWord();
        pointWord = view.findViewById(R.id.pointWord);
        getPointWord();
        permissionMng.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });

        accountMng.setOnClickListener(v -> {
            Intent accountIntent = new Intent(getContext(), AccountManagementActivity.class);
            accountIntent.putExtra("userId", userId);
            startActivity(accountIntent);
        });

        btnLogout.setOnClickListener(v -> {
            clearUserCredentials();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void clearUserCredentials() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Remove the keys related to user credentials
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_REMEMBER_ME);

        // Apply the changes
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (userId != null) {
            loadUserData(userId);
        }
    }
    public void getNumberWord(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("learn").whereEqualTo("user",userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            numWord.setText(String.valueOf(task.getResult().size()));
                        }
                        else {
                            numWord.setText("0");
                        }
                    }
                });
    };
    public void getPointWord(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("learn").whereEqualTo("user",userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int point = 0;
                            for (DocumentSnapshot doc: task.getResult()){
                                point += Integer.parseInt(doc.getString("count").toString());
                            }
                            pointWord.setText(String.valueOf(point));
                        }
                        else {
                            pointWord.setText("0");
                        }
                    }
                });
    }
}