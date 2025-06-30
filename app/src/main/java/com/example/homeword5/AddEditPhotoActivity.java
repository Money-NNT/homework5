package com.example.homeword5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddEditPhotoActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ImageView imageViewPhoto;
    private EditText editTextTitle, editTextDescription;
    private Button buttonSave;
    private DatabaseHelper dbHelper;
    private long photoId = -1;
    private byte[] currentImageBytes = null;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    try {
                        Bitmap bitmap = uriToBitmap(result.getData().getData());
                        imageViewPhoto.setImageBitmap(bitmap);
                        currentImageBytes = bitmapToBytes(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_photo);
        imageViewPhoto = findViewById(R.id.imageView_photo);
        editTextTitle = findViewById(R.id.editText_title);
        editTextDescription = findViewById(R.id.editText_description);
        buttonSave = findViewById(R.id.button_save);
        dbHelper = new DatabaseHelper(this);

        photoId = getIntent().getLongExtra("PHOTO_ID", -1);
        if (photoId != -1) { setTitle("Edit Photo"); loadPhotoData(); } else { setTitle("Add Photo"); }

        imageViewPhoto.setOnClickListener(v -> checkAndRequestPermission());
        buttonSave.setOnClickListener(v -> savePhoto());
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else { openGallery(); }
        } else { openGallery(); }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else { Toast.makeText(this, "Permission denied. Cannot access photos.", Toast.LENGTH_SHORT).show(); }
        }
    }

    private void loadPhotoData() {
        Photo photo = dbHelper.getPhotoById(photoId);
        if (photo != null) {
            editTextTitle.setText(photo.getTitle());
            editTextDescription.setText(photo.getDescription());
            currentImageBytes = photo.getImage();
            if (currentImageBytes != null) {
                imageViewPhoto.setImageBitmap(BitmapFactory.decodeByteArray(currentImageBytes, 0, currentImageBytes.length));
            }
        }
    }

    private void savePhoto() {
        String title = editTextTitle.getText().toString().trim();
        if (title.isEmpty()) { editTextTitle.setError("Title cannot be empty"); return; }
        if (currentImageBytes == null) { Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show(); return; }
        String description = editTextDescription.getText().toString().trim();
        Photo photo = (photoId == -1) ? new Photo(title, description, currentImageBytes)
                : new Photo(photoId, title, description, currentImageBytes);
        if (photoId == -1) { dbHelper.addPhoto(photo); Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show(); }
        else { dbHelper.updatePhoto(photo); Toast.makeText(this, "Photo updated", Toast.LENGTH_SHORT).show(); }
        finish();
    }

    private Bitmap uriToBitmap(Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), uri));
        } else { return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri); }
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}