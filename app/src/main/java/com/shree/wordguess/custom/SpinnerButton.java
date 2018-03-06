package com.shree.wordguess.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shree.wordguess.R;

/**
 * Created by SrinivasDonapati on 1/23/2018.
 */

public class SpinnerButton extends LinearLayout {
    private TextView titleTV;
    private TextView contentTV;

    public SpinnerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    public SpinnerButton(Context context) {
        super(context, null);
        _init();
    }

    public SpinnerButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init();
    }

    private void _init() {
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.spinner_button, this, true);

        View container = getChildAt(0);

        titleTV =  container.findViewById(R.id.title);
        contentTV = container.findViewById(R.id.content);
    }
    public void setTitle(String title) {
        titleTV.setText(title);
    }

    public void setContent(String content) {
        contentTV.setText(content);
    }

}
