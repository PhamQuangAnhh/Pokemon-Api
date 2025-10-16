package vn.edu.usth.pokemonapp;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonRepository {

    private final PokeApiService apiService;
    private static final String TAG = "PokemonRepository";

    public interface PokemonCallback {
        void onSuccess(Pokemon pokemon);
        void onFailure(String message);
    }

    public interface PokemonListCallback {
        void onSuccess(List<PokemonListItem> pokemonList);
        void onFailure(String message);
    }

    public PokemonRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(PokeApiService.class);
    }

    public void fetchPokemonList(int limit, int offset, final PokemonListCallback callback) {
        apiService.getPokemonList(limit, offset).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onFailure("Could not fetch Pokemon list");
                    return;
                }
                List<PokemonListItem> list = new ArrayList<>();
                JsonArray results = response.body().getAsJsonArray("results");
                for (JsonElement element : results) {
                    JsonObject obj = element.getAsJsonObject();
                    PokemonListItem item = new PokemonListItem();
                    item.name = obj.get("name").getAsString();
                    item.url = obj.get("url").getAsString();
                    list.add(item);
                }
                callback.onSuccess(list);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void fetchPokemonDetails(String pokemonName, final PokemonCallback callback) {
        apiService.getPokemon(pokemonName).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> pokemonResponse) {
                if (!pokemonResponse.isSuccessful() || pokemonResponse.body() == null) {
                    String errorMsg = "Lỗi khi lấy dữ liệu Pokemon: " + pokemonResponse.code() + " " + pokemonResponse.message();
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                    return;
                }
                JsonObject pokemonData = pokemonResponse.body();
                apiService.getPokemonSpecies(pokemonName).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> speciesResponse) {
                        if (!speciesResponse.isSuccessful() || speciesResponse.body() == null) {
                            String errorMsg = "Lỗi khi lấy dữ liệu loài Pokemon: " + speciesResponse.message();
                            Log.e(TAG, errorMsg);
                            callback.onFailure(errorMsg);
                            return;
                        }
                        JsonObject speciesData = speciesResponse.body();
                        String evolutionChainUrl = speciesData.getAsJsonObject("evolution_chain").get("url").getAsString();
                        fetchEvolutionChain(evolutionChainUrl, (evolutionSteps) -> {
                            Pokemon pokemon = processPokemonData(pokemonData, speciesData, evolutionSteps);
                            callback.onSuccess(pokemon);
                        });
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e(TAG, "Thất bại khi gọi API loài: ", t);
                        callback.onFailure(t.getMessage());
                    }
                });
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Thất bại khi gọi API Pokemon: ", t);
                callback.onFailure(t.getMessage());
            }
        });
    }

    // --- HÀM NÀY ĐÃ ĐƯỢC CẬP NHẬT ---
    private Pokemon processPokemonData(JsonObject pokemonData, JsonObject speciesData, List<EvolutionStep> evolutions) {
        String name = pokemonData.get("name").getAsString();
        String imageUrl = pokemonData.getAsJsonObject("sprites").getAsJsonObject("other").getAsJsonObject("official-artwork").get("front_default").getAsString();

        int hp = 0, attack = 0, defense = 0, speed = 0;
        for (JsonElement statEl : pokemonData.getAsJsonArray("stats")) {
            JsonObject statObj = statEl.getAsJsonObject();
            String statName = statObj.getAsJsonObject("stat").get("name").getAsString();
            int baseStat = statObj.get("base_stat").getAsInt();
            if (statName.equals("hp")) hp = baseStat;
            if (statName.equals("attack")) attack = baseStat;
            if (statName.equals("defense")) defense = baseStat;
            if (statName.equals("speed")) speed = baseStat;
        }

        List<String> types = new ArrayList<>();
        for (JsonElement typeEl : pokemonData.getAsJsonArray("types")) {
            types.add(typeEl.getAsJsonObject().getAsJsonObject("type").get("name").getAsString());
        }

        // Trích xuất mô tả từ speciesData
        String description = "No description available.";
        JsonArray flavorTexts = speciesData.getAsJsonArray("flavor_text_entries");
        for (JsonElement textEl : flavorTexts) {
            JsonObject textObj = textEl.getAsJsonObject();
            if (textObj.getAsJsonObject("language").get("name").getAsString().equals("en")) {
                description = textObj.get("flavor_text").getAsString().replace('\n', ' ').replace('\f', ' ');
                break; // Dừng lại khi tìm thấy mô tả tiếng Anh đầu tiên
            }
        }

        // Truyền description vào constructor của Pokemon
        return new Pokemon(name, imageUrl, hp, attack, defense, speed, types, description, evolutions);
    }
    // ------------------------------------

    private interface EvolutionChainCallback {
        void onComplete(List<EvolutionStep> steps);
    }

    private void fetchEvolutionChain(String url, final EvolutionChainCallback callback) {
        apiService.getEvolutionChain(url).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Lỗi khi lấy chuỗi tiến hóa: " + response.message());
                    callback.onComplete(new ArrayList<>());
                    return;
                }
                List<EvolutionStep> steps = new ArrayList<>();
                parseEvolutionChain(response.body().getAsJsonObject("chain"), steps, callback);
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Thất bại khi gọi API chuỗi tiến hóa: ", t);
                callback.onComplete(new ArrayList<>());
            }
        });
    }

    private void parseEvolutionChain(JsonObject chain, List<EvolutionStep> steps, EvolutionChainCallback finalCallback) {
        if (chain == null) return;
        String name = chain.getAsJsonObject("species").get("name").getAsString();
        apiService.getPokemon(name).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getAsJsonObject("sprites").get("front_default").getAsString();
                    steps.add(new EvolutionStep(name, imageUrl));
                }
                JsonArray evolvesToArray = chain.getAsJsonArray("evolves_to");
                if (evolvesToArray.size() > 0) {
                    parseEvolutionChain(evolvesToArray.get(0).getAsJsonObject(), steps, finalCallback);
                } else {
                    finalCallback.onComplete(steps);
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Thất bại khi lấy ảnh sprite cho " + name, t);
                finalCallback.onComplete(steps);
            }
        });
    }
}