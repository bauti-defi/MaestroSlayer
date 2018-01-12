package org.osbot.maestro.script.slayer.utils;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;

public class WithdrawRequest {

    private final String name;
    private final int amount;
    private final boolean stackable, needExactAmount, emergency;
    private Filter<Item> filter;

    public WithdrawRequest(String name, int amount, boolean stackable, boolean needExactAmount, boolean emergency) {
        this.name = name;
        this.amount = amount;
        this.stackable = stackable;
        this.needExactAmount = needExactAmount;
        this.emergency = emergency;
    }

    public WithdrawRequest(String name, Filter<Item> filter, int amount, boolean stackable, boolean needExactAmount, boolean emergency) {
        this(name, amount, stackable, needExactAmount, emergency);
        this.filter = filter;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public Filter<Item> getFilter() {
        return filter;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isStackable() {
        return stackable;
    }

    public boolean isNeedExactAmount() {
        return needExactAmount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WithdrawRequest) {
            WithdrawRequest request = (WithdrawRequest) obj;
            return request.getName().equalsIgnoreCase(name);
        }
        return false;
    }
}
