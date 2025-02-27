package com.example.restaurantorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantorderapp.R;
import com.example.restaurantorderapp.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookings = new ArrayList<>();
    private final BookingActionListener listener;

    public interface BookingActionListener {
        void onEdit(Booking booking);
        void onDelete(Booking booking);
    }

    public BookingAdapter(BookingActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking, listener);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textDateTime;
        TextView textTableStatus;
        TextView textPeopleCount;
        Button buttonDelete;

        BookingViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textTableStatus = itemView.findViewById(R.id.textTableStatus);
            textPeopleCount = itemView.findViewById(R.id.textPeopleCount);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        void bind(Booking booking, BookingActionListener listener) {
            // Format time (HH:MM)
            String formattedTime = booking.getTime().substring(0, 5);

            // Set name
            textName.setText(booking.getName());

            // Set date and time
            textDateTime.setText(String.format("Datum: %s, Tid: %s",
                    booking.getDate(), formattedTime));

            // Set table number
            String displayTable = "Bord " + booking.getTableNumber().toString();
            textTableStatus.setText(displayTable);

            // Set people count
            String displayPeople = "Antal gäster: " + booking.getPeopleCount().toString();
            textPeopleCount.setText(displayPeople);

            //buttonEdit.setOnClickListener(v -> listener.onEdit(booking));
            buttonDelete.setOnClickListener(v -> listener.onDelete(booking));
        }
    }
}