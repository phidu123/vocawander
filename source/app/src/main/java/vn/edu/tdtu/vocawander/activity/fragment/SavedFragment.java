package vn.edu.tdtu.vocawander.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.VocabularyListActivity;
import vn.edu.tdtu.vocawander.activity.adapter.PackageAdapter;
import vn.edu.tdtu.vocawander.model.Package;
import vn.edu.tdtu.vocawander.model.SharedViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedFragment extends Fragment {
    private FirebaseFirestore db;
    private ListView listView;
    private PackageAdapter topicAdapter;
    private ArrayList<Package> topicList = new ArrayList<>();
    SharedViewModel sharedViewModel;

    public SavedFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        db = FirebaseFirestore.getInstance();
        listView = view.findViewById(R.id.rvPackage);
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
                startActivity(intent);
            }
        });

        String userId = sharedViewModel.getId().getValue();

        db.collection("saved")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String documentID = document.getId();

                                String[] parts = documentID.split("_");
                                if (parts.length > 1) {
                                    String topicID = parts[1];
                                    db.collection("Topic")
                                        .document(topicID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot topicDocument = task.getResult();
                                                    if (topicDocument.exists()) {
                                                        String topicName = topicDocument.getString("name");
                                                        String topicOwner = topicDocument.getString("owner");
                                                        String topicImg = topicDocument.getString("img");
                                                        Package topicPackage = new Package(topicID, topicName, topicOwner, topicImg);

                                                        topicList.add(topicPackage);
                                                        if (topicAdapter == null) {
                                                            topicAdapter = new PackageAdapter(getContext(), R.layout.item_topic, topicList, userId);
                                                            listView.setAdapter(topicAdapter);
                                                        } else {
                                                            topicAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                }
                            }
                        }
                    }
                });

        return view;
    }
}