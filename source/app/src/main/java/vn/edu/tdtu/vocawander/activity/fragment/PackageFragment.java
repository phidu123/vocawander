package vn.edu.tdtu.vocawander.activity.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.FillBlankActivity;
import vn.edu.tdtu.vocawander.activity.FlashCardActivity;
import vn.edu.tdtu.vocawander.activity.MultipleChoiceActivity;
import vn.edu.tdtu.vocawander.activity.adapter.PackageAdapter;
import vn.edu.tdtu.vocawander.model.FillBlank;
import vn.edu.tdtu.vocawander.model.Package;
import vn.edu.tdtu.vocawander.model.SharedViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PackageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PackageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String collection;
    private ListView mListView;
    private ArrayList<Package> mPackage = new ArrayList<>();
    private SharedViewModel sharedViewModel;

    public  PackageFragment(){
        // Required empty public constructor
    }
    public PackageFragment(String collection) {
        this.collection = collection;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PackageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PackageFragment newInstance(String param1, String param2) {
        PackageFragment fragment = new PackageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_package, container, false);

        mListView = view.findViewById(R.id.rvPackage);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Topic").whereEqualTo("public",true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()){
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                mPackage.add(new Package(doc.getId().toString(), doc.getString("name"),doc.getString("owner"), doc.getString("img")));
                                PackageAdapter pkAdapter = new PackageAdapter(getContext(), R.layout.item_package, mPackage);
                                mListView.setAdapter(pkAdapter);
                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Package pck = (Package) adapterView.getItemAtPosition(i);
                if(collection == "PFC"){
                    Intent intent = new Intent(getContext(),FlashCardActivity.class);
                    intent.putExtra("ID",pck.getId());
                    startActivity(intent);
                } else if (collection == "PMC") {
                    Intent intent = new Intent(getContext(), MultipleChoiceActivity.class);
                    intent.putExtra("ID",pck.getId());
                    intent.putExtra("ID_USER",sharedViewModel.getId().getValue());
                    startActivity(intent);
                }else if(collection == "PF"){
                    Intent intent = new Intent(getContext(), FillBlankActivity.class);
                    intent.putExtra("ID",pck.getId());
                    intent.putExtra("ID_USER",sharedViewModel.getId().getValue());
                    startActivity(intent);
                }
            }
        });
        return view;
    }
}