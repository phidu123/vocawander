package vn.edu.tdtu.vocawander.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.fragment.ChallengeFragment;
import vn.edu.tdtu.vocawander.activity.fragment.HomeFragment;
import vn.edu.tdtu.vocawander.activity.fragment.LibraryFragment;
import vn.edu.tdtu.vocawander.activity.fragment.ProfileFragment;
import vn.edu.tdtu.vocawander.model.SharedViewModel;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;

    private LinearLayout searchView;

    private static final String TAG_HOME = "home";
    private static final String TAG_CHALLENGE = "challenge";
    private static final String TAG_LIBRARY = "library";
    private static final String TAG_PROFILE = "profile";

    private String CURRENT_TAG = null;

    private HomeFragment homeFragment;
    private ChallengeFragment challengeFragment;
    private LibraryFragment libraryFragment;
    private ProfileFragment profileFragment;

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("username");
            String email = intent.getStringExtra("email");
            String id = intent.getStringExtra("ID");
            sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            sharedViewModel.setUsername(username);
            sharedViewModel.setEmail(email);
            sharedViewModel.setId(id);
        }
        initFragments();

        searchView = findViewById(R.id.search_bar);
        if (searchView != null) {
            searchView.setOnClickListener(view -> {
                Intent intent1 = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent1);
            });
        }

        mBottomNavigationView = findViewById(R.id.bottomNavigationView);

        mBottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                setSearchBarVisibility(true);
                loadFragment(homeFragment, TAG_HOME);
                return true;
            }
            if (id == R.id.challenge) {
                setSearchBarVisibility(true);
                loadFragment(challengeFragment, TAG_CHALLENGE);
                return true;
            }
            if (id == R.id.library) {
                setSearchBarVisibility(true);
                loadFragment(libraryFragment, TAG_LIBRARY);
                return true;
            }
            if (id == R.id.profile) {
                // TODO: set search_bar to gone
                setSearchBarVisibility(false);
                loadFragment(profileFragment, TAG_PROFILE);
                return true;
            }
            return false;
        });

        // Load home automatically on start-up
        loadFragment(homeFragment, TAG_HOME);

        // Handle back button press to navigate through fragments
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!CURRENT_TAG.equals(TAG_HOME)) {
                    loadFragment(homeFragment, TAG_HOME);
                    mBottomNavigationView.setSelectedItemId(R.id.home);
                } else {
                    finish();
                }
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, callback);

    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        challengeFragment = new ChallengeFragment();
        libraryFragment = new LibraryFragment();
        profileFragment = new ProfileFragment();
    }

    private void loadFragment(Fragment fragment, String tag) {
        if (!tag.equals(CURRENT_TAG)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentContent, fragment, tag);
            ft.commit();
            CURRENT_TAG = tag;
        }
    }

    private void setSearchBarVisibility(boolean isVisible) {
        searchView = findViewById(R.id.search_bar);
        if (searchView != null) {
            searchView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }
}

