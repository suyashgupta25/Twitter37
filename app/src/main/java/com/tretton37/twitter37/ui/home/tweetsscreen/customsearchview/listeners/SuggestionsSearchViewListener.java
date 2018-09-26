package com.tretton37.twitter37.ui.home.tweetsscreen.customsearchview.listeners;

/**
 * Interface that handles the opening and closing of the SearchView.
 */
public interface SuggestionsSearchViewListener {
    /**
     * Called when the searchview is opened.
     */
    void onSearchViewOpened();

    /**
     * Called when the search view closes.
     */
    void onSearchViewClosed();
}