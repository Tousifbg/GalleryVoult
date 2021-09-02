package www.goldpay.exchange.galleryvoult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import www.goldpay.exchange.galleryvoult.FileUtilsContent.FileUtils;

public class AudioActivity extends AppCompatActivity implements OnItemClick{

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int VIDEO_PICK_GALLERY_CODE = 1005;
    private static final int AUDIO_FILE_REQUEST = 1001 ;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
//jj
    private String[] cameraPermissions;
    private String[] storagePermissions;

    RecyclerView recyclerView;

    ArrayList<File> myAudioFile;
    AudioListAdapter audioListAdapter;
    List<String> mList;

    public File sdCard;

    FloatingActionButton add;

    private Uri uri;

    Uri audio_uri;

    String myPath;

    FileUtils fileUtils;

    private RelativeLayout adshere;
    SharedPreferences pref;
    String pref_banner, pref_interstitial;

    Boolean bannerShow, InterstitialShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        add = findViewById(R.id.add);

        adshere=findViewById(R.id.adView);
        //SHARED PREFERENCES TO CHECK IF USER HAS PASSWORD OR NOT
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        //CHECK PREFERENCES
        pref_banner = pref.getString("banner_id","");
        pref_interstitial = pref.getString("interstitial_id","");
        bannerShow = pref.getBoolean("banner_show",false);
        InterstitialShow = pref.getBoolean("interstitial_show",false);
        Log.e("TOUSIF", String.valueOf(bannerShow));
        Log.e("PREF_DATA",pref_banner + ": " + pref_interstitial);

        if (pref_banner != null && !bannerShow){
            showBannerAds(pref_banner);
            Log.e("TOUSIF","BANNER ID exist");
        }else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            add.setLayoutParams(params);
            Log.e("TOUSIF","No banner id");
            Toast.makeText(this, "banner false", Toast.LENGTH_SHORT).show();
        }

        //init permission array
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //Directory position which I want to show
        sdCard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC + "/.MyAUDIOS");

        mList=new ArrayList<>();
        recyclerView = findViewById(R.id.my_audio_lists);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //RecyclerView.LayoutManager manager = new GridLayoutManager(this,2);
        //recyclerView.setLayoutManager(manager);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioPickDialog();
            }
        });

        display();
    }

    private void showBannerAds(String pref_banner) {
        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId(pref_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adshere.addView(adView);
    }

    private void AudioPickDialog() {
        if (checkStoragePermission()){
            //storage permission allowed
            pickAudio();
        }
        else {
            //not allowed
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);

    }

    private void pickAudio() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("audio/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Your Audio"),
                AUDIO_FILE_REQUEST);
        Toast.makeText(this, "audio", Toast.LENGTH_SHORT).show();
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return  result;
    }

    private void display() {
        myAudioFile = findImage(sdCard);


        if (myAudioFile.size() > 0){
            for (int j = 0; j < myAudioFile.size(); j++) {

                mList.add(String.valueOf(myAudioFile.get(j)));
                audioListAdapter = new AudioListAdapter(AudioActivity.this,mList, this);
                //recyclerView.addItemDecoration(new ItemOffSetDecoration(5));
                //recyclerView.addItemDecoration(new ItemOffSetDecoration(5));
                audioListAdapter.setHasStableIds(true);
                recyclerView.setAdapter(audioListAdapter);
                audioListAdapter.notifyDataSetChanged();

            }
        }
        else{
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<File> findImage(File sdCard) {
        ArrayList<File> audioList=new ArrayList<>();

        File[] audioFile=  sdCard.listFiles();
        if (audioFile != null){

            for (File singleimage : audioFile) {

                if (singleimage.isDirectory() && !singleimage.isHidden()) {

                    audioList.addAll(findImage(singleimage));

                } else {

                    if (singleimage.getName().endsWith(".mp3")
                    ) {
                        audioList.add(singleimage);
                    }
                }
            }
        }
        else{
            Toast.makeText(this, "noting", Toast.LENGTH_SHORT).show();
        }
        Log.d("TOUSIF", "findImage: "+audioList);
        return audioList;
    }

    @Override
    public void onClick(int position) {
        Toast.makeText(this, "Postion: "+position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //PERMISSION ALLOWED
                        pickFromCamera();
                    }
                    else {
                        //PERMISSION DENIED
                        Toast.makeText(this, "Camera permissions are necessary!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //PERMISSION ALLOWED
                        pickFromGallery();
                    }
                    else {
                        //PERMISSION DENIED
                        Toast.makeText(this, "Storage permission is necessary!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
      /*  Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);*/

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Your Image"),
                IMAGE_PICK_GALLERY_CODE);
        Toast.makeText(this, "image", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK)
        {
            if (requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                //uri = data.getData();

                //path = getRealPathFromUri(this, uri);
                //name = getFileName(uri);

                try {
                    uri = data.getData();
                    myPath = fileUtils.getPath(this, uri);
                    Log.e("FILES",myPath);
                    if (myPath != null){
                        //audioEdt.setText("Audio Picked");
                        Toast.makeText(this, "IMAGE PICKED"+myPath,
                                Toast.LENGTH_SHORT).show();
                        //filesList.add(new File(audioPath));
                        Log.e("TOUSIF",myPath);

                        //pickedImage.setImageURI(uri);
                        //moveImgData();
                    } else {
                        Log.e("TOUSIF","Image");
                        //audioEdt.setText("Audio");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                //pickedImage.setImageURI(uri);
            }

            else if (requestCode == VIDEO_PICK_GALLERY_CODE)
            {
                try {
                    uri = data.getData();
                    myPath = fileUtils.getPath(this, uri);
                    Log.e("FILES",myPath);
                    if (myPath != null){
                        //audioEdt.setText("Audio Picked");
                        Toast.makeText(this, "VIDEO PICKED"+myPath, Toast.LENGTH_SHORT).show();
                        //filesList.add(new File(audioPath));
                        Log.e("TOUSIF",myPath);

                        //moveVideoData();
                    } else {
                        Log.e("TOUSIF","Video");
                        //audioEdt.setText("Audio");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Log.e("TOUSIF",path);
            }

            else if (requestCode == AUDIO_FILE_REQUEST)
            {
                try {
                    audio_uri = data.getData();
                    myPath = fileUtils.getPath(this, audio_uri);
                    Log.e("FILES",myPath);
                    if (myPath != null){
                        //audioEdt.setText("Audio Picked");
                        Toast.makeText(this, "AUDIO PICKED"+myPath, Toast.LENGTH_SHORT).show();
                        //filesList.add(new File(audioPath));
                        Log.e("TOUSIF",myPath);

                        moveAudioData();

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    } else {
                        Log.e("TOUSIF","Audio");
                        //audioEdt.setText("Audio");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void moveAudioData() throws FileNotFoundException {
        File source = new File(myPath);
        String sdCard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/.MyAUDIOS";
        File destination = new File(sdCard);
        destination.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File expFile = new File(destination.getPath() + File.separator +
                "AUD_" + timeStamp + ".mp3");
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
                File file = new File(audio_uri.getPath());
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
                MediaScannerConnection.scanFile(this,
                        new String[] { audio_uri.getPath() }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {

                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("TAG", "Finished scanning " + path);
                            }
                        });
                Toast.makeText(this, ""+myPath+ "moved to: " +destination, Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, ""+myPath+ "moved to: " +destination, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioListAdapter != null){
            audioListAdapter.release();
        }
    }
}