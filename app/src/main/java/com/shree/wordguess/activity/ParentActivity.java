package com.shree.wordguess.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.shree.wordguess.BuildConfig;
import com.shree.wordguess.R;
import com.shree.wordguess.fragment.HomeFragment;
import com.shree.wordguess.network.NetworkOperations;
import com.shree.wordguess.util.ApplicationConstants;
import com.shree.wordguess.util.DatabaseUtil;
import com.shree.wordguess.util.Utils;
import com.shree.wordguess.network.UINotificationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public abstract class ParentActivity extends AppCompatActivity implements UINotificationListener {

	public View coordinatorLayout = null;
	public UiNotificationReceiver uiNotificationReceiver = null;
	private FirebaseAnalytics mFirebaseAnalytics;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		if (!DatabaseUtil.getInstance().getDemoStatus()) {
			finish();
			Intent intent = new Intent(this, DemoActivity.class);
			startActivity(intent);
			return;
		}

		Utils.onActivityCreateSetTheme(this);

		// Obtain the FirebaseAnalytics instance.
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		MobileAds.initialize(this, "YOUR_ADMOB_APP_ID");
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiNotificationReceiver = new UiNotificationReceiver();
		IntentFilter filter = new IntentFilter(ApplicationConstants.UI_NOTIFIER);
		registerReceiver(uiNotificationReceiver, filter);
	}

	@Override
	protected void onPause() {
		if(uiNotificationReceiver!=null) {
			unregisterReceiver(uiNotificationReceiver);
			uiNotificationReceiver = null;
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NetworkOperations.getInstance().clear();
	}

	/**
	 * Activity rest api callback receiver for notifying components about the rest call response
	 */
	private class UiNotificationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type = intent.getIntExtra(ApplicationConstants.NOTIFICATION_TYPE, 0);
			String data = intent.getStringExtra(ApplicationConstants.NOTIFICATION_DATA);

			if (!(ParentActivity.this instanceof HomeActivity)) {
				return;
			}

			Fragment fragment = getActivieFragment();
			if (type == ApplicationConstants.APP_DATA_UPDATE_NOTIFICATION ) {
				if (fragment instanceof HomeFragment) {
					onUiNotification(type, data);
					return;
				} else {
					return;
				}
			}
			onUiNotification(type, data);
		}
	}

	public Fragment getActivieFragment(){
		FragmentManager fragmentManager = getSupportFragmentManager();
		List<Fragment> fragments = fragmentManager.getFragments();
		if(fragments != null){
			for(Fragment fragment : fragments){
				if(fragment != null && fragment.isVisible())
					return fragment;
			}
		}

//		try {
//			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//				return null;
//			}
//			String tag = getSupportFragmentManager().getBackStackEntryAt(
//					getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
//			return getSupportFragmentManager().findFragmentByTag(tag);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return  null;
	}

	/**
	 * Launching fragment from activity or fragment <br>
	 * It uses R.id.fragmentContainer as the holder.
	 * @param fragment Fragment to be added
	 * @param addFragment Is fragment to be added/replaced
	 * @param addtoBackStack Add previous fragment to backstack
	 */
	public void launchFragment(final Fragment fragment, boolean addFragment, boolean addtoBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		if (addFragment) {
			ft.add(R.id.fragmentContainer, fragment, fragment.getClass().getName());
		} else {
			ft.replace(R.id.fragmentContainer, fragment, fragment.getClass().getName());
		}

		if(addtoBackStack) {
			ft.addToBackStack(fragment.getClass().getName());
		}
		ft.commit();
	}

	public void configureToolbar(String title, boolean showBackButton) {
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
			getSupportActionBar().setTitle(title);
		}
	}

	public void showSnackBarMessage(boolean autoCancel, String message, String button, final int type) {
		if (autoCancel) {
			Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
					.setAction(button, new View.OnClickListener() {
						@Override
						public void onClick(View view) {
						}
					})
					.show();
		} else {
			Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
					.setAction(button, new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							if (type == ApplicationConstants.PERMISSIONS_MSG) {
								startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
										Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
							}
						}
					})
					.show();
		}
	}

	public void showToast(String message, int toastType) {
		Toast.makeText(this, message, toastType).show();
	}

	public void requestAppPermissions(List<String> permissions, int code) {
		String[] permissionArr = new String[permissions.size()];
		permissionArr= permissions.toArray(permissionArr);
		ActivityCompat.requestPermissions(this, permissionArr , code);
	}

	public boolean isPermissionGranted(String permission) {
		if (!isUserPermissionsRequired()) {
			return false;
		}
		return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getApplicationContext(), permission);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		JSONObject data = new JSONObject();
		try {
			JSONArray perm = new JSONArray();
			for(String permission : permissions) {
				perm.put(permission);
			}

			JSONArray grant = new JSONArray();
			for(int grantRes : grantResults) {
				grant.put(grantRes);
			}

			data.put(ApplicationConstants.PERMISSION_CODE, requestCode);
			data.put(ApplicationConstants.REQUESTED_PERMISSIONS, perm);
			data.put(ApplicationConstants.GRANTED_PERMISSIONS,grant);
		}catch (JSONException je) {
			je.printStackTrace();
		}
		onUiNotification(ApplicationConstants.APP_PERMISSION_NOTIFICATION, data.toString());
	}

	private boolean isUserPermissionsRequired() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return true;
		}
		return false;
	}

	public void testCrash() {
		Crashlytics.getInstance().crash(); // Force a crash
	}

	public void addAnalyticData(String key, String value) {
		mFirebaseAnalytics.setUserProperty(key, value);
	}

}