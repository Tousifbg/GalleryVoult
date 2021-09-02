package www.goldpay.exchange.galleryvoult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import www.goldpay.exchange.galleryvoult.MyConstants.Constants;
import www.goldpay.exchange.galleryvoult.NetworkConstant.NetworkUtils;

public class MainActivity extends AppCompatActivity {


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int VIDEO_PICK_GALLERY_CODE = 1005;
    private static final int AUDIO_FILE_REQUEST = 1001 ;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private static final String TAG ="MainActivity" ;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    //IMAGE PICK URI
    private Uri uri;

    private RelativeLayout btnFile,btnAudio,btnVideo,btnImage;

    www.goldpay.exchange.galleryvoult.FileUtilsContent.FileUtils fileUtils;

    TextView myTxt;

    Uri audio_uri;

    String myPath;

    ImageView settings,about_us,rate_us;

    private InterstitialAd mInterstitialAd;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private int counter = 0;

    private AsyncHttpClient client;
    ShowNow showNow;

    String  banner_id,intrestial_id;

    static boolean showAds, banner_show, intrestial_show;

    LinearLayout bottom_layout;
    private RelativeLayout adshere;

    String pref_banner, pref_interstitial;

    Boolean bannerShow, InterstitialShow;
    private String adsEnabled;
    private String intrestialEnabled;
    private String bannerEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adshere=findViewById(R.id.adView);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        initViews();

        //SHARED PREFERENCES TO CHECK IF USER HAS PASSWORD OR NOT
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        //init permission array
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //CALL API
        if (NetworkUtils.isNetworkConnected(MainActivity.this))
        {
            callAPI();
        }else {
            Log.e("TOUSIF","Internet is not connected");
        }


        //CHECK PREFERENCES
         pref_banner = pref.getString("banner_id","");
         pref_interstitial = pref.getString("interstitial_id","");
        bannerShow = pref.getBoolean("banner_show",false);
        InterstitialShow = pref.getBoolean("interstitial_show",false);
        Log.e("MY_PREF_BANNER_ID",bannerShow.toString());
         Log.e("PREF_DATA",pref_banner + ": " + pref_interstitial);

       /* if (!banner_show){
            showBannerAds(pref_banner);
            Log.e("TOUSIF","BANNER ID exist");
        }else {
            Log.e("TOUSIF","BANNER ID NOT EXIST");
        }*/

