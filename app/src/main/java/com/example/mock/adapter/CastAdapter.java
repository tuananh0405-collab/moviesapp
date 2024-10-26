package com.example.mock.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mock.R;
import com.example.mock.model.Cast;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<Cast> castList;

    public CastAdapter(List<Cast> castList) {
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {
        Cast cast = castList.get(position);
        holder.bind(cast);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public static class CastViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private ImageView profileImageView;

        public CastViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewCastName);
            profileImageView = itemView.findViewById(R.id.imageViewCastProfile);
        }

        public void bind(Cast cast) {
            nameTextView.setText(cast.getName());

            Picasso.get()
                    .load("https://image.tmdb.org/t/p/w500" + cast.getProfilePath())
                    .placeholder(R.drawable.ic_account_circle)
                    .into(profileImageView);
        }
    }
}
