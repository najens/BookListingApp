package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.booklistingapp.QueryUtils.LOG_TAG;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    // Define variables used in MainActivity class
    private TextView mEmptyStateTextView;
    private Button titleButton;
    private Button authorButton;
    private Button subjectButton;
    private boolean titleClick;
    private boolean authorClick;
    private boolean subjectClick;
    private SearchView searchView;
    private static final int BOOK_LOADER_ID = 1;
    private BookAdapter mAdapter;
    private static String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST: Main Activity onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        final ListView bookListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book currentBook = mAdapter.getItem(position);

                Uri bookUri = Uri.parse(currentBook.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        final boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // If there is a network connection, fetch data
        if (isConnected) {
            LoaderManager loaderManager = getLoaderManager();

            Log.i(LOG_TAG, "TEST: calling initLoader() ...");
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setSubmitButtonEnabled(true);

        //Set an OnQueryTextListener to the search button
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                mQuery = query;

                if (isConnected) {
                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                    return true;
                } else {
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    return false;
                }
            }
        });

        // Change boolean values and color using an onClickListener(). This will be used to
        //update the query URL.
        titleClick = true;
        authorClick = false;
        subjectClick = false;
        titleButton = (Button) findViewById(R.id.title_button);
        authorButton = (Button) findViewById(R.id.author_button);
        subjectButton = (Button) findViewById(R.id.subject_button);
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleClick || !titleClick) {
                    titleButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    titleClick = true;
                    authorButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    authorClick = false;
                    subjectButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    subjectClick = false;
                }
            }
        });

        authorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authorClick || !authorClick) {
                    authorButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    authorClick = true;
                    titleButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    titleClick = false;
                    subjectButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    subjectClick = false;
                }
            }
        });

        subjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectClick || !subjectClick) {
                    subjectButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    subjectClick = true;
                    titleButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    titleClick = false;
                    authorButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    authorClick = false;
                }
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: onCreateLoader() called ...");

        if (titleClick) {
            mQuery = "intitle:" + mQuery;
        } else if (authorClick) {
            mQuery = "inauthor:" + mQuery;
        } else if (subjectClick) {
            mQuery = "subject:" + mQuery;
        } else {
            mQuery = "";
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("books")
                .appendPath("v1")
                .appendPath("volumes")
                .appendQueryParameter("q", mQuery);
        String BOOK_REQUEST_URL = URLDecoder.decode(builder.build().toString());
        BOOK_REQUEST_URL = BOOK_REQUEST_URL + "&maxResults=20";

        return new BookLoader(this, BOOK_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        Log.i(LOG_TAG, "TEST: onLoadFinished() called ...");
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_books);

        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mQuery = searchView.getQuery().toString();
        outState.putString("query", mQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mQuery = savedInstanceState.getString("query");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
