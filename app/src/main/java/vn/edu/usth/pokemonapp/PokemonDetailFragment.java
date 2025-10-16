package vn.edu.usth.pokemonapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Locale;

public class PokemonDetailFragment extends Fragment {

    private static final String ARG_POKEMON_NAME = "pokemon_name";
    private String pokemonName;

    private PokemonDetailViewModel detailViewModel;

    private ImageView pokemonImageView;
    private TextView pokemonNameTextView, hpTextView, attackTextView, defenseTextView, speedTextView, typeTextView, descriptionTextView;
    private ProgressBar progressBar;

    private Pokemon currentPokemonData;

    public static PokemonDetailFragment newInstance(String pokemonName) {
        PokemonDetailFragment fragment = new PokemonDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POKEMON_NAME, pokemonName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pokemonName = getArguments().getString(ARG_POKEMON_NAME);
        }
        detailViewModel = new ViewModelProvider(requireActivity()).get(PokemonDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pokemon_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        if (pokemonName != null) {
            observePokemonDetails();
        }
    }

    private void setupViews(View view) {
        pokemonImageView = view.findViewById(R.id.pokemonImageView);
        pokemonNameTextView = view.findViewById(R.id.pokemonNameTextView);
        hpTextView = view.findViewById(R.id.hpTextView);
        attackTextView = view.findViewById(R.id.attackTextView);
        defenseTextView = view.findViewById(R.id.defenseTextView);
        speedTextView = view.findViewById(R.id.speedTextView);
        typeTextView = view.findViewById(R.id.typeTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        progressBar = view.findViewById(R.id.progressBar);

        // --- LOGIC MỚI: HIỂN THỊ BOTTOM SHEET KHI NHẤN VÀO ẢNH ---
        pokemonImageView.setOnClickListener(v -> {
            if (currentPokemonData != null && currentPokemonData.evolutions != null && !currentPokemonData.evolutions.isEmpty()) {

                ArrayList<String> evolutionNames = new ArrayList<>();
                for (EvolutionStep step : currentPokemonData.evolutions) {
                    evolutionNames.add(step.name);
                }

                EvolutionBottomSheetFragment bottomSheet = EvolutionBottomSheetFragment.newInstance(evolutionNames, capitalize(currentPokemonData.name));
                // Hiển thị Bottom Sheet thông qua trình quản lý Fragment của Fragment cha
                bottomSheet.show(getParentFragmentManager(), "EvolutionBottomSheetFragment");

            } else {
                Toast.makeText(getContext(), "This Pokémon does not have any evolutions.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observePokemonDetails() {
        progressBar.setVisibility(View.VISIBLE);
        detailViewModel.getPokemonDetails(pokemonName).observe(getViewLifecycleOwner(), pokemon -> {
            if (pokemon != null) {
                this.currentPokemonData = pokemon;
                updateUi(pokemon);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateUi(Pokemon pokemon) {
        pokemonNameTextView.setText(capitalize(pokemon.name));
        hpTextView.setText(String.format(Locale.US, "HP: %d", pokemon.hp));
        attackTextView.setText(String.format(Locale.US, "Attack: %d", pokemon.attack));
        defenseTextView.setText(String.format(Locale.US, "Defense: %d", pokemon.defense));
        speedTextView.setText(String.format(Locale.US, "Speed: %d", pokemon.speed));
        typeTextView.setText("Type: " + capitalize(String.join(" / ", pokemon.types)));
        descriptionTextView.setText(pokemon.description);
        Glide.with(this).load(pokemon.imageUrl).into(pokemonImageView);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}