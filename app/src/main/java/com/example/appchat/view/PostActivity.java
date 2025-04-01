package com.example.appchat.view;

import com.example.appchat.R;
import com.example.appchat.adapters.ImageAdapter;
import com.example.appchat.databinding.ActivityPostBinding;
import com.example.appchat.util.ImageUtils;
import com.example.appchat.viewmodel.PostViewModel;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.appchat.model.Post;
import com.example.appchat.util.Validaciones;
import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private static final int MAX_IMAGES = 3;
    private final int REQUEST_IMAGE = 1;
    private ActivityPostBinding binding;

    private PostViewModel postViewModel;
    private final List<String> imagenesUrls = new ArrayList<>();
    private ImageAdapter adapter;
    private String categoria;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar el OnClickListener para el ImageView backMain
        binding.backMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra la actividad actual y regresa a la anterior
                finish();
            }
        });

        setupRecyclerView();
        setupViewModels();
        setupCategorySpinner();
        setupGalleryLauncher();
        binding.btnPublicar.setOnClickListener(v -> publicarPost());
    }

    private void setupViewModels() {
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPostSuccess().observe(this, exito -> {
            Toast.makeText(this, exito, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, getResources().getStringArray(R.array.categorias_array)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoria.setAdapter(adapter);
        binding.spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoria = null;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            // Selección múltiple de imágenes
                            int itemCount = data.getClipData().getItemCount();
                            for (int i = 0; i < itemCount; i++) {
                                if (imagenesUrls.size() < MAX_IMAGES) {
                                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                    subirImagen(imageUri);
                                } else {
                                    Toast.makeText(this, "Máximo de imágenes alcanzado", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        } else if (data.getData() != null) {
                            // Selección de una sola imagen
                            Uri imageUri = data.getData();
                            if (imagenesUrls.size() < MAX_IMAGES) {
                                subirImagen(imageUri);
                            } else {
                                Toast.makeText(this, "Máximo de imágenes alcanzado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        binding.uploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Permitir selección múltiple
            galleryLauncher.launch(intent);
        });
    }

    private void subirImagen(Uri imageUri) {
        ImageUtils.subirImagenAParse(PostActivity.this, imageUri, new ImageUtils.ImageUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                Log.d("PostActivity", "Imagen subida con éxito: " + imageUrl);
                imagenesUrls.add(imageUrl);
                adapter.notifyDataSetChanged();
                updateRecyclerViewVisibility();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("PostActivity", "Error al subir la imagen", e);
                Toast.makeText(PostActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void publicarPost() {
        String titulo = binding.itTitulo.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        String duracionStr = binding.etDuracion.getText().toString().trim();
        String presupuestoStr = binding.etPresupuesto.getText().toString().trim();

        if (!Validaciones.validarTexto(titulo)) {
            binding.itTitulo.setError("El título no es válido");
            return;
        }
        if (!Validaciones.validarTexto(descripcion)) {
            binding.etDescripcion.setError("La descripción no es válida");
            return;
        }
        int duracion = Validaciones.validarNumero(duracionStr);
        if (duracion == -1) {
            binding.etDuracion.setError("Duración no válida");
            return;
        }
        double presupuesto;
        try {
            presupuesto = Double.parseDouble(presupuestoStr);
        } catch (NumberFormatException e) {
            binding.etPresupuesto.setError("Presupuesto no válido");
            return;
        }
        Post post = new Post();
        post.setTitulo(titulo);
        post.setDescripcion(descripcion);
        post.setDuracion(duracion);
        post.setCategoria(categoria);
        post.setPresupuesto(presupuesto);
        post.setImagenes(new ArrayList<>(imagenesUrls));
        postViewModel.publicar(post).observe(this, result -> {
            if (result != null) {
                Toast.makeText(this, "Post publicado con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al publicar el post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerViewVisibility() {
        boolean hasImages = !imagenesUrls.isEmpty();
        binding.recyclerView.setVisibility(hasImages ? View.VISIBLE : View.GONE);
        binding.uploadImage.setVisibility(imagenesUrls.size() < MAX_IMAGES ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_IMAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("PostActivity", "Permiso concedido, abriendo galería");
            ImageUtils.openGallery(PostActivity.this, galleryLauncher);
        } else {
            Log.d("PostActivity", "Permiso denegado");
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new ImageAdapter(imagenesUrls, this, new ImageAdapter.OnImageRemoveListener() {
            @Override
            public void onImageRemove(int position) {
                imagenesUrls.remove(position);
                adapter.notifyDataSetChanged();
                updateRecyclerViewVisibility();
            }
        });
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.setAdapter(adapter);
        updateRecyclerViewVisibility();
    }
}