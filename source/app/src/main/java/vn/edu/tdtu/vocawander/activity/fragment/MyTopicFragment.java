package vn.edu.tdtu.vocawander.activity.fragment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.AddVocab;
import vn.edu.tdtu.vocawander.activity.VocabularyListActivity;
import vn.edu.tdtu.vocawander.activity.adapter.PackageAdapter;
import vn.edu.tdtu.vocawander.model.Flashcard;
import vn.edu.tdtu.vocawander.model.Package;
import vn.edu.tdtu.vocawander.model.SharedViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTopicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTopicFragment extends Fragment {

    private ListView listView;
    private PackageAdapter adapter;
    private ArrayList<Package> packageList = new ArrayList<>();
    Package selectedTopic;

    private DatabaseReference topicsRef;
    private ArrayList<Flashcard> mPackage = new ArrayList<>();
    private static final int ADD_VOCAB_REQUEST_CODE = 100;

    private SharedViewModel sharedViewModel;

    public MyTopicFragment() {}
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_topic, container, false);

        listView = rootView.findViewById(R.id.rvPackage);
        listView.setOnCreateContextMenuListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Package pck = (Package) adapterView.getItemAtPosition(i);
                String userId = sharedViewModel.getId().getValue();

                Intent intent = new Intent(getContext(), VocabularyListActivity.class);
                intent.putExtra("ID_package", pck.getId());
                intent.putExtra("owner", pck.getOwner());
                intent.putExtra("userId", userId);
                intent.putExtra("topicName", pck.getName());
                startActivity(intent);
            }
        });

        adapter = new PackageAdapter(getContext(), R.layout.item_package, packageList);
        listView.setAdapter(adapter);

        FloatingActionButton addFab = rootView.findViewById(R.id.addFab);

        addFab.setOnClickListener(view -> {
            Intent intent = new Intent(rootView.getContext(), AddVocab.class);
            String userId = sharedViewModel.getId().getValue();
            intent.putExtra("ID", userId);
            startActivityForResult(intent, ADD_VOCAB_REQUEST_CODE);
        });

        topicsRef = FirebaseDatabase.getInstance().getReference("Topic");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = sharedViewModel.getId().getValue();
        db.collection("Topic").whereEqualTo("owner", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            packageList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                packageList.add(new Package(document.getId().toString(), document.getString("name"),
                                        document.getString("owner"), document.getString("img")));
                                PackageAdapter pkAdapter = new PackageAdapter(rootView.getContext(), R.layout.item_package, packageList);
                                listView.setAdapter(pkAdapter);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_VOCAB_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.topic, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        if (item.getItemId() == R.id.edit_topic) {
            editTopic(position);
            return true;
        }
        else if (item.getItemId() == R.id.delete_topic) {
            deleteTopic(position);
            return true;
        }
        else{
            return super.onContextItemSelected(item);
        }
    }

    private void editTopic(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_topic_dialog, null);
        builder.setView(dialogView);

        EditText editWord = dialogView.findViewById(R.id.editName);

        selectedTopic = packageList.get(position);

        editWord.setText(selectedTopic.getName());
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedName = editWord.getText().toString();

                selectedTopic.setName(updatedName);
                adapter.notifyDataSetChanged();

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                String id = selectedTopic.getId();
                if (id != null && !id.isEmpty()) {
                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("name", updatedName);
                    firestore.collection("Topic").document(id)
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
        builder.setTitle("Edit Topic");
        AlertDialog dialog = builder.create();
        dialog.show();
        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(getResources().getColor(R.color.primary));
        }
    }
    private void deleteTopic(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm deletion");
        builder.setMessage("Are you sure you want to delete this topic?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            Package deletedPackage = packageList.remove(position);
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.notifyDataSetChanged();
                String packageIdToDelete = deletedPackage.getId();
                if (packageIdToDelete != null && !packageIdToDelete.isEmpty()) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("Topic").document(packageIdToDelete)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    deleteVocabulariesUnderPackage(packageIdToDelete);
                                }
                            });
                } else {
                    Log.e("Delete", "ID chủ đề null hoặc trống!");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteVocabulariesUnderPackage(String packageId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Item").whereEqualTo("topic", packageId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Delete", "Xoá từ vựng thành công!");
                                    }
                                });
                        }
                    } else {
                        Log.d("Delete", "Error getting documents: ", task.getException());
                    }
                }
            });
    }
}
