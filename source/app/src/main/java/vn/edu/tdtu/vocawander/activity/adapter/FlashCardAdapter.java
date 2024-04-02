package vn.edu.tdtu.vocawander.activity.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.activity.HomeActivity;
import vn.edu.tdtu.vocawander.activity.MainActivity;
import vn.edu.tdtu.vocawander.model.Flashcard;
import vn.edu.tdtu.vocawander.model.Package;

public class FlashCardAdapter extends ArrayAdapter<Flashcard> {
    ArrayList<Flashcard> pckList;
    ArrayList<Flashcard> pckListOld;
    TextToSpeech textToSpeech;
    public FlashCardAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Flashcard> objects) {
        super(context, resource, objects);
        pckList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(v == null){
            v = inflater.inflate(R.layout.item_flashcard,null);
        }
        TextView front = v.findViewById(R.id.tvFront);
        TextView back = v.findViewById(R.id.tvBack);
        ImageView img = v.findViewById(R.id.backImg);
        ImageButton ivAudio = v.findViewById(R.id.ivAudio);
        front.setText(pckList.get(position).getFrontS());
        back.setText(pckList.get(position).getBackS());
        Glide.with(getContext()).load(pckList.get(position).getImg()).into(img);
        ivAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak(front.getText().toString());
            }
        });
        return v;
    }
    public void speak(final String text){ // make text 'final'

        // ... do not declare tts here
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS", "Language not supported");
                    } else {
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Log.e("TTS", "Failed");
                }
            }
        });
    }
}
