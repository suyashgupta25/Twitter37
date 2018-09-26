package com.tretton37.twitter37.ui.home.tweetsscreen.customsearchview.listeners;

/**
 * Interface that handles the submission and change of search queries.
 */
public interface OnSuggestionsQueryTextListener {
    /**
     * Called when a search query is submitted.
     *
     * @param query The text that will be searched.
     * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
     */
    boolean onQueryTextSubmit(String query);

    /**
     * Called when a search query is changed.
     *
     * @param newText The new text of the search query.
     * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
     */
    boolean onQueryTextChange(String newText);
}
