package com.tretton37.twitter37.utils.ui.customsearchview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tretton37.twitter37.R;
import com.tretton37.twitter37.data.db.HistoryContract;

import static com.tretton37.twitter37.utils.AppConstants.EMPTY;
import static com.tretton37.twitter37.utils.AppConstants.ZERO;

/**
 * Created by suyashg
 */
public class SuggestionsSearchView extends FrameLayout implements FilterQueryProvider, TextWatcher {
    //region Properties
    /**
     * Number of suggestions to show.
     */
    private static int MAX_HISTORY = 10;

    /**
     * Whether or not the search view is open right now.
     */
    private boolean mOpen;

    /**
     * The Context that this view appears in.
     */
    private Context mContext;

    /**
     * Whether or not the SuggestionsSearchView will animate into view or just appear.
     */
    private boolean mShouldAnimate;

    /**
     * Wheter to keep the search history or not.
     */
    private boolean mShouldKeepHistory;

    /**
     * Flag for whether or not we are clearing focus.
     */
    private boolean mClearingFocus;

    //region UI Elements
    /**
     * The tint that appears over the search view.
     */
    private View mTintView;

    /**
     * The root of the search view.
     */
    private FrameLayout mRoot;

    /**
     * The bar at the top of the SearchView containing the EditText and ImageButtons.
     */
    private LinearLayout mSearchBar;

    /**
     * The EditText for entering a search.
     */
    private EditText mSearchEditText;

    /**
     * The ImageButton for navigating back.
     */
    private ImageButton mBack;

    /**
     * The ImageButton for clearing the search text.
     */
    private ImageButton mClear;

    /**
     * The ListView for displaying suggestions based on the search.
     */
    private ListView mSuggestionsListView;

    /**
     * Adapter for displaying suggestions.
     */
    private CursorAdapter mAdapter;
    //endregion

    //region Query Properties
    /**
     * The current query text.
     */
    private CharSequence mCurrentQuery;
    //endregion

    //region Listeners
    /**
     * Listener for when the query text is submitted or changed.
     */
    private OnQueryTextListener mOnQueryTextListener;

    /**
     * Listener for when the search view opens and closes.
     */
    private SearchViewListener mSearchViewListener;

    //endregion

    //region Constructors
    public SuggestionsSearchView(Context context) {
        this(context, null);
    }

