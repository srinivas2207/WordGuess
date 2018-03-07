package com.shree.wordguess.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.shree.wordguess.R;
import com.shree.wordguess.adapter.DemoPageAdapter;
import com.shree.wordguess.util.DatabaseUtil;

/**
 * This activity is for showing intro of the application, on first launch.
 */
public class DemoActivity extends AppCompatActivity {

    private DemoPageAdapter mAdapter;
    private Button skipBtn = null;
    private Button doneBtn = null;

    // Demo screenshots
    private int[] mResources = {
            R.drawable.demo_1,
            R.drawable.demo_2,
            R.drawable.demo_3,
            R.drawable.demo_4,
            R.drawable.demo_5,
            R.drawable.demo_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        initializeViews();
    }

    private void initializeViews() {
        skipBtn = findViewById(R.id.skip);
        doneBtn = findViewById(R.id.done);

        doneBtn.setVisibility(View.GONE);
        skipBtn.setVisibility(View.VISIBLE);

        ViewPager mImageViewPager =  findViewById(R.id.demoPager);
        TabLayout tabLayout =  findViewById(R.id.tabDots);

        mAdapter = new DemoPageAdapter(this, mResources);
        mImageViewPager.setAdapter(mAdapter);


        tabLayout.setupWithViewPager(mImageViewPager);

        mImageViewPager.setCurrentItem(0);
        mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == mResources.length - 1) {
                    doneBtn.setVisibility(View.VISIBLE);
                    skipBtn.setVisibility(View.GONE);
                } else {
                    doneBtn.setVisibility(View.GONE);
                    skipBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDemo();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDemo();
            }
        });

    }

    private void finishDemo() {
        DatabaseUtil.getInstance().setDemoStatus(true);
        launchHomeActivity();
    }

    private void launchHomeActivity() {
        finish();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
