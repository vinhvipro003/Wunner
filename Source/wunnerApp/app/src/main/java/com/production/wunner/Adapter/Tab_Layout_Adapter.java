package com.production.wunner.Adapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class Tab_Layout_Adapter  extends FragmentPagerAdapter {
    private final ArrayList<Fragment> fragmentslist = new ArrayList<>();
    private final ArrayList<String> fragmentTitlelist = new ArrayList<>();
    public Tab_Layout_Adapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentslist.get(position);
    }

    @Override
    public int getCount() {
        return fragmentslist.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitlelist.get(position);
    }
    public void addFragment(Fragment fragment, String title)
    {
        fragmentslist.add(fragment);
        fragmentTitlelist.add(title);
    }
}
