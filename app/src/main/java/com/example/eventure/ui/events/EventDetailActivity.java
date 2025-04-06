package com.example.eventure.ui.events;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.model.Event;
import com.example.eventure.model.PriceRange;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT = "extra_event";
    
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US);
    private SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
    private SimpleDateFormat outputTimeFormat = new SimpleDateFormat("h:mm a", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        
        // Get event from intent
        Event event = (Event) getIntent().getSerializableExtra(EXTRA_EVENT);
        if (event == null) {
            finish();
            return;
        }
        
        // Set up views
        ImageView imageEvent = findViewById(R.id.image_event);
        TextView textEventName = findViewById(R.id.text_event_name);
        TextView textEventDate = findViewById(R.id.text_event_date);
        TextView textEventVenue = findViewById(R.id.text_event_venue);
        TextView textEventAddress = findViewById(R.id.text_event_address);
        TextView textPriceRange = findViewById(R.id.text_price_range);
        TextView textEventInfo = findViewById(R.id.text_event_info);
        TextView textEventNote = findViewById(R.id.text_event_note);
        Button buttonBuyTickets = findViewById(R.id.button_buy_tickets);
        
        // Set toolbar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(event.getName());
        }
        
        // Set event details
        textEventName.setText(event.getName());
        
        // Set date and time
        if (event.getDates() != null && event.getDates().getStart() != null) {
            String formattedDate = "Date not available";
            String formattedTime = "Time not available";
            
            try {
                if (event.getDates().getStart().getLocalDate() != null) {
                    Date date = inputDateFormat.parse(event.getDates().getStart().getLocalDate());
                    if (date != null) {
                        formattedDate = outputDateFormat.format(date);
                    }
                }
                
                if (event.getDates().getStart().getLocalTime() != null) {
                    Date time = inputTimeFormat.parse(event.getDates().getStart().getLocalTime());
                    if (time != null) {
                        formattedTime = outputTimeFormat.format(time);
                    }
                }
                
                textEventDate.setText(formattedDate + " at " + formattedTime);
            } catch (ParseException e) {
                e.printStackTrace();
                textEventDate.setText("Date/time not available");
            }
        } else {
            textEventDate.setText("Date/time not available");
        }
        
        // Set venue info
        textEventVenue.setText(event.getVenueName());
        
        // Set address
        if (event.getEmbedded() != null && 
            event.getEmbedded().getVenues() != null && 
            !event.getEmbedded().getVenues().isEmpty()) {
            
            StringBuilder addressBuilder = new StringBuilder();
            if (event.getEmbedded().getVenues().get(0).getAddress() != null && 
                event.getEmbedded().getVenues().get(0).getAddress().getLine1() != null) {
                addressBuilder.append(event.getEmbedded().getVenues().get(0).getAddress().getLine1());
            }
            
            if (event.getCity() != null && !event.getCity().equals("Unknown City")) {
                if (addressBuilder.length() > 0) {
                    addressBuilder.append(", ");
                }
                addressBuilder.append(event.getCity());
                
                if (event.getState() != null && !event.getState().isEmpty()) {
                    addressBuilder.append(", ").append(event.getState());
                }
            }
            
            if (addressBuilder.length() > 0) {
                textEventAddress.setText(addressBuilder.toString());
            } else {
                textEventAddress.setText("Address not available");
            }
        } else {
            textEventAddress.setText("Address not available");
        }
        
        // Set price range
        List<PriceRange> priceRanges = event.getPriceRanges();
        if (priceRanges != null && !priceRanges.isEmpty()) {
            PriceRange priceRange = priceRanges.get(0);
            textPriceRange.setText(priceRange.getPriceRangeDisplay());
        } else {
            textPriceRange.setText("Price information not available");
        }
        
        // Set event info
        if (event.getInfo() != null && !event.getInfo().isEmpty()) {
            textEventInfo.setText(event.getInfo());
        } else {
            findViewById(R.id.text_event_info_label).setVisibility(View.GONE);
            textEventInfo.setVisibility(View.GONE);
        }
        
        // Set event notes
        if (event.getPleaseNote() != null && !event.getPleaseNote().isEmpty()) {
            textEventNote.setText(event.getPleaseNote());
        } else {
            findViewById(R.id.text_event_note_label).setVisibility(View.GONE);
            textEventNote.setVisibility(View.GONE);
        }
        
        // Load event image
        if (event.getImageUrl() != null) {
            Glide.with(this)
                    .load(event.getImageUrl())
                    .centerCrop()
                    .into(imageEvent);
        }
        
        // Set buy tickets button
        buttonBuyTickets.setOnClickListener(v -> {
            if (event.getUrl() != null && !event.getUrl().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrl()));
                startActivity(browserIntent);
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
