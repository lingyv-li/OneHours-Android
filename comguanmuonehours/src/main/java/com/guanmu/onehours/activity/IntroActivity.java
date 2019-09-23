package com.guanmu.onehours.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.guanmu.onehours.R;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroActivity extends BaseActivity implements View.OnClickListener {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private ViewPager mViewpager;
    private ViewPagerAdapter mPagerAdapter;
    private List<View> mPagesContainer = new ArrayList<>();
    private ImageButton mBefore, mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_activity);

        initData();
        mViewpager = (ViewPager) findViewById(R.id.fragment_frame);
        mPagerAdapter = new ViewPagerAdapter();
        mViewpager.setAdapter(mPagerAdapter);

        mBefore = (ImageButton) findViewById(R.id.before);
        mNext = (ImageButton) findViewById(R.id.next);
        mBefore.setOnClickListener(this);
        mNext.setOnClickListener(this);
    }

    private void initData() {
        LayoutInflater inflater = LayoutInflater.from(this);
        IntroViewHolder[] introViewHolders = new IntroViewHolder[]{
                new IntroViewHolder(R.string.title_intro_0, 0, R.drawable.intro_0_1, R.drawable.intro_0_2),
                new IntroViewHolder(R.string.title_intro_1, R.string.subtitle_intro_1, R.drawable.intro_1),
                new IntroViewHolder(R.string.title_intro_2, R.string.subtitle_intro_2, R.drawable.intro_2),
                new IntroViewHolder(R.string.title_intro_3, R.string.subtitle_intro_3, R.drawable.intro_3),
                new IntroViewHolder(R.string.title_intro_4, R.string.subtitle_intro_4, R.drawable.intro_4_1, R.drawable.intro_4_2)};
        for (IntroViewHolder introViewHolder :
                introViewHolders) {
            View introView = inflater.inflate(R.layout.view_intro, null);
            TextView title = (TextView) introView.findViewById(R.id.title);
            TextView subtitle = (TextView) introView.findViewById(R.id.subtitle);
            ImageView image1 = (ImageView) introView.findViewById(R.id.image1);
            ImageView image2 = (ImageView) introView.findViewById(R.id.image2);

            title.setText(introViewHolder.getTitle());
            if (introViewHolder.getSubtitle() != 0) {
                subtitle.setText(introViewHolder.getSubtitle());
            }
            if (introViewHolder.getImageResource1() != 0) {
                image1.setImageResource(introViewHolder.getImageResource1());
            }
            if (introViewHolder.getImageResource2() != 0) {
                introView.findViewById(R.id.card2).setVisibility(View.VISIBLE);
                image2.setImageResource(introViewHolder.getImageResource2());
            }
            mPagesContainer.add(introView);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                setClickable(mBefore, true);
                if (mViewpager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
//                    if (AVUser.getCurrentUser() == null) {
//                        startActivity(new Intent(this, LoginActivity.class));
//                    } else {
                        startActivity(new Intent(this, HomeActivity.class));
//                    }
                    finish();
                } else {
                    mViewpager.arrowScroll(View.FOCUS_FORWARD);
                }
                break;
            case R.id.before:
                setClickable(mNext, true);
                if (mViewpager.getCurrentItem() == 0) {
                    setClickable(mBefore, false);
                }
                mViewpager.arrowScroll(View.FOCUS_BACKWARD);
                break;
        }

    }

    void setClickable(ImageButton button, boolean clickable) {
        button.setClickable(clickable);
        button.setAlpha(clickable ? (float) 0.5 : (float) 0.2);
    }

    class ViewPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mPagesContainer.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mPagesContainer.get(position));
        }

        @Override
        public int getCount() {
            return mPagesContainer.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    class IntroViewHolder {
        @StringRes
        int title, subtitle;
        @DrawableRes
        int imageResource1, imageResource2;

        IntroViewHolder(@StringRes int title, @StringRes int subtitle,
                        @DrawableRes int imageResource1, @DrawableRes int imageResource2) {
            this.title = title;
            this.subtitle = subtitle;
            this.imageResource1 = imageResource1;
            this.imageResource2 = imageResource2;
        }

        IntroViewHolder(@StringRes int title, @StringRes int subtitle,
                        @DrawableRes int imageResource1) {
            this.title = title;
            this.subtitle = subtitle;
            this.imageResource1 = imageResource1;
            this.imageResource2 = 0;
        }

        public
        @StringRes
        int getTitle() {
            return title;
        }

        public
        @StringRes
        int getSubtitle() {
            return subtitle;
        }

        public
        @DrawableRes
        int getImageResource1() {
            return imageResource1;
        }

        public
        @DrawableRes
        int getImageResource2() {
            return imageResource2;
        }

    }

}
