package vn.edu.usth.pokemonapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.Map;

public class PokemonDetailViewModel extends ViewModel {

    private final PokemonRepository repository = new PokemonRepository();

    // Cache for Pokémon data. Key is the Pokémon name, Value is LiveData containing detailed info.
    private final Map<String, MutableLiveData<Pokemon>> pokemonCache = new HashMap<>();

    // LiveData for error messages
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    // Main function for the Fragment to call to get data
    public LiveData<Pokemon> getPokemonDetails(String pokemonName) {
        String name = pokemonName.toLowerCase();

        // If it's already in the cache, return it immediately
        if (pokemonCache.containsKey(name)) {
            return pokemonCache.get(name);
        }

        // If not, create new LiveData, start loading, and save to cache
        MutableLiveData<Pokemon> newPokemonData = new MutableLiveData<>();
        pokemonCache.put(name, newPokemonData);

        loadDataFor(name, newPokemonData);

        return newPokemonData;
    }

    // Function for MainActivity to call and preload data
    public void preloadPokemon(String pokemonName) {
        // Only load if it's not already in the cache
        if (!pokemonCache.containsKey(pokemonName.toLowerCase())) {
            getPokemonDetails(pokemonName);
        }
    }

    // Internal function to make the API call
    private void loadDataFor(String name, MutableLiveData<Pokemon> liveData) {
        repository.fetchPokemonDetails(name, new PokemonRepository.PokemonCallback() {
            @Override
            public void onSuccess(Pokemon pokemon) {
                // Set the value in LiveData on success
                liveData.postValue(pokemon);
            }

            @Override
            public void onFailure(String message) {
                // Send the error
                _error.postValue("Failed to load " + name + ": " + message);
            }
        });
    }
}
