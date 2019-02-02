package com.example.hp.markmep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jgabrielfreitas.core.BlurImageView;

public class MainActivity extends AppCompatActivity {

    private static final int PER_LOGIN =1000;
    EditText name;
    ImageView imgTS;
    EditText regnum;
    Button bProceed,bTeacher,bStudent;
    AlertDialog alert;
    Firebase refT,refS;
    TextView tvRegTeacher,note;
    public String nameText,regText;
     int count=1;
    RelativeLayout relLayout;
    BlurImageView blurImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        Window window=this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.tw__composer_black));
        NetworkCheck();//to check network state
        bProceed=findViewById(R.id.bProceed);
        regnum=findViewById(R.id.etRegNum);
        name=findViewById(R.id.etName);
        bStudent=findViewById(R.id.bStudent);
        bTeacher=findViewById(R.id.bTeacher);
        imgTS=findViewById(R.id.imgTS);
        tvRegTeacher=findViewById(R.id.tvTeacher);
        note=findViewById(R.id.note);
        relLayout=findViewById(R.id.relLayout);
        blurImageView=findViewById(R.id.blur);
        //tvRegTeacher=findViewById(R.id.tvTeacher);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseAuth auth=FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){//to check if alredy registered
            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
            loadData();
            Intent newActivity = new Intent(MainActivity.this, HomeScreen.class)
                    .putExtra("name",name.getText().toString())
                    .putExtra("regnum",regnum.getText().toString())
                    .putExtra("count",count);

            assert user != null;
            //Toast.makeText(this,user.getEmail(),Toast.LENGTH_SHORT).show();
            startActivity(newActivity);
            finish();

        }
        else {
            Toast.makeText(this,"you need to register first",Toast.LENGTH_SHORT).show();

        }
        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString()!=""&&regnum.getText().toString()!="")//to ensure non empty fields
                {
                    saveData();
                    loadData();
                    if (count%2!=0){

                        refS=new Firebase("https://attendance-app-3558c.firebaseio.com/students/");
                        Firebase childRef=refS.child(name.getText().toString());
                        childRef.setValue(regnum.getText().toString());
                    }
                    else
                    {
                        refT=new Firebase("https://attendance-app-3558c.firebaseio.com/teachers/");
                        refT.child(regnum.getText().toString()).setValue(name.getText().toString());

                    }


                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                            .setAllowNewEmailAccounts(true).build(),PER_LOGIN);//google sign in start
                }
                else
                    Toast.makeText(MainActivity.this,"fields are mandatory",Toast.LENGTH_SHORT).show();



            }
        });
    }

    private void saveData() {//to save name and reg num using shared preferences

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString("name",name.getText().toString());
        editor.putString("regnum",regnum.getText().toString());
        editor.putInt("count", count);
        editor.apply();

    }
    public void loadData(){//to fetch user data when required
            SharedPreferences sharedPreferences=getSharedPreferences("logDetails",Context.MODE_PRIVATE);
            nameText=sharedPreferences.getString("name","na");
            regText=sharedPreferences.getString("regnum","na");
            count=sharedPreferences.getInt("count",1);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==PER_LOGIN){
            handleSignInResponse(resultCode,data);
        }
    }

    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode==RESULT_OK) {

            loadData();
            Intent newActivity = new Intent(MainActivity.this, HomeScreen.class)
                    .putExtra("name",nameText)//send info to next activity
                    .putExtra("regnum",regText)
                    .putExtra("count",count);

            startActivity(newActivity);
        }
        else
        {
            Toast.makeText(MainActivity.this, "login failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void NetworkCheck() {//to ensure connectivity

        if (isNetworkAvailable(MainActivity.this)){


        }
        else {//alert dialog to show no internet error

            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("NO INTERNET");
            builder.setMessage("this app requires internet to work!");
            builder.setCancelable(true);
            alert=builder.create();
            alert.show();
        }
    }
    private static boolean isNetworkAvailable(Context context) {//network check
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo()!=null
                &&connectivityManager.getActiveNetworkInfo().isConnected();
    }



    public void bStudentClick(View view) {//to do on student button click
        count=1;
        bProceed.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        regnum.setVisibility(View.VISIBLE);
        imgTS.setVisibility(View.INVISIBLE);
        tvRegTeacher.setText("Student Registration");
        blurImageView.setVisibility(View.VISIBLE);
        note.setVisibility(View.VISIBLE);
        int i;
        for(i=1;i<=100;i++){
            blurImageView.setBlur(i);
        }
        bTeacher.setVisibility(View.INVISIBLE);


    }

    public void bTeacherClick(View view) {//to do on teacher button click
        count=2;
        bProceed.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        regnum.setVisibility(View.VISIBLE);imgTS.setVisibility(View.INVISIBLE);
        tvRegTeacher.setText("Teacher Registration");
        relLayout.setBackgroundColor(R.drawable.mainbackground);
        blurImageView.setVisibility(View.VISIBLE);
        note.setVisibility(View.VISIBLE);
        int i;
        for(i=1;i<=100;i++){
            blurImageView.setBlur(i);
        }
        bStudent.setVisibility(View.INVISIBLE);
    }

}
/*
* */