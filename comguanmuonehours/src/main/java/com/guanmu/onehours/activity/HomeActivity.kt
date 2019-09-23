package com.guanmu.onehours.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.guanmu.onehours.R
import com.guanmu.onehours.SharePreferenceUtils
import com.guanmu.onehours.fragment.FragmentTabList
import com.guanmu.onehours.fragment.FragmentTabMain
import java.util.*

class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var fragList: FragmentTabList
    private lateinit var fragMain: FragmentTabMain
    private var mCoordinator: CoordinatorLayout? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerHeader: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        initView()

        if (SharePreferenceUtils.instance.firstIntro) {
            startActivity(Intent(this, IntroActivity::class.java))
        }
    }


    private fun initView() {
        mCoordinator = findViewById<View>(R.id.root_coordinator) as CoordinatorLayout

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val mDrawer = findViewById<View>(R.id.main_drawer) as NavigationView
        mDrawer.setNavigationItemSelectedListener(this)


        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                val userText = mDrawerHeader!!.findViewById<View>(R.id.username) as TextView
                userText.text = getString(R.string.welcome)
            }
        }
        mDrawerLayout!!.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        // Show drawer at the first time
        if (SharePreferenceUtils.instance.firstOpen) {
            mDrawerLayout!!.openDrawer(GravityCompat.START)
        }

        val viewPager = findViewById<View>(R.id.fragment_frame) as ViewPager
        setupViewPager(viewPager)

        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        mDrawerHeader = mDrawer.getHeaderView(0) as RelativeLayout
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        fragMain = FragmentTabMain()
        adapter.addFragment(fragMain, getString(com.guanmu.onehours.R.string.tab_title_main))
        fragList = FragmentTabList()
        adapter.addFragment(fragList, getString(com.guanmu.onehours.R.string.tab_title_list))
        viewPager.adapter = adapter
    }

    internal fun tryLogin() {
        if (false) {
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        }
    }

    internal fun tryLogout() {
        Snackbar.make(mCoordinator!!, R.string.tip_logout_success, Snackbar.LENGTH_SHORT)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        val i = Intent()
        return when (item.itemId) {
            R.id.action_about -> {

                i.setClass(this@HomeActivity, AboutActivity::class.java)
                startActivity(i)
                true
            }
            //            case R.id.action_login:
            //
            //                return true;
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> Snackbar.make(mCoordinator!!, R.string.tip_save_success, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START))
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true

        return true
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) :
            FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}
