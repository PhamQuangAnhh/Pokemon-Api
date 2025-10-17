package vn.edu.usth.pokemonapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PokemonListViewModel listViewModel;
    private PokemonDetailViewModel detailViewModel;
    private ViewPager2 viewPager;
    private PokemonPagerAdapter pagerAdapter;
    private ImageView searchIcon;
    private ProgressBar initialLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Link views from layout ---
        viewPager = findViewById(R.id.pokemonViewPager);
        searchIcon = findViewById(R.id.searchIcon);
        initialLoadingProgressBar = findViewById(R.id.initialLoadingProgressBar);

        // --- Initialize ViewModels ---
        listViewModel = new ViewModelProvider(this).get(PokemonListViewModel.class);
        detailViewModel = new ViewModelProvider(this).get(PokemonDetailViewModel.class);

        // --- Configure ViewPager ---
        viewPager.setOffscreenPageLimit(2);

        // --- Set up event for the search button ---
        searchIcon.setOnClickListener(v -> {
            SearchBottomSheetFragment searchFragment = new SearchBottomSheetFragment();
            searchFragment.show(getSupportFragmentManager(), "SearchBottomSheetFragment");
        });

        // --- Set up observers for ViewModel ---
        setupObservers();

        // --- Trigger initial data loading ---
        listViewModel.loadPokemonList();
    }

    private void setupObservers() {
        // Observe the list of Pokémon to be displayed on the ViewPager
        listViewModel.displayedPokemonList.observe(this, pokemonList -> {
            if (pokemonList != null) {
                pagerAdapter = new PokemonPagerAdapter(this, pokemonList);
                viewPager.setAdapter(pagerAdapter);

                // Preload detail data for nearby Pokémon
                if (pokemonList.size() > 1) detailViewModel.preloadPokemon(pokemonList.get(1).name);
                if (pokemonList.size() > 2) detailViewModel.preloadPokemon(pokemonList.get(2).name);
            }
        });

        // Observe navigation commands (e.g., after searching by name)
        listViewModel.navigateToPokemon.observe(this, pokemonToNavigate -> {
            if (pokemonToNavigate != null) {
                List<PokemonListItem> currentList = listViewModel.displayedPokemonList.getValue();
                if (currentList != null) {
                    for (int i = 0; i < currentList.size(); i++) {
                        if (currentList.get(i).name.equals(pokemonToNavigate.name)) {
                            viewPager.setCurrentItem(i, true); // Scroll to the page of the selected Pokémon
                            break;
                        }
                    }
                }
                listViewModel.onNavigationComplete(); // Notify the ViewModel that navigation is complete
            }
        });

        // Observe the loading state
        listViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null) {
                initialLoadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Observe error messages
        listViewModel.error.observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
