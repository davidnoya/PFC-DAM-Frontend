package com.pfc.trasladocuentas.models;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class IbanFormatter implements TextWatcher {

    private final EditText editText;
    private boolean mSelfChange = false;

    public IbanFormatter(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (mSelfChange) return;

        mSelfChange = true;

        int initialCursorPos = editText.getSelectionStart();
        String stringOriginal = s.toString();

        String stringLimpio = stringOriginal.replaceAll(" ", "").toUpperCase();

        StringBuilder formattedString = new StringBuilder();
        for (int i = 0; i < stringLimpio.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formattedString.append(" ");
            }
            formattedString.append(stringLimpio.charAt(i));
        }

        editText.setText(formattedString.toString());

        int newCursorPos = calculateCursorPosition(stringOriginal, formattedString.toString(), initialCursorPos);
        editText.setSelection(newCursorPos);

        mSelfChange = false;
    }

    private int calculateCursorPosition(String original, String formatted, int currentPos) {
        int numSpacesBeforeCursor = 0;
        for (int i = 0; i < currentPos && i < original.length(); i++) {
            if (original.charAt(i) == ' ') {
                numSpacesBeforeCursor++;
            }
        }

        int cleanCharsBeforeCursor = currentPos - numSpacesBeforeCursor;
        int newPos = cleanCharsBeforeCursor;

        for (int i = 0; i < newPos; i++) {
            if (i > 0 && i % 4 == 0) {
                newPos++;
            }
        }

        if (newPos > formatted.length()) {
            return formatted.length();
        }
        return Math.max(newPos, 0);
    }
}