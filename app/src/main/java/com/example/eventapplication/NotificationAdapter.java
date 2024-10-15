package com.example.eventapplication;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.titleTextView.setText(notification.getTitle());
        holder.descriptionTextView.setText(notification.getDescription());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notificationTitle);
            descriptionTextView = itemView.findViewById(R.id.notificationDescription);
        }
    }
}
