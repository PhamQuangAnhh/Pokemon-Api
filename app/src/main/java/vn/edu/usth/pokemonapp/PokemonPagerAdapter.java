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
        // For each position in the ViewPager, create a new Fragment
        // and pass the name of the corresponding Pokémon into it.
        PokemonListItem currentPokemon = pokemonList.get(position);
        return PokemonDetailFragment.newInstance(currentPokemon.name);
    }

    @Override
    public int getItemCount() {
        // The ViewPager will have the same number of pages as the number of Pokémon in the list.
        return pokemonList.size();
    }
}
