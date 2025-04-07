package com.example.eventure.ui.events;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.api.ApiClient;
import com.example.eventure.model.Event;
import com.example.eventure.model.EventResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int DEFAULT_RADIUS = 50; // miles

    private TextInputEditText editTextSearch;
    private TextInputEditText editTextLocation;
    private TextInputLayout inputLayoutLocation;
    private RadioButton radioCurrentLocation;
    private RadioButton radioSpecificLocation;
    private RadioGroup radioGroupLocation;
    private MaterialButton buttonSearch;
    private RecyclerView recyclerEvents;
    private ProgressBar progressBar;
    private TextView textNoResults;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private String pendingKeywordSearch = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);

        editTextSearch = root.findViewById(R.id.edit_text_search);
        editTextLocation = root.findViewById(R.id.edit_text_location);
        inputLayoutLocation = root.findViewById(R.id.input_layout_location);
        radioCurrentLocation = root.findViewById(R.id.radio_current_location);
        radioSpecificLocation = root.findViewById(R.id.radio_specific_location);
        radioGroupLocation = root.findViewById(R.id.radio_group_location);
        buttonSearch = root.findViewById(R.id.button_search);
        recyclerEvents = root.findViewById(R.id.recycler_events);
        progressBar = root.findViewById(R.id.progress_bar);
        textNoResults = root.findViewById(R.id.text_no_results);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        setupLocationRequest();

        // Set up RecyclerView
        recyclerEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(getContext(), eventList);
        recyclerEvents.setAdapter(eventAdapter);

        // Set up listeners
        setupListeners();

        return root;
    }

    private void setupListeners() {
        // Radio group change listener
        radioGroupLocation.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_current_location) {
                inputLayoutLocation.setVisibility(View.GONE);
            } else {
                inputLayoutLocation.setVisibility(View.VISIBLE);
            }
        });

        // Search button click listener
        buttonSearch.setOnClickListener(v -> searchEvents());

        // Search on keyboard action
        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchEvents();
                return true;
            }
            return false;
        });
    }

    private void searchEvents() {
        hideKeyboard();
        String keyword = editTextSearch.getText() != null ? editTextSearch.getText().toString() : "";

        if (radioCurrentLocation.isChecked()) {
            if (hasLocationPermission()) {
                getCurrentLocationAndSearch(keyword);
            } else {
                requestLocationPermission();
            }
        } else {
            // Use specified location
            String location = editTextLocation.getText() != null ? editTextLocation.getText().toString() : "";
            if (location.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a location", Toast.LENGTH_SHORT).show();
                return;
            }
            searchEventsByCity(keyword, location);
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        requestPermissions(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String keyword = editTextSearch.getText() != null ? editTextSearch.getText().toString() : "";
                getCurrentLocationAndSearch(keyword);
            } else {
                Toast.makeText(getContext(), "Location permission denied. Please enter a specific location.", Toast.LENGTH_LONG).show();
                radioSpecificLocation.setChecked(true);
                inputLayoutLocation.setVisibility(View.VISIBLE);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    
    private void setupLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build();
        
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null && pendingKeywordSearch != null) {
                    Location location = locationResult.getLastLocation();
                    searchEventsByLatLng(pendingKeywordSearch, location.getLatitude(), location.getLongitude());
                    pendingKeywordSearch = null;
                    stopLocationUpdates();
                }
            }
        };
    }
    
    private void getCurrentLocationAndSearch(String keyword) {
        showProgress();
        
        // Checks if location permissions are granted by android
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            hideProgress();
            return;
        }
        
        // Check if gps is enabled
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            hideProgress();
            Toast.makeText(getContext(), "Location services are disabled. Please enable them in settings.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }
        
        // Store the keyword to be used when location is retrieved
        pendingKeywordSearch = keyword;
        
        // Try to get the last known location first
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // We got a location, use it
                        searchEventsByLatLng(keyword, location.getLatitude(), location.getLongitude());
                        pendingKeywordSearch = null; // Clear the pending search
                    } else {
                        // No last location, request location updates
                        requestLocationUpdates();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error getting last location, request updates
                    requestLocationUpdates();
                });
    }
    
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
        
        // Set a timeout for location updates
        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (pendingKeywordSearch != null) {
                stopLocationUpdates();
                hideProgress();
                Toast.makeText(getContext(), "Could not get location. Please try again or enter a specific location.", Toast.LENGTH_LONG).show();
                pendingKeywordSearch = null;
            }
        }, 15000); // 15 seconds timeout
    }
    
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void searchEventsByLatLng(String keyword, double latitude, double longitude) {
        String latLong = latitude + "," + longitude;
        
        ApiClient.getTicketmasterService().searchEvents(
                ApiClient.API_KEY,
                latLong,
                String.valueOf(DEFAULT_RADIUS),
                null,
                null,
                "US",
                keyword,
                25,
                0,
                "date,asc"
        ).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventResponse> call, @NonNull Response<EventResponse> response) {
                processResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<EventResponse> call, @NonNull Throwable t) {
                handleApiFailure(t);
            }
        });
    }

    private void searchEventsByCity(String keyword, String location) {
        showProgress();
        String city = location;
        String stateCode = null;
        
        // Try to parse city and state if in format "City, State"
        if (location.contains(",")) {
            String[] parts = location.split(",");
            if (parts.length == 2) {
                city = parts[0].trim();
                stateCode = parts[1].trim();
            }
        }
        
        ApiClient.getTicketmasterService().searchEvents(
                ApiClient.API_KEY,
                null,
                null,
                city,
                stateCode,
                "US",
                keyword,
                25,
                0,
                "date,asc"
        ).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventResponse> call, @NonNull Response<EventResponse> response) {
                processResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<EventResponse> call, @NonNull Throwable t) {
                handleApiFailure(t);
            }
        });
    }

    private void processResponse(Response<EventResponse> response) {
        hideProgress();
        if (response.isSuccessful() && response.body() != null) {
            EventResponse eventResponse = response.body();
            if (eventResponse.getEmbedded() != null && eventResponse.getEmbedded().getEvents() != null) {
                List<Event> events = eventResponse.getEmbedded().getEvents();
                if (events.isEmpty()) {
                    showNoResults();
                } else {
                    showResults(events);
                }
            } else {
                showNoResults();
            }
        } else {
            showError("API Error: " + response.code());
        }
    }

    private void handleApiFailure(Throwable t) {
        hideProgress();
        showError("Network Error: " + t.getMessage());
    }
    
    private void showResults(List<Event> events) {
        textNoResults.setVisibility(View.GONE);
        recyclerEvents.setVisibility(View.VISIBLE);
        eventList.clear();
        eventList.addAll(events);
        eventAdapter.notifyDataSetChanged();
    }
    
    private void showNoResults() {
        recyclerEvents.setVisibility(View.GONE);
        textNoResults.setVisibility(View.VISIBLE);
    }
    
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        showNoResults();
    }
    
    private void hideKeyboard() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        textNoResults.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
