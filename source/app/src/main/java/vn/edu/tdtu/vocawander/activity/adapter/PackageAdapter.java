package vn.edu.tdtu.vocawander.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.model.Package;

public class PackageAdapter extends ArrayAdapter<Package> {
    ArrayList<Package> pckList;
    ArrayList<Package> pckListOld;
    int resourceValue;
    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId(){
        return this.userId;
    }

    public PackageAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Package> objects) {
        super(context, resource, objects);
        pckList = objects;
        pckListOld = objects;
        this.resourceValue = resource;
    }
    public PackageAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Package> objects, String userId) {
        super(context, resource, objects);
        pckList = objects;
        pckListOld = objects;
        this.resourceValue = resource;
        this.userId = userId;
    }
    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(v == null){
                v = inflater.inflate(this.resourceValue,null);

        }
        TextView name = (TextView) v.findViewById(R.id.namePk);
        TextView owner = (TextView) v.findViewById(R.id.ownerPk);
        ImageView img = (ImageView) v.findViewById(R.id.pck_img);
        //Glide.with(this.getContext()).load(pckList.get(position).getImg()).into(img);
        name.setText(pckList.get(position).getName());
        Glide.with(getContext()).load(pckList.get(position).getImg()).into(img);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference doc = db.document("user/"+pckList.get(position).getOwner());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                owner.setText("by: "+ documentSnapshot.getString("name"));
            }
        });

        ImageView saveImageView = v.findViewById(R.id.save);
        final Package currentPackage = pckList.get(position);
        DocumentReference saveDB = FirebaseFirestore.getInstance().collection("saved").document(userId+"_"+currentPackage.getId());
        saveDB.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    saveImageView.setImageResource(R.drawable.baseline_star_rate_24);
                }else {
                    saveImageView.setImageResource(R.drawable.baseline_star_outline_24);
                }
            }
        });
        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String topicId = currentPackage.getId();
                DocumentReference savedDocRef = db.collection("saved").document(userId + "_" + topicId);

                savedDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Nếu document tồn tại, thì xóa nó đi
                                savedDocRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Cập nhật hình ảnh khi document bị xóa
                                        saveImageView.setImageResource(R.drawable.baseline_star_outline_24);
                                        notifyDataSetChanged();
                                    }
                                });
                            } else {
                                // Nếu document không tồn tại, thì thêm document mới vào
                                savedDocRef.set(new HashMap<>()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Cập nhật hình ảnh khi document được thêm vào
                                        saveImageView.setImageResource(R.drawable.baseline_star_rate_24);
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        return v;
    }
}
