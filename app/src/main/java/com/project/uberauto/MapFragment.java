package com.project.uberauto;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    View mapview;
    private GoogleMap mMap;
    GoogleApiClient googleapiclient;
    Location mlocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    LocationRequest mlocationequest;
    private Boolean mLocationPermissionsGranted = true;
    private LocationManager locationManager;
    private static final float DEFAULT_ZOOM = 16f;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private Marker mPreviousMarker;
    private EditText msearchtext;
    private static final String TAG = "Map";
    private static final int ERROR_DIALOG_REQUEST=9001;
   //private LocationClient
    // private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mapview = inflater.inflate(R.layout.fragment_map, container, false);
     //   isServiceOk();
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return mapview;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        msearchtext = mapview.findViewById(R.id.searchbar);
        return mapview;
    }

    private void init(){
        msearchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH
                              || actionId==EditorInfo.IME_ACTION_DONE
                              ||event.getAction()==event.ACTION_DOWN
                              ||event.getAction()==event.KEYCODE_ENTER){
                    // EXECUTE method to search
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate(){
        String searchstring = msearchtext.getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address>list = new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchstring,1);
        }
        catch (IOException e){
            Log.d(TAG,"GEOlOCATION exception" + e.getMessage());
        }
        if(list.size()>0){
            Address address = list.get(0);
            Log.d(TAG,"lOCATION FOUND :" +address.toString());
            Toast.makeText(getContext(), "ADDREESS FOUND " +address.toString(), Toast.LENGTH_LONG).show();
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),15f);
        }
        else{
            Toast.makeText(getContext(), "failed to search location", Toast.LENGTH_LONG).show();
        }

    }

    private void getDeviceLocation() {
        Log.d("", "getting current device loctaion");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            // if permission granted
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                       //     Log.d(TAG,"location found", );
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        } else {
                        //    Log.d(TAG,"CURRENT LOCATION IS ",);
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        } catch (SecurityException se) {
            Log.d("devicelocationException", String.valueOf(se));

        }
    }

    private void moveCamera(LatLng latlng, float zoom) {
        Log.d("moving camera too ", "latitude=" + String.valueOf(latlng.latitude) + "longitude=" + String.valueOf(latlng.longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }


    @Override
    public void onLocationChanged(Location location) {
        mlocation=location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getContext(), "Connection Established", Toast.LENGTH_SHORT).show();
        mlocationequest = new LocationRequest();
        mlocationequest.setInterval(1000);
        mlocationequest.setFastestInterval(1000);
        mlocationequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);   //high battery drainage
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(), "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        init();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mPreviousMarker != null)
                    mPreviousMarker.remove();
                mPreviousMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));

            }
        });
    }
}
