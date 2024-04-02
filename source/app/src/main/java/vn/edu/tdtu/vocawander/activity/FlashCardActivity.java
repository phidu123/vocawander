package vn.edu.tdtu.vocawander.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.style.EasyEditSpan;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.adapter.FlashCardAdapter;
import vn.edu.tdtu.vocawander.model.Flashcard;
import vn.edu.tdtu.vocawander.model.Package;

public class FlashCardActivity extends AppCompatActivity {
    private ListView mlvFlashcard;
    private ArrayList<Flashcard> mPackage = new ArrayList<>();
    private ImageView btnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        final Context context = FlashCardActivity.this;
        btnBack = findViewById(R.id.btnBack);
        String idPck = getIntent().getStringExtra("ID");
        mlvFlashcard = findViewById(R.id.flashcardContainer);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Item").whereEqualTo("topic", idPck).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()){
                            for (DocumentSnapshot doc: task.getResult()){
                                mPackage.add(new Flashcard(doc.getId(), doc.getString("name"),doc.getString("trans"),doc.getString("img")));
                                Collections.shuffle(mPackage);
                                FlashCardAdapter listAdapter = new FlashCardAdapter(context, R.layout.item_flashcard,mPackage);
                                mlvFlashcard.setAdapter(listAdapter);
                            }
                        }
                    }
                });
        btnBack.setOnClickListener(view -> onBackPressed());
    }
}
