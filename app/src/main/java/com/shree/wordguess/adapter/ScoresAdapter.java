package com.shree.wordguess.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shree.wordguess.R;
import com.shree.wordguess.util.AppData;
import com.shree.wordguess.util.DatabaseUtil;
import com.shree.wordguess.util.JsonConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Adapter to display List of scores/leader board of the user
 */
public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.MyViewHolder> {

    private JSONArray scoreList = null;
    private Context context;
    private List<AppData.Category> categories = null;

    public ScoresAdapter(Context context, JSONArray scoreList) {
        this.scoreList = scoreList;
        this.context = context;
        this.categories = DatabaseUtil.getInstance().getAppData().getCategories();
    }

    public void setData(JSONArray scoreList) {
        this.scoreList = scoreList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView rankTV;
        TextView categoryTV;
        TextView scoreTV;
        TextView gamesTV;

        View headerContainer;

        public MyViewHolder(View view) {
            super(view);
            rankTV = (TextView) view.findViewById(R.id.rank);
            categoryTV = (TextView) view.findViewById(R.id.category);
            scoreTV = (TextView) view.findViewById(R.id.score);
            gamesTV = (TextView) view.findViewById(R.id.games);

            headerContainer = view.findViewById(R.id.header);
        }
    }

    @Override
    public int getItemCount() {
        if (scoreList != null) {
            return scoreList.length();
        }
        return 0;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scores_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder vh, int position) {
        JSONObject scoreObj = null;
        try {
            scoreObj = scoreList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String rank = "";
        String category = "";
        String score = "";
        String games = "";

        if (scoreObj != null) {
            try {
                rank += position + 1 ;
                category = getCategory(scoreObj.getInt(JsonConstants.CATEGORY));
                score += scoreObj.getInt(JsonConstants.SCORE);
                games += scoreObj.getInt(JsonConstants.GAMES);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        vh.rankTV.setText(rank);
        vh.categoryTV.setText(category);
        vh.scoreTV.setText(score);
        vh.gamesTV.setText(games);

        // Showing the list header
        if (position == 0) {
            vh.headerContainer.setVisibility(View.VISIBLE);
        } else {
            vh.headerContainer.setVisibility(View.GONE);
        }

    }

    /**
     * Fetching the category name using id
     * @param id
     * @return
     */
    private String getCategory(int id) {
        if (categories == null) {
            return  "";
        }
        for (AppData.Category category : categories ){
            if (category.getId() == id) {
                return category.getName();
            }
        }
        return null;
    }

}