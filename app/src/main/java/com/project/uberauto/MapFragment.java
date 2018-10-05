package com.project.uberauto;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener, RoutingListener {


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
    private AutoCompleteTextView msearchtext;
    private static final String TAG = "Map";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private PlaceAutoCompleteAdaptar mPlaceAutoCompleteAdaptar;
    private GoogleApiClient mGoogleApiClient;
    private ImageView mylocation;
    private List<Polyline> polylines;
    private static final int REQUEST_CODE_PERMISSION = 2;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.light_blue_500, R.color.purple_500, R.color.primary_dark_material_light};

    //    //private LocationClient
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        polylines = new ArrayList<>();
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return mapview;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        mylocation = mapview.findViewById(R.id.mylocation);
        msearchtext = mapview.findViewById(R.id.searchbar);
        mylocation.setOnClickListener(gotomylocation);
        return mapview;
    }

    View.OnClickListener gotomylocation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                            moveCamera(new LatLng(location.getLatitude(),location.getLatitude()),16);
                    } else {
                        Toast.makeText(getContext(), "Location empty :(", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    private void init() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        mPlaceAutoCompleteAdaptar = new PlaceAutoCompleteAdaptar(getActivity(), Places.getGeoDataClient(getActivity(), null), LAT_LNG_BOUNDS, null);
        msearchtext.setAdapter(mPlaceAutoCompleteAdaptar);
        msearchtext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(getContext(), getView());
                eraseRoute();
                geoLocate();
            }
        });
        msearchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == event.ACTION_DOWN
                        || event.getAction() == event.KEYCODE_ENTER) {
                    // EXECUTE method to search
                    geoLocate();
                }
                return false;
            }
        });
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void geoLocate() {
        String searchstring = msearchtext.getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchstring, 1);
        } catch (IOException e) {
            Log.d(TAG, "GEOlOCATION exception" + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "lOCATION FOUND :" + address.toString());
            // Toast.makeText(getContext(), "ADDREESS FOUND " +address.toString(), Toast.LENGTH_LONG).show();
            LatLng destination = new LatLng(address.getLatitude(), address.getLongitude());
            moveCamera(destination, 15f);
            if(mPreviousMarker != null)
                mPreviousMarker.remove();
            mPreviousMarker = mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
            getRoutetoLocation(new LatLng(address.getLatitude(),address.getLongitude()));
        } else {
            Toast.makeText(getContext(), "Location not found", Toast.LENGTH_LONG).show();
        }
    }

    public void getRoutetoLocation(LatLng destinationlatlng){
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.BIKING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mlocation.getLatitude(),mlocation.getLongitude()),destinationlatlng)
                .key("AIzaSyB_FOWftbNi8uXdXXOJmrb0Y_MBqykux7E")
                .build();
        routing.execute();
    }

    private void getDeviceLocation() {
        Log.d("", "getting current device location");
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
        mlocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
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
    public void onMapReady(GoogleMap googleMap) {
        // Toast.makeText(getContext(), "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        // if Permission not already given
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(getActivity(), new String[]{mPermission}, REQUEST_CODE_PERMISSION);
            Toast.makeText(getContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
            // checkLocationPermission();
            return;
        }
        else{
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
            init();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
            mylocation.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.addRule(5, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
            mylocation.setLayoutParams(layoutParams);
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mPreviousMarker != null)
                    mPreviousMarker.remove();
                mPreviousMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission ","Granted");
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        Toast.makeText(getContext(), "Location Permission granted", Toast.LENGTH_SHORT).show();
                        getDeviceLocation();
                        init();
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(getContext(), "Routing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(), "Something went wrong while Routing, Try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int index) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }
    private void eraseRoute(){
        for(Polyline line:polylines){
            line.remove();
        }
        polylines.clear();
    }
}
