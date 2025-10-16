package vn.edu.usth.pokemonapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class PokemonPagerAdapter extends FragmentStateAdapter {

    private List<PokemonListItem> pokemonList;

    public PokemonPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<PokemonListItem> pokemonList) {
        super(fragmentActivity);
        this.pokemonList = pokemonList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Với mỗi vị trí trong ViewPager, tạo một Fragment mới
        // và truyền tên của Pokémon tương ứng vào đó.
        PokemonListItem currentPokemon = pokemonList.get(position);
        return PokemonDetailFragment.newInstance(currentPokemon.name);
    }

    @Override
    public int getItemCount() {
        // ViewPager sẽ có số trang bằng số Pokémon trong danh sách.
        return pokemonList.size();
    }
}
