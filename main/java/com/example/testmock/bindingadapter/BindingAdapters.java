package com.example.testmock.bindingadapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.testmock.R;
import com.example.testmock.model.Movie;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BindingAdapters {
    @BindingAdapter("posterImageUrl")
    public static void loadPosterImage(ImageView imageView, String posterPath) {
        if (posterPath != null && !posterPath.isEmpty()) {
            // Construct the full URL
            String url = "https://image.tmdb.org/t/p/original/" + posterPath;

            // Load the image using Picasso
            Picasso.get()
                    .load(url)
                    .into(imageView);
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @BindingAdapter("ratingText")
    public static void setRatingText(TextView textView, Double rating) {
        if (rating != null) {
            textView.setText(String.format("Rating: %.1f/10", rating));
        } else {
            textView.setText("Rating: N/A");
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @BindingAdapter("popularityText")
    public static void setPopularityText(TextView textView, Double popularity) {
        if (popularity != null) {
            textView.setText(String.format("Popularity: %.1f", popularity));
        } else textView.setText("Popularity: N/A");
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @BindingAdapter("voteAverageText")
    public static void setVoteAverageText(TextView textView, Double voteAverage) {
        if (voteAverage != null) {
            textView.setText(String.format("Vote Average: %.1f", voteAverage));
        } else {
            textView.setText("Vote Average: N/A");
        }
    }

    @BindingAdapter("favoriteImage")
    public static void setFavoriteImage(ImageView imageView, boolean isFavorite) {
        imageView.setImageResource(isFavorite ? R.drawable.ic_like : R.drawable.ic_dislike);
    }

    @BindingAdapter("profileImage")
    public static void setProfileImage(ImageView imageView, String base64String) {
        if (base64String != null && !base64String.isEmpty()) {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(bitmap);
        } else {
            // Set a default image if base64String is null or empty
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String base64Image) {
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            view.setImageBitmap(decodedBitmap);
        } else {
            // Set a default image or handle null case
            view.setImageResource(R.drawable.ic_launcher_background); // Replace with your default image resource
        }
    }

    @BindingAdapter("formattedDateTime")
    public static void setFormattedDateTime(TextView textView, long timestamp) {
        if (timestamp == 0) {
            textView.setText("");
            return;
        }
        LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);
        textView.setText(formattedDateTime);
    }

}
