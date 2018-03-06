package com.shree.wordguess.fragment;


import com.shree.wordguess.network.UINotificationListener;

public interface FragmentInterface extends UINotificationListener {
    public void initializeViews();
    public void initializeListeners();
    public void loadData();
}
