package it.manzolo.pastiarzach;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinUserAdapter extends ArrayAdapter<User> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<User> values;

    public SpinUserAdapter(Context context, int textViewResourceId, ArrayList<User> users) {
        super(context, textViewResourceId, users);
        this.context = context;
        this.values = users;
    }

    public int getCount() {
        return values.size();
    }

    public User getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getUserId(int position) {
        return values.get(position).getId();
    }

    public String getUserNominativo(int position) {
        return values.get(position).getNominativo();
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values.get(position).getNominativo());
        label.setTextSize(25);

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position).getNominativo());
        label.setTextSize(25);

        return label;
    }
}