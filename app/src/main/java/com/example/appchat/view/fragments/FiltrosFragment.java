package com.example.appchat.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchat.R;
import com.example.appchat.adapters.PostAdapter;
import com.example.appchat.model.Post;
import com.example.appchat.providers.PostProvider;
import java.util.ArrayList;
import java.util.List;

public class FiltrosFragment extends Fragment {

    private PostProvider postProvider;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private EditText editTextSearch;
    private Button buttonSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtros, container, false);

        // Inicializar las vistas
        postProvider = new PostProvider();
        recyclerView = view.findViewById(R.id.recyclerViewFiltros);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonSearch = view.findViewById(R.id.buttonSearch);

        // Configurar el RecyclerView
        postAdapter = new PostAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postAdapter);

        // Configurar el botón de búsqueda
        buttonSearch.setOnClickListener(v -> {
            String searchText = editTextSearch.getText().toString();
            filterPosts(searchText);
        });

        return view;
    }

    private void filterPosts(String searchText) {
        // Obtener todos los posts
        postProvider.getAllPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if (posts != null) {
                    List<Post> filteredPosts = new ArrayList<>();
                    for (Post post : posts) {
                        // Filtrar por título o descripción
                        if (post.getTitulo().toLowerCase().contains(searchText.toLowerCase()) ||
                                post.getDescripcion().toLowerCase().contains(searchText.toLowerCase())) {
                            filteredPosts.add(post);
                        }
                    }
                    // Actualizar el adaptador con los posts filtrados
                    postAdapter.updatePosts(filteredPosts);
                } else {
                    Log.e("FiltrosFragment", "No se pudieron obtener los posts.");
                }
            }
        });
    }
}