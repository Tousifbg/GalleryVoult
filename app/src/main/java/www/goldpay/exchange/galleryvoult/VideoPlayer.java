package www.goldpay.exchange.galleryvoult;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


public class VideoPlayer extends AppCompatActivity {

    VideoView videoView;

    String uriii;

    ImageView btnBACK;

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
    }

    private void initViews() {
        videoView = (VideoView) findViewById(R.id.videoView);
        btnBACK = (ImageView) findViewById(R.id.btnBACK);
    }
}