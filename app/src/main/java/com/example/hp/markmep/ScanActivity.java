package com.example.hp.markmep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScanActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemSelectedListener{

    TextView tText;
    public Firebase mRef,mRefAttCheck,studAttendance;
    String name,regnum;
    DataBaseHelper mydb;
    AlertDialog alert;
    int count;
    String attref;
    ImageButton StartScan;
    Spinner spinner;
    String subselec,selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Firebase.setAndroidContext(this);
        name=getIntent().getStringExtra("name");
        regnum=getIntent().getStringExtra("regnum");
        count=getIntent().getIntExtra("count",1);
        spinner=findViewById(R.id.spinner);
        spinner.setPopupBackgroundResource(R.drawable.btnw);
        loadSpinnerData();

        tText=findViewById(R.id.tText);
        if (count%2==0){
            tText.setText("click to generate QR");
        }
        StartScan=findViewById(R.id.buttonScan);
        mydb=new DataBaseHelper(this);


        final Activity activity=this;
        StartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkCheck();

                if (count%2!=0){
                    if (isNetworkAvailable(ScanActivity.this)){


                        IntentIntegrator integrator=new IntentIntegrator(activity);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                        integrator.setPrompt("focus on QR code");
                        integrator.setCameraId(0);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.setOrientationLocked(true);
                        integrator.setBeepEnabled(false);
                        integrator.initiateScan();
                    }


                }
                else {
                    if (!subselec.isEmpty()){
                        Toast.makeText(ScanActivity.this,subselec+" "+selectedDate,Toast.LENGTH_LONG).show();
                        attref="https://attendance-app-3558c.firebaseio.com/"+name+"/"+subselec+"/"+selectedDate+"/";
                        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
                        try
                        {
                            BitMatrix bitMatrix=multiFormatWriter.encode(attref, BarcodeFormat.QR_CODE,500,500);
                            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
                            Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);

                            StartScan.setImageBitmap(bitmap);
                            studAttendance=new Firebase("https://attendance-app-3558c.firebaseio.com/"+name+"/"+subselec+"/"+selectedDate);
                            studAttendance.child("Status").setValue(1);

                        }
                        catch (WriterException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                        Toast.makeText(ScanActivity.this,"select subject then date",Toast.LENGTH_SHORT).show();

                }

            }
        });
        spinner.setOnItemSelectedListener(this);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
         selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());


    }
    private void NetworkCheck() {

        if (isNetworkAvailable(ScanActivity.this)){


        }
        else {

            AlertDialog.Builder builder=new AlertDialog.Builder(ScanActivity.this);
            builder.setTitle("NO INTERNET");
            builder.setMessage("Your attendance can not be marked without Internet");
            Toast.makeText(this,"Turn internet on",Toast.LENGTH_LONG).show();
            builder.setCancelable(true);
            alert=builder.create();
            alert.show();
        }
    }//to check the internet connection state
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo()!=null
                &&connectivityManager.getActiveNetworkInfo().isConnected();
    }//to check if a connection can be made

    private void loadSpinnerData() {
        DataBaseHelper db = new DataBaseHelper(getApplicationContext());
        //Cursor c = db.getSubjects();
        ArrayList<String>subjects=new ArrayList<String>();
        Cursor c=db.getSubjects();
         while(c.moveToNext())
         {
             subjects.add(c.getString(0));
         }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, subjects);


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }//load the data onto a spinner widget


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result!=null)
        {
            if (result.getContents()==null){
                Toast.makeText(this,"attendance not marked",Toast.LENGTH_LONG).show();
            }
            else
            {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert v != null;
                v.vibrate(100);
                final String output=result.getContents();
                mRefAttCheck=new Firebase(output).child("Status");

                mRefAttCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue(int.class)==0){
                            Toast.makeText(ScanActivity.this,"attendance is turned off",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            mRef = new Firebase(output).child("present");
                            Firebase mRefChild = mRef.child(regnum);
                            mRefChild.setValue(name);

                            Toast.makeText(ScanActivity.this,"marked present ",Toast.LENGTH_SHORT).show();



                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                        Toast.makeText(ScanActivity.this,"error,retry!",Toast.LENGTH_SHORT).show();
                    }
                });

                        finish();


            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }//to handle output response of QR code




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        subselec=parent.getItemAtPosition(position).toString();



    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        subselec=parent.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        studAttendance=new Firebase("https://attendance-app-3558c.firebaseio.com/"+name+"/"+subselec+"/"+selectedDate);
        studAttendance.child("Status").setValue(0);


    }
}
