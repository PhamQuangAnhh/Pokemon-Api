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

public class EvolutionAdapter extends RecyclerView.Adapter<EvolutionAdapter.EvolutionViewHolder> {

    private List<EvolutionStep> evolutionSteps;

    public EvolutionAdapter(List<EvolutionStep> evolutionSteps) {
        this.evolutionSteps = evolutionSteps;
    }

    @NonNull
    @Override
    public EvolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evolution, parent, false);
        return new EvolutionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EvolutionViewHolder holder, int position) {
        EvolutionStep step = evolutionSteps.get(position);
        holder.nameTextView.setText(capitalize(step.name));
        Glide.with(holder.itemView.getContext()).load(step.imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return evolutionSteps.size();
    }

    static class EvolutionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;

        public EvolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.evolutionImageView);
            nameTextView = itemView.findViewById(R.id.evolutionNameTextView);
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
