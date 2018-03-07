package com.shree.wordguess.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shree.wordguess.R;
import com.shree.wordguess.activity.ParentActivity;
import com.shree.wordguess.custom.SearchListDialog;
import com.shree.wordguess.custom.SpinnerButton;
import com.shree.wordguess.util.AppData;
import com.shree.wordguess.util.ApplicationConstants;
import com.shree.wordguess.util.DatabaseUtil;
import com.shree.wordguess.util.JsonConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Home screen to show logo and form
 */
public class HomeFragment extends Fragment implements FragmentInterface {

    private String langCode = null;
    private int categoryId = 0;
    private int gameType = 1;

    private View content_layout;

    private SpinnerButton typeSpinner;
    private SpinnerButton langSpinner;
    private SpinnerButton categorySpinner;
    private Button playButton;

    private List<AppData.Language> langList = null;
    private List<AppData.Category> categoryList = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        content_layout = (View) inflater.inflate(
                R.layout.fragment_home, container, false);
        initializeViews();
        loadData();
        initAd();
        return content_layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ParentActivity)getActivity()).configureToolbar( getResources().getString(R.string.app_name), false);
    }

    @Override
    public void initializeViews() {
        langSpinner = content_layout.findViewById(R.id.langSpinner);
        categorySpinner = content_layout.findViewById(R.id.categorySpinner);
        typeSpinner = content_layout.findViewById(R.id.typeSpinner);

        langSpinner.setTitle(getResources().getString(R.string.language));
        categorySpinner.setTitle(getResources().getString(R.string.category));
        typeSpinner.setTitle(getResources().getString(R.string.gameType));

        playButton = content_layout.findViewById(R.id.play);
        playButton.setEnabled(false);
        initializeListeners();
    }


    @Override
    public void initializeListeners() {
        langSpinner.setOnClickListener(languageSpinnerClickListener);
        categorySpinner.setOnClickListener(categorySpinnerClickListener);
        playButton.setOnClickListener(playButtonClickListener);
        typeSpinner.setOnClickListener(typeClickListener);
    }


    public void updateUI() {
        String language = getLanguage(langCode);
        String category = getCategory(categoryId);
        if (category == null) {
            categoryId = 0;
            category = getCategory(categoryId);
        }

        if (category != null) {
            categorySpinner.setContent(category);
        }

        if (gameType == 1) {
            typeSpinner.setContent(getResources().getString(R.string.vocabulary));
        } else if (gameType == 2) {
            typeSpinner.setContent(getResources().getString(R.string.spellBee));
        }

        if (language != null) {
            langSpinner.setContent(language);
            playButton.setEnabled(true);
        } else {
            playButton.setEnabled(false);
            langSpinner.setContent(getResources().getString(R.string.selectFromList));
        }
    }


    private String getLanguage(String langCode) {
        if (langCode == null || langList == null) {
            return  null;
        }
        for (AppData.Language language : langList ){
            if (language.getCode().equalsIgnoreCase(langCode)) {
                return language.getName();
            }
        }
        return null;
    }

    private String getCategory(int id) {
        if (categoryList == null) {
            return  null;
        }
        for (AppData.Category category : categoryList ){
            if (category.getId() == id) {
                return category.getName();
            }
        }
        return null;
    }

    @Override
    public void loadData() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                AppData appData = DatabaseUtil.getInstance().getAppData();
                langList = appData.getLanguages();
                categoryList = appData.getCategories();
                JSONObject homeInfo  = DatabaseUtil.getInstance().getHomePageInfo();
                if (homeInfo != null) {
                    try {
                        if (homeInfo.has(JsonConstants.TYPE)) {
                            gameType = homeInfo.getInt(JsonConstants.TYPE);
                        }

                        if (homeInfo.has(JsonConstants.ID)) {
                            categoryId = homeInfo.getInt(JsonConstants.ID);
                        }

                        if (homeInfo.has(JsonConstants.CODE)) {
                            langCode = homeInfo.getString(JsonConstants.CODE);
                        }
                    } catch (JSONException jse) {
                        jse.printStackTrace();
                    }
                }
                return null;
            }

            protected void onPostExecute(Boolean result) {
                updateUI();
            }

        }.execute(null, null, null);
    }


    @Override
    public void onUiNotification(int type, String data) {
        if (type == ApplicationConstants.APP_DATA_UPDATE_NOTIFICATION) {
            loadData();
        }
    }


    View.OnClickListener playButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PlayFragment playFragment = new PlayFragment();
            playFragment.setCategory(categoryId);
            playFragment.setLangCode(langCode);
            playFragment.setGameType(gameType);
            ((ParentActivity)getActivity()).launchFragment(playFragment, false, true);
            DatabaseUtil.getInstance().updateHomePageInfo(gameType, langCode, categoryId);
        }
    };


    View.OnClickListener languageSpinnerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SearchListDialog dialog = new SearchListDialog(getActivity(), SearchListDialog.LANGUAGE_DATA)
                    .setListData(langList)
                    .setTitle(getResources().getString(R.string.language))
                    .setDialogListener(dialogListener)
                    .build();
            dialog.show();
        }
    };

    View.OnClickListener typeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu popup = new PopupMenu(getActivity(), typeSpinner);
            popup.getMenuInflater()
                    .inflate(R.menu.menu_type, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                   if(item.getItemId() == R.id.spellBee) {
                       gameType = 2;
                   } else {
                       gameType = 1;
                   }
                   updateUI();
                    return true;
                }
            });
            popup.show();
        }
    };

    View.OnClickListener categorySpinnerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SearchListDialog dialog = new SearchListDialog(getActivity(), SearchListDialog.CATEGORY_DATA)
                    .setListData(categoryList)
                    .setTitle(getResources().getString(R.string.category))
                    .setDialogListener(dialogListener)
                    .build();
            dialog.show();
        }
    };


    SearchListDialog.DialogListener dialogListener = new SearchListDialog.DialogListener() {
        @Override
        public void onListItemSelection(int dialogType, Object data) {
            if (dialogType == SearchListDialog.LANGUAGE_DATA ) {
                langCode = ((AppData.Language)data).getCode();
                ((ParentActivity)getActivity()).addAnalyticData(ApplicationConstants.LANGUAGE_UP, langCode);
            }

            if (dialogType == SearchListDialog.CATEGORY_DATA ) {
                categoryId = ((AppData.Category)data).getId();
            }
            updateUI();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initAd() {
        System.out.println("AD_INTERVAL " + ApplicationConstants.AD_INTERVAL);
        AdView bannerAd = content_layout.findViewById(R.id.bannerAd);
        if (ApplicationConstants.AD_INTERVAL > 20) {
            bannerAd.setVisibility(View.GONE);
            return;
        }

        bannerAd.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("33BE2250B43518CCDA7DE426D04EE231")
                .build();

        bannerAd.loadAd(adRequest);
    }

}
