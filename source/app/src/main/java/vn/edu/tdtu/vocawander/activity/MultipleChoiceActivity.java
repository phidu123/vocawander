package vn.edu.tdtu.vocawander.activity;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.style.EasyEditSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.adapter.FillBlankAdapter;
import vn.edu.tdtu.vocawander.activity.adapter.FlashCardAdapter;
import vn.edu.tdtu.vocawander.activity.adapter.MultipleChoiceAdapter;
import vn.edu.tdtu.vocawander.model.FillBlank;
import vn.edu.tdtu.vocawander.model.Flashcard;
import vn.edu.tdtu.vocawander.model.MultipleChoice;

public class MultipleChoiceActivity extends AppCompatActivity {
    private ListView mlvMC;
    private ArrayList<MultipleChoice> mPackage = new ArrayList<>();
    private ImageView btnBack;
    private ImageView btnSwap;
    private ImageView btnShuffle;
    String Quest = "name";
    String Ans = "trans";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice);
        final Context context = MultipleChoiceActivity.this;
        btnBack = findViewById(R.id.btnBack);
        btnSwap = findViewById(R.id.swapBtn);
        btnShuffle = findViewById(R.id.btnShuffle);
        String idPck = getIntent().getStringExtra("ID");
        String idUser = getIntent().getStringExtra("ID_USER");
        mlvMC = findViewById(R.id.mcContainer);
        ArrayList<String> stringList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Item").whereEqualTo("topic", idPck).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()){
                            for (DocumentSnapshot doc: task.getResult()){
                                mPackage.add(new MultipleChoice(doc.getId(),doc.getString("img"),doc.getString("name"),doc.getString("trans")));
                            }
                            MultipleChoiceAdapter listAdapter = new MultipleChoiceAdapter(context, R.layout.item_multiple_choice,mPackage,idUser);
                            mlvMC.setAdapter(listAdapter);
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
                                        mPackage.add(new MultipleChoice(doc.getId(), doc.getString("img"),doc.getString(Quest), doc.getString(Ans)));
                                        Collections.shuffle(mPackage);
                                        MultipleChoiceAdapter listAdapter = new MultipleChoiceAdapter(context, R.layout.item_fill_blank,mPackage,idUser);
                                        mlvMC.setAdapter(listAdapter);
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
                MultipleChoiceAdapter listAdapter = new MultipleChoiceAdapter(context, R.layout.item_fill_blank,mPackage,idUser);
                mlvMC.setAdapter(listAdapter);
            }
        });
        btnBack.setOnClickListener(view -> onBackPressed());
    }
}