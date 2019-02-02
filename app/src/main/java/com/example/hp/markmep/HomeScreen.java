package com.example.hp.markmep;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class HomeScreen extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    ImageView circleImageView, c;
    ImageView plus, cross;
    String name, regnum;
    DataBaseHelper mydb;
    Firebase ref;
    EditText textSub;
    TextView tName, tRoll,tvdate;
    int count;
    Button bDone,Calder;
    private static final int PICK_IMAGE = 100;
    RelativeLayout container;
    RecyclerView recyclerView;
    RingProgressBar ringProgressBar;
    int progress = 0;
    Spinner spinner;
    List<Product> productList;
    ImageView tutorial;
    String subselec;

    LinearLayout l2;






    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (progress < count * 100) {

                    progress++;
                    ringProgressBar.setProgress(progress);
                }
            }
        }
    };


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @RequiresApi(api = Build.VERSION_CODES.DONUT)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    if (count % 2 == 0) {
                        setTitle("Generate");

                    }

                    startActivity(new Intent(HomeScreen.this, ScanActivity.class)
                            .putExtra("name", name)
                            .putExtra("regnum", regnum)
                            .putExtra("count", count));
                    return true;
                case R.id.navigation_dashboard:
                         Cursor res=mydb.viewData();
                         int c=0;
                         update();


                    productList = new ArrayList<>();
                    while (res.moveToNext()) {
                        Product p=new Product(c,res.getString(1),res.getString(0),res.getString(2));
                        c++;

                        productList.add(p);


                    }



                    return true;
                case R.id.shareMenu:


                    ApplicationInfo api = getApplicationContext().getApplicationInfo();
                    String path = api.sourceDir;


                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("application/vnd.android.package-archive");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                    return true;
            }
            return false;
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mydb = new DataBaseHelper(this);


        tName = findViewById(R.id.tName);
        tRoll = findViewById(R.id.tRoll);
        tvdate=findViewById(R.id.tvdate);

        bDone = findViewById(R.id.bDone);
        Calder=findViewById(R.id.calender);
        textSub = findViewById(R.id.textSub);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        name = getIntent().getStringExtra("name");
        regnum = getIntent().getStringExtra("regnum");
        count = getIntent().getIntExtra("count", 1);
        tutorial=findViewById(R.id.tutorial);


        l2=findViewById(R.id.l2);
        mydb=new DataBaseHelper(this);
        circleImageView = findViewById(R.id.photo);
        c = findViewById(R.id.photoT);
        plus = findViewById(R.id.plus);
        cross = findViewById(R.id.cross);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        circleImageView.setBackgroundColor(getResources().getColor(R.color.white));



        //initializing the productlist
        productList = new ArrayList<>();


        //creating recyclerview adapter
        ProductAdapter adapter = new ProductAdapter(this, productList);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);

        ringProgressBar=findViewById(R.id.circularP);
        new Thread(new Runnable(){
            @Override
            public void run() {
                for (int i=0;i<count*50;i++){

                        try {
                            Thread.sleep(40);
                            myHandler.sendEmptyMessage(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
            }
        }).start();
        spinner=findViewById(R.id.spinnerT);
        spinner.setPopupBackgroundResource(R.drawable.btnw);


        spinner.setOnItemSelectedListener(HomeScreen.this);


        if (count%2==0){
           circleImageView.setVisibility(View.INVISIBLE);
           c.setVisibility(View.VISIBLE);
           ringProgressBar.setVisibility(View.INVISIBLE);
           Calder.setVisibility(View.VISIBLE);
           tvdate.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            tutorial.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            l2.setVisibility(View.VISIBLE);
            loadSpinnerData();


        }
        else
            update();
        tName.setText(name);
        tRoll.setText("("+regnum+")");

        //viewData(mydb);








    }


    private void loadSpinnerData()//to load spinner data
    {
            DataBaseHelper db = new DataBaseHelper(getApplicationContext());
            //Cursor c = db.getSubjects();
            ArrayList<String>subjects=new ArrayList<String>();
            Cursor c=db.getSubjects();
            while(c.moveToNext())
            {
                subjects.add(c.getString(0));
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, subjects);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinner.setAdapter(dataAdapter);
        }

    @Override
    protected void onStart() {
        super.onStart();
        update();



    }





    private void update() {
        ProductAdapter adapter = new ProductAdapter(this, productList);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }

    public void AddSubject(View view) {
        update();
            textSub.setVisibility(View.VISIBLE);
            bDone.setVisibility(View.VISIBLE);
            plus.setVisibility(View.INVISIBLE);
            cross.setVisibility(View.VISIBLE);



    }

    public void DONE(View view) {//on subject add button click
        if (textSub.getText().length()==0){
            Toast.makeText(this,"enter the subject first",Toast.LENGTH_LONG).show();
        }
        else
        {
            boolean a= mydb.insert_subject(textSub.getText().toString());
            if (a){
                textSub.setText("");
                //Toast.makeText(this,"added",Toast.LENGTH_SHORT).show();
                Cursor res=mydb.viewData();
                int c=0;


                productList = new ArrayList<>();
                while (res.moveToNext()) {
                    Product p = new Product(c, res.getString(1), res.getString(0),res.getString(2));
                    c++;
                    productList.add(p);
                }
                update();
            }
            else
                Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
        }

    }

    public void Clear(View view) {

        textSub.setVisibility(View.INVISIBLE);
        bDone.setVisibility(View.INVISIBLE);
        plus.setVisibility(View.VISIBLE);
        cross.setVisibility(View.INVISIBLE);


    }


    public void DatePicker(View view) {//to let teacher select date
        android.support.v4.app.DialogFragment datePicker=new com.example.hp.markmep.DatePicker();
        datePicker.show(getSupportFragmentManager(),"date picker");

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);

        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        Toast.makeText(this,"Loading students for "+selectedDate,Toast.LENGTH_LONG).show();


        if (subselec!=null) {
            ref = new Firebase("https://attendance-app-3558c.firebaseio.com/" + name + "/" + subselec + "/" + selectedDate + "/present/");
            Intent i=new Intent(HomeScreen.this,PresentStudents.class)
                    .putExtra("name",name)
                    .putExtra("subselec",subselec)
                    .putExtra("selectedDate",selectedDate);
            startActivity(i);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text=parent.getItemAtPosition(position).toString();
        subselec=text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

