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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;

public class WithdrawFundsDialog extends AppCompatDialogFragment {

    private WithdrawFundsDialogListener listener;
    private double currBalance;

    public WithdrawFundsDialog(){
        super();
    }

    public WithdrawFundsDialog(double currBalance){
        super();
        this.currBalance = currBalance;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.withdraw_funds_dialog, null);
        builder.setView(view).setTitle("Withdraw Funds").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        TextView currBalanceLabel = view.findViewById(R.id.withdrawDialogCurrBalanceLabel);
        currBalanceLabel.setText("Withdrawable Balance: $" + currBalance);

        EditText fundsInput = view.findViewById(R.id.withdrawFundsInput);
        fundsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1 && s.toString().equals("0"))
                    fundsInput.setText("");
            }
        });
        Button withdrawConfirmButton = view.findViewById(R.id.withdrawFundsConfirmBtn);
        withdrawConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fundsInput.getText().toString().isEmpty()){
                    return;
                }
                if(Double.parseDouble(fundsInput.getText().toString()) > currBalance){
//                    Toast toast = Toast.makeText(getContext(), "Not enough funds", Toast.LENGTH_SHORT);
//                    toast.show();
                    return;
                }
                listener.applyTexts(Double.parseDouble(fundsInput.getText().toString()), "holder");
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (WithdrawFundsDialog.WithdrawFundsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement dialog listener");
        }
    }

    public interface WithdrawFundsDialogListener {
        void applyTexts(double amount, String holder);
    }
}
