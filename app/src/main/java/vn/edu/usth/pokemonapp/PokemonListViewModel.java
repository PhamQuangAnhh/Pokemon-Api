package vn.edu.usth.pokemonapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class PokemonListViewModel extends ViewModel {

    private final PokemonRepository repository = new PokemonRepository();

    private final MutableLiveData<List<PokemonListItem>> _pokemonList = new MutableLiveData<>();
    public LiveData<List<PokemonListItem>> pokemonList = _pokemonList;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    public void loadPokemonList() {
        _isLoading.setValue(true);
        // Lấy 151 Pokémon thế hệ đầu tiên
        repository.fetchPokemonList(151, 0, new PokemonRepository.PokemonListCallback() {
            @Override
            public void onSuccess(List<PokemonListItem> fetchedList) {
                _pokemonList.postValue(fetchedList);
                _isLoading.postValue(false);
            }

            @Override
            public void onFailure(String message) {
                _error.postValue(message);
                _isLoading.postValue(false);
            }
        });
    }
}