package com.taylorngo.stockpapertrading;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    public static final String SHARED_PREFS = "sharedPrefs";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        RadioButton defaultRadio = view.findViewById(R.id.defaultRadioBtn);
        RadioButton lastPriceRadio = view.findViewById(R.id.priceRadioBtn);
        RadioButton totalReturnRadio = view.findViewById(R.id.profitRadioButton);
        RadioButton equityRadio = view.findViewById(R.id.equityRadioBtn);

        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switch(sharedPreferences.getString("sortBy", "")){
           case "Default":
               defaultRadio.setChecked(true);
               break;
           case "Last Price":
               lastPriceRadio.setChecked(true);
               break;
           case "Total Return":
               totalReturnRadio.setChecked(true);
               break;
           case "Your Equity":
               equityRadio.setChecked(true);
               break;
           default:
               break;
        }
        RadioButton ascendingRadio = view.findViewById(R.id.ascendingRadioBtn);
        RadioButton descendingRadio = view.findViewById(R.id.descendingRadioBtn);
        switch(sharedPreferences.getString("sortOrder", "")){
            case "Ascending":
                ascendingRadio.setChecked(true);
                break;
            case "Descending":
                descendingRadio.setChecked(true);
                break;
            default:
                break;
        }
        RadioGroup sortBy = view.findViewById(R.id.sortByRadioGroup);
        sortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sortBy", checkedRadioButton.getText().toString());
                editor.apply();
            }
        });
        RadioGroup sortOrder = view.findViewById(R.id.sortOrderRadioGroup);
        sortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sortOrder", checkedRadioButton.getText().toString());
                editor.apply();
            }
        });
        return view;
    }
}