        if (pref_banner != null && pref_interstitial != null){
            if (banner_show != false && intrestial_show != false){
                showBothAds(pref_banner,pref_interstitial);
                Log.e("TOUSIF","BANNER ID && INTERS_ID exist");
            }else {
                Log.e("TOUSIF","BANNER ID && INTERS_ID NOT EXIST");
            }
        }
        else if (pref_banner != null){
            if (banner_show != false){
                showBannerAds(pref_banner);
                Log.e("TOUSIF","BANNER ID exist");
            }else {
                Log.e("TOUSIF","BANNER ID NOT EXIST");
            }
        }
        else if (pref_interstitial != null){
            if (intrestial_show != false){
                intrestialsetup(pref_interstitial);
                Log.e("TOUSIF","INTERS ID exist");
            }else {
                Log.e("TOUSIF","INTERS ID NOT EXIST");
            }
        }

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ImageActivity.class);

                if (intrestialEnabled.equalsIgnoreCase("true")&& mInterstitialAd!=null){


                    mInterstitialAd.show(MainActivity.this);

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            startActivity(intent);
                            Log.d("TAG", "The ad was dismissed.");
                        }
                    });
                }
                else{
                    startActivity(intent);
                }
               /* if (counter == 1) {                   //show ads after 2 clicks on button
                    if (pref_interstitial != null) {
                        intrestialsetup(pref_interstitial);
                        if (mInterstitialAd != null && intrestial_show == true) {
                            mInterstitialAd.show(MainActivity.this);
                            counter = 0;
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    startActivity(intent);
                                    Log.d("TAG", "The ad was dismissed.");
                                }
                            });
                        } else {
                            startActivity(intent);
                            Log.e("TOUSIF", "The interstitial ad wasn't ready yet.");
                        }
                    } else {
                        startActivity(intent);
                        Log.e("TOUSIF", "INTERSTITIAL SHOW is false");
                    }
                } else {
                    //increment counter
                    startActivity(intent);
                    counter++;
                    Log.e("MyClickCounter", String.valueOf(counter));
                }*/
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,VideoActivity.class);

                if (intrestialEnabled.equalsIgnoreCase("true")&& mInterstitialAd!=null){
                    Log.d(TAG, "onClick: true ");

                    intrestialsetup(intrestial_id);


                    mInterstitialAd.show(MainActivity.this);

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            startActivity(intent);
                            Log.d("TAG", "The ad was dismissed.");
                        }
                    });
                }
                else{
                    Log.d(TAG, "onClick: else");
                    startActivity(intent);
                }
            }
        });

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,AudioActivity.class);

                if (intrestialEnabled.equalsIgnoreCase("true")&& mInterstitialAd!=null){


                    mInterstitialAd.show(MainActivity.this);

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            startActivity(intent);
                            Log.d("TAG", "The ad was dismissed.");
                        }
                    });
                }
                else{
                    startActivity(intent);
                }

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,Settings.class);
                if (counter == 1) {                   //show ads after 2 clicks on button
                    if (pref_interstitial != null) {
                        intrestialsetup(pref_interstitial);
                        if (mInterstitialAd != null && intrestial_show == true) {
                            mInterstitialAd.show(MainActivity.this);
                            counter = 0;
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();

                                    startActivity(intent);
                                    Log.d("TAG", "The ad was dismissed.");
                                }
                            });
                        } else {
                            startActivity(intent);
                            Log.e("TOUSIF", "The interstitial ad wasn't ready yet.");
                        }
                    } else {
                        startActivity(intent);
                        Log.e("TOUSIF", "INTERSTITIAL SHOW is false");
                    }
                } else {
                    //increment counter
                    startActivity(intent);
                    counter++;
                    Log.e("MyClickCounter", String.valueOf(counter));
                }
            }
        });
    }

    private void showBannerAds(String banner_id){
        if (bannerEnabled.equalsIgnoreCase("true")){
            Log.d(TAG, "showBannerAds: true");
            //banner
            AdView adView = new AdView(this);

            adView.setAdSize(AdSize.BANNER);

            adView.setAdUnitId(banner_id);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adshere.addView(adView);
        }
        else{
            Log.d(TAG, "showBannerAds: false");

            adshere.setVisibility(View.INVISIBLE);
        }

    }

    private void showBothAds(String banner_id, String intrestial_id){
        //banner
        showBannerAds(banner_id);

        //interstitial
        intrestialsetup(intrestial_id);
    }

    private AsyncHttpClient getClient(){
        if (client == null)
        {
            client = new AsyncHttpClient();
            client.setTimeout(46000);
            client.setConnectTimeout(40000); // default is 10 seconds, minimum is 1 second
            client.setResponseTimeout(40000);
        }
        return client;
    }

    private void callAPI() {
        RequestParams jsonParams = new RequestParams();

        String id = "1";
        jsonParams.put("id",id);

        getClient().post(Constants.AD_URL, jsonParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showNow.showLoadingDialog(MainActivity.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String json = new String(responseBody);
                Log.e("RESPONSE", "onSuccess: " + json);
                showNow.scheduleDismiss();

                try {
                    JSONArray jsonArray=new JSONArray(json);
                    for(int i=0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String ID       = jsonobject.getString("id");
                        String app_name    = jsonobject.getString("app_name");
                        String app_id  = jsonobject.getString("app_id");
                        adsEnabled = jsonobject.getString("showAds");
                        bannerEnabled= jsonobject.getString("banner_show");
                        banner_id = jsonobject.getString("banner_id");
                        intrestialEnabled = jsonobject.getString("intrestial_show");
                        intrestial_id = jsonobject.getString("intrestial_id");

                        Log.e("ID",ID + "app_name" + app_name + "app_id" + app_id + "showAds" + showAds
                                + "banner_show" + banner_show + "banner_id" + banner_id + "intrestial_show" + intrestial_show
                                + "intrestial_id" + intrestial_id);



                        if (adsEnabled=="false"){
                            Log.d(TAG, "onSuccess: showAds false"+showAds);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
                            bottom_layout.setLayoutParams(params);
                        }else {

                            showBannerAds(banner_id);
                            intrestialsetup(intrestial_id);
                            Log.d(TAG, "onSuccess: showAds true "+"And intrestials="+intrestialEnabled);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("RESPONSEERROR", e.getMessage());
                    showNow.desplayErrorToast(MainActivity.this,e.getMessage());
                    //progressBar.setVisibility(View.GONE);
                    showNow.scheduleDismiss();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String json = new String(responseBody);
                Log.e("REPONSE2", "onSuccess: " + json);
                showNow.desplayErrorToast(MainActivity.this,json);
                showNow.scheduleDismiss();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                //progressBar.setVisibility(View.GONE);
                showNow.scheduleDismiss();
            }
        });
    }

    private void audioPickDialog() {
        if (checkStoragePermission()){
            //storage permission allowed
            pickAudio();
        }
        else {
            //not allowed
            requestStoragePermission();
        }
    }

    private void pickAudio() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("audio/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Your Audio"),
                AUDIO_FILE_REQUEST);
        Toast.makeText(this, "audio", Toast.LENGTH_SHORT).show();
    }

    private void videoPickDialog() {
        String[] options = {"Video Files"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Video")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //HANDLE CLICKS
                        if (which == 0){
                            //GALLERY CLICK
                            if (checkStoragePermission()){
                                //STORAGE PERMISSION ALLOWED
                                pickVideo();
                            } else
                            {
                                //NOT ALLOWED
                                requestStoragePermission();
                            }
                        }
                        else {

                        }
                    }
                })
                .show();
    }

    private void pickVideo() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("video/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Your Video"),
                VIDEO_PICK_GALLERY_CODE);
        Toast.makeText(this, "video", Toast.LENGTH_SHORT).show();
    }

    private void moveImgData() throws FileNotFoundException{
        File my_file_source = new File(myPath);
        File sdCard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File my_file_Destination = new File(sdCard.getAbsolutePath() + "/.MyPICS");
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, ""+myPath+ "moved to: " +expFile, Toast.LENGTH_SHORT).show();
    }

    private void moveVideoData() throws FileNotFoundException{
        File source = new File(myPath);
        String sdCard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/.MyVIDEOS";
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
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, ""+myPath+ "moved to: " +destination, Toast.LENGTH_SHORT).show();
    }

    private void moveAudioData() throws FileNotFoundException{
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
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, ""+myPath+ "moved to: " +destination, Toast.LENGTH_SHORT).show();
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean isAudioFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("audio");
    }

    private byte[] getBytesFromFile(File file) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(file);
        return data;
    }

    private void imagePickDialog() {
        String[] options = {"Image Files"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //HANDLE CLICKS
                            //GALLERY CLICK
                            if (checkStoragePermission()){
                                //STORAGE PERMISSION ALLOWED
                                pickFromGallery();
                            } else
                            {
                                //NOT ALLOWED
                                requestStoragePermission();
                            }
                    }
                })
                .show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);

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

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return  result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
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

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return  result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
                        myTxt.setVisibility(View.VISIBLE);
                        myTxt.setText("Picked Image");

                        moveImgData();
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

                        moveVideoData();
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

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null, null, null,
                                         null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getRealPathFromUri(Context context, Uri my_uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(my_uri, proj, null, null,
                       null);
        if (cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }

    private void initViews() {

        btnAudio = findViewById(R.id.btnAudio);
        btnVideo = findViewById(R.id.btnVideo);
        btnFile = findViewById(R.id.btnFile);
        btnImage = findViewById(R.id.btnImage);

        about_us = findViewById(R.id.abt_us);
        settings = findViewById(R.id.settings);
        rate_us = findViewById(R.id.rate_us);


        showNow=new ShowNow(this);

        bottom_layout = findViewById(R.id.bottom_layout);
    }

    public void intrestialsetup(String intrestial_id){

        if (intrestialEnabled.equalsIgnoreCase("true")){
            Log.d(TAG, "intrestialsetup: true");
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            Log.i("TAG", "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i("TAG", loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });
        }
        else {
            Log.d(TAG, "intrestialsetup: false");
        }

    }

    }
