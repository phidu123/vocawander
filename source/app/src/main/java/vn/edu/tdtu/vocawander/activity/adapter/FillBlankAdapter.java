package vn.edu.tdtu.vocawander.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.model.FillBlank;

public class FillBlankAdapter extends ArrayAdapter<FillBlank> {
    String userId;
    Context context;
    ArrayList<FillBlank> pckList;
    ArrayList<FillBlank> pckListOld;

    public FillBlankAdapter(@NonNull Context context, int resource, @NonNull ArrayList<FillBlank> objects, String userId) {
        super(context, resource, objects);
        this.context = context;
        pckList = objects;
        pckListOld = objects;
        this.userId = userId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.item_fill_blank,null);
        TextView tvQuest = v.findViewById(R.id.tvQuest);
        EditText edtAns = v.findViewById(R.id.edtAnswer);
        Button btn = v.findViewById(R.id.submitbutton);
        ImageView img = v.findViewById(R.id.imgAns);
        CardView cv = v.findViewById(R.id.cv);
        FillBlank fill = new FillBlank(pckList.get(position).getId(),pckList.get(position).getQuestion(),pckList.get(position).getAnswer(),pckList.get(position).getImg());
        tvQuest.setText(fill.getQuestion());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtAns.getText().toString().toLowerCase().equals(fill.getAnswer().toLowerCase())){
                    cv.setBackgroundColor(Color.GREEN);
                    Glide.with(getContext()).load(pckList.get(position).getImg()).into(img);
                    updateScore(pckList.get(position).getId(),userId);
                }else {
                    cv.setBackgroundColor(Color.RED);
                }
            }
        });
        return v;
    }
    private void updateScore(String idItem, String idUser){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference savedDocRef = firestore.collection("learn").document(idUser + "_" + idItem);

        savedDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Nếu document tồn tại, thì cộng 1
                        int count =Integer.parseInt(document.getString("count"));
                        count ++;
                        savedDocRef.update("count",String.valueOf(count));
                    } else {
                        Map<String, String> map = new HashMap<>();
                        map.put("count","5");
                        map.put("user",idUser);
                        // Nếu document không tồn tại, thì thêm document mới vào
                        savedDocRef.set(map);
                    }
                }
            }
        });
    }
}
