package vn.edu.tdtu.vocawander.activity.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.edu.tdtu.vocawander.activity.fragment.PackageFragment;

public class ViewChallengeAdapter extends FragmentStatePagerAdapter {

    public ViewChallengeAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PackageFragment("PFC");
            case 1:
                return new PackageFragment("PMC");
            case 2:
                return new PackageFragment("PF");
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
