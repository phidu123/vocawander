package vn.edu.tdtu.vocawander.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.adapter.VocabularyAdapter;
import vn.edu.tdtu.vocawander.model.SharedViewModel;
import vn.edu.tdtu.vocawander.model.Vocabulary;
import vn.edu.tdtu.vocawander.util.ObjectDetector;
import vn.edu.tdtu.vocawander.util.Recognition;

public class NewVocabularyActivity extends AppCompatActivity {
    private SharedViewModel sharedViewModel;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 123;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 124;

    private static final int GALLERY_TO_UPLOAD_PERMISSION_REQUEST_CODE = 127;
    private List<Vocabulary> vocabularyList = new ArrayList<>();

    private Set<String> vocabularySet = new HashSet<>();

    private VocabularyAdapter adapter;

    ObjectDetector detector;
    private String imgUrl;
    private StorageReference mStorageRef;
    private Uri imgUri;
    private RecyclerView recyclerView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Button addNewWord = findViewById(R.id.addWordBtn);

        TextView fileBtn = findViewById(R.id.attachFile);
        TextView cameraBtn = findViewById(R.id.importCamera);
        TextView done = findViewById(R.id.done);

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("ID");
        }

        recyclerView = findViewById(R.id.recyclerVocab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VocabularyAdapter(vocabularyList, this);

        VocabularyAdapter adapter = new VocabularyAdapter(vocabularyList, NewVocabularyActivity.this);

        recyclerView.setAdapter(adapter);

        detector = new ObjectDetector();
        detector.setModelFile("model.tflite");
        detector.initialModel(this);

        addNewWord.setOnClickListener(view -> {
            vocabularyList.add(new Vocabulary());
            adapter.notifyItemInserted(vocabularyList.size() - 1);
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idPck = getIntent().getStringExtra("ID_package");
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    vocabularyList = adapter.getList();
                    for(Vocabulary i: vocabularyList){
                        Map<String,Object> item = new HashMap<>();
                        item.put("name",i.getWord());
                        item.put("trans",i.getMeaning());
                        item.put("topic",idPck);
                        item.put("img",i.getImgUrl());
                        item.put("example", i.getExample());
                        firestore.collection("Item").add(item);
                    }
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        fileBtn.setOnClickListener(view -> {

        });

        cameraBtn.setOnClickListener(view -> {
            checkCameraPermission();
        });
    }

    private void checkCameraPermission() {
        // Check if the CAMERA permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the CAMERA permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, proceed with opening the camera
            openCamera();
        }
    }

    private void openCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Open the camera
                        openCameraIntent();
                        break;
                    case 1:
                        // Open the gallery
                        openGalleryIntent();
                        break;
                }
            }
        });
        builder.show();
    }

    private void openCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if there's a camera app available on the device
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (galleryIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(galleryIntent, GALLERY_PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No gallery app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
                performDetection(mutableBitmap);
            }
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                if (inputStream != null) {
                    inputStream.close();
                }

                performDetection(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == GALLERY_TO_UPLOAD_PERMISSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int itemPosition = adapter.ITEM_POSITION;

            imgUri = data.getData();

            if (itemPosition != RecyclerView.NO_POSITION) {
                upLoadFile(itemPosition);
            }

        }
    }

    private void performDetection(Bitmap bitmap) {
        if (bitmap != null) {
            ArrayList<Recognition> recognitions =  detector.detect(bitmap);

            // create a set of vocabulary and add to recycler view
            for(Recognition recognition: recognitions){
                if(recognition.getConfidence() > 0.4){
                    vocabularySet.add(recognition.getLabelName());
                }
            }

            for (String vocabulary : vocabularySet) {
                String idPck = getIntent().getStringExtra("ID_package");
                vocabularyList.add(new Vocabulary(vocabulary, idPck, "", "", ""));
                adapter.notifyItemInserted(vocabularyList.size() - 1);
            }
        }
    }



    private void upLoadFile(int position){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(imgUri != null){
            StorageReference storageReference = mStorageRef.child(UUID.randomUUID().toString());
            storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imgUrl = uri.toString();
                                    Vocabulary vocabulary = vocabularyList.get(position);
                                    vocabulary.setImgUrl(imgUrl);
                                    adapter.notifyDataSetChanged();

                                }
                            });
                            progressDialog.setMessage("Uploading 100%");
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Success" , Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(e -> {
                        // Upload failure
                        progressDialog.dismiss();
                        Toast.makeText(NewVocabularyActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        // Show a progress dialog or update a ProgressBar here
                        int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading " + progress + "%");
                    });
        }else {
            Toast.makeText(getApplicationContext(), "No image chosen" , Toast.LENGTH_LONG).show();
        }
    }
}