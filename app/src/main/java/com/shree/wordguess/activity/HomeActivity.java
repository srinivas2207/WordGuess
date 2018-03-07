package com.shree.wordguess.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.shree.wordguess.R;
import com.shree.wordguess.fragment.AboutFragment;
import com.shree.wordguess.fragment.FavouriteFragment;
import com.shree.wordguess.fragment.HomeFragment;
import com.shree.wordguess.fragment.PlayFragment;
import com.shree.wordguess.fragment.ScoresFragment;
import com.shree.wordguess.fragment.SettingsFragment;
import com.shree.wordguess.network.UINotificationListener;
import com.shree.wordguess.util.ApplicationConstants;

/**
 * Home activity of the application<br/>
 * It's the placeholder for all the screens(fragments)
 */
public class HomeActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initializing the coordinatorLayout, which can be used by parent activity for app level snackbar purposes.
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Launching home fragment
        HomeFragment homeFragment = new HomeFragment();
        launchFragment(homeFragment, true, false);

        // Showing settings fragment, If the activity launched from Settings screen by changing theme
        boolean isFromThemeChange = getIntent().getBooleanExtra(ApplicationConstants.IS_THEME_CHANGED, false);
        if (isFromThemeChange) {
            SettingsFragment settingsFragment = new SettingsFragment();
            launchFragment(settingsFragment, false, true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = getActiveFragment();

        switch (id) {
            case R.id.themes:
                SettingsFragment settingsFragment = new SettingsFragment();
                launchFragment(settingsFragment, false, true);
                break;

            case R.id.favourites:
                FavouriteFragment favouriteFragment = new FavouriteFragment();
                launchFragment(favouriteFragment, false, true);
                break;

            case R.id.scores:
                ScoresFragment scoresFragment = new ScoresFragment();
                launchFragment(scoresFragment, false, true);
                break;

            case R.id.vocabBee:
            case R.id.spellBee:
               if(fragment instanceof ScoresFragment) {
                   ((ScoresFragment) fragment).changeGame(id == R.id.vocabBee);
               }
                break;

            case R.id.about:
                AboutFragment aboutFragment = new AboutFragment();
                launchFragment(aboutFragment, false, true);
                break;

            case R.id.hint:
                ((PlayFragment)fragment).showHint();
                break;

            case R.id.favourite:
                ((PlayFragment)fragment).favourite();
                break;

            case R.id.reveal:
                ((PlayFragment)fragment).reveal();
                break;

            case R.id.refresh:
                ((PlayFragment)fragment).refresh();
                break;

            case R.id.share:
                if (fragment instanceof PlayFragment) {
                    ((PlayFragment)fragment).share();
                } else {
                    ((FavouriteFragment)fragment).share();
                }

                break;
            case R.id.delete:
                ((FavouriteFragment)fragment).delete();
                break;

            case android.R.id.home:
                if (fragment != null && fragment instanceof PlayFragment) {
                    if (((PlayFragment) fragment).canExitFromGame()) {
                        onBackPressed();
                    } else {
                        ((PlayFragment) fragment).showAppExitDialog();
                    }
                } else {
                    onBackPressed();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Object fragment = getActiveFragment();
        //If the current fragment is PlayFragment, handling backpress based on game state.
        if (fragment != null  && fragment instanceof PlayFragment) {
            if (!((PlayFragment) fragment).canExitFromGame()) {
                ((PlayFragment) fragment).showAppExitDialog();
                return;
            }
        }

        //If the current fragment is Favourites, handling backpress based on list item selection.
        if (fragment != null  && fragment instanceof FavouriteFragment) {
            if (((FavouriteFragment) fragment).isWordSelected()) {
                ((FavouriteFragment) fragment).handleBackPress();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onUiNotification(int type, String data) {
        UINotificationListener fragment = (UINotificationListener) getActiveFragment();
        fragment.onUiNotification(type, data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        Object fragment = getActiveFragment();
        if(fragment!=null && fragment instanceof  PlayFragment ) {
            ((PlayFragment)fragment).onWindowFocusChanged(hasFocus);
        }
    }
}
