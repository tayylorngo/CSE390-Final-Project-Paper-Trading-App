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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;

/**
 *  The AddFundsDialog class implements the dialog used when the user
 *  adds funds to their account.
 *
 *  @author Taylor Ngo
 */
public class AddFundsDialog extends AppCompatDialogFragment {

    private AddFundsDialogListener listener;

    /**
     * The constructor used to create the AddFundsDialog
     */
    public AddFundsDialog(){
        super();
    }

    /**
     * This method creates the dialog and sets the TextViews and EditTexts
     * to their proper values.
     * @param savedInstanceState savedInstanceState
     * @return A Dialog representing the AddFundsDialog
     */
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_funds_dialog, null);
        builder.setView(view).setTitle("Add Funds").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        EditText fundsInput = view.findViewById(R.id.addFundsInput);
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

        Button addFundsConfirmButton = view.findViewById(R.id.addFundsConfirmButton);
        addFundsConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fundsInput.getText().toString().isEmpty()){
                    return;
                }
                listener.applyTexts(Double.parseDouble(fundsInput.getText().toString()));
            }
        });
        return builder.create();
    }

    /**
     * Overriden method to assign the AddFundsDialogListener
     * @param context Context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddFundsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement dialog listener");
        }
    }

    /**
     * This interface represents the AddFundsDialogListener
     * and blueprints the function when the user adds funds to their account.
     */
    public interface AddFundsDialogListener {
        void applyTexts(double amount);
    }
}