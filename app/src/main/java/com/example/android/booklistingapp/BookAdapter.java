package com.example.android.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nate on 10/10/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Create a new {@link BookAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param books   is the list of {@link Book}s to be displayed.
     */
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentBook.getTitle());

        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);
        authorTextView.setText(currentBook.getAuthor().replace("[\"", "")
                .replace("\"]", " ").replace("\",\"", ", "));

        TextView subjectTextView = (TextView) listItemView.findViewById(R.id.subject_text_view);
        subjectTextView.setText(currentBook.getSubject().replace("[\"", "")
                .replace("\"]", " ").replace("\",\"", ", "));

        TextView pagesTextView = (TextView) listItemView.findViewById(R.id.pages_text_view);
        pagesTextView.setText("p " + currentBook.getPages());

        ImageView bookImageView = (ImageView) listItemView.findViewById(R.id.book_image_view);
        new DownloadImageTask(bookImageView)
                .execute(currentBook.getImage());

        return listItemView;
    }
}
