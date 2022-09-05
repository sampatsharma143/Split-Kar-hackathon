package com.shunyank.split_kar.adapters.listeners;

import com.shunyank.split_kar.models.SettlementModel;
import com.shunyank.split_kar.utils.SettleType;

public interface SettlementButtonClickListener {
    void onClick(SettleType type, SettlementModel model);
}
