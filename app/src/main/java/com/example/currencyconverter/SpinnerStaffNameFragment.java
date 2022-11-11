package com.example.currencyconverter;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.example.currencyconverter.adapter.CustomArrayAdapter;
import com.example.currencyconverter.model.CountryModel;

import java.util.ArrayList;


public class SpinnerStaffNameFragment extends DialogFragment {


    public void setArguments(ArrayList<CountryModel> list, CurrencyConverterFragment.SpinnerSelection listener) {
        this.list = list;
        this.mListener = listener;
        Log.e("TAG", "setArguments: " + list.size());
    }


    private EditText search;
    private ListView listView;
    private CustomArrayAdapter adapter;
    private ArrayList<CountryModel> list = new ArrayList<>();
    private CurrencyConverterFragment.SpinnerSelection mListener;


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.spinner_location_fragment, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search = view.findViewById(R.id.search_item);
        listView = view.findViewById(R.id.list_item);
        adapter = new CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapter.getItem(i).getCurrencyy() + "-" + adapter.getItem(i).getName();
                mListener.onSelect(selected);
                adapter.setUsersData();
                dismiss();
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


}
