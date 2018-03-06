package com.shree.wordguess.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.shree.wordguess.R;
import com.shree.wordguess.adapter.FilterAdapter;

import java.util.List;

public class SearchListDialog {

	public static final int LANGUAGE_DATA = 1;
	public static final int CATEGORY_DATA = 2;

	public static interface DialogListener {
		public void onListItemSelection(int dialogType, Object data);
	}

	private Context context;
	private String title;
	private DialogListener listener;
	private AlertDialog alertDialog;
	private int dialogType;
	private List listData;

	private TextInputLayout searchContainer;
	private EditText searchET;
	private FilterAdapter filterAdapter;
	private RecyclerView recyclerView;

	public SearchListDialog(Context context, int dialogType) {
		this.context = context;
		this.dialogType = dialogType;
	}

	public SearchListDialog setDialogListener(DialogListener listener) {
		this.listener = listener;
		return this;
	}

	public SearchListDialog setTitle(String title) {
		this.title = title;
		return this;
	}

	public SearchListDialog setListData(List listData) {
		this.listData = listData;
		return this;
	}


	public SearchListDialog build() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		View view = LayoutInflater.from(context)
				.inflate(R.layout.search_list_layout, null, false);

		searchContainer = view.findViewById(R.id.searchContainer);
		searchET = view.findViewById(R.id.search);
		recyclerView = view.findViewById(R.id.recycler_view);
		alertDialogBuilder.setView(view);

		searchContainer.setHint(title);
		searchET.setHint(title);
		searchET.addTextChangedListener(searchWatcher);

		setAdapter();

		alertDialog = alertDialogBuilder.create();
		return this;
	}


	private void setAdapter() {
		filterAdapter = new FilterAdapter(context, listData);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.addOnItemTouchListener(recyclerTouchListener);
		recyclerView.setAdapter(filterAdapter);
	}

	public void show() {
		if (alertDialog!=null) {
			alertDialog.show();
		}
	}

	public void close() {
		if (alertDialog!=null) {
			alertDialog.dismiss();
		}
	}

	TextWatcher searchWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
			filterAdapter.getFilter().filter(charSequence);
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}
	};


	RecyclerTouchListener recyclerTouchListener =new RecyclerTouchListener(context,  new RecyclerTouchListener.ClickListener() {
		@Override
		public void onClick(View view, int position) {
			Object item = filterAdapter.getData(position);
			listener.onListItemSelection(dialogType, item);
			close();
		}

		@Override
		public void onLongClick(View view, int position) {
		}
	});
}
