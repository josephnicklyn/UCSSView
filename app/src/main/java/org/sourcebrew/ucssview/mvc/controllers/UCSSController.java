package org.sourcebrew.ucssview.mvc.controllers;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.sourcebrew.ucssview.mvc.models.TermModel;

/**
 * Created by John on 1/12/2018.
 */

public class UCSSController extends FragmentStatePagerAdapter {

    private int nTabCount = 0;

    public static UCSSController globalAdapter;

    public static UCSSController getAdapter() {
        return globalAdapter;
    }

    private final TabLayout tabLayout;
    private final ViewPager viewPager;

    private final HomeFragment homeFragment;
    private final CampusViewFragment campusViewFragment;
    private final InitializeFragment initializeFragment;
    private final Activity activity;

    private UCSSFragment currentFragment = null;

    public UCSSController(
            Activity activity,
            FragmentManager fm,
            TabLayout tabLayout,
            ViewPager viewPager) {
        super(fm);
        this.activity = activity;
        viewPager.setOffscreenPageLimit(10);
        globalAdapter = this;

        this.tabLayout = tabLayout;
        this.viewPager = viewPager;

        viewPager.setAdapter(this);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        homeFragment = new HomeFragment();
        campusViewFragment = new CampusViewFragment();
        initializeFragment = new InitializeFragment();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                UCSSController.this.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        addTab("Terms", homeFragment);
        addTab("Graphs", campusViewFragment);
        addTab("Context", initializeFragment);

        //setTab(1);

    }

    @Override
    public Fragment getItem(int position) {
        TabLayout.Tab t = tabLayout.getTabAt(position);
        currentFragment = (UCSSFragment)t.getTag();
        return currentFragment;
    }

    @Override
    public int getCount() {
        return nTabCount;
    }

    private void addTab(String text, Fragment fragment) {
        TabLayout.Tab t = tabLayout.newTab().setText(text);
        t.setTag(fragment);
        tabLayout.addTab(t);

        nTabCount++;
        super.notifyDataSetChanged();

    }

    public void setTab(int i) {
        if (i >= 0 && i < tabLayout.getTabCount()) {
            tabLayout.getTabAt(i).select();
        }
    }

    public void setTab(int i, boolean force) {
        if (tabLayout.getSelectedTabPosition() != 2 && force == false)
            return;
        if (i >= 0 && i < tabLayout.getTabCount()) {
            tabLayout.getTabAt(i).select();
        }
    }

    private TermModel termModel;
    /**
     *
     * @param term
     */

    public void selectTerm(TermModel term) {
        //if (termModel != term) {
            setTitle(term.getValue());
            termModel = term;
            homeFragment.termChanged(term);
            campusViewFragment.termChanged(term);
            initializeFragment.termChanged(term);

       // }

        setTab(1, true);
    }

    public  TermModel getTermModel() {
        return termModel;
    }

    public void setTitle(String title) {
        activity.setTitle("UCSS " + title);
    }

    public int getCurrentTab() {
        return tabLayout.getSelectedTabPosition();
    }

    public UCSSFragment getCurrentFragment() {
        return currentFragment;
    }
}