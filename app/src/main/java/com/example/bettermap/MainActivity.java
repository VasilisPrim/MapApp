package com.example.bettermap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.DecimalFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.bettermap.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    public static final int INTERVAL_MILLIS1 = 10000;
    public static final int INTERVAL_MILLIS2 = 5000;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    TextView speed;
    TextView locality;
    TextView countryName;
    TextView postaCode;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    MyDatabase myDatabase ;
    private List<MyMarker> allMarkers;
    MarkerOptions markerOptions;
    LatLng latLng;
    Marker myLocationMarker;
    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private float[] results  = new float[3];


    @RequiresApi(api = Build.VERSION_CODES.S)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
               .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        speed = findViewById(R.id.txt_speed);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        myDatabase = new MyDatabase(MainActivity.this);
        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setInterval(INTERVAL_MILLIS1);
        locationRequest.setFastestInterval(INTERVAL_MILLIS2);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                myLocationMarker.remove();//αφαιρουμε τον προηγούμενο marker πριν την ανανέωση.
                updateOrientationAngles();
                updateValues(location);
            }
        };

        updateGps();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 99:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGps();
                } else {
                    Toast.makeText(this, "The app require permission to work", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void updateGps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(Location location) {
                    updateValues(location);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateValues(Location location) {
        DecimalFormat f = new DecimalFormat("##.00");
        if (location.hasSpeed()) {
            speed.setText(String.valueOf(f.format(location.getSpeed()*3.6))+ " Km/h");
        } else {
            speed.setText("0");
        }
        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            if(location.getSpeed()*3.6<=5){
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                markerOptions= new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressList.get(0).getLocality()+","+addressList.get(0).getCountryName());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.zIndex(1.0f);
                markerOptions.snippet(addressList.get(0).getPostalCode()+addressList.get(0));
                myLocationMarker = mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.0f));
            }
            else{List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                markerOptions= new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressList.get(0).getLocality()+","+addressList.get(0).getCountryName());
                markerOptions.icon(bitmapDescriptor(getApplicationContext(),R.drawable.ic_baseline_arrow_upward_24));
                markerOptions.zIndex(1.0f);
                markerOptions.snippet(addressList.get(0).getPostalCode()+addressList.get(0)).flat(true).rotation((float) Math.toDegrees(results[0]));
                myLocationMarker = mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.0f));}


        }catch(Exception e){
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window,null);
                locality = v.findViewById(R.id.locality);
                countryName = v.findViewById(R.id.country_name);
                postaCode = v.findViewById(R.id.postal_code);
                MyMarker myMarker = (MyMarker) marker.getTag();
                if (myMarker!= null) {
                    locality.setText(myMarker.getLocality());
                    countryName.setText(myMarker.getCountryName());
                    postaCode.setText(myMarker.getPostalCode());
                }
                return v;
            }
        });
        //Listener για προσθήκη σημείου ενδιαφέροντος.
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
               markerOptions.position(latLng);
               mMap.addMarker(markerOptions).setTag(createTheMarker(latLng));

            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                marker.hideInfoWindow();
            }
        });

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(@NonNull Marker marker) {
                myDatabase.deleteOne((MyMarker) marker.getTag());
                marker.remove();
            }
        });

        allMarkers = myDatabase.getTheMarkers();
        for (MyMarker marker:allMarkers) {
            latLng = new LatLng(marker.getLatitude(),marker.getLongitude());
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            mMap.addMarker(markerOptions).setTag(marker);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private MyMarker createTheMarker(LatLng latLng){
        Geocoder geocoder = new Geocoder(MainActivity.this);
        try{
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            String locality = addressList.get(0).getLocality();
            String countryName = addressList.get(0).getCountryName();
            String postalCode = addressList.get(0).getPostalCode();
            double latitude = addressList.get(0).getLatitude();
            double longitude = addressList.get(0).getLongitude();
            MyMarker marker  = new MyMarker(locality,countryName,postalCode,latitude,longitude);
            myDatabase.addOne(marker);
            Toast.makeText(this, marker.toString(), Toast.LENGTH_SHORT).show();
            return marker;
        }catch (Exception e){
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        sensorManager.unregisterListener(this);

    }
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);
        results = SensorManager.getOrientation(rotationMatrix, orientationAngles);
        Toast.makeText(this, String.valueOf(Math.toDegrees(results[0])), Toast.LENGTH_SHORT).show();
    }
    private BitmapDescriptor bitmapDescriptor(Context context,int vectorId){

        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getMinimumWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}



