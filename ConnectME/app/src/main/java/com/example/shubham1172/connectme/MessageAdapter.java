package com.example.shubham1172.connectme;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by shubham1172 on 23/10/17.
 */

public class MessageAdapter extends ArrayAdapter<ConnectMessage> {

    public MessageAdapter(Context context, int resource, List<ConnectMessage> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.chat_message, parent, false);
        }

        TextView messageTextView = (TextView)convertView.findViewById(R.id.messageTextView);
        TextView nameTextView = (TextView)convertView.findViewById(R.id.nameTextView);
        TextView timeTextView = (TextView)convertView.findViewById(R.id.timeTextView);

        ConnectMessage message = getItem(position);
        messageTextView.setText(message.getText());

        nameTextView.setText("");
        if(message.getUsername().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()))
            nameTextView.setText("You - ");
        nameTextView.append(message.getUsername());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(message.getTime());

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        timeTextView.setText(format.format(calendar.getTime()));

        return convertView;
    }
}
