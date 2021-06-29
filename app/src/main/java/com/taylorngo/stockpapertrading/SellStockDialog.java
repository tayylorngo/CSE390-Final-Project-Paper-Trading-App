// Taylor Ngo
// 112626118
package com.taylorngo.stockpapertrading;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;

/**
 * The SellStockDialog class implements the Dialog used
 * when the user sells an asset.
 *
 * @author Taylor Ngo
 */
public class SellStockDialog extends AppCompatDialogFragment {
    private SellStockDialogListener listener;

    private double balance;
    private double stockPrice;
    private double sharesOwned;
    private String stockName;

    private TextView currBalanceLabel;
    private TextView totalSellValueLabel;
    private TextView pricePerShareToSellLabel;
    private TextView availableSharesToSellLabel;
    private EditText amountToSellInput;
    private Button confirmSellButton;

    /**
     * Default constructor for SellStockDialog.
     */
    public SellStockDialog(){
        super();
    }

    /**
     * Constructor with values for SellStockDialog.
     *
     * @param balance User balance
     * @param stockPrice Price of the asset the user wants to sell.
     * @param sharesOwned The current amount of shares owned by the user.
     * @param stockName The name of the asset to sell.
     */
    public SellStockDialog(double balance, double stockPrice, double sharesOwned, String stockName){
        super();
        this.balance = balance;
        this.stockPrice = stockPrice;
        this.sharesOwned = sharesOwned;
        this.stockName = stockName;
    }

    /**
     * This method creates the SellStockDialog and initializes the
     * proper values for the TextViews and functionality of the
     * buttons.
     *
     * @param savedInstanceState savedInstanceState
     * @return A Dialog representing the SellStockDialog.
     */
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.sell_stock_dialog , null);
        builder.setView(view).setTitle("Sell " + stockName).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        availableSharesToSellLabel = view.findViewById(R.id.availableSharesToSellLabel);
        availableSharesToSellLabel.setText("Available shares to sell: " + sharesOwned);

        currBalanceLabel = view.findViewById(R.id.currBalanceSellLabel);
        currBalanceLabel.setText("Current Balance: $" + this.balance);

        pricePerShareToSellLabel = view.findViewById(R.id.pricePerShareSellLabel);
        pricePerShareToSellLabel.setText("Price per share (approx.): $" + stockPrice);

        amountToSellInput = view.findViewById(R.id.amountToSellInput);

        amountToSellInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(amountToSellInput.getText().toString().isEmpty()){
                    totalSellValueLabel.setText("Total: $" + 0.0);
                    return;
                }
                double total = stockPrice * Double.parseDouble(amountToSellInput.getText().toString());
                total = MainActivity.round(total);
                totalSellValueLabel.setText("Total: $" + total);
            }
        });

        totalSellValueLabel = view.findViewById(R.id.totalSellValueLabel);
        totalSellValueLabel.setText("Total: $" + 0.0);

        confirmSellButton = view.findViewById(R.id.sellConfirmBtn);
        confirmSellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountToSellInput.getText().toString().isEmpty() || Double.parseDouble(amountToSellInput.getText().toString()) == 0){
                    return;
                }
                if(Double.parseDouble(amountToSellInput.getText().toString()) > sharesOwned){
                    return;
                }
                listener.applyTexts(Double.parseDouble(amountToSellInput.getText().toString()), stockName);
            }
        });
        return builder.create();
    }

    /**
     * Overridden method that implements the SellStocKDialogListener
     *
     * @param context Context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SellStockDialog.SellStockDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement dialog listener");
        }
    }

    /**
     * This interface represents the SellStockDialogListener
     * and blueprints the method for selling an asset.
     */
    public interface SellStockDialogListener {
        void applyTexts(double amount, String stockName);
    }
}