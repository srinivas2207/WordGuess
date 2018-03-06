package com.shree.wordguess.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.shree.wordguess.R;
import com.shree.wordguess.util.AppData;
import com.shree.wordguess.util.JsonConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> implements Filterable{

    private List data = null;
    private List filteredData = null;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public FilterAdapter(Context context, List data) {
        this.filteredData = data;
        this.data = data;
        this.context = context;
    }

    public void setData(List data) {
        this.data = data;
        this.filteredData = data;
        notifyDataSetChanged();
    }

    public Object getData(int index) {
        try {
            return filteredData.get(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        public MyViewHolder(View view) {
            super(view);
            item = (TextView) view.findViewById(R.id.title);
        }
    }

    @Override
    public int getItemCount() {
        if (filteredData != null) {
            return filteredData.size();
        }
        return 0;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder vh, int position) {
        String text = null;
        Object dataObj = filteredData.get(position);
        if (dataObj instanceof AppData.Language) {
            text = ((AppData.Language) dataObj).getName();
        } else if (dataObj instanceof AppData.Category) {
            text = ((AppData.Category) dataObj).getName();
        }
        vh.item.setText(text);
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            String filterableString = charSequence.toString().toLowerCase();

            List filterList = new ArrayList();
            if(filterableString.length() == 0) {
                filterList = data;
            } else {
                for (Object dataObj : data) {
                    String name = null;
                    String desc = null;
                    if (dataObj instanceof AppData.Language) {
                        name = ((AppData.Language) dataObj).getName();
                        desc = ((AppData.Language) dataObj).getCode();
                    } else if (dataObj instanceof AppData.Category) {
                        name = ((AppData.Category) dataObj).getName();
                        desc = ((AppData.Category) dataObj).getDesc();
                    }

                    if (name != null && name.toLowerCase().contains(filterableString)) {
                        filterList.add(dataObj);
                        continue;
                    }

                    if (desc != null && desc.toLowerCase().contains(filterableString)) {
                        filterList.add(dataObj);
                        continue;
                    }
                }
            }

            results.values = filterList;
            results.count = filterList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredData = (List) filterResults.values;
            notifyDataSetChanged();
        }
    }

}