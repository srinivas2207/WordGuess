package com.shree.wordguess.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shree.wordguess.R;
import com.shree.wordguess.util.WordData;

import java.util.List;

/**
 * Adapter to display list of favourite words
 */
public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    private List<WordData.Word> data = null;
    private Context context;
    private int selectedIndex = -1;

    public FavouriteAdapter(Context context, List data) {
        this.data = data;
        this.context = context;
    }

    public void setData(List data) {
        this.data = data;
        notifyDataSetChanged();
    }

    /**
     * Changing the index of selected item
     * @param selectedIndex
     */
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subTitle;
        View listItemContainer;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            subTitle = (TextView) view.findViewById(R.id.subTitle);
            listItemContainer = view.findViewById(R.id.listItemContainer);
        }
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder vh, int position) {
        WordData.Word favWord = data.get(position);
        String titleStr = favWord.getName();
        String subTitleStr = null;
        if (favWord.getTranslatedValue() != null) {
            subTitleStr = favWord.getTranslatedValue();
        } else if (favWord.getDesc() != null) {
            subTitleStr = favWord.getDesc();
        }

        vh.title.setText(titleStr);
        if (subTitleStr != null) {
            vh.subTitle.setText(subTitleStr);
        } else {
            vh.subTitle.setText("");
        }

        //Changing the background of the item, based on selected index
        if (position == selectedIndex) {
            vh.listItemContainer.setBackgroundColor(R.attr.selectableItemBackground);
        } else {
            vh.listItemContainer.setBackgroundResource(R.color.listBgColor);
        }
    }

}