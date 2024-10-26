package com.example.testmock.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testmock.R;
import com.example.testmock.api.model.CastMember;
import com.example.testmock.databinding.ItemCastAndCrewBinding;

import java.util.List;

public class CastAndCrewAdapter extends RecyclerView.Adapter<CastAndCrewAdapter.CastAndCrewViewHolder> {

    private static final String TAG = "TAGTAGTAG";
    private List<CastMember> castMembers;

    public CastAndCrewAdapter(List<CastMember> castMembers) {
        this.castMembers = castMembers;
    }

    @NonNull
    @Override
    public CastAndCrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCastAndCrewBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_cast_and_crew, parent, false);
        return new CastAndCrewViewHolder(binding);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCastMembers(List<CastMember> castMembers) {
        this.castMembers = castMembers;
        Log.d(TAG, "setCastMembers: " + castMembers.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CastAndCrewViewHolder holder, int position) {
        CastMember castMember = castMembers.get(position);
        holder.bind(castMember);
    }

    @Override
    public int getItemCount() {
        return castMembers.size();
    }

    class CastAndCrewViewHolder extends RecyclerView.ViewHolder {
        private final ItemCastAndCrewBinding binding;

        public CastAndCrewViewHolder(ItemCastAndCrewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CastMember castMember) {
            binding.setCastMember(castMember);
            binding.executePendingBindings();
        }
    }
}
