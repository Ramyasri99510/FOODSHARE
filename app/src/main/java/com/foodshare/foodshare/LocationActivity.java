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
        put("Andaman and Nicobar Islands", new String[]{"Port Blair", "Diglipur", "Mayabunder"});
        put("Andhra Pradesh", new String[]{"Visakhapatnam", "Vijayawada", "Guntur", "Nellore", "Kurnool", "Kakinada", "Rajahmundry", "Tirupati"});
        put("Arunachal Pradesh", new String[]{"Itanagar", "Naharlagun", "Pasighat", "Tawang"});
        put("Assam", new String[]{"Guwahati", "Silchar", "Dibrugarh", "Jorhat", "Nagaon", "Tinsukia"});
        put("Bihar", new String[]{"Patna", "Gaya", "Bhagalpur", "Muzaffarpur", "Purnia", "Darbhanga", "Arrah"});
        put("Chandigarh", new String[]{"Chandigarh"});
        put("Chhattisgarh", new String[]{"Raipur", "Bhilai", "Bilaspur", "Korba", "Durg"});
        put("Dadra and Nagar Haveli and Daman and Diu", new String[]{"Daman", "Diu", "Silvassa"});
        put("Delhi", new String[]{"New Delhi", "Dwarka", "Rohini", "Noida", "Gurgaon", "North Delhi", "South Delhi", "West Delhi", "East Delhi"});
        put("Goa", new String[]{"Panaji", "Margao", "Vasco da Gama", "Mapusa", "Ponda"});
        put("Gujarat", new String[]{"Ahmedabad", "Surat", "Vadodara", "Rajkot", "Bhavnagar", "Jamnagar", "Gandhinagar", "Junagadh"});
        put("Haryana", new String[]{"Faridabad", "Gurugram", "Panipat", "Ambala", "Yamunanagar", "Rohtak", "Hisar", "Karnal"});
        put("Himachal Pradesh", new String[]{"Shimla", "Dharamshala", "Solan", "Mandi", "Palampur"});
        put("Jammu and Kashmir", new String[]{"Srinagar", "Jammu", "Anantnag", "Baramulla", "Kathua"});
        put("Jharkhand", new String[]{"Ranchi", "Jamshedpur", "Dhanbad", "Bokaro Steel City", "Deoghar", "Phusro"});
        put("Karnataka", new String[]{"Bangalore", "Mysore", "Hubli", "Mangalore", "Belgaum", "Gulbarga", "Davanagere", "Bellary", "Shimoga"});
        put("Kerala", new String[]{"Kochi", "Thiruvananthapuram", "Kozhikode", "Thrissur", "Kollam", "Alappuzha", "Palakkad", "Malappuram"});
        put("Ladakh", new String[]{"Leh", "Kargil"});
        put("Lakshadweep", new String[]{"Kavaratti", "Agatti", "Amini"});
        put("Madhya Pradesh", new String[]{"Indore", "Bhopal", "Jabalpur", "Gwalior", "Ujjain", "Sagar", "Dewas", "Satna"});
        put("Maharashtra", new String[]{"Mumbai", "Pune", "Nagpur", "Nashik", "Thane", "Aurangabad", "Solapur", "Amravati", "Navi Mumbai", "Kolhapur"});
        put("Manipur", new String[]{"Imphal", "Thoubal", "Churachandpur"});
        put("Meghalaya", new String[]{"Shillong", "Tura", "Jowai"});
        put("Mizoram", new String[]{"Aizawl", "Lunglei", "Champhai"});
        put("Nagaland", new String[]{"Kohima", "Dimapur", "Mokokchung"});
        put("Odisha", new String[]{"Bhubaneswar", "Cuttack", "Rourkela", "Berhampur", "Sambalpur", "Puri", "Balasore"});
        put("Puducherry", new String[]{"Puducherry", "Karaikal", "Mahe", "Yanam"});
        put("Punjab", new String[]{"Ludhiana", "Amritsar", "Jalandhar", "Patiala", "Bathinda", "Mohali", "Pathankot"});
        put("Rajasthan", new String[]{"Jaipur", "Jodhpur", "Kota", "Bikaner", "Ajmer", "Udaipur", "Bhilwara", "Alwar"});
        put("Sikkim", new String[]{"Gangtok", "Namchi", "Geyzing"});
        put("Tamil Nadu", new String[]{"Chennai", "Coimbatore", "Madurai", "Salem", "Tiruchirappalli", "Tirunelveli", "Vellore", "Erode", "Tiruppur", "Thoothukudi"});
        put("Telangana", new String[]{"Hyderabad", "Warangal", "Nizamabad", "Karimnagar", "Khammam", "Ramagundam", "Mahbubnagar"});
        put("Tripura", new String[]{"Agartala", "Udaipur", "Dharmanagar"});
        put("Uttar Pradesh", new String[]{"Lucknow", "Kanpur", "Ghaziabad", "Agra", "Meerut", "Varanasi", "Prayagraj", "Bareilly", "Aligarh", "Moradabad", "Noida"});
        put("Uttarakhand", new String[]{"Dehradun", "Haridwar", "Haldwani", "Roorkee", "Rudrapur"});
        put("West Bengal", new String[]{"Kolkata", "Howrah", "Asansol", "Siliguri", "Durgapur", "Bardhaman", "Malda", "Baharampur"});
    }};

    private final Map<String, String[]> cityAreaMap = new HashMap<String, String[]>() {{
        put("Chennai", new String[]{"Anna Nagar", "T Nagar", "Velachery", "Adyar", "Tambaram", "Chromepet", "Perambur", "Kodambakkam", "Mylapore", "Porur", "Ambattur", "Avadi", "Sholinganallur", "OMR", "ECR", "Guindy", "Royapettah", "Saidapet"});
        put("Coimbatore", new String[]{"RS Puram", "Gandhipuram", "Saibaba Colony", "Peelamedu", "Singanallur", "Thudiyalur", "Vadavalli", "Race Course"});
        put("Madurai", new String[]{"Anna Nagar", "KK Nagar", "Tallakulam", "Bypass Road", "Mattuthavani", "Simmakkal", "K Pudur"});
        put("Bangalore", new String[]{"Indiranagar", "Koramangala", "HSR Layout", "Whitefield", "Jayanagar", "JP Nagar", "Electronic City", "Bannerghatta Road", "Malleshwaram", "Rajajinagar", "Hebbal", "Yelahanka", "Banashankari"});
        put("Hyderabad", new String[]{"Banjara Hills", "Jubilee Hills", "Gachibowli", "Madhapur", "Kukatpally", "Kondapur", "Manikonda", "Begumpet", "Ameerpet", "Secunderabad", "Uppal", "LB Nagar", "Hitech City"});
        put("Mumbai", new String[]{"Andheri", "Bandra", "Juhu", "Powai", "Worli", "Colaba", "Dadar", "Borivali", "Malad", "Goregaon", "Kandivali", "Chembur", "Kurla", "Ghatkopar", "Mulund", "Parel"});
        put("New Delhi", new String[]{"Connaught Place", "South Ex", "Hauz Khas", "Karol Bagh", "Dwarka", "Rohini", "Pitampura", "Janakpuri", "Saket", "Vasant Kunj", "Lajpat Nagar", "Greater Kailash"});
        put("Kolkata", new String[]{"Salt Lake", "New Town", "Park Street", "Ballygunge", "Behala", "Garia", "Dum Dum", "Howrah", "Alipore", "Tollygunge", "Lake Town"});
        put("Pune", new String[]{"Kothrud", "Baner", "Hinjewadi", "Viman Nagar", "Kalyani Nagar", "Hadapsar", "Wakad", "Pimple Saudagar", "Aundh", "Camp"});
        put("Ahmedabad", new String[]{"Satellite", "SG Highway", "Prahlad Nagar", "Bodakdev", "Bopal", "Vastrapur", "Navrangpura", "Chandkheda"});
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
            Intent intent = new Intent(LocationActivity.this, LoginActivity.class);
            startActivity(intent);
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
            // Default generic areas if specific ones aren't mapped
            String[] defaultAreas = {"Main Market", "Railway Station Road", "Civil Lines", "Industrial Area", "Housing Board"};
            ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, defaultAreas);
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