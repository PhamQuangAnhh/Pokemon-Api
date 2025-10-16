package vn.edu.usth.pokemonapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;

public class SearchBottomSheetFragment extends BottomSheetDialogFragment {

    private PokemonListViewModel viewModel;
    private SearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PokemonListViewModel.class);

        EditText searchEditText = view.findViewById(R.id.searchEditText);
        RecyclerView searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        TextView resetSearchButton = view.findViewById(R.id.resetSearchButton);

        adapter = new SearchAdapter(new ArrayList<>(), item -> {
            viewModel.navigateToPokemon(item);
            dismiss();
        });
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRecyclerView.setAdapter(adapter);

        // Lắng nghe kết quả tìm kiếm theo tên từ ViewModel
        viewModel.searchResults.observe(getViewLifecycleOwner(), results -> {
            if (results != null) {
                adapter.updateData(results);
            }
        });

        // Lắng nghe sự kiện gõ chữ để tìm kiếm theo tên real-time
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Lắng nghe sự kiện nhấn "Search" trên bàn phím để tìm theo HỆ
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString();
                viewModel.executeTypeSearch(query);
                dismiss();
                return true;
            }
            return false;
        });

        // Lắng nghe sự kiện nhấn nút Reset
        resetSearchButton.setOnClickListener(v -> {
            viewModel.resetSearch();
            dismiss();
        });
    }
}