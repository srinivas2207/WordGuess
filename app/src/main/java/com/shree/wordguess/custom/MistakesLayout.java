package com.shree.wordguess.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shree.wordguess.R;

public class MistakesLayout extends LinearLayout {
	private ProgressBar progressBar;
	private TextView progressNumber;

	public MistakesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		_init();
	}

	public MistakesLayout(Context context) {
		super(context, null);
		_init();
	}

	public MistakesLayout(Context context, AttributeSet attrs, int defStyle) {
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
