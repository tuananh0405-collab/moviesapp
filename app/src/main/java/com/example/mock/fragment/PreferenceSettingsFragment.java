package com.example.mock.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.example.mock.R;

public class PreferenceSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        EditTextPreference number = findPreference("number");

        if (number != null){
            number.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(EditText editText) {
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editText.setTextColor(getResources().getColor(R.color.colorBackground));
                        }
                    }
            );
        }
//
//        EditTextPreference numberRate = findPreference("numberRate");
//        if (numberRate!= null){
//            numberRate.setOnPreferenceClickListener(preference -> {
//                Log.d("Rate", " preference clicked");
//                return false;
//            });
//        }
    }


}