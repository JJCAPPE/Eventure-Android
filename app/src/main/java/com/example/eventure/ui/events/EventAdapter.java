package com.example.eventure.ui.events;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventure.R;
import com.example.eventure.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> events;
    private final Context context;
    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.textEventName.setText(event.getName());
        
        // Format date
        String formattedDate = "Date not available";
        if (event.getDates() != null && event.getDates().getStart() != null 
                && event.getDates().getStart().getLocalDate() != null) {
            try {
                Date date = inputDateFormat.parse(event.getDates().getStart().getLocalDate());
                if (date != null) {
                    formattedDate = outputDateFormat.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        holder.textEventDate.setText(formattedDate);
        
        // Set location
        holder.textEventLocation.setText(event.getFormattedLocation());
        
        // Load image
        if (event.getImageUrl() != null) {
            Glide.with(context)
                    .load(event.getImageUrl())
                    .centerCrop()
                    .into(holder.imageEvent);
        } else {
            holder.imageEvent.setImageResource(R.drawable.ic_launcher_background); // Placeholder
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.EXTRA_EVENT, event);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEvents(List<Event> newEvents) {
        events.clear();
        events.addAll(newEvents);
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageEvent;
        final TextView textEventName;
        final TextView textEventDate;
        final TextView textEventLocation;

        EventViewHolder(View view) {
            super(view);
            imageEvent = view.findViewById(R.id.image_event);
            textEventName = view.findViewById(R.id.text_event_name);
            textEventDate = view.findViewById(R.id.text_event_date);
            textEventLocation = view.findViewById(R.id.text_event_location);
        }
    }
}
