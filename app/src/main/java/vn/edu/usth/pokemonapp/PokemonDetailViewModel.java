package vn.edu.usth.pokemonapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.Map;

public class PokemonDetailViewModel extends ViewModel {

    private final PokemonRepository repository = new PokemonRepository();

    // "Kho chứa" (cache) dữ liệu Pokémon. Key là tên Pokémon, Value là LiveData chứa thông tin chi tiết.
    private final Map<String, MutableLiveData<Pokemon>> pokemonCache = new HashMap<>();

    // LiveData để thông báo lỗi
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    // Hàm chính mà Fragment sẽ gọi để lấy dữ liệu
    public LiveData<Pokemon> getPokemonDetails(String pokemonName) {
        String name = pokemonName.toLowerCase();

        // Nếu đã có trong kho, trả về ngay lập tức
        if (pokemonCache.containsKey(name)) {
            return pokemonCache.get(name);
        }

        // Nếu chưa có, tạo LiveData mới, bắt đầu tải, và lưu vào kho
        MutableLiveData<Pokemon> newPokemonData = new MutableLiveData<>();
        pokemonCache.put(name, newPokemonData);

        loadDataFor(name, newPokemonData);

        return newPokemonData;
    }

    // Hàm để MainActivity gọi và tải trước dữ liệu
    public void preloadPokemon(String pokemonName) {
        // Chỉ tải nếu chưa có trong kho
        if (!pokemonCache.containsKey(pokemonName.toLowerCase())) {
            getPokemonDetails(pokemonName);
        }
    }

    // Hàm nội bộ để thực hiện việc gọi API
    private void loadDataFor(String name, MutableLiveData<Pokemon> liveData) {
        repository.fetchPokemonDetails(name, new PokemonRepository.PokemonCallback() {
            @Override
            public void onSuccess(Pokemon pokemon) {
                // Đặt giá trị vào LiveData khi thành công
                liveData.postValue(pokemon);
            }

            @Override
            public void onFailure(String message) {
                // Gửi lỗi
                _error.postValue("Failed to load " + name + ": " + message);
            }
        });
    }
}