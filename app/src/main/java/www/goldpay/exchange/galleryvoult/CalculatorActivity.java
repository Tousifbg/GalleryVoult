package www.goldpay.exchange.galleryvoult;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class CalculatorActivity extends AppCompatActivity {

    Button btn0,btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btnPercent,btnPlus,btnMinus,btnMultiply,btnDivision,btnEqual,btnClear,btnDot,btnBracket;
    TextView tvInput,tvOutput;
    String process;
    boolean checkBracket = false;

    Dialog myPasswordDialog;
    String password_input_data;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String password_code;

    String check_pass_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        initViews();

        //SHARED PREFERENCES TO CHECK IF USER HAS PASSWORD OR NOT
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        boolean firstRun = pref.getBoolean("firstRun",false);
        if (firstRun == false){
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstRun", true);
            editor.commit();
            Intent i=new Intent(CalculatorActivity.this,OnBoardingScreens.class);
            startActivity(i);
            finish();
        }
        else {
            password_code = pref.getString("my_password", "");
            Log.e("SAVED_SHARED_PREF", password_code);

            Bundle extra = getIntent().getExtras();
            if (extra == null) {

            } else {
                check_pass_value = extra.getString("myValue");
            }

            if (password_code != null && !password_code.equals("")) {
                Log.e("TOUSIF", "password exist");
                //Toast.makeText(this, "PASS EXIST", Toast.LENGTH_SHORT).show();
                if (password_code.equals(check_pass_value)){
                    showMyPasswordNewDialog();
                }
            }
            else{
                //Toast.makeText(this, "PASS NOT EXIST", Toast.LENGTH_SHORT).show();
                Log.e("TOUSIF", "password not exist");
                showMyPasswordDialog();
            }
        }


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvInput.setText("");
                tvOutput.setText("");
            }
        });

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "0");
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "1");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "2");
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "3");
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "4");
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "5");
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "6");
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "6");
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "7");
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "8");
            }
        });

        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "9");
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "+");
            }
        });


        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "-");
            }
        });

        btnMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "×");
            }
        });

        btnDivision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "÷");
            }
        });

        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + ".");
            }
        });

        btnPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvInput.getText().toString();
                tvInput.setText(process + "%");
            }
        });

        btnBracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBracket){
                    process = tvInput.getText().toString();
                    tvInput.setText(process + ")");
                    checkBracket = false;
                }else{
                    process = tvInput.getText().toString();
                    tvInput.setText(process + "(");
                    checkBracket = true;
                }

            }
        });

        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input;
                input = tvInput.getText().toString();
                if (input.equals(password_code))
                {
                    Intent intent = new Intent(CalculatorActivity.this,MainActivity.class);
                    startActivity(intent);
                }else {
                    if (process == null)
                    {
                        Toast.makeText(CalculatorActivity.this, "Nothing to calculate !"
                                , Toast.LENGTH_SHORT).show();
                    }else {
                        process = tvInput.getText().toString();

                        process = process.replaceAll("×","*");
                        process = process.replaceAll("%","/100");
                        process = process.replaceAll("÷","/");

                        Context rhino = Context.enter();

                        rhino.setOptimizationLevel(-1);

                        String finalResult = "";

                        try {
                            Scriptable scriptable = rhino.initStandardObjects();
                            finalResult = rhino.evaluateString(scriptable,process,"javascript",
                                    1,null).toString();
                        }catch (Exception e){
                            finalResult="0";
                        }

                        tvOutput.setText(finalResult);
                    }
                }
            }
        });
    }

    private void showMyPasswordNewDialog() {
        myPasswordDialog = new Dialog(CalculatorActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
        myPasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myPasswordDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        myPasswordDialog.setContentView(R.layout.password_dialog);
        myPasswordDialog.setCancelable(false);
        myPasswordDialog.show();
        ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
        applyDim(root, 0.5f);

        TextView upperTxt;
        ImageView btnOK;
        EditText passwordField;

        btnOK = myPasswordDialog.findViewById(R.id.btnOK);
        passwordField = myPasswordDialog.findViewById(R.id.passwordField);
        upperTxt = myPasswordDialog.findViewById(R.id.upperTxt);

        upperTxt.setText("Confirm your password !");

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

                    if (password_code.equals(password_input_data))
                    {
                        Toast.makeText(CalculatorActivity.this, "Password matched!", Toast.LENGTH_SHORT).show();
                        myPasswordDialog.dismiss();
                        clearDim(root);

                    }
                    else {
                        passwordField.setError("Type your accurate password");
                    }
                }
            }
        });
    }


    private void showMyPasswordDialog() {
        myPasswordDialog = new Dialog(CalculatorActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
        myPasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myPasswordDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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
                    editor = pref.edit();
                    editor.putString("my_password", password_input_data);
                    Log.e("SHARED_OK", "ok");
                    editor.apply(); // commit changes
                    Toast.makeText(CalculatorActivity.this, "Password saved!", Toast.LENGTH_SHORT).show();
                    myPasswordDialog.dismiss();
                    clearDim(root);

                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    public static void clearDim(ViewGroup root) {
        ViewGroupOverlay overlay = root.getOverlay();
        overlay.clear();
    }

    public static void applyDim(ViewGroup root, float dimAmount) {
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, root.getWidth(), root.getHeight());
        dim.setAlpha((int) (255 * dimAmount));

        ViewGroupOverlay overlay = root.getOverlay();
        overlay.add(dim);
    }

    private void initViews() {
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);

        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnDivision = findViewById(R.id.btnDivision);
        btnMultiply = findViewById(R.id.btnMultiply);

        btnEqual = findViewById(R.id.btnEqual);

        btnClear = findViewById(R.id.btnClear);
        btnDot = findViewById(R.id.btnDot);
        btnPercent = findViewById(R.id.btnPercent);
        btnBracket = findViewById(R.id.btnBracket);

        tvInput = findViewById(R.id.tvInput);
        tvOutput = findViewById(R.id.tvOutput);
    }
}