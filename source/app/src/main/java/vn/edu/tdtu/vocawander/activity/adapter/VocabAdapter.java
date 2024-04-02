package vn.edu.tdtu.vocawander.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.model.Vocabulary;

public class VocabAdapter extends ArrayAdapter<Vocabulary> {
    ArrayList<Vocabulary> vocabularies;
    public VocabAdapter(Context context, int resource, ArrayList<Vocabulary> vocabularies) {
        super(context, resource, vocabularies);
        this.vocabularies = vocabularies;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_vocab,null);
        }

        ImageView imgVocab = convertView.findViewById(R.id.imgVocab);
        TextView txtWord = convertView.findViewById(R.id.txtWord);

        TextView txtMeaning = convertView.findViewById(R.id.txtMeaning);
        TextView txtExample = convertView.findViewById(R.id.txtExample);

        Vocabulary vocab = getItem(position);

        if (vocab != null) {
            Glide.with(getContext()).load(vocab.getImgUrl()).into(imgVocab);
            txtWord.setText(vocab.getWord());
            txtMeaning.setText(vocab.getMeaning());
            txtExample.setText("Eg: " + vocab.getExample());
        }

        return convertView;
    }
}
