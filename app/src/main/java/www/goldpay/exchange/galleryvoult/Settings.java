package www.goldpay.exchange.galleryvoult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Random;

public class Settings extends AppCompatActivity {

    //AppCompatButton btnPasswordChnage;


    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String password_code;
    String password_input_data;

    Dialog myPasswordDialog;

    private RelativeLayout adshere;
    String pref_banner, pref_interstitial;

    Boolean bannerShow, InterstitialShow;

    EditText passwordFieldd;

    ImageView passChangeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        adshere=findViewById(R.id.adView);

        initViews();

     /*   //banner
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/

        //SHARED PREFERENCES TO CHECK IF USER HAS PASSWORD OR NOT
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        password_code = pref.getString("my_password","");
        bannerShow = pref.getBoolean("banner_show",false);
        InterstitialShow = pref.getBoolean("interstitial_show",false);
        Log.e("TOUSIF", String.valueOf(bannerShow));
        Log.e("SAVED_SHARED_PREF", password_code);

        if (password_code != null && !password_code.equals("")){
            Log.e("TOUSIF", "password exist");
            passwordFieldd.setText(password_code);
            passwordFieldd.setFocusable(false);
            passwordFieldd.setClickable(false);
        }else {
            Log.e("TOUSIF", "password not exist");
        }

        pref_interstitial = pref.getString("interstitial_id","");
        Log.e("PREF_DATA",pref_banner + ": " + pref_interstitial);

        if (pref_banner != null && !bannerShow){
            showBannerAds(pref_banner);
            Log.e("TOUSIF","BANNER ID exist");
        }else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            adshere.setLayoutParams(params);
            Log.e("TOUSIF","No banner id");
            Toast.makeText(this, "banner false", Toast.LENGTH_SHORT).show();
        }

        passChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyPasswordDialog();   //show password dialog
            }
        });
    }

    private void showBannerAds(String pref_banner) {
        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId(pref_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adshere.addView(adView);
    }

    private void showMyPasswordDialog() {
        SharedPreferences.Editor editor =  pref.edit();
        editor.clear();
        editor.commit();
        myPasswordDialog = new Dialog(Settings.this, android.R.style.Theme_DeviceDefault_Dialog);
        myPasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        myPasswordDialog.setContentView(R.layout.password_dialog);
        myPasswordDialog.setCancelable(false);
        myPasswordDialog.show();
        ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
        applyDim(root, 0.5f);

        ImageView btnOK;
        EditText passwordField;

        btnOK = myPasswordDialog.findViewById(R.id.btnOK);
        passwordField = myPasswordDialog.findViewById(R.id.passwordField);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password_input_data = passwordField.getText().toString().trim();

                if (TextUtils.isEmpty(password_input_data))
                {
                    passwordField.setError("Set your password first");
                }
                else
                {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("my_password", password_input_data);
                    editor.putBoolean("firstRun", true);
                    Log.e("SHARED_OK", "ok");
                    editor.apply(); // commit changes
                    Toast.makeText(Settings.this, "Password saved!", Toast.LENGTH_SHORT).show();
                    myPasswordDialog.dismiss();
                    clearDim(root);

                  /*  finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);*/
                    Intent intent = new Intent(Settings.this,CalculatorActivity.class);
                    intent.putExtra("myValue",password_input_data);
                    startActivity(intent);
                }
            }
        });
    }

    private void applyDim(ViewGroup root, float dimAmount) {
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, root.getWidth(), root.getHeight());
        dim.setAlpha((int) (255 * dimAmount));

        ViewGroupOverlay overlay = root.getOverlay();
        overlay.add(dim);
    }

    public static void clearDim(ViewGroup root) {
        ViewGroupOverlay overlay = root.getOverlay();
        overlay.clear();
    }


    private void initViews() {
        passChangeBtn = findViewById(R.id.passChangeBtn);
        //passwordField = findViewById(R.id.passwordField);
        passwordFieldd = findViewById(R.id.passwordFieldd);
        //adView = findViewById(R.id.adView);
    }
}