// Taylor Ngo
// 112626118
package com.taylorngo.stockpapertrading;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;

/**
 * The MainActivity class implements the MainActivity of the application which includes
 * all the functionality and FragmentContainer to store each screen.
 *
 * @author Taylor Ngo
 */
public class MainActivity extends AppCompatActivity implements AddFundsDialog.AddFundsDialogListener, WithdrawFundsDialog.WithdrawFundsDialogListener {
    private static final String SHARED_PREFS = "sharedPrefs";
    private SettingsFragment currFrag;

    /**
     * This method creates the MainActivity and represents the onCreate()
     * in the application's life cycle.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("YOOOO");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.navbar);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("profit", "0.0");
//        editor.clear();
//        editor.apply();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_history:
                            selectedFragment = new SettingsFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    selectedFragment, "SETTINGS").commit();
                            currFrag = (SettingsFragment) selectedFragment;
                            return true;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    /**
     * This method adds funds to the user's account
     *
     * @param amount Amount of funds to be added.
     */
    @Override
    public void applyTexts(double amount) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        double currBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        double newBalance = amount + currBalance;
        newBalance = round(newBalance);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("balance", String.valueOf(newBalance));
        editor.apply();
        Toast toast = Toast.makeText(this, "Added $" + amount, Toast.LENGTH_SHORT);
        toast.show();
        currFrag.closeAddDialog();
    }

    /**
     * This method withdraws funds from the user's account.
     *
     * @param amount Amount to be withdrawn.
     * @param holder String to differentiate between add funds.
     */
    @Override
    public void applyTexts(double amount, String holder) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        double currBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        double newBalance = currBalance - amount;
        newBalance = round(newBalance);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("balance", String.valueOf(newBalance));
        editor.apply();
        Toast toast = Toast.makeText(this, "Withdrew $" + amount, Toast.LENGTH_SHORT);
        toast.show();
        currFrag.closeWithdrawDialog();
    }

    /**
     * This method rounds any number to two decimal places
     *
     * @param num Number to be rounded.
     * @return Number rounded to two decimal places.
     */
    public static double round(double num){
        return Math.round(num * 100.0) / 100.0;
    }

    /**
     * This method returns a String representation of a price
     * with proper formatting (i.e commas)
     *
     * @param num The number to be formatted
     * @return String representation of a price with proper formatting
     */
    public static String priceify(double num){
        if(num == 0){
            return String.valueOf(num);
        }
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(num);
    }
}
