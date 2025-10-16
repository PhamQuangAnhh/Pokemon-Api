package vn.edu.usth.pokemonapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private PokemonListViewModel listViewModel;
    private PokemonDetailViewModel detailViewModel; // ViewModel chung cho các Fragment chi tiết
    private ViewPager2 viewPager;
    private PokemonPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.pokemonViewPager);

        // Khởi tạo cả hai ViewModel
        listViewModel = new ViewModelProvider(this).get(PokemonListViewModel.class);
        detailViewModel = new ViewModelProvider(this).get(PokemonDetailViewModel.class);

        // --- CÀI ĐẶT TẢI TRƯỚC CHO VIEWPAGER2 ---
        // Giữ 2 trang ở mỗi bên. Tổng cộng 1 (hiện tại) + 2 (trái) + 2 (phải) = 5 Fragment sẽ được giữ trong bộ nhớ.
        viewPager.setOffscreenPageLimit(2);
        // ------------------------------------------

        // Lắng nghe danh sách Pokémon
        listViewModel.pokemonList.observe(this, pokemonList -> {
            pagerAdapter = new PokemonPagerAdapter(this, pokemonList);
            viewPager.setAdapter(pagerAdapter);

            // --- BẮT ĐẦU TẢI TRƯỚC DỮ LIỆU ---
            // Tải trước cho 2 Pokémon tiếp theo trong danh sách
            if (pokemonList.size() > 1) {
                detailViewModel.preloadPokemon(pokemonList.get(1).name);
            }
            if (pokemonList.size() > 2) {
                detailViewModel.preloadPokemon(pokemonList.get(2).name);
            }
            // ------------------------------------
        });

        listViewModel.error.observe(this, errorMessage -> {
            Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        });

        // Bắt đầu tải danh sách Pokémon
        listViewModel.loadPokemonList();
    }
}