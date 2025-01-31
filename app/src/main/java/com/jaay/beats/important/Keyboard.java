package com.jaay.beats.important;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.jaay.beats.R;

public class Keyboard extends InputMethodService {

    private InputMethodManager inputMethodManager;
    private View keyboardView;
    private EditText searchBar;
    private IBinder token;

    @Override
    public void onCreate() {
        super.onCreate();
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateInputView() {
        // Inflate the keyboard layout
        keyboardView = getLayoutInflater().inflate(R.layout.keyboard, null);
        searchBar = keyboardView.findViewById(R.id.search_bar);

        // Set up search bar
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Get the search query
                String searchQuery = searchBar.getText().toString();

                // Send the text to the current input connection
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    ic.commitText(searchQuery, 1);
                }

                // Clear search bar and hide keyboard
                searchBar.setText("");
                hideKeyboard();
                return true;
            }
            return false;
        });

        return keyboardView;
    }

    private void hideKeyboard() {
        requestHideSelf(0);
        if (token != null) {
            inputMethodManager.hideSoftInputFromWindow(token, 0);
        }
    }
}
