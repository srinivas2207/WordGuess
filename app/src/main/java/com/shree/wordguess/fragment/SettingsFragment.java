package com.shree.wordguess.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.shree.wordguess.R;
import com.shree.wordguess.activity.ParentActivity;
import com.shree.wordguess.util.ApplicationConstants;
import com.shree.wordguess.util.Utils;

public class SettingsFragment extends Fragment implements FragmentInterface{

	public View content_layout;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		content_layout = (View) inflater.inflate(R.layout.settings_fragment,
				container, false);

		initializeViews();
		return content_layout;
	}

	@Override
	public void initializeViews() {
		RadioButton blueTheme = (RadioButton)content_layout.findViewById(R.id.blueTheme);
		RadioButton purpleTheme = (RadioButton)content_layout.findViewById(R.id.purpleTheme);
		RadioButton redTheme = (RadioButton)content_layout.findViewById(R.id.redTheme);
		RadioButton greenTheme = (RadioButton)content_layout.findViewById(R.id.greenTheme);

		blueTheme.setOnClickListener(onClickListener);
		greenTheme.setOnClickListener(onClickListener);
		purpleTheme.setOnClickListener(onClickListener);
		redTheme.setOnClickListener(onClickListener);

		switch (Utils.currentTheme) {
			case ApplicationConstants.BLUE_THEME:
				blueTheme.setChecked(true);
				break;
			case ApplicationConstants.GREEN_THEME:
				greenTheme.setChecked(true);
				break;
			case ApplicationConstants.PURPLE_THEME:
				purpleTheme.setChecked(true);
				break;
			case ApplicationConstants.RED_THEME:
				redTheme.setChecked(true);
				break;
			default:
				break;
		}
	}

	public void changeTheTheme(View v) {
		switch (v.getId()) {
			case R.id.blueTheme:
				Utils.changeToTheme(getActivity(), ApplicationConstants.BLUE_THEME);
				break;
			case R.id.purpleTheme:
				Utils.changeToTheme(getActivity(), ApplicationConstants.PURPLE_THEME);
				break;
			case R.id.greenTheme:
				Utils.changeToTheme(getActivity(), ApplicationConstants.GREEN_THEME);
				break;
			case R.id.redTheme:
				Utils.changeToTheme(getActivity(), ApplicationConstants.RED_THEME);
				break;
			default:
				break;
		}
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			changeTheTheme(v);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		((ParentActivity)getActivity()).configureToolbar(getResources().getString(R.string.settings), true);
	}

	@Override
	public void onUiNotification(int type, String data) {

	}

	@Override
	public void initializeListeners() {

	}

	@Override
	public void loadData() {

	}
}