package com.example.homeword5;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private DatabaseHelper dbHelper;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_spacing);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(spacingInPixels));
        photoAdapter = new PhotoAdapter(new ArrayList<>(), photo -> {
            Intent intent = new Intent(MainActivity.this, AddEditPhotoActivity.class);
            intent.putExtra("PHOTO_ID", photo.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(photoAdapter);
        findViewById(R.id.fab_add_photo).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, AddEditPhotoActivity.class)));
        setupSwipeToDelete();
    }

    @Override protected void onResume() {
        super.onResume();
        loadPhotosFromDatabase();
    }

    private void loadPhotosFromDatabase() {
        photoAdapter.setPhotos(dbHelper.getAllPhotos());
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) { return false; }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Photo deletedPhoto = photoAdapter.getPhotos().get(position);
                dbHelper.deletePhoto(deletedPhoto.getId());
                loadPhotosFromDatabase();
                Snackbar.make(recyclerView, "Photo deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", v -> {
                            dbHelper.addPhoto(deletedPhoto);
                            loadPhotosFromDatabase();
                        }).show();
            }
        }).attachToRecyclerView(recyclerView);
    }
}