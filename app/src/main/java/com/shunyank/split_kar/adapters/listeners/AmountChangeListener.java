package com.shunyank.split_kar.adapters.listeners;

import com.shunyank.split_kar.models.SplitAmountModel;

import java.util.ArrayList;

public interface AmountChangeListener {
    void onAmountChange(ArrayList<SplitAmountModel> splitAmountModels);
}
