package com.project.uberauto;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
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
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MapFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener{


    Dialog dcard ;
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
    private Location currentLocation;
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
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Button direction;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String phone;
    Button book ;
   public TextView tvname;
   public TextView tvphone;
    //    //private LocationClient
    // private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dcard=new Dialog(getContext());
        dcard.setCanceledOnTouchOutside(true);
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
        book= mapview.findViewById(R.id.booknow);
        tvname = mapview.findViewById(R.id.drivername);
        tvphone = mapview.findViewById(R.id.driverphone);
        phone = getContext().getSharedPreferences("phonecache",MODE_PRIVATE).getString("phone","9423757172");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        mylocation = mapview.findViewById(R.id.mylocation);
        msearchtext = mapview.findViewById(R.id.searchbar);
        mylocation.setOnClickListener(gotomylocation);
        direction = mapview.findViewById(R.id.directionbtn);
        direction.setOnClickListener(getDirections);
        return mapview;
    }

    View.OnClickListener gotomylocation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "failed to get Location permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            // if permission already granted
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        //     Log.d(TAG,"location found", );
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getContext(), "location permission required", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                    } else {
                        //    Log.d(TAG,"CURRENT LOCATION IS ",);
                        Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
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
                .enableAutoManage(getActivity(),this)
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
            if (mPreviousMarker != null)
                mPreviousMarker.remove();
            mPreviousMarker = mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
            Log.d("Dest:Lat "+ address.getLatitude() + " long"+address.getLongitude(),"Src:Lat"+ mlocation.getLatitude() +" " + mlocation.getLongitude());
        } else {
            Toast.makeText(getContext(), "Location not found", Toast.LENGTH_LONG).show();
        }
    }

    View.OnClickListener getDirections = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Location Permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    mlocation = (Location) task.getResult();
                }
            });
            if(mPreviousMarker==null){
                Toast.makeText(getContext(), "Please Choose Destination", Toast.LENGTH_SHORT).show();
                return;
            }
            final LatLng dest = new LatLng(mPreviousMarker.getPosition().latitude, mPreviousMarker.getPosition().longitude);
            final LatLng origin = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
            Log.d("origin=" + origin.latitude + " " + origin.longitude,"destination" + dest.latitude + " " + dest.longitude);
            /*LatLng origin = new LatLng(37.7849569, -122.4068855);
            LatLng dest= new LatLng(37.7814432, -122.4460177);*/
            GoogleDirection.withServerKey("AIzaSyB_FOWftbNi8uXdXXOJmrb0Y_MBqykux7E")//AIzaSyD0A9AV_LSOWyVaeJ1bMWiQUQtoH5Kb5aU")
                    .from(origin)
                    .to(dest)
                    .transportMode(TransportMode.DRIVING)
               /*     .avoid(AvoidType.FERRIES)
                    .avoid(AvoidType.HIGHWAYS)*/
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if (direction.isOK()) {
                                Toast.makeText(getContext(), "direction success", Toast.LENGTH_SHORT).show();
                                String status = direction.getStatus();
                                com.akexorcist.googledirection.model.Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                List<Step> list = leg.getStepList();
                                if (status.equals(RequestResult.OK)) {
                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(getContext(), directionPositionList, 4,getResources().getColor(R.color.blue_500));
                                    mMap.addPolyline(polylineOptions);
                                }
                                else if(status.equals(RequestResult.OVER_QUERY_LIMIT)){
                                    Toast.makeText(getContext(), "Limit exceeded", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), direction.getErrorMessage() + direction.getStatus(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            Toast.makeText(getContext(),"direction failure", Toast.LENGTH_SHORT).show();
                        }
                    });
            //DownloadTask downloadTask = new DownloadTask();
        }
    };

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
                            currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            Toast.makeText(getContext(), "Location fetched", Toast.LENGTH_SHORT).show();
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
        Log.d("Location Changed","");
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
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            getDeviceLocation();
            init();
        }
        showNearBy();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mPreviousMarker != null) {
                    mPreviousMarker.remove();
                    mPreviousMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
                    Log.d(latLng.toString()," latlng");
                }
                else
                    mPreviousMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               // Toast.makeText(getContext(), "MARKER CLICKED "+marker.getTitle(), Toast.LENGTH_SHORT).show();
                if(!marker.getTitle().equalsIgnoreCase("Destination")) {
                    if(mPreviousMarker !=null)  // destination is marked
                    show_card(marker.getTitle(),true);
                    else
                        show_card(marker.getTitle(),false);
                }else
                    mPreviousMarker.getSnippet();

                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(getContext(), "Location Permission granted", Toast.LENGTH_SHORT).show();
                getDeviceLocation();
                init();
            } else {
                Toast.makeText(getContext(), "location permission denied by user", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showNearBy(){
        db.collection("Driver")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc:task.getResult()) {
                                if(doc.getString("Lat")!=null) {  // check state if not offline
                                    String lat = doc.getString("Lat");
                                    String lan = doc.getString("Lan");
                                    Log.d(doc.getString("First_Name") + doc.getString("Last_Name"),"------*****");
                                   String phoneno = doc.getString("Phone_Number");
                                   // Toast.makeText(getContext(), "phone: " + phone , Toast.LENGTH_SHORT).show();*/
                                   // Log.d("phone no---> ",phoneno);
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(lat), Double.valueOf(lan))).title(phoneno)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.taxismall));
                                }
                            }
                        }else{

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
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

    private void eraseRoute(){
        for(Polyline line:polylines){
            line.remove();
        }
        polylines.clear();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        //Snackbar.make(View,"Icon Clicked",Snackbar.LENGTH_LONG);
        Toast.makeText(getContext(), "Icon clicked", Toast.LENGTH_SHORT).show();
    }

    public void show_card(String marker_title,Boolean destination_marked)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        Bundle b=new Bundle();
        b.putString("title",marker_title);
        b.putBoolean("destination_marked",destination_marked);
        MyCustomDialogFragment md=new MyCustomDialogFragment();
        md.setArguments(b);
        DialogFragment dialogFragment = md;
        dialogFragment.show(ft, "dialog");
       // dcard.setContentView(R.layout.driver_dialog);
/*      FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Driver")
                .document(Phoneno)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Button book = mapview.findViewById(R.id.button);
                        TextView tvname = mapview.findViewById(R.id.drivername);
                        TextView tvphone = mapview.findViewById(R.id.driverphone);
                        DocumentSnapshot doc = task.getResult();
                        if(task.isSuccessful()){
                            tvname.setText("Mahesh" + " " +"Jamdade");
                            tvphone.setText("8668284377");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failure:" + e, Toast.LENGTH_LONG).show();
            }
        });*/
      //  dcard.show();
    }
}
