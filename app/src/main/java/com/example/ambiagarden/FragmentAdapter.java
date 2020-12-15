package com.example.ambiagarden;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
    private final List<Fragment>mFragmentList=new ArrayList<>();
    private final List<String>mFragmentTitleList=new ArrayList<>();

    public FragmentAdapter(@NonNull FragmentManager fm, int BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        super(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       return mFragmentList.get(position);
    }

    @Override
    public int getCount() {

        return  mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
        //return mFragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
        //mFragmentTitleList.add("a");
    }
}
