package vn.edu.tdtu.vocawander.activity.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.model.SharedViewModel;


public class LibraryFragment extends Fragment {
    private BottomNavigationView mBottomNavigationView;

    private static final String TAG_PUBLIC_TOPIC = "public_topic";
    private static final String TAG_SAVED = "saved";
    private static final String TAG_MY_TOPIC = "my_topic";
    private String CURRENT_TAG = null;
    private MyTopicFragment myTopicFragment;
    private SavedFragment savedFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.library_activity, container, false);
//        Toolbar toolbar = rootView.findViewById(R.id.toolbar);

        initFragments();

        mBottomNavigationView = rootView.findViewById(R.id.bottomNavigationViewLib);

        mBottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.my_topic) {
                loadFragment(myTopicFragment, TAG_MY_TOPIC);
                return true;
            }
            if (id == R.id.saved) {
                loadFragment(savedFragment, TAG_SAVED);
                return true;
            }
            return false;
        });

        // Load home automatically on start-up
        loadFragment(myTopicFragment, TAG_MY_TOPIC);

        // Handle back button press to navigate through fragments
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!CURRENT_TAG.equals(TAG_MY_TOPIC)) {
                    loadFragment(myTopicFragment, TAG_MY_TOPIC);
                    mBottomNavigationView.setSelectedItemId(R.id.my_topic);
                } else {
                    requireActivity().finish();
                }
            }
        };
        // Add the callback to the OnBackPressedDispatcher
        OnBackPressedDispatcher onBackPressedDispatcher = requireActivity().getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(getViewLifecycleOwner(), callback);
        return rootView;

    }

    private void initFragments() {
        myTopicFragment = new MyTopicFragment();
        savedFragment = new SavedFragment();
    }

    private void loadFragment(Fragment fragment, String tag) {
        if (!tag.equals(CURRENT_TAG)) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentContentLib, fragment, tag);
            ft.commit();
            CURRENT_TAG = tag;
        }
    }

}