package vn.edu.tdtu.vocawander.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.adapter.FillBlankAdapter;
import vn.edu.tdtu.vocawander.activity.adapter.FlashCardAdapter;
import vn.edu.tdtu.vocawander.activity.adapter.MultipleChoiceAdapter;
import vn.edu.tdtu.vocawander.model.FillBlank;
import vn.edu.tdtu.vocawander.model.Flashcard;

public class FillBlankActivity extends AppCompatActivity {
    private ListView mlvFill;
    private ArrayList<FillBlank> mPackage = new ArrayList<>();
    private ImageView btnBack;
    private ImageView btnSwap;
    private ImageView btnShuffle;
    private String Quest = "name";
    private String Ans = "trans";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_blank);
        final Context context = FillBlankActivity.this;
        btnBack = findViewById(R.id.btnBack);
        btnSwap = findViewById(R.id.swapBtn);
        btnShuffle = findViewById(R.id.btnShuffle);
        String idPck = getIntent().getStringExtra("ID");
        String idUser = getIntent().getStringExtra("ID_USER");
        mlvFill = findViewById(R.id.flashcardContainer);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Item").whereEqualTo("topic", idPck).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()){
                            for (DocumentSnapshot doc: task.getResult()){
                                mPackage.add(new FillBlank(doc.getId(), doc.getString("name"),doc.getString("trans"), doc.getString("img")));
                                Collections.shuffle(mPackage);
                                FillBlankAdapter listAdapter = new FillBlankAdapter(context, R.layout.item_fill_blank,mPackage,idUser);
                                mlvFill.setAdapter(listAdapter);
                            }
                        }
                    }
                });
        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = new String(Quest);
                Quest = new String(Ans);
                Ans = s;
                mPackage = new ArrayList<>();
                db.collection("Item").whereEqualTo("topic", idPck).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isComplete()){
                                    for (DocumentSnapshot doc: task.getResult()){
                                        mPackage.add(new FillBlank(doc.getId(), doc.getString(Quest),doc.getString(Ans), doc.getString("img")));
                                        Collections.shuffle(mPackage);
                                        FillBlankAdapter listAdapter = new FillBlankAdapter(context, R.layout.item_fill_blank,mPackage,idUser);
                                        mlvFill.setAdapter(listAdapter);
                                    }
                                }
                            }
                        });
            }
        });
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.shuffle(mPackage);
                FillBlankAdapter listAdapter = new FillBlankAdapter(context, R.layout.item_fill_blank,mPackage,idUser);
                mlvFill.setAdapter(listAdapter);
            }
        });
        btnBack.setOnClickListener(view -> onBackPressed());
    }
}