    public SuggestionsSearchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, ZERO);
    }

    public SuggestionsSearchView(Context context, AttributeSet attributeSet, int defStyleAttributes) {
        super(context, attributeSet);

        // Set variables
        this.mContext = context;
        this.mShouldAnimate = true;
        this.mShouldKeepHistory = true;

        // Initialize view
        init();

        // Initialize style
        initStyle(attributeSet, defStyleAttributes);
    }
    //endregion

    //region Initializers

    /**
     * Preforms any required initializations for the search view.
     */
    private void init() {
        // Inflate view
        LayoutInflater.from(mContext).inflate(R.layout.view_search_view, this, true);

        // Get items
        mRoot = findViewById(R.id.search_layout);
        mTintView = mRoot.findViewById(R.id.transparent_view);
        mSearchBar = mRoot.findViewById(R.id.search_bar);
        mBack = mRoot.findViewById(R.id.action_back);
        mSearchEditText = mRoot.findViewById(R.id.et_search);
        mClear = mRoot.findViewById(R.id.action_clear);
        mSuggestionsListView = mRoot.findViewById(R.id.suggestion_list);

        // Set click listeners
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });

        mClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEditText.setText(EMPTY);
            }
        });

        // Initialize the search view.
        initSearchView();

        mAdapter = new CursorSearchAdapter(mContext, getHistoryCursor(), ZERO);
        mAdapter.setFilterQueryProvider(this);
        mSuggestionsListView.setAdapter(mAdapter);
        mSuggestionsListView.setTextFilterEnabled(true);
        mSuggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String suggestion = getSuggestionAtPosition(position);
                setQuery(suggestion, true);
            }
        });
    }

    /**
     * Initializes the style of this view.
     *
     * @param attributeSet      The attributes to apply to the view.
     * @param defStyleAttribute An attribute to the style theme applied to this view.
     */
    private void initStyle(AttributeSet attributeSet, int defStyleAttribute) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet,
                R.styleable.SuggestionsSearchView, defStyleAttribute, ZERO);

        if (typedArray != null) {
            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_searchBackground)) {
                setBackground(typedArray.getDrawable(R.styleable.SuggestionsSearchView_searchBackground));
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_android_textColor)) {
                setTextColor(typedArray.getColor(R.styleable.SuggestionsSearchView_android_textColor,
                        ContextCompat.getColor(mContext, R.color.black)));
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_android_textColorHint)) {
                setHintTextColor(typedArray.getColor(R.styleable.SuggestionsSearchView_android_textColorHint,
                        ContextCompat.getColor(mContext, R.color.gray_50)));
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_android_hint)) {
                setHint(typedArray.getString(R.styleable.SuggestionsSearchView_android_hint));
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_searchCloseIcon)) {
                setClearIcon(typedArray.getResourceId(
                        R.styleable.SuggestionsSearchView_searchCloseIcon,
                        R.drawable.ic_action_navigation_close)
                );
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_searchBackIcon)) {
                setBackIcon(typedArray.getResourceId(
                        R.styleable.SuggestionsSearchView_searchBackIcon,
                        R.drawable.ic_action_navigation_arrow_back)
                );
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_searchSuggestionBackground)) {
                setSuggestionBackground(typedArray.getResourceId(
                        R.styleable.SuggestionsSearchView_searchSuggestionBackground,
                        R.color.search_layover_bg)
                );
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_historyIcon) && mAdapter instanceof CursorSearchAdapter) {
                ((CursorSearchAdapter) mAdapter).setHistoryIcon(typedArray.getResourceId(
                        R.styleable.SuggestionsSearchView_historyIcon,
                        R.drawable.ic_history_white));
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_suggestionIcon) && mAdapter instanceof CursorSearchAdapter) {
                ((CursorSearchAdapter) mAdapter).setSuggestionIcon(typedArray.getResourceId(
                        R.styleable.SuggestionsSearchView_suggestionIcon,
                        R.drawable.ic_action_search_white));
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_listTextColor) && mAdapter instanceof CursorSearchAdapter) {
                ((CursorSearchAdapter) mAdapter).setTextColor(typedArray.getColor(R.styleable.SuggestionsSearchView_listTextColor,
                        ContextCompat.getColor(mContext, android.R.color.white)));
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_android_inputType)) {
                setInputType(typedArray.getInteger(
                        R.styleable.SuggestionsSearchView_android_inputType,
                        InputType.TYPE_CLASS_TEXT)
                );
            }

            if (typedArray.hasValue(R.styleable.SuggestionsSearchView_searchBarHeight)) {
                setSearchBarHeight(typedArray.getDimensionPixelSize(R.styleable.SuggestionsSearchView_searchBarHeight, getAppCompatActionBarHeight()));
            } else {
                setSearchBarHeight(getAppCompatActionBarHeight());
            }

            ViewCompat.setFitsSystemWindows(this, typedArray.getBoolean(R.styleable.SuggestionsSearchView_android_fitsSystemWindows, false));

            typedArray.recycle();
        }
    }

    /**
     * Preforms necessary initializations on the SearchView.
     */
    private void initSearchView() {
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // When an edit occurs, submit the query.
                onSubmitQuery();
                return true;
            }
        });

        mSearchEditText.addTextChangedListener(this);

        mSearchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If we gain focus, show keyboard and show suggestions.
                if (hasFocus) {
                    showKeyboard(mSearchEditText);
                    showSuggestions();
                }
            }
        });
    }
    //endregion

    //region Show Methods

    /**
     * Displays the keyboard with a focus on the Search EditText.
     *
     * @param view The view to attach the keyboard to.
     */
    private void showKeyboard(View view) {
        if (view.hasFocus()) {
            view.clearFocus();
        }

        view.requestFocus();

        if (!isHardKeyboardAvailable()) {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, ZERO);
        }
    }

    /**
     * Method that checks if there's a physical keyboard on the phone.
     *
     * @return true if there's a physical keyboard connected, false otherwise.
     */
    private boolean isHardKeyboardAvailable() {
        return mContext.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
    }

    /**
     * Changes the visibility of the clear button to VISIBLE or GONE.
     *
     * @param display True to display the clear button, false to hide it.
     */
    private void displayClearButton(boolean display) {
        mClear.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    /**
     * Displays the available suggestions, if any.
     */
    private void showSuggestions() {
        mSuggestionsListView.setVisibility(View.VISIBLE);
    }

    /**
     * Displays the SearchView.
     */
    public void openSearch() {
        // If search is already open, just return.
        if (mOpen) {
            return;
        }

        // Get focus
        mSearchEditText.setText(EMPTY);
        mSearchEditText.requestFocus();

        if (mShouldAnimate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRoot.setVisibility(View.VISIBLE);
                AnimationUtils.circleRevealView(mSearchBar);
            } else {
                AnimationUtils.fadeInView(mRoot);
            }

        } else {
            mRoot.setVisibility(View.VISIBLE);
        }

        if (mSearchViewListener != null) {
            mSearchViewListener.onSearchViewOpened();
        }
        mOpen = true;
    }
    //endregion

    //region Hide Methods

    /**
     * Hides the suggestion list.
     */
    private void dismissSuggestions() {
        mSuggestionsListView.setVisibility(View.GONE);
    }

    /**
     * Hides the keyboard displayed for the SearchEditText.
     *
     * @param view The view to detach the keyboard from.
     */
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), ZERO);
    }

    /**
     * Closes the search view if necessary.
     */
    public void closeSearch() {
        // If we're already closed, just return.
        if (!mOpen) {
            return;
        }

        // Clear text, values, and focus.
        mSearchEditText.setText(EMPTY);
        dismissSuggestions();
        clearFocus();

        if (mShouldAnimate) {
            final View v = mRoot;

            AnimatorListenerAdapter listenerAdapter = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // After the animation is done. Hide the root view.
                    v.setVisibility(View.GONE);
                }
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AnimationUtils.circleHideView(mSearchBar, listenerAdapter);
            } else {
                AnimationUtils.fadeOutView(mRoot);
            }
        } else {
            // Just hide the view.
            mRoot.setVisibility(View.GONE);
        }

        // Call listener if we have one
        if (mSearchViewListener != null) {
            mSearchViewListener.onSearchViewClosed();
        }
        mOpen = false;
    }
    //endregion

    //region Interface Methods

    /**
     * Filters and updates the buttons when text is changed.
     *
     * @param newText The new text.
     */
    private void onTextChanged(CharSequence newText) {
        // Get current query
        mCurrentQuery = mSearchEditText.getText();

        // If the text is not empty, show the empty button
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            displayClearButton(true);
        } else {
            displayClearButton(false);
        }

        // If we have a query listener and the text has changed, call it.
        if (mOnQueryTextListener != null) {
            mOnQueryTextListener.onQueryTextChange(newText.toString());
        }
    }

    /**
     * Called when a query is submitted. This will close the search view.
     */
    private void onSubmitQuery() {
        // Get the query.
        CharSequence query = mSearchEditText.getText();

        // If the query is not null and it has some text, submit it.
        if (query != null) {

            // If we don't have a listener, or if the search view handled the query, close it.
            if (mOnQueryTextListener == null || !mOnQueryTextListener.onQueryTextSubmit(query.toString())) {

                if (mShouldKeepHistory) {
                    saveQueryToDb(query.toString(), System.currentTimeMillis());
                }

                // Refresh the cursor on the adapter,
                // so the new entry will be shown on the next time the user opens the search view.
                refreshAdapterCursor();

                closeSearch();
                mSearchEditText.setText(EMPTY);
            }
        }
    }

    //endregion

    //region Mutators
    public void setOnQueryTextListener(OnQueryTextListener mOnQueryTextListener) {
        this.mOnQueryTextListener = mOnQueryTextListener;
    }

    public void setSearchViewListener(SearchViewListener mSearchViewListener) {
        this.mSearchViewListener = mSearchViewListener;
    }

    /**
     * Sets an OnItemClickListener to the suggestion list.
     *
     * @param listener - The ItemClickListener.
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mSuggestionsListView.setOnItemClickListener(listener);
    }

    /**
     * Set the query to search view. If submit is set to true, it'll submit the query.
     *
     * @param query  - The Query value.
     * @param submit - Whether to submit or not the query or not.
     */
    public void setQuery(CharSequence query, boolean submit) {
        mSearchEditText.setText(query);

        if (query != null) {
            mSearchEditText.setSelection(mSearchEditText.length());
            mCurrentQuery = query;
        }

        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    /**
     * Sets the background of the SearchView.
     *
     * @param background The drawable to use as a background.
     */
    @Override
    public void setBackground(Drawable background) {
        mTintView.setBackground(background);
    }

    /**
     * Sets the background color of the SearchView.
     *
     * @param color The color to use for the background.
     */
    @Override
    public void setBackgroundColor(int color) {
        setTintColor(color);
    }

    /**
     * Change the color of the background tint.
     *
     * @param color The new color.
     */
    private void setTintColor(int color) {
        mTintView.setBackgroundColor(color);
    }

    /**
     * Sets the text color of the EditText.
     *
     * @param color The color to use for the EditText.
     */
    public void setTextColor(int color) {
        mSearchEditText.setTextColor(color);
    }

    /**
     * Sets the text color of the search hint.
     *
     * @param color The color to be used for the hint text.
     */
    public void setHintTextColor(int color) {
        mSearchEditText.setHintTextColor(color);
    }

    /**
     * Sets the hint to be used for the search EditText.
     *
     * @param hint The hint to be displayed in the search EditText.
     */
    public void setHint(CharSequence hint) {
        mSearchEditText.setHint(hint);
    }

    /**
     * Sets the icon for the clear action.
     *
     * @param resourceId The resource ID of drawable that will represent the clear action.
     */
    public void setClearIcon(int resourceId) {
        mClear.setImageResource(resourceId);
    }

    /**
     * Sets the icon for the back action.
     *
     * @param resourceId The resource Id of the drawable that will represent the back action.
     */
    public void setBackIcon(int resourceId) {
        mBack.setImageResource(resourceId);
    }

    /**
     * Sets the background of the suggestions ListView.
     *
     * @param resource The resource to use as a background for the
     *                 suggestions listview.
     */
    public void setSuggestionBackground(int resource) {
        if (resource > ZERO) {
            mSuggestionsListView.setBackgroundResource(resource);
        }
    }

    /**
     * Changes the default history list icon.
     *
     * @param resourceId The resource id of the new history icon.
     */
    public void setHistoryIcon(@DrawableRes int resourceId) {
        if (mAdapter instanceof CursorSearchAdapter) {
            ((CursorSearchAdapter) mAdapter).setHistoryIcon(resourceId);
        }
    }

    /**
     * Changes the default suggestion list icon.
     *
     * @param resourceId The resource id of the new suggestion icon.
     */
    public void setSuggestionIcon(@DrawableRes int resourceId) {
        if (mAdapter instanceof CursorSearchAdapter) {
            ((CursorSearchAdapter) mAdapter).setSuggestionIcon(resourceId);
        }
    }

    /**
     * Changes the default suggestion list item text color.
     *
     * @param color The new color.
     */
    public void setListTextColor(int color) {
        if (mAdapter instanceof CursorSearchAdapter) {
            ((CursorSearchAdapter) mAdapter).setTextColor(color);
        }
    }

    /**
     * Sets the input type of the SearchEditText.
     *
     * @param inputType The input type to set to the EditText.
     */
    public void setInputType(int inputType) {
        mSearchEditText.setInputType(inputType);
    }

    /**
     * Sets the bar height if prefered to not use the existing actionbar height value
     *
     * @param height The value of the height in pixels
     */
    public void setSearchBarHeight(final int height) {
        mSearchBar.setMinimumHeight(height);
        mSearchBar.getLayoutParams().height = height;
    }

    /**
     * Returns the actual AppCompat ActionBar height value. This will be used as the default
     *
     * @return The value of the actual actionbar height in pixels
     */
    private int getAppCompatActionBarHeight() {
        TypedValue tv = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
        return getResources().getDimensionPixelSize(tv.resourceId);
    }

    //endregion

    //region Accessors

    /**
     * Determines if the search view is opened or closed.
     *
     * @return True if the search view is open, false if it is closed.
     */
    public boolean isOpen() {
        return mOpen;
    }

    /**
     * Retrieves a suggestion at a given index in the adapter.
     *
     * @return The search suggestion for that index.
     */
    public String getSuggestionAtPosition(int position) {
        // If position is out of range just return empty string.
        if (position < ZERO || position >= mAdapter.getCount()) {
            return EMPTY;
        } else {
            return mAdapter.getItem(position).toString();
        }
    }
    //endregion

    //region View Methods

    /**
     * Handles any cleanup when focus is cleared from the view.
     */
    @Override
    public void clearFocus() {
        this.mClearingFocus = true;
        hideKeyboard(this);
        super.clearFocus();
        mSearchEditText.clearFocus();
        this.mClearingFocus = false;
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        // Don't accept if we are clearing focus, or if the view isn't focusable.
        return !(mClearingFocus || !isFocusable()) && mSearchEditText.requestFocus(direction, previouslyFocusedRect);
    }

    //----- Lifecycle methods -----//

//    public void activityPaused() {
//        Cursor cursor = ((CursorAdapter)mAdapter).getCursor();
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//    }

    public void activityResumed() {
        refreshAdapterCursor();
    }
    //endregion

    //region Database Methods

    /**
     * Save a query to the local database.
     *
     * @param query - The query to be saved. Can't be empty or null.
     * @param ms    - The insert date, in millis. As a suggestion, use System.currentTimeMillis();
     **/
    public synchronized void saveQueryToDb(String query, long ms) {
        if (!TextUtils.isEmpty(query) && ms > ZERO) {
            ContentValues values = new ContentValues();

            values.put(HistoryContract.HistoryEntry.COLUMN_QUERY, query);
            values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, ms);
            values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 1); // Saving as history.

            mContext.getContentResolver().insert(HistoryContract.HistoryEntry.CONTENT_URI, values);
        }
    }

    private Cursor getHistoryCursor() {
        return mContext.getContentResolver().query(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                new String[]{"1"},
                HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " DESC LIMIT " + MAX_HISTORY
        );
    }

    private void refreshAdapterCursor() {
        Cursor historyCursor = getHistoryCursor();
        mAdapter.changeCursor(historyCursor);
    }

    public synchronized void clearHistory() {
        mContext.getContentResolver().delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                new String[]{"1"}
        );
    }

    public synchronized void clearAll() {
        mContext.getContentResolver().delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null
        );
    }

    @Override
    public Cursor runQuery(CharSequence constraint) {
        String filter = constraint.toString();
        if (filter.isEmpty()) {
            return getHistoryCursor();
        } else {
            return mContext.getContentResolver().query(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    null,
                    HistoryContract.HistoryEntry.COLUMN_QUERY + " LIKE ?",
                    new String[]{"%" + filter + "%"},
                    HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " DESC, " +
                            HistoryContract.HistoryEntry.COLUMN_QUERY
            );
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //not used as of now
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // When the text changes, filter
        mAdapter.getFilter().filter(s.toString());
        mAdapter.notifyDataSetChanged();
        SuggestionsSearchView.this.onTextChanged(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
        //not used as of now
    }
    //endregion

    //region Interfaces

    /**
     * Interface that handles the submission and change of search queries.
     */
    public interface OnQueryTextListener {
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

    /**
     * Interface that handles the opening and closing of the SearchView.
     */
    public interface SearchViewListener {
        /**
         * Called when the searchview is opened.
         */
        void onSearchViewOpened();

        /**
         * Called when the search view closes.
         */
        void onSearchViewClosed();
    }
    //endregion
}
