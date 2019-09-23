package com.guanmu.onehours.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.guanmu.onehours.R
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class IntroActivity : BaseActivity(), View.OnClickListener {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private var mViewpager: ViewPager? = null
    private var mPagerAdapter: ViewPagerAdapter? = null
    private val mPagesContainer = ArrayList<View>()
    private var mBefore: ImageButton? = null
    private var mNext: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.intro_activity)

        initData()
        mViewpager = findViewById<View>(R.id.fragment_frame) as ViewPager
        mPagerAdapter = ViewPagerAdapter()
        mViewpager!!.adapter = mPagerAdapter

        mBefore = findViewById<View>(R.id.before) as ImageButton
        mNext = findViewById<View>(R.id.next) as ImageButton
        mBefore!!.setOnClickListener(this)
        mNext!!.setOnClickListener(this)
    }

    private fun initData() {
        val inflater = LayoutInflater.from(this)
        val introViewHolders = arrayOf(IntroViewHolder(R.string.title_intro_0, 0, R.drawable.intro_0_1, R.drawable.intro_0_2), IntroViewHolder(R.string.title_intro_1, R.string.subtitle_intro_1, R.drawable.intro_1), IntroViewHolder(R.string.title_intro_2, R.string.subtitle_intro_2, R.drawable.intro_2), IntroViewHolder(R.string.title_intro_3, R.string.subtitle_intro_3, R.drawable.intro_3), IntroViewHolder(R.string.title_intro_4, R.string.subtitle_intro_4, R.drawable.intro_4_1, R.drawable.intro_4_2))
        for (introViewHolder in introViewHolders) {
            val introView = inflater.inflate(R.layout.view_intro, null)
            val title = introView.findViewById<View>(R.id.title) as TextView
            val subtitle = introView.findViewById<View>(R.id.subtitle) as TextView
            val image1 = introView.findViewById<View>(R.id.image1) as ImageView
            val image2 = introView.findViewById<View>(R.id.image2) as ImageView

            title.setText(introViewHolder.title)
            if (introViewHolder.subtitle != 0) {
                subtitle.setText(introViewHolder.subtitle)
            }
            if (introViewHolder.imageResource1 != 0) {
                image1.setImageResource(introViewHolder.imageResource1)
            }
            if (introViewHolder.imageResource2 != 0) {
                introView.findViewById<View>(R.id.card2).visibility = View.VISIBLE
                image2.setImageResource(introViewHolder.imageResource2)
            }
            mPagesContainer.add(introView)
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.next -> {
                setClickable(mBefore!!, true)
                if (mViewpager!!.currentItem == mPagerAdapter!!.count - 1) {
                    //                    if (AVUser.getCurrentUser() == null) {
                    //                        startActivity(new Intent(this, LoginActivity.class));
                    //                    } else {
                    startActivity(Intent(this, HomeActivity::class.java))
                    //                    }
                    finish()
                } else {
                    mViewpager!!.arrowScroll(View.FOCUS_FORWARD)
                }
            }
            R.id.before -> {
                setClickable(mNext!!, true)
                if (mViewpager!!.currentItem == 0) {
                    setClickable(mBefore!!, false)
                }
                mViewpager!!.arrowScroll(View.FOCUS_BACKWARD)
            }
        }

    }

    private fun setClickable(button: ImageButton, clickable: Boolean) {
        button.isClickable = clickable
        button.alpha = if (clickable) 0.5.toFloat() else 0.2.toFloat()
    }

    internal inner class ViewPagerAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val view = mPagesContainer[position]
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(mPagesContainer[position])
        }

        override fun getCount(): Int {
            return mPagesContainer.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    internal inner class IntroViewHolder {
        @StringRes
        @get:StringRes
        var title: Int = 0
        @StringRes
        @get:StringRes
        var subtitle: Int = 0
        @DrawableRes
        @get:DrawableRes
        var imageResource1: Int = 0
        @DrawableRes
        @get:DrawableRes
        var imageResource2: Int = 0

        constructor(@StringRes title: Int, @StringRes subtitle: Int,
                    @DrawableRes imageResource1: Int, @DrawableRes imageResource2: Int) {
            this.title = title
            this.subtitle = subtitle
            this.imageResource1 = imageResource1
            this.imageResource2 = imageResource2
        }

        constructor(@StringRes title: Int, @StringRes subtitle: Int,
                    @DrawableRes imageResource1: Int) {
            this.title = title
            this.subtitle = subtitle
            this.imageResource1 = imageResource1
            this.imageResource2 = 0
        }

    }

}
