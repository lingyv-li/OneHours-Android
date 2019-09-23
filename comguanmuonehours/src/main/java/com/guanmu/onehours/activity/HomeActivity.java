package com.guanmu.onehours.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guanmu.onehours.R;
import com.guanmu.onehours.SharePreferenceUtils;
import com.guanmu.onehours.fragment.FragmentTabList;
import com.guanmu.onehours.fragment.FragmentTabMain;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    public FragmentTabList fragList;
    public FragmentTabMain fragMain;
    private CoordinatorLayout mCoordinator;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.guanmu.onehours.R.layout.home_activity);
        initView();

        if (SharePreferenceUtils.getInstance().getFirstIntro()) {
            startActivity(new Intent(this, IntroActivity.class));
        }
    }


    private void initView() {
        mCoordinator = (CoordinatorLayout) findViewById(R.id.root_coordinator);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView mDrawer = (NavigationView) findViewById(R.id.main_drawer);
        assert mDrawer != null;
        mDrawer.setNavigationItemSelectedListener(this);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                TextView userText = (TextView) mDrawerHeader.findViewById(R.id.username);
                userText.setText(getString(R.string.welcome));
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Show drawer at the first time
        if (SharePreferenceUtils.getInstance().getFirstOpen()) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_frame);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);

        mDrawerHeader = (RelativeLayout) mDrawer.getHeaderView(0);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragMain = new FragmentTabMain();
        adapter.addFragment(fragMain, getString(com.guanmu.onehours.R.string.tab_title_main));
        fragList = new FragmentTabList();
        adapter.addFragment(fragList, getString(com.guanmu.onehours.R.string.tab_title_list));
        viewPager.setAdapter(adapter);
    }

    void tryLogin() {
        if (false) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
    }

    void tryLogout() {
        Snackbar.make(mCoordinator, R.string.tip_logout_success, Snackbar.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.guanmu.onehours.R.menu.activity_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Intent i = new Intent();
        switch (item.getItemId()) {
            case R.id.action_about:

                i.setClass(HomeActivity.this, AboutActivity.class);
                startActivity(i);
                return true;
//            case R.id.action_login:
//
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Snackbar.make(mCoordinator, R.string.tip_save_success, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
//        menu.findItem(R.id.action_login)
//                .setTitle(AVUser.getCurrentUser() == null ? R.string.login : R.string.action_logout);
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);

        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
