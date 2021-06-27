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

    public SellStockDialog(){
        super();
    }

    public SellStockDialog(double balance, double stockPrice, double sharesOwned, String stockName){
        super();
        this.balance = balance;
        this.stockPrice = stockPrice;
        this.sharesOwned = sharesOwned;
        this.stockName = stockName;
    }

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
                    totalSellValueLabel.setText("Total Cost: $" + 0.0);
                    return;
                }
                double total = stockPrice * Double.parseDouble(amountToSellInput.getText().toString());
                total = Math.round(total * 100.0) / 100.0;
                totalSellValueLabel.setText("Total Cost: $" + total);
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

    public interface SellStockDialogListener {
        void applyTexts(double amount, String stockName);
    }
}


