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
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * The BuyStockDialog class implements a dialog for when
 * the user wants to purchase an asset.
 *
 * @author Taylor Ngo
 */
public class BuyStockDialog extends AppCompatDialogFragment {

    private BuyStockDialogListener listener;
    private Button orderButton;
    private EditText amountToOrder;
    private TextView currBalanceLabel;
    private TextView totalCostLabel;
    private TextView pricePerShareLabel;
    private double balance;
    private double stockPrice;
    private String stockName;

    /**
     * Default constructor used to create the BuyStockDialog
     */
    public BuyStockDialog(){
        super();
    }

    /**
     * Constructor used to create BuyStockDialog with
     * values of balance, stockPrice, and stockName.
     *
     * @param balance User balance
     * @param stockPrice Current price of the stock.
     * @param stockName Name of the stock.
     */
    public BuyStockDialog(double balance, double stockPrice, String stockName){
        super();
        this.balance = balance;
        this.stockPrice = stockPrice;
        this.stockName = stockName;
    }

    /**
     * This method creates the BuyStockDialog.
     *
     * @param savedInstanceState savedInstanceState
     * @return A Dialog representing the BuyStockDialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.buy_stock_dialog , null);
        builder.setView(view).setTitle("Buy " + stockName).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        currBalanceLabel = view.findViewById(R.id.availableBalanceLabel);
        currBalanceLabel.setText("Amount available to purchase: $" + balance);

        pricePerShareLabel = view.findViewById(R.id.pricePerShareLabel);
        pricePerShareLabel.setText("Price per share (approx.): $" + stockPrice);

        totalCostLabel = view.findViewById(R.id.totalOrderCostLabel);
        totalCostLabel.setText("Total Cost: $" + 0.0);

        amountToOrder = view.findViewById(R.id.amountToPurchaseInput);

        amountToOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(amountToOrder.getText().toString().isEmpty()){
                    totalCostLabel.setText("Total Cost: $" + 0.0);
                    return;
                }
                double total = stockPrice * Double.parseDouble(amountToOrder.getText().toString());
                total = MainActivity.round(total);
                totalCostLabel.setText("Total Cost: $" + total);
            }
        });

        orderButton = view.findViewById(R.id.orderBtn);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountToOrder.getText().toString().isEmpty() || Double.parseDouble(amountToOrder.getText().toString()) == 0){
                    return;
                }
                listener.applyTexts(Double.parseDouble(amountToOrder.getText().toString()));
            }
        });
        return builder.create();
    }

    /**
     * Overridden method to assign the BuyStockDialogListener
     * @param context Context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (BuyStockDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement dialog listener");
        }
    }

    /**
     * This interface represents the BuyStockDialogListener
     * and blueprints the function when the user purchases an asset.
     */
    public interface BuyStockDialogListener {
        void applyTexts(double amount);
    }
}