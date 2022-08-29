package com.example.bettermap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase  extends SQLiteOpenHelper {

    private static final String MARKERS_TABLE ="MARKERS_TABLE" ;
    public static final String MARKER_LOCALITY = "MARKER_LOCALITY";
    public static final String MARKER_COUNTRYNAME = "MARKER_COUNTRYNAME";
    public static final String MARKER_POSTALCODE = "MARKER_POSTALCODE";
    public static final String ID = "ID";
    private static final String MARKER_LATITUDE = "MARKER_LATITUDE";
    private static final String MARKER_LONGITUDE ="MARKER_LONGITUDE" ;

    public MyDatabase(@Nullable Context context) {
        super(context,"my_markers3.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + MARKERS_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MARKER_LOCALITY + " TEXT, " + MARKER_COUNTRYNAME +" TEXT, "+ MARKER_POSTALCODE +" TEXT, "+ MARKER_LATITUDE + " REAL, "+ MARKER_LONGITUDE +" REAL "+")";
        db.execSQL(createTable);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    //Προσθήκη γεγονότος
    public boolean addOne(MyMarker marker){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MARKER_LOCALITY,marker.getLocality());
        cv.put(MARKER_COUNTRYNAME,marker.getCountryName());
        cv.put(MARKER_POSTALCODE,marker.getPostalCode());
        cv.put(MARKER_LATITUDE,marker.getLatitude());
        cv.put(MARKER_LONGITUDE,marker.getLongitude());
        long insert = db.insert(MARKERS_TABLE, null, cv);
        db.close();

        if (insert == -1) return  false;
        else return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    //Επιστροφή λίστας με τα γεγονότα,απο τον σχεσιακό πίνακα.
    public List<MyMarker> getTheMarkers(){
        List<MyMarker> markersList = new ArrayList<>();
        String query = "SELECT * FROM "+ MARKERS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                int markerId = cursor.getInt(0);
                String markerLocality = cursor.getString(1);
                String markerCountry = cursor.getString(2);
                String markerPostalCode = cursor.getString(3);
                Double markerLatitude = cursor.getDouble(4);
                Double markerLongitude = cursor.getDouble(5);
                MyMarker newMarker = new MyMarker(markerId,markerLocality,markerCountry,markerPostalCode,markerLatitude,markerLongitude);
                markersList.add(newMarker);
            }while (cursor.moveToNext());
        }
        else{}
        cursor.close();
        db.close();
        return markersList;
    }
    public boolean deleteOne(MyMarker marker){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+ MARKERS_TABLE +" WHERE "+ID+" = "+ marker.getId();
        Cursor cursor = db.rawQuery(query, null);


        if (cursor.moveToFirst()) return true;
        else return false;

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
