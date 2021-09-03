package www.goldpay.exchange.galleryvoult;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import www.goldpay.exchange.galleryvoult.FileUtilsContent.FileUtils;

public class ImageData extends AppCompatActivity {

    ImageView imgView;

    String uriii;

    ImageView btnBACK;

    ImageView restoreImg;

    String myPath;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_data);

        imgView = findViewById(R.id.imgView);
        btnBACK = findViewById(R.id.btnBACK);
        restoreImg = findViewById(R.id.restoreImg);

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


        restoreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmDialog();
            }
        });
    }

    private void confirmDialog() {
        final android.app.AlertDialog alert_dialog;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.confirmation_dialog, null);
        builder.setView(view);

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnOkay = view.findViewById(R.id.btnOkay);

        alert_dialog = builder.create();
        alert_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alert_dialog.show();

        btnOkay.setOnClickListener(v -> {

            uri = Uri.parse(uriii);
            myPath = uri.getPath();
            Log.e("FILES",myPath);

            if (myPath != null){
                try {
                    moveImgData();
                    //startActivity(getIntent());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });
        btnCancel.setOnClickListener(v -> alert_dialog.dismiss());
    }

    private void moveImgData() throws FileNotFoundException {
        File my_file_source = new File(myPath);
        File sdCard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File my_file_Destination = new File(sdCard.getAbsolutePath() + "/MyRestoredPICS");
        my_file_Destination.mkdirs();

        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File expFile = new File(my_file_Destination.getPath() + File.separator +
                "IMG_" + fileName);

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(my_file_source).getChannel();
        destination = new FileOutputStream(expFile).getChannel();
        if (destination != null && source != null) {
            try {
                destination.transferFrom(source, 0, source.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (source != null) {
            try {
                source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (destination != null) {
            try {
                destination.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            File file = new File(myPath);
            file.delete();
            if (file.exists()) {
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    getApplicationContext().deleteFile(file.getName());
                }
            }

            notifyNewFileToSystem(my_file_Destination);
            Toast.makeText(this, "Image restored successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ImageData.this,MainActivity.class));
            finish();

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    //scan file or refresh system
    private void notifyNewFileToSystem(File my_file_source) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(my_file_source.getAbsolutePath());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[]{my_file_source.getAbsolutePath()},
                new String[]{type},
                (path, uri) -> {
                    Log.e("SCANNED_PATH", "Path: " + path);
                    Log.e("SCANNED_URI", "Uri: " + uri);
                }
        );
    }
}













