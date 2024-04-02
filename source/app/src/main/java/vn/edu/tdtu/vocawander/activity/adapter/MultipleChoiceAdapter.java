package vn.edu.tdtu.vocawander.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.model.Flashcard;
import vn.edu.tdtu.vocawander.model.MultipleChoice;

public class MultipleChoiceAdapter extends ArrayAdapter<MultipleChoice> {
    String userID;
    String PCK;
    Context context;
    ArrayList<MultipleChoice> pckList;
    ArrayList<MultipleChoice> pckListOld;
    public MultipleChoiceAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MultipleChoice> objects, String userID) {
        super(context, resource, objects);
        Collections.shuffle(objects);
        pckList = objects;
        this.context = context;
        this.userID = userID;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v;
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.item_multiple_choice, parent,false);
        holder = new ViewHolder(v);
        v.setTag(holder);
        MultipleChoice mc = getItem(position);
        holder.ques.setText(mc.getQuestion());
        ArrayList<String> listAns = new ArrayList<>();
        Random rand = new Random();
        listAns.add(mc.getAnswer());
        String choice = pckList.get(rand.nextInt(pckList.size())).getAnswer();
        for (int i  = 0 ; i<3; i++){
            for (;listAns.contains(choice);){
                choice = pckList.get(rand.nextInt(pckList.size())).getAnswer();
            }
            listAns.add(choice);
        }
        Collections.shuffle(listAns);
        holder.tv1.setText(listAns.get(0));
        holder.tv2.setText(listAns.get(1));
        holder.tv3.setText(listAns.get(2));
        holder.tv4.setText(listAns.get(3));
        holder.cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.ques.getText().equals(pckList.get(position).getQuestion())){
                    if(holder.tv1.getText().equals(mc.getAnswer())){
                        holder.cv1.setBackgroundColor(Color.GREEN);
                        Glide.with(getContext()).load(mc.getImg()).into(holder.img);
                        updateScore(pckList.get(position).getId(),userID);
                    }else{
                        holder.cv1.setBackgroundColor(Color.RED);
                    }
                }
            }
        });
        holder.cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.ques.getText().equals(pckList.get(position).getQuestion())){
                    if(holder.tv2.getText().equals(mc.getAnswer())){
                        holder.cv2.setBackgroundColor(Color.GREEN);
                        Glide.with(getContext()).load(mc.getImg()).into(holder.img);
                        updateScore(pckList.get(position).getId(),userID);
                    }else{
                        holder.cv2.setBackgroundColor(Color.RED);
                    }
                }
            }
        });
        holder.cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.ques.getText().equals(pckList.get(position).getQuestion())){
                    if(holder.tv3.getText().equals(mc.getAnswer())){
                        holder.cv3.setBackgroundColor(Color.GREEN);
                        Glide.with(getContext()).load(mc.getImg()).into(holder.img);
                        updateScore(pckList.get(position).getId(),userID);
                    }else{
                        holder.cv3.setBackgroundColor(Color.RED);
                    }
                }
            }
        });
        holder.cv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.ques.getText().equals(pckList.get(position).getQuestion())){
                    if(holder.tv4.getText().equals(mc.getAnswer())){
                        holder.cv4.setBackgroundColor(Color.GREEN);
                        Glide.with(getContext()).load(mc.getImg()).into(holder.img);
                        updateScore(pckList.get(position).getId(),userID);
                    }else{
                        holder.cv4.setBackgroundColor(Color.RED);
                    }
                }
            }
        });
        return v;
    }
    static public class ViewHolder{
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        CardView cv1;
        CardView cv2;
        CardView cv3;
        CardView cv4;
        ImageView img;
        TextView ques;
        public ViewHolder(View convertView){
            this.tv1 = convertView.findViewById(R.id.ans1);
            this.tv2 = convertView.findViewById(R.id.ans2);
            this.tv3 = convertView.findViewById(R.id.ans3);
            this.tv4 = convertView.findViewById(R.id.ans4);
            this.cv1 = convertView.findViewById(R.id.cv1);
            this.cv2 = convertView.findViewById(R.id.cv2);
            this.cv3 = convertView.findViewById(R.id.cv3);
            this.cv4 = convertView.findViewById(R.id.cv4);
            this.img = convertView.findViewById(R.id.imgMC);
            this.ques = convertView.findViewById(R.id.question);
        }
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
