package com.shree.wordguess.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.shree.wordguess.R;
import com.shree.wordguess.activity.ParentActivity;

/**
 * App's About screen
 */
public class AboutFragment extends Fragment implements FragmentInterface {

    private View content_layout;
    private WebView aboutWV;

    private String aboutHtml = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        content_layout = (View) inflater.inflate(
                R.layout.about_fragment, container, false);
        initializeViews();
        loadData();
        return content_layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ParentActivity)getActivity()).configureToolbar( getResources().getString(R.string.about), true);
    }

    @Override
    public void initializeViews() {
        aboutWV = content_layout.findViewById(R.id.aboutTV);

        Spanned htmlText;
        aboutHtml = getResources().getString(R.string.aboutContent);
        if (Build.VERSION.SDK_INT >= 24) {
            htmlText = Html.fromHtml(aboutHtml,Html.FROM_HTML_MODE_COMPACT );
        } else {
            htmlText =  Html.fromHtml(aboutHtml);
        }

        aboutWV.loadData(aboutHtml, "text/html; charset=utf-8", "UTF-8");
    }

    @Override
    public void initializeListeners() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void onUiNotification(int type, String data) {

    }
}
