package com.foodshare.foodshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class LocationActivity extends BaseActivity {

    private AutoCompleteTextView actState, actCity, actArea;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private final Map<String, String[]> stateCityMap = new TreeMap<String, String[]>() {{
        put("Andhra Pradesh", new String[]{"Visakhapatnam", "Vijayawada", "Guntur", "Nellore", "Kurnool", "Tirupati"});
        put("Delhi", new String[]{"New Delhi", "Dwarka", "Rohini", "Noida", "Gurgaon"});
        put("Karnataka", new String[]{"Bangalore", "Mysore", "Hubli", "Mangalore", "Belgaum"});
        put("Kerala", new String[]{"Kochi", "Thiruvananthapuram", "Kozhikode", "Thrissur", "Kollam"});
        put("Maharashtra", new String[]{"Mumbai", "Pune", "Nagpur", "Nashik", "Thane"});
        put("Tamil Nadu", new String[]{"Chennai", "Coimbatore", "Madurai", "Salem", "Tiruchirappalli"});
        put("Telangana", new String[]{"Hyderabad", "Warangal", "Nizamabad", "Karimnagar"});
    }};

    private final Map<String, String[]> cityAreaMap = new HashMap<String, String[]>() {{
        put("Chennai", new String[]{"Anna Nagar", "T Nagar", "Velachery", "Adyar", "Tambaram", "Mylapore", "OMR", "ECR"});
        put("Coimbatore", new String[]{"RS Puram", "Gandhipuram", "Saibaba Colony", "Peelamedu"});
        put("Bangalore", new String[]{"Indiranagar", "Koramangala", "HSR Layout", "Whitefield", "Jayanagar"});
        put("Hyderabad", new String[]{"Banjara Hills", "Jubilee Hills", "Gachibowli", "Madhapur", "Kukatpally"});
        put("Mumbai", new String[]{"Andheri", "Bandra", "Juhu", "Powai", "Worli", "Colaba"});
        put("New Delhi", new String[]{"Connaught Place", "South Ex", "Hauz Khas", "Karol Bagh", "Dwarka"});
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        actState = findViewById(R.id.actState);
        actCity = findViewById(R.id.actCity);
        actArea = findViewById(R.id.actArea);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button btnGps = findViewById(R.id.btnGps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        List<String> states = new ArrayList<>(stateCityMap.keySet());
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states);
        actState.setAdapter(stateAdapter);

        actState.setOnItemClickListener((parent, view, position, id) -> {
            String selectedState = (String) parent.getItemAtPosition(position);
            updateCityDropdown(selectedState);
        });

        actCity.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCity = (String) parent.getItemAtPosition(position);
            updateAreaDropdown(selectedCity);
        });

        btnGps.setOnClickListener(v -> requestLocationPermission());

        btnConfirm.setOnClickListener(v -> {
            startActivity(new Intent(LocationActivity.this, LoginActivity.class));
        });
    }

    private void updateCityDropdown(String state) {
        String[] cities = stateCityMap.get(state);
        if (cities != null) {
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
            actCity.setAdapter(cityAdapter);
            actCity.setText("", false);
            actArea.setText("", false);
            actArea.setAdapter(null);
        }
    }

    private void updateAreaDropdown(String city) {
        String[] areas = cityAreaMap.get(city);
        if (areas != null) {
            ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, areas);
            actArea.setAdapter(areaAdapter);
            actArea.setText("", false);
        } else {
            String[] defaults = {"Main Market", "Railway Station", "Civil Lines"};
            ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, defaults);
            actArea.setAdapter(areaAdapter);
            actArea.setText("", false);
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                reverseGeocode(location);
            } else {
                Toast.makeText(this, "Could not detect location. Make sure GPS is on.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reverseGeocode(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String state = address.getAdminArea();
                String city = address.getLocality();

                for (String s : stateCityMap.keySet()) {
                    if (s.equalsIgnoreCase(state) || (state != null && state.contains(s))) {
                        actState.setText(s, false);
                        updateCityDropdown(s);
                        break;
                    }
                }
                
                if (city != null) {
                    actCity.setText(city, false);
                    updateAreaDropdown(city);
                }
                
                Toast.makeText(this, "Location detected: " + city + ", " + state, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}