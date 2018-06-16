package tfg.app.laurapadial.rateart.Upload;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import tfg.app.laurapadial.rateart.R;

public class UploadActivity extends AppCompatActivity {

    ImageView mImageView = findViewById(R.id.imageView2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
        finish();
    }
}
