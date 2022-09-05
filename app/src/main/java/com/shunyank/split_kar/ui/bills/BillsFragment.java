package com.shunyank.split_kar.ui.bills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.shunyank.split_kar.databinding.FragmentBillsBinding;
import com.shunyank.split_kar.utils.Helper;

public class BillsFragment extends Fragment {

    private FragmentBillsBinding binding;
    int numberofpeople =0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BillsViewModel billsViewModel =
                new ViewModelProvider(this).get(BillsViewModel.class);

        binding = FragmentBillsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(binding.eventEdt.getText().toString().isEmpty()){

                    Toast.makeText(requireContext(), "Enter Total Amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.numberOfPeople.setText(String.valueOf(progress));
                numberofpeople = progress;
                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return root;
    }

    private void calculate() {
        if(numberofpeople>0) {
            float totalamount = Float.parseFloat(binding.eventEdt.getText().toString());
            float equal = Helper.getValueInFloat(totalamount / numberofpeople);

            binding.splitAmount.setText("₹" + equal + "/head");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}