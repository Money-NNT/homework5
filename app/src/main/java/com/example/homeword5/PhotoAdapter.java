package com.example.homeword5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Photo> photoList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener { void onItemClick(Photo photo); }

    public PhotoAdapter(List<Photo> photoList, OnItemClickListener listener) {
        this.photoList = photoList;
        this.listener = listener;
    }

    @NonNull @Override public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.bind(photoList.get(position), listener);
    }

    @Override public int getItemCount() { return photoList.size(); }
    public void setPhotos(List<Photo> photos) { this.photoList = photos; notifyDataSetChanged(); }
    public List<Photo> getPhotos() { return photoList; }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage; TextView itemTitle; TextView itemDescription;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDescription = itemView.findViewById(R.id.item_description);
        }
        public void bind(final Photo photo, final OnItemClickListener listener) {
            itemTitle.setText(photo.getTitle());
            itemDescription.setText(photo.getDescription());
            if (photo.getImage() != null && photo.getImage().length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(photo.getImage(), 0, photo.getImage().length);
                itemImage.setImageBitmap(bitmap);
            } else {
                itemImage.setImageResource(R.drawable.ic_image_placeholder);
            }
            itemView.setOnClickListener(v -> listener.onItemClick(photo));
        }
    }
}