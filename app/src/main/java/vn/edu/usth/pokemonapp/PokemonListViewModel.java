package vn.edu.usth.pokemonapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PokemonListViewModel extends ViewModel {

    private final PokemonRepository repository = new PokemonRepository();
    private List<PokemonListItem> originalMasterList = new ArrayList<>();

    // The list displayed on the ViewPager
    private final MutableLiveData<List<PokemonListItem>> _displayedPokemonList = new MutableLiveData<>();
    public LiveData<List<PokemonListItem>> displayedPokemonList = _displayedPokemonList;

    // LiveData containing search results for the Bottom Sheet
    private final MutableLiveData<List<PokemonListItem>> _searchResults = new MutableLiveData<>();
    public LiveData<List<PokemonListItem>> searchResults = _searchResults;

    // LiveData for navigation
    private final MutableLiveData<PokemonListItem> _navigateToPokemon = new MutableLiveData<>();
    public LiveData<PokemonListItem> navigateToPokemon = _navigateToPokemon;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final Set<String> pokemonTypes = new HashSet<>(Arrays.asList(
            "normal", "fire", "water", "grass", "electric", "ice", "fighting", "poison",
            "ground", "flying", "psychic", "bug", "rock", "ghost", "dragon", "dark",
            "steel", "fairy"
    ));

    public void loadPokemonList() {
        if (!originalMasterList.isEmpty()) return;
        _isLoading.setValue(true);
        repository.fetchPokemonList(200, 0, new PokemonRepository.PokemonListCallback() {
            @Override
            public void onSuccess(List<PokemonListItem> fetchedList) {
                originalMasterList = fetchedList;
                _displayedPokemonList.postValue(fetchedList);
                _isLoading.postValue(false);
            }
            @Override
            public void onFailure(String message) {
                _error.postValue(message);
                _isLoading.postValue(false);
            }
        });
    }

    // This function is called EVERY TIME the user types to filter the list by NAME
    public void search(String query) {
        String cleanedQuery = query.toLowerCase().trim();

        if (cleanedQuery.isEmpty()) {
            _searchResults.postValue(new ArrayList<>());
            return;
        }

        List<PokemonListItem> filteredList = new ArrayList<>();
        for (PokemonListItem item : originalMasterList) {
            if (item.name.contains(cleanedQuery)) {
                filteredList.add(item);
            }
        }
        _searchResults.postValue(filteredList);
    }

    // This function is called when the user presses ENTER to search by TYPE
    public void executeTypeSearch(String query) {
        String cleanedQuery = query.toLowerCase().trim();

        if (pokemonTypes.contains(cleanedQuery)) {
            _isLoading.setValue(true);
            repository.fetchPokemonByType(cleanedQuery, new PokemonRepository.PokemonListCallback() {
                @Override
                public void onSuccess(List<PokemonListItem> fetchedList) {
                    _displayedPokemonList.postValue(fetchedList); // Update the main list
                    _isLoading.postValue(false);
                }
                @Override
                public void onFailure(String message) {
                    _error.postValue(message);
                    _isLoading.postValue(false);
                }
            });
        } else {
            _error.postValue("'" + query + "' is not a valid Pokémon type.");
        }
    }

    public void resetSearch() {
        _displayedPokemonList.setValue(originalMasterList);
    }

    public void navigateToPokemon(PokemonListItem item) {
        _navigateToPokemon.setValue(item);
    }

    public void onNavigationComplete() {
        _navigateToPokemon.setValue(null);
    }
}
