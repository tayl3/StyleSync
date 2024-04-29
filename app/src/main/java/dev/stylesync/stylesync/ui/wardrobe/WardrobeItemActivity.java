package dev.stylesync.stylesync.ui.wardrobe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import dev.stylesync.stylesync.R;

public class WardrobeItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_item);

        TextView descriptionView = findViewById(R.id.description);
        descriptionView.setText(getIntent().getStringExtra("description"));

        ImageView imageView = findViewById(R.id.image);
        String imageUrl = getIntent().getStringExtra("url");

        Glide.with(this)
                .load(imageUrl)
                .error(R.drawable.baseline_10k_24)
                .into(imageView);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));

        findViewById(R.id.close).setOnClickListener(v -> {
            resultIntent.putExtra("delete", false);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        findViewById(R.id.delete).setOnClickListener(v -> {
            resultIntent.putExtra("delete", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}