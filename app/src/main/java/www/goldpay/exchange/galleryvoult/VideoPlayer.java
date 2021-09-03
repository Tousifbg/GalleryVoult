package www.goldpay.exchange.galleryvoult;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import www.goldpay.exchange.galleryvoult.FileUtilsContent.FileUtils;


public class VideoPlayer extends AppCompatActivity {

    VideoView videoView;

    String uriii;

    ImageView btnBACK;

    String myPath;

    Uri uri;

    ImageView restoreVid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        initViews();

        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            Toast.makeText(this, "NO URI", Toast.LENGTH_SHORT).show();
        } else {
            uriii = extra.getString("myURL");
            Log.d("TOUSIF_URI",uriii);
        }

        //videoView.setActivity(this);
        try {
            final MediaController mc = new MediaController(VideoPlayer.this);
            mc.setAnchorView(videoView);
            mc.setMediaPlayer(videoView);
            Uri myUri = Uri.parse(uriii);
            videoView.setMediaController(mc);
            //mc.setVisibility(View.GONE);
            videoView.setVideoURI(myUri);
            //myVideoView.seekTo(100);
            videoView.start();
        }catch (Exception e){
            e.printStackTrace();
            Log.d("TOUSIF", "onCreateVideoException: "+e.getMessage());
        }

        btnBACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        restoreVid.setOnClickListener(new View.OnClickListener() {
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
                    moveVidData();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });
        btnCancel.setOnClickListener(v -> alert_dialog.dismiss());
    }

    private void moveVidData() throws FileNotFoundException {
        File source = new File(myPath);
        String sdCard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath() + "/MyRestoredVIDS";
        File destination = new File(sdCard);
        destination.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File expFile = new File(destination.getPath() + File.separator +
                "VID_" + timeStamp + ".mp4");
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(source).getChannel();
            outChannel = new FileOutputStream(expFile).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                /*File file = new File(myPath);
                file.delete();*/
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
                notifyNewFileToSystem(destination);
                Toast.makeText(this, "Video restored successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VideoPlayer.this,MainActivity.class));
                finish();

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
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

    private void initViews() {
        videoView = (VideoView) findViewById(R.id.videoView);
        btnBACK = (ImageView) findViewById(R.id.btnBACK);
        restoreVid = (ImageView) findViewById(R.id.restoreVid);
    }
}