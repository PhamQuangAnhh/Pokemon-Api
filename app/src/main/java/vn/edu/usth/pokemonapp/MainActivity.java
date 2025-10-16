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

        // --- Liên kết các View từ layout ---
        viewPager = findViewById(R.id.pokemonViewPager);
        searchIcon = findViewById(R.id.searchIcon);
        initialLoadingProgressBar = findViewById(R.id.initialLoadingProgressBar);

        // --- Khởi tạo các ViewModel ---
        listViewModel = new ViewModelProvider(this).get(PokemonListViewModel.class);
        detailViewModel = new ViewModelProvider(this).get(PokemonDetailViewModel.class);

        // --- Cấu hình ViewPager ---
        viewPager.setOffscreenPageLimit(2);

        // --- Thiết lập sự kiện cho nút tìm kiếm ---
        searchIcon.setOnClickListener(v -> {
            SearchBottomSheetFragment searchFragment = new SearchBottomSheetFragment();
            searchFragment.show(getSupportFragmentManager(), "SearchBottomSheetFragment");
        });

        // --- Thiết lập các lắng nghe (Observer) cho ViewModel ---
        setupObservers();

        // --- Ra lệnh tải dữ liệu ban đầu ---
        listViewModel.loadPokemonList();
    }

    private void setupObservers() {
        // Lắng nghe danh sách Pokémon sẽ được hiển thị trên ViewPager
        listViewModel.displayedPokemonList.observe(this, pokemonList -> {
            if (pokemonList != null) {
                pagerAdapter = new PokemonPagerAdapter(this, pokemonList);
                viewPager.setAdapter(pagerAdapter);

                // Tải trước dữ liệu chi tiết cho các Pokémon lân cận
                if (pokemonList.size() > 1) detailViewModel.preloadPokemon(pokemonList.get(1).name);
                if (pokemonList.size() > 2) detailViewModel.preloadPokemon(pokemonList.get(2).name);
            }
        });

        // Lắng nghe lệnh điều hướng (ví dụ: sau khi tìm kiếm theo tên)
        listViewModel.navigateToPokemon.observe(this, pokemonToNavigate -> {
            if (pokemonToNavigate != null) {
                List<PokemonListItem> currentList = listViewModel.displayedPokemonList.getValue();
                if (currentList != null) {
                    for (int i = 0; i < currentList.size(); i++) {
                        if (currentList.get(i).name.equals(pokemonToNavigate.name)) {
                            viewPager.setCurrentItem(i, true); // Lướt đến trang của Pokémon được chọn
                            break;
                        }
                    }
                }
                listViewModel.onNavigationComplete(); // Báo cho ViewModel là đã điều hướng xong
            }
        });

        // Lắng nghe trạng thái đang tải
        listViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null) {
                initialLoadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Lắng nghe các thông báo lỗi
        listViewModel.error.observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}