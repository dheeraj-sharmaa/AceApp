package com.example.hp.markmep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import static android.hardware.camera2.params.BlackLevelPattern.COUNT;
import static android.provider.Telephony.BaseMmsColumns.SUBJECT;
import static io.fabric.sdk.android.Fabric.TAG;

/**
 * Created by bm on 9/18/2018.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    Context context;
    public static final String DATABASE_NAME="Attendance.db";
    public static final String TABLE_NAME="Student_table";
    public static final String COL2="Count";
    public static final String COL3="Present";
    public static final String COL1="Subject";
    public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"(" +COL1+" TEXT PRIMARY KEY,"+COL2+" INTEGER,"+COL3+" INTEGER);";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
            this.context = context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("CREATE_QUERY", "onCreate: "+CREATE_TABLE);
       //Toast.makeText(context,CREATE_TABLE,Toast.LENGTH_LONG).show();
        db.execSQL(CREATE_TABLE);


    }//to create a table

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

   /* public boolean insert_data(String Roll_no,String name)
    {
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put(COL1,Integer.parseInt(Roll_no));
        cv.put(COL2,name);
        cv.put(COL3,0);
        cv.put(COL4,0);
        cv.put(COL5,"");

        long result= db.insert(TABLE_NAME,null,cv);


        if(result==-1)
            return false;
        else
            return true;
    }*/

    public boolean insert_present(String subject)//to increase the count of present in local db
    {     SQLiteDatabase db=this.getWritableDatabase();


        ContentValues cv=new ContentValues();

        String query="SELECT * from "+TABLE_NAME+" where "+COL1+"="+subject;

        Cursor c=db.rawQuery(query,null);
        String cou=null;
        String pres=null;
        while(c.moveToNext())
        { cou=c.getString(1);
            pres=c.getString(2);}

        int present=0;
        if(pres!=null)
            present=Integer.parseInt(pres);
        int count=0;
        if(cou!=null)
         count=Integer.parseInt(cou);
        present=present+1;
        count=count+1;

        cv.put(COL1,subject);
        cv.put(COL2,count);
        cv.put(COL3,present);



        db.update(TABLE_NAME,cv,"Subject=?",new String[]{subject });
        return true;
    }
    public boolean insert_subject(String subject)//to insert a subject into the table
    {
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues cv=new ContentValues();
        int count=0;
        int present=0;

          cv.put(COL1,subject);
          cv.put(COL2,count);
          cv.put(COL3,present);


//      db.rawQuery("insert into "+TABLE_NAME+" ("+COL1+","+COL2+","+COL3+") VALUES("+subject+",0,0);",null);



       long res= db.insert(TABLE_NAME,null,cv);
        if(res==-1)
            return false;
        else
            return true;


     }

    public Cursor viewData()//to view the contents of the table
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME,null);
        return  res;
    }
    public Cursor getSubjects()//to get the subjects
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME,null);
        return  res;
    }
}
