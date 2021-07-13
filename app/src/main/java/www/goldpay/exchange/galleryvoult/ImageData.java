package www.goldpay.exchange.galleryvoult;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ImageData extends AppCompatActivity {

    ImageView imgView;

    String uriii;

    ImageView btnBACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_data);

        imgView = findViewById(R.id.imgView);
        btnBACK = findViewById(R.id.btnBACK);

        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            Toast.makeText(this, "NO URI", Toast.LENGTH_SHORT).show();
        } else {
            uriii = extra.getString("imgPath");
            Log.d("TOUSIF_URI",uriii);

            Glide.with(this).load(uriii)
                    .placeholder(R.drawable.ic_no_img)
                    .into(imgView);
        }

        btnBACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}