package vn.edu.tdtu.vocawander.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tdtu.vocawander.R;
import vn.edu.tdtu.vocawander.model.Vocabulary;
import vn.edu.tdtu.vocawander.util.TranslateApi;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {
    private Context context;

    public int ITEM_POSITION = -1;

    private final String ENG = "en";
    private final String VIE = "vi";

    private static final int GALLERY_TO_UPLOAD_PERMISSION_REQUEST_CODE = 127;
    private List<Vocabulary> vocabularyList;
    public VocabularyAdapter(List<Vocabulary> vocabularyList, Context context) {
        this.vocabularyList = vocabularyList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vocab_item, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        holder.textWord.setText(vocabulary.getWord());
        holder.textMeaning.setText(vocabulary.getMeaning());
        holder.textExample.setText(vocabulary.getExample());

        if (vocabulary.getImgUrl().equals("")) {
            holder.textImg.setImageResource(R.drawable.quest);
        } else {
            Glide.with(this.context).load(vocabulary.getImgUrl()).into(holder.textImg);
            if (getItemCount() > 1) {
                holder.closeButton.setVisibility(View.VISIBLE);
            } else {
                holder.closeButton.setVisibility(View.GONE);
            }
        }

        TextWatcher textWatcherExample = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                vocabulary.setExample(editable.toString());
            }
        };
        holder.textExample.addTextChangedListener(textWatcherExample);

        TextWatcher textWatcherName = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                vocabulary.setWord(editable.toString());
            }
        };
        holder.textWord.addTextChangedListener(textWatcherName);
        TextWatcher textWatcherMean = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                vocabulary.setMeaning(editable.toString());
            }
        };
        holder.textMeaning.addTextChangedListener(textWatcherMean);


        holder.closeButton.setOnClickListener(v -> {
            vocabularyList.remove(position);
            notifyItemRemoved(position);

            if (getItemCount() > 1) {
                holder.closeButton.setVisibility(View.VISIBLE);
            } else {
                holder.closeButton.setVisibility(View.INVISIBLE);
            }
        });

        //this.notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textWord;
        TextView textExample;
        TextView textMeaning;
        ImageView textImg;
        ImageButton closeButton;
        Context context;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            textWord = itemView.findViewById(R.id.term);
            textMeaning = itemView.findViewById(R.id.meaning);
            textImg = itemView.findViewById(R.id.img);
            textExample = itemView.findViewById(R.id.example);
            closeButton = itemView.findViewById(R.id.closeButton);

            textImg.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    openImagePicker(position);
                }
            });

            textWord.setOnTouchListener((view, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableRight = textWord.getRight() - textWord.getCompoundDrawables()[2].getBounds().width();

                    if (event.getRawX() >= drawableRight) {
                        // Handle the click on the drawable here
                        getTranslation(textWord.getText().toString(), true);
                        return true;
                    }
                }
                return false;
            });

            textMeaning.setOnTouchListener((view, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int drawableRight = textMeaning.getRight() - textMeaning.getCompoundDrawables()[2].getBounds().width();

                    if (event.getRawX() >= drawableRight) {
                        // Handle the click on the drawable here
                        getTranslation(textMeaning.getText().toString(), false);
                        return true;
                    }
                }
                return false;
            });
        }

        private void getTranslation(String word, boolean isTop) {
            TranslateApi translate = new TranslateApi();
            translate.setOnTranslationCompleteListener(new TranslateApi.OnTranslationCompleteListener() {
                @Override
                public void onStartTranslation() {
                    // here you can perform initial work before translated the text like displaying progress bar
                }

                @Override
                public void onCompleted(String text) {
                    if (isTop) {
                        textMeaning.setText(text.toLowerCase());
                    } else {
                        textWord.setText(text.toLowerCase());
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });
            if (isTop) {
                translate.execute(word, ENG, VIE);
            } else {
                translate.execute(word, VIE, ENG);
            }
        }

        private void openImagePicker(int position) {
            // Use the context reference to start the image picker
            if (context instanceof Activity) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ITEM_POSITION = position;
                ((Activity) context).startActivityForResult(intent, GALLERY_TO_UPLOAD_PERMISSION_REQUEST_CODE);
            }
        }
    }



    public List<Vocabulary> getList(){
        return this.vocabularyList;
    }
}