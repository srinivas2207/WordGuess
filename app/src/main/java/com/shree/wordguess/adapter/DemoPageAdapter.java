package com.shree.wordguess.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shree.wordguess.R;
import com.shree.wordguess.custom.RoundedCornersTransformation;

/**
 * Adapter to display demo screenshots used by view pager
 */

public class DemoPageAdapter extends  PagerAdapter {
    public static int sCorner = 30;
    public static int sMargin = 5;
    public static int sBorder = 5;
    public static String sColor = "#a9a9a9";


     private Context mContext;
    private int[] mResources = null;

        public DemoPageAdapter(Context mContext, int[] mResources) {
            this.mContext = mContext;
            this.mResources = mResources;
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.demo_pager_item, container, false);


            ImageView imageView = itemView.findViewById(R.id.img_pager_item);

            Glide.with(mContext).load(mResources[position])
                    .apply(RequestOptions.bitmapTransform(
                            new RoundedCornersTransformation(mContext, sCorner, sMargin, sColor, sBorder))).into(imageView);

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
}