package vn.edu.tdtu.vocawander.activity.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.VocabularyListActivity;
import vn.edu.tdtu.vocawander.activity.adapter.PackageAdapter;
import vn.edu.tdtu.vocawander.model.Flashcard;
import vn.edu.tdtu.vocawander.model.Package;
import vn.edu.tdtu.vocawander.model.SharedViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private ListView listView;
    private PackageAdapter adapter;
    private ArrayList<Package> packageList = new ArrayList<>();
    Package selectedTopic;

    private DatabaseReference topicsRef;
    private ArrayList<Flashcard> mPackage = new ArrayList<>();
    private static final int ADD_VOCAB_REQUEST_CODE = 100;

    private SharedViewModel sharedViewModel;
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

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
                startActivity(intent);
            }

        });

        String userId = sharedViewModel.getId().getValue();
        if (userId != null) {
            adapter = new PackageAdapter(getContext(), R.layout.item_topic, packageList, userId);
            listView.setAdapter(adapter);
        }

        topicsRef = FirebaseDatabase.getInstance().getReference("Topic");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Topic")
                .whereEqualTo("public", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            packageList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                packageList.add(new Package(document.getId().toString(), document.getString("name"),
                                        document.getString("owner"), document.getString("img")));
                            }
                            PackageAdapter pkAdapter = new PackageAdapter(rootView.getContext(), R.layout.item_topic, packageList, userId);
                            listView.setAdapter(pkAdapter);
                            pkAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return rootView;
    }

}