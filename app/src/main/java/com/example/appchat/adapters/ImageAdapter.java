package com.example.appchat.adapters;
import com.example.appchat.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageUrls;
    private Context context;
    private OnImageRemoveListener removeListener; // Listener para eliminar imágenes

    public ImageAdapter(List<String> imageUrls, Context context, OnImageRemoveListener removeListener) {
        this.imageUrls = imageUrls;
        this.context = context;
        this.removeListener = removeListener; // Inicializar el listener
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Picasso.get()
                .load(imageUrl)  // Usar la URL de la imagen
                .into(holder.imageView);  // Asignar a la vista de imagen

        // Configurar el ícono de eliminar
        holder.removeImage.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onImageRemove(position); // Notificar la eliminación
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void updateImages(List<String> newImageUrls) {
        this.imageUrls = newImageUrls;
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView removeImage; // Ícono de eliminar

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);  // ID de tu ImageView
            removeImage = itemView.findViewById(R.id.removeImage); // ID del ícono de eliminar
        }
    }

    // Interfaz para manejar la eliminación de imágenes
    public interface OnImageRemoveListener {
        void onImageRemove(int position);
    }
}