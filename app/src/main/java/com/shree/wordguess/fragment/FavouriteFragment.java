package com.shree.wordguess.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shree.wordguess.R;
import com.shree.wordguess.activity.ParentActivity;
import com.shree.wordguess.adapter.FavouriteAdapter;
import com.shree.wordguess.adapter.RecyclerTouchListener;
import com.shree.wordguess.util.DatabaseUtil;
import com.shree.wordguess.util.WordData;

import java.util.List;

/**
 * Fragment to show favourite words
 */
public class FavouriteFragment extends Fragment implements FragmentInterface {

    private View content_layout;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FavouriteAdapter favouriteAdapter;
    private ProgressBar progressBar;
    private List<WordData.Word> favData;
    private int selectedIndex = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        content_layout = (View) inflater.inflate(
                R.layout.recycle_list, container,false);
        initializeViews();
        return content_layout;
    }


    public void initializeViews() {
        swipeRefreshLayout = (SwipeRefreshLayout)content_layout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);

        message = (TextView)content_layout.findViewById(R.id.message);
        progressBar = (ProgressBar)content_layout.findViewById(R.id.loadingBar);

        recyclerView = (RecyclerView) content_layout.findViewById(R.id.recycler_view);
        content_layout.setBackgroundColor(getResources().getColor(R.color.favMsgListBgColorDefault));

        recyclerTouchListener.setRecyclerView(recyclerView);
        recyclerView.addOnItemTouchListener(recyclerTouchListener);
    }

    @Override
    public void initializeListeners() {

    }

    @Override
    public void loadData() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                favData = DatabaseUtil.getInstance().getFavouriteList();
                return null;
            }

            protected void onPostExecute(Boolean result) {
                updateUi();
            }

        }.execute(null, null, null);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        ((ParentActivity)getActivity()).configureToolbar( getResources().getString(R.string.favourites), true);
        loadData();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        favouriteAdapter = null;
    }


    public void updateUi() {
        if (progressBar == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);

        recyclerView.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);

        if (favouriteAdapter == null) {
            favouriteAdapter = new FavouriteAdapter(getActivity(), favData);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(favouriteAdapter);
        } else {
            favouriteAdapter.setData(favData);
        }

        favouriteAdapter.setSelectedIndex(selectedIndex);

        if (favData == null || favData.size() == 0) {
            message.setVisibility(View.VISIBLE);
            message.setText(getResources().getString(R.string.noFavouriteWords));
        }
    }


    @Override
    public void onUiNotification(int type, String data) {

    }

    RecyclerTouchListener recyclerTouchListener =new RecyclerTouchListener(getContext(),  new RecyclerTouchListener.ClickListener() {
        @Override
        public void onClick(View view, int position) {
        }

        @Override
        public void onLongClick(View view, int position) {
            try {
                selectedIndex = position;
                favouriteAdapter.setSelectedIndex(selectedIndex);
                getActivity().invalidateOptionsMenu();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fav, menu);

        MenuItem delMenu = menu.findItem(R.id.delete);
        MenuItem shareMenu = menu.findItem(R.id.share);

        if (selectedIndex != -1) {
            delMenu.setVisible(true);
            shareMenu.setVisible(true);
        } else {
            delMenu.setVisible(false);
            shareMenu.setVisible(false);
        }
    }


    public void share() {
        if (selectedIndex == -1 || selectedIndex >= favData.size()) {
            return;
        }

        WordData.Word word = favData.get(selectedIndex);

        String message = "WordGuess word of the day\n\n";
        message += "Word : " + word.getName();
        if (word.getDesc() != null) {
            message += "\n" + "Description : " + word.getDesc();
        }

        if (word.getTranslatedValue() != null) {
            message += "\n" + "Translation : " + word.getTranslatedValue();
        }

        message += "\n\n" + "https://play.google.com/store/apps/details?id=com.shree.mychat";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share this word with your friends !"));
    }

    public void delete() {
        if (selectedIndex == -1 || selectedIndex >= favData.size()) {
            return;
        }

        final WordData.Word word = favData.get(selectedIndex);
        favData.remove(selectedIndex);
        selectedIndex = -1;
        updateUi();
        getActivity().invalidateOptionsMenu();

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                if (word != null) {
                    word.setFavourite(false);
                    DatabaseUtil.getInstance().updateWord(word);
                }
                return null;
            }

            protected void onPostExecute(Boolean result) {
            }

        }.execute(null, null, null);
    }

    public void handleBackPress() {
        selectedIndex = -1;
        updateUi();
        getActivity().invalidateOptionsMenu();
    }

    public boolean isWordSelected() {
        return selectedIndex != -1;
    }
}