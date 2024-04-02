package vn.edu.tdtu.vocawander.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.adapter.VocabAdapter;
import vn.edu.tdtu.vocawander.model.Vocabulary;
public class VocabularyListActivity extends AppCompatActivity {
    private ListView listView;
    private TextToSpeech textToSpeech;
    private ArrayList<Vocabulary> vocabularies = new ArrayList<>();
    private ImageView imageView, btnBack;
    Vocabulary selectedVocabulary;
    private TextView export;

    private String idPck, owner, userId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void exportToCSV(String filename) {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, filename);

        try {
            // Use FileOutputStream with UTF-8 encoding
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");

            for (Vocabulary vocabulary : vocabularies) {
                String eng = vocabulary.getWord();
                String vie = vocabulary.getMeaning();
                String eg = vocabulary.getExample();

                String line = "\"" + eng + "\",\"" + vie + "\",\"" + eg + "\"\n";
                writer.write(line);
            }

            writer.close();
            fos.close();

            String[] pathElements = file.getAbsolutePath().split("/");
            String filePath = pathElements[pathElements.length - 2] + "/" + pathElements[pathElements.length - 1];

            Toast.makeText(this, "CSV Exported to " + filePath , Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting CSV", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_vocabulary);

        final Context context = VocabularyListActivity.this;
        idPck = getIntent().getStringExtra("ID_package");
        owner = getIntent().getStringExtra("owner");
        userId = getIntent().getStringExtra("userId");
        listView = findViewById(R.id.listVocab);
        btnBack = findViewById(R.id.btnBack);
        export = findViewById(R.id.export);

        export.setOnClickListener(view -> {
            String filename = getIntent().getStringExtra("topicName") + ".csv";
            if (filename != null) {
                exportToCSV(filename);
            }
        });


        listView.setOnCreateContextMenuListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameToSpeak = vocabularies.get(position).getWord();
                speak(nameToSpeak);
            }
        });

        db.collection("Item").whereEqualTo("topic", idPck).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            vocabularies.clear();
                            for (DocumentSnapshot doc : task.getResult()) {
                                vocabularies.add(new Vocabulary(doc.getId(), doc.getString("name"), doc.getString("topic"), doc.getString("trans"), doc.getString("example"), doc.getString("img")));
                                Collections.shuffle(vocabularies);
                                VocabAdapter listAdapter = new VocabAdapter(context, R.layout.item_vocab, vocabularies);
                                listView.setAdapter(listAdapter);
                            }
                        }
                        else {

                        }
                    }
                });

        FloatingActionButton addFab = findViewById(R.id.addFab);
        if (!owner.equals(userId)) {
            addFab.setVisibility(View.GONE);
        }
        addFab.setOnClickListener(view -> {
            Intent intent = new Intent(VocabularyListActivity.this, AddVocab.class);
            String idPck = getIntent().getStringExtra("ID_package");
            intent.putExtra("index", 1);
            intent.putExtra("ID_package", idPck);
            startActivity(intent);
        });
        btnBack.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.vocab, menu);

        if (!userId.equals(owner)) {
            // If different, hide the menu items
            menu.findItem(R.id.edit_vocabulary).setVisible(false);
            menu.findItem(R.id.delete_vocabulary).setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
            if (item.getItemId()== R.id.edit_vocabulary){
                editVocabulary(position);
                return true;
            }
            else if (item.getItemId()== R.id.delete_vocabulary) {
                deleteVocabulary(position);
                return true;
            }
            else{
                return super.onContextItemSelected(item);
        }
    }
    public void speak(final String text){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS", "Language not supported");
                    } else {
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Log.e("TTS", "Failed");
                }
            }
        });
    }
    private void deleteVocabulary(int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Deletion");
        alertDialogBuilder.setMessage("Are you sure you want to delete this vocabulary?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Vocabulary deletedVocabulary = vocabularies.remove(position);
                ((VocabAdapter) listView.getAdapter()).notifyDataSetChanged();

                String vocabularyIdToDelete = deletedVocabulary.getId();
                if (vocabularyIdToDelete != null && !vocabularyIdToDelete.isEmpty()) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("Item").document(vocabularyIdToDelete)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Delete", "Xóa tài liệu thành công!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Delete", "Lỗi xóa tài liệu", e);
                                }
                            });
                } else {
                    Log.e("Delete", "ID từ vựng null hoặc trống!");
                }
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void editVocabulary(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_vocabulary_dialog, null);
        builder.setView(dialogView);

        EditText editWord = dialogView.findViewById(R.id.editWord);
        EditText editMeaning = dialogView.findViewById(R.id.editMeaning);
        EditText editExample = dialogView.findViewById(R.id.editExample);
        imageView = dialogView.findViewById(R.id.imageView);

        selectedVocabulary = vocabularies.get(position);

        editWord.setText(selectedVocabulary.getWord());
        editMeaning.setText(selectedVocabulary.getMeaning());
        editExample.setText(selectedVocabulary.getExample());


        String imageUrl = selectedVocabulary.getImgUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.with(this).load(imageUrl).into(imageView);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedWord = editWord.getText().toString();
                String updatedMeaning = editMeaning.getText().toString();
                String updatedExample = editExample.getText().toString();

                selectedVocabulary.setWord(updatedWord);
                selectedVocabulary.setMeaning(updatedMeaning);
                selectedVocabulary.setExample(updatedExample);

                ((VocabAdapter) listView.getAdapter()).notifyDataSetChanged();

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                String vocabularyId = selectedVocabulary.getId();
                if (vocabularyId != null && !vocabularyId.isEmpty()) {
                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("name", updatedWord);
                    updatedData.put("trans", updatedMeaning);
                    updatedData.put("example", updatedExample);

                    firestore.collection("Item").document(vocabularyId)
                            .update(updatedData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                } else {
                    Log.e("Edit", "Vocabulary ID is null or empty!");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle("Edit Vocabulary");
        AlertDialog dialog = builder.create();
        dialog.show();
        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(getResources().getColor(R.color.primary));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

            UploadTask uploadTask = imageRef.putFile(selectedImageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String newImageUrl = uri.toString();
                            Picasso.with(VocabularyListActivity.this)
                                    .load(newImageUrl)
                                    .into(imageView);

                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            String vocabularyId = selectedVocabulary.getId();
                            if (vocabularyId != null && !vocabularyId.isEmpty()) {
                                Map<String, Object> updatedData = new HashMap<>();
                                updatedData.put("img", newImageUrl);

                                firestore.collection("Item").document(vocabularyId)
                                        .update(updatedData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Image updated successfully
                                            }
                                        });
                            } else {
                                Log.e("Edit", "Vocabulary ID is null or empty!");
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.collection("Item").whereEqualTo("topic", idPck).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            vocabularies.clear();
                            for (DocumentSnapshot doc : task.getResult()) {
                                vocabularies.add(new Vocabulary(doc.getId(), doc.getString("name"), doc.getString("topic"), doc.getString("trans"), doc.getString("example"), doc.getString("img")));
                                Collections.shuffle(vocabularies);
                                VocabAdapter listAdapter = new VocabAdapter(getApplicationContext(), R.layout.item_vocab, vocabularies);
                                listView.setAdapter(listAdapter);
                            }
                        }
                        else {

                        }
                    }
                });
    }
}
