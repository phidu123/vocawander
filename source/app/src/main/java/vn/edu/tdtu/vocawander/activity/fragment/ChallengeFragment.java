package vn.edu.tdtu.vocawander.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.adapter.ViewChallengeAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeFragment extends Fragment {

    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private static final String TAG_FLASH_CARD = "PFC";
    private static final String TAG_MULTIPLE_CHOICE = "PMC";
    private static final String TAG_FILL = "PF";
    private String CURRENT_TAG = null;
    private PackageFragment mPackageFC, mPackageMC, mPackageF;
    Intent intent;
    public ChallengeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge,container,false);
        mViewPager = view.findViewById(R.id.fragmentContentLib);
        mBottomNavigationView = view.findViewById(R.id.menu_challenge_frag);

        ViewChallengeAdapter adapter = new ViewChallengeAdapter(getParentFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.flash_card).setChecked(true);
                        break;
                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.multiple_choice).setChecked(true);
                        break;
                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.fill).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.flash_card){
                    mViewPager.setCurrentItem(0);
                } else if (id == R.id.multiple_choice) {
                    mViewPager.setCurrentItem(1);
                }else if (id == R.id.fill) {
                    mViewPager.setCurrentItem(2);
                }
                return true;
            }
        });
        return view;
    }
}