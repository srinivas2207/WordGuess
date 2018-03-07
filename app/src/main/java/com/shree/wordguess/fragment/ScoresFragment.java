package com.shree.wordguess.fragment;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.shree.wordguess.R;
import com.shree.wordguess.activity.ParentActivity;
import com.shree.wordguess.adapter.FavouriteAdapter;
import com.shree.wordguess.adapter.RecyclerTouchListener;
import com.shree.wordguess.adapter.ScoresAdapter;
import com.shree.wordguess.util.DatabaseUtil;
import com.shree.wordguess.util.JsonConstants;
import com.shree.wordguess.util.Utils;
import com.shree.wordguess.util.WordData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Scores fragment to show score information
 */
public class ScoresFragment extends Fragment implements FragmentInterface {

    private View content_layout;
    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ScoresAdapter scoresAdapter;
    private ProgressBar progressBar;
    private JSONObject scoresData;
    private JSONArray scoreList;
    private boolean isVocabBee = true;
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

    }

    @Override
    public void initializeListeners() {

    }

    @Override
    public void loadData() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                scoresData = DatabaseUtil.getInstance().getScores();
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
        ((ParentActivity)getActivity()).configureToolbar( getResources().getString(R.string.scores), true);
        loadData();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        scoresAdapter = null;
    }


    public void updateUi() {
        if (progressBar == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);

        scoreList = null;
        try {
            if (isVocabBee) {
                scoreList = scoresData.getJSONArray(JsonConstants.VOCAB_SCORES);
            } else {
                scoreList = scoresData.getJSONArray(JsonConstants.SPELL_SCORES);
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }

        scoreList = sortScores();

        if (scoresAdapter == null) {
            scoresAdapter = new ScoresAdapter(getActivity(), scoreList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(scoresAdapter);
        } else {
            scoresAdapter.setData(scoreList);
        }


        if (scoreList == null || scoreList.length() == 0) {
            message.setVisibility(View.VISIBLE);
            message.setText(getResources().getString(R.string.noScores));
        }
    }


    @Override
    public void onUiNotification(int type, String data) {

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scores, menu);

        MenuItem item = menu.findItem(R.id.gameType);
        if (isVocabBee) {
            item.setTitle(getResources().getString(R.string.vocabulary));
        } else {
            item.setTitle(getResources().getString(R.string.spellBee));
        }
    }

    public void changeGame(boolean isVocabBee) {
        if (this.isVocabBee == isVocabBee) {
            return;
        }
        this.isVocabBee = isVocabBee;
        getActivity().invalidateOptionsMenu();
        updateUi();
    }

    private JSONArray sortScores() {
        return Utils.sortScores(scoreList);
    }


}