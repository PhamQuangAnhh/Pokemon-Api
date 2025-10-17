package vn.edu.usth.pokemonapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class EvolutionBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_EVOLUTION_NAMES = "evolution_names";
    private static final String ARG_POKEMON_NAME = "pokemon_name";

    private RecyclerView evolutionRecyclerView;
    private EvolutionDetailAdapter adapter;
    private PokemonRepository repository;
    private List<Pokemon> evolutionDetailsList = new ArrayList<>();

    public static EvolutionBottomSheetFragment newInstance(ArrayList<String> evolutionNames, String pokemonName) {
        EvolutionBottomSheetFragment fragment = new EvolutionBottomSheetFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_EVOLUTION_NAMES, evolutionNames);
        args.putString(ARG_POKEMON_NAME, pokemonName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_evolution_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView titleTextView = view.findViewById(R.id.evolutionTitleTextView);
        evolutionRecyclerView = view.findViewById(R.id.evolutionDetailRecyclerView);
        repository = new PokemonRepository();

        if (getArguments() != null) {
            ArrayList<String> evolutionNames = getArguments().getStringArrayList(ARG_EVOLUTION_NAMES);
            String originalPokemonName = getArguments().getString(ARG_POKEMON_NAME);

            if (originalPokemonName != null) {
                titleTextView.setText(originalPokemonName + "'s Evolution Chain");
            }

            adapter = new EvolutionDetailAdapter(evolutionDetailsList);
            evolutionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            evolutionRecyclerView.setAdapter(adapter);

            if (evolutionNames != null && !evolutionNames.isEmpty()) {
                fetchEvolutionDetails(evolutionNames);
            }
        }
    }

    private void fetchEvolutionDetails(List<String> names) {
        for (String name : names) {
            repository.fetchPokemonDetails(name, new PokemonRepository.PokemonCallback() {
                @Override
                public void onSuccess(Pokemon pokemon) {
                    if (getActivity() != null) {
                        // Update the UI on the main thread (UI Thread)
                        getActivity().runOnUiThread(() -> {
                            evolutionDetailsList.add(pokemon);
                            adapter.notifyItemInserted(evolutionDetailsList.size() - 1);
                        });
                    }
                }

                @Override
                public void onFailure(String message) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Failed to load " + name, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        }
    }
}