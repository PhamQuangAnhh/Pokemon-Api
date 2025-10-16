package vn.edu.usth.pokemonapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

public class EvolutionDetailAdapter extends RecyclerView.Adapter<EvolutionDetailAdapter.EvolutionDetailViewHolder> {

    private final List<Pokemon> pokemonEvolutions;

    public EvolutionDetailAdapter(List<Pokemon> pokemonEvolutions) {
        this.pokemonEvolutions = pokemonEvolutions;
    }

    @NonNull
    @Override
    public EvolutionDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evolution_detail, parent, false);
        return new EvolutionDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EvolutionDetailViewHolder holder, int position) {
        Pokemon evolution = pokemonEvolutions.get(position);
        holder.bind(evolution);
    }

    @Override
    public int getItemCount() {
        return pokemonEvolutions.size();
    }

    static class EvolutionDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView evolutionImageView;
        TextView evolutionNameTextView;
        TextView evolutionTypeTextView;
        TextView evolutionStatsTextView;

        public EvolutionDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            evolutionImageView = itemView.findViewById(R.id.evolutionImageView);
            evolutionNameTextView = itemView.findViewById(R.id.evolutionNameTextView);
            evolutionTypeTextView = itemView.findViewById(R.id.evolutionTypeTextView);
            evolutionStatsTextView = itemView.findViewById(R.id.evolutionStatsTextView);
        }

        void bind(Pokemon evolution) {
            evolutionNameTextView.setText(capitalize(evolution.name));
            evolutionTypeTextView.setText(capitalize(String.join(" / ", evolution.types)));

            String stats = String.format(Locale.US, "HP: %d, Atk: %d, Def: %d",
                    evolution.hp, evolution.attack, evolution.defense);
            evolutionStatsTextView.setText(stats);

            Glide.with(itemView.getContext())
                    .load(evolution.imageUrl)
                    .into(evolutionImageView);
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) return str;
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }
}