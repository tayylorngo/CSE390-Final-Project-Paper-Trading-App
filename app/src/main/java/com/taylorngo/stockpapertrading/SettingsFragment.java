// Taylor Ngo
// 112626118
package com.taylorngo.stockpapertrading;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * The SettingsFragment class represents the Settings page of the application.
 *
 * @author Taylor Ngo
 */
public class SettingsFragment extends Fragment {

    public static final String SHARED_PREFS = "sharedPrefs";
    private AddFundsDialog addFundsDialog;
    private WithdrawFundsDialog withdrawFundsDialog;

    /**
     * This method creates the SettingsFragment which has the
     * sorting values, add/withdraw funds button and etc.
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return A View representing the SettingsFragment.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        RadioButton defaultRadio = view.findViewById(R.id.defaultRadioBtn);
        RadioButton nameRadio = view.findViewById(R.id.nameRadioBtn);
        RadioButton numSharesRadio = view.findViewById(R.id.numSharesRadioButton);
        RadioButton totalCostRadio = view.findViewById(R.id.totalCostRadioBtn);

        Button addFundsButton = view.findViewById(R.id.addFundsButton);
        addFundsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddFundsDialog();
            }
        });

        Button withdrawFundsButton = view.findViewById(R.id.withdrawFundsBtn);
        withdrawFundsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWithdrawFundsDialog();
            }
        });

        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switch(sharedPreferences.getString("sortBy", "")){
           case "Default":
               defaultRadio.setChecked(true);
               break;
           case "Name":
               nameRadio.setChecked(true);
               break;
           case "Number of Shares":
               numSharesRadio.setChecked(true);
               break;
           case "Total Cost":
               totalCostRadio.setChecked(true);
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
            /**
             * This method acts as a listener to set the right
             * sort value when a RadioButton is checked.
             *
             * @param group The RadioGroup
             * @param checkedId The id of the checked RadioButton
             */
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
            /**
             * This method acts as a listener to set the right
             * sort order when a RadioButton is checked.
             *
             * @param group The RadioGroup
             * @param checkedId The id of the checked RadioButton
             */
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

    /**
     * This method creates and shows the AddFundsDialog
     */
    public void openAddFundsDialog(){
        addFundsDialog = new AddFundsDialog();
        addFundsDialog.show(getActivity().getSupportFragmentManager(), "Add Funds");
    }

    /**
     * This method creates and shows the WithdrawFundsDialog
     */
    public void openWithdrawFundsDialog(){
        SharedPreferences sharedPreferences = getView().getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        double currBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        withdrawFundsDialog = new WithdrawFundsDialog(currBalance);
        withdrawFundsDialog.show(getActivity().getSupportFragmentManager(), "Withdraw Funds");
    }

    /**
     * This method closes the AddFundsDialog
     */
    public void closeAddDialog(){
        this.addFundsDialog.dismiss();
    }

    /**
     * This method closes the WithdrawFundsDialog
     */
    public void closeWithdrawDialog(){
        this.withdrawFundsDialog.dismiss();
    }
}
