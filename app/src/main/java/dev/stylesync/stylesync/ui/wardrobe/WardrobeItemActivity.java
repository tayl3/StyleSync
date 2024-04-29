package dev.stylesync.stylesync.ui.wardrobe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.R;
import dev.stylesync.stylesync.service.UserService;

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