package com.shree.wordguess.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shree.wordguess.R;

/**
 * Custom progressbar with Rounded circle and percentage inside
 */
public class CustomProgressBar extends LinearLayout {
	private ProgressBar progressBar;
	private TextView progressNumber;

	public CustomProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomProgressBar(Context context) {
		this(context, null);
	}

	public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_init();
	}

	private void _init() {
		setOrientation(LinearLayout.HORIZONTAL);
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.custom_progress, this, true);

		View container = getChildAt(0);

		progressBar =  container.findViewById(R.id.progress);
		progressNumber = container.findViewById(R.id.number);
	}

	public void setProgress(int number) {
		progressNumber.setText("" + number);
		progressBar.setProgress(number);
	}

}
