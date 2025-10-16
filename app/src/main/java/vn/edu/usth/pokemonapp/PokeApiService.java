package vn.edu.usth.pokemonapp;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PokeApiService {

    @GET("pokemon/{name}")
    Call<JsonObject> getPokemon(@Path("name") String name);

    @GET("pokemon-species/{name}")
    Call<JsonObject> getPokemonSpecies(@Path("name") String name);

    @GET
    Call<JsonObject> getEvolutionChain(@Url String url);

    @GET("pokemon")
    Call<JsonObject> getPokemonList(@Query("limit") int limit, @Query("offset") int offset);

    @GET("type/{name}")
    Call<JsonObject> getPokemonByType(@Path("name") String name);
}