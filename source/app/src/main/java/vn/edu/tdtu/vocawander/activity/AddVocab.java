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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import vn.edu.tdtu.vocawander.util.TranslateApi;

public class AddVocab extends AppCompatActivity {
    private final String ENG = "en";
    private final String VIE = "vi";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 123;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 124;

    private static final int GALLERY_TO_UPLOAD_PERMISSION_REQUEST_CODE = 127;

    private static final int PICK_CSV_FILE_REQUEST_CODE = 125;
    private List<Vocabulary> vocabularyList = new ArrayList<>();

    private List<String> vietnameseWords = new ArrayList<>();

    private TranslateApi translate = new TranslateApi();

    private Set<String> vocabularySet = new HashSet<>();

    private VocabularyAdapter adapter;

    ObjectDetector detector;

    private String imgUrl;
    private StorageReference mStorageRef;
    private Uri imgUri;
    private boolean isPublic = false;
    private RecyclerView recyclerView;
    private String userId;

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vocab);
        mStorageRef = FirebaseStorage.getInstance().getReference();
//        progressBar = findViewById(R.id.progressBar);

        Button addNewWord = findViewById(R.id.addWordBtn);

        TextView fileBtn = findViewById(R.id.attachFile);
        TextView cameraBtn = findViewById(R.id.importCamera);
        TextView done = findViewById(R.id.done);
        EditText topicName = findViewById(R.id.topicName);
        Switch publicSwitch = findViewById(R.id.isPublic);

        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
        if (intent != null) {
            if (index == 1) {
                TextView header = findViewById(R.id.intro);
                header.setText("Vocabulary");
                topicName.setVisibility(View.GONE);
                publicSwitch.setVisibility(View.GONE);
            } else {
                userId = intent.getStringExtra("ID");
            }
        }
        publicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPublic = isChecked;
            }
        });

        recyclerView = findViewById(R.id.recyclerVocab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VocabularyAdapter(vocabularyList, this);

        recyclerView.setAdapter(adapter);

        detector = new ObjectDetector();
        detector.setModelFile("model.tflite");
        detector.initialModel(this);

        addNewWord.setOnClickListener(view -> {
            vocabularyList.add(new Vocabulary());
//            openFileChooser();
            adapter.notifyItemInserted(vocabularyList.size() - 1);
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == 0) {
                    String idT;
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("Topic").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    String idTopic = "";
                                    if((task.getResult().size()+1)<10){
                                        idTopic = "TP000"+(task.getResult().size()+1);
                                    }else if((task.getResult().size()+1)<100){
                                        idTopic = "TP00"+(task.getResult().size()+1);
                                    } else if ((task.getResult().size()+1)<1000) {
                                        idTopic = "TP0"+(task.getResult().size()+1);
                                    }else {
                                        idTopic = "TP"+(task.getResult().size()+1);
                                    }
                                    Map<String,Object> topic = new HashMap<>();
                                    topic.put("name", topicName.getText().toString());
                                    topic.put("img", "https://firebasestorage.googleapis.com/v0/b/vocawander-c11a9.appspot.com/o/png-transparent-computer-icons-implementation-networking-topics-orange-art-system_preview_rev_1.png?alt=media&token=85bc90b3-eb92-4804-ad6b-cd82333bbff3");
                                    topic.put("owner", userId);
                                    topic.put("public", isPublic);
                                    firestore.collection("Topic").document(idTopic).set(topic);
                                    vocabularyList = adapter.getList();
                                    for(Vocabulary i: vocabularyList){
                                        Map<String,Object> item = new HashMap<>();
                                        item.put("name",i.getWord());
                                        item.put("trans",i.getMeaning());
                                        item.put("example", i.getExample());
                                        item.put("topic",idTopic);
                                        item.put("img",i.getImgUrl());
                                        firestore.collection("Item").add(item);
                                    }
                                }
                            });
                } else {
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
                }
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        fileBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PICK_CSV_FILE_REQUEST_CODE
                );
            } else {
                pickCsvFile();
            }
        });

        cameraBtn.setOnClickListener(view -> {
            checkCameraPermission();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_CSV_FILE_REQUEST_CODE) {
            handleCsvFilePermissionResult(grantResults);
        } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            handleCameraPermissionResult(grantResults);
        }
    }

    private void handleCsvFilePermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed to pick CSV file
            pickCsvFile();
        } else {
            // Permission denied, show a message or take appropriate action
            Toast.makeText(this, "Permission denied. Cannot read CSV file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCameraPermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }


    private void pickCsvFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");

        startActivityForResult(Intent.createChooser(intent, "Select CSV File"), PICK_CSV_FILE_REQUEST_CODE);
    }



    private void readCsvFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Split the line into word, meaning, and example using the CSV format
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String word = parts[0].trim();
                        String meaning = parts[1].trim();
                        String example = parts[2].trim();
                        vocabularyList.add(new Vocabulary(word, meaning, example, ""));
                        adapter.notifyItemInserted(vocabularyList.size() - 1);
                    }
                }
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading CSV file", Toast.LENGTH_SHORT).show();
        }
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
        } else if (requestCode == PICK_CSV_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    readCsvFile(uri);
                }
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
                getTranslation(vocabulary, new TranslationCallback() {
                    @Override
                    public void onTranslationCompleted(String translatedText) {
                        // Access the translated text or do further processing here
                        vocabularyList.add(new Vocabulary(vocabulary, translatedText.toLowerCase(), "", ""));
                        adapter.notifyItemInserted(vocabularyList.size() - 1);
                    }

                    @Override
                    public void onTranslationError(Exception e) {
                        // Handle translation error here
                    }
                });

            }
        }
    }

    private void getTranslation(String word, final TranslationCallback callback) {
        TranslateApi translate = new TranslateApi();
        translate.setOnTranslationCompleteListener(new TranslateApi.OnTranslationCompleteListener() {
            @Override
            public void onStartTranslation() {
                // Here you can perform initial work before translating the text, like displaying a progress bar
            }

            @Override
            public void onCompleted(String text) {
                String translatedText = text.toLowerCase();
                callback.onTranslationCompleted(translatedText);
            }

            @Override
            public void onError(Exception e) {
                callback.onTranslationError(e);
            }
        });
        translate.execute(word, ENG, VIE);
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
                Toast.makeText(AddVocab.this, "Upload failed", Toast.LENGTH_LONG).show();
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

    public interface TranslationCallback {
        void onTranslationCompleted(String translatedText);
        void onTranslationError(Exception e);
    }
}