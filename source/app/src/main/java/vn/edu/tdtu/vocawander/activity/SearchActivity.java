package vn.edu.tdtu.vocawander.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import vn.edu.tdtu.vocawander.R;

public class SearchActivity extends AppCompatActivity {

    private TextView btnBack, tvSearchButton;
    private RecyclerView rvSearchContent;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            finish();
        });

        System.out.println("HIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");

        tvSearchButton = findViewById(R.id.tvSearchButton);
        tvSearchButton.setOnClickListener(view -> {

        });

        searchView = findViewById(R.id.searchView);

        rvSearchContent = findViewById(R.id.rvSearchContent);
        rvSearchContent.setLayoutManager(new LinearLayoutManager(this));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SearchActivity.this, searchView.getQuery().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(SearchActivity.this, searchView.getQuery().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}