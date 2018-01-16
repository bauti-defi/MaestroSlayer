package org.osbot.maestro.script.slayer.utils.banking;

import org.osbot.maestro.script.slayer.utils.slayeritem.SlayerInventoryItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

public class BankRequestManager {

    private final LinkedList<BankRequest> requests;

    public BankRequestManager() {
        this.requests = new LinkedList<>();
    }

    public boolean requestPending() {
        return !requests.isEmpty();
    }

    public boolean requiredRequestPending() {
        return requests.stream().anyMatch(new Predicate<BankRequest>() {
            @Override
            public boolean test(BankRequest bankRequest) {
                return bankRequest.isRequired();
            }
        });
    }

    public void addRequest(BankRequest request) {
        if (requests.contains(request)) {
            return;
        }
        this.requests.add(request);
    }

    public void flush() {
        requests.clear();
    }

    private void applyDeposites(MethodProvider provider) {
        sort();
        outter:
        for (Item item : provider.getInventory().getItems()) {
            for (BankRequest request : requests) {
                if (request.getItem().getName().contains(item.getName()) && request.isRequired()) {
                    continue outter;
                }
            }
            requests.add(new DepositRequest(SlayerInventoryItem.wrap(item, true, true)));
        }
    }

    private void sort() {
        requests.sort(new Comparator<BankRequest>() {
            @Override
            public int compare(BankRequest o1, BankRequest o2) {
                if (o1 instanceof DepositRequest) {
                    return 1;
                } else if (o2 instanceof DepositRequest) {
                    return -1;
                } else if (o1.isRequired() && !o2.isRequired()) {
                    return 1;
                } else if (!o1.isRequired() && o2.isRequired()) {
                    return -1;
                } else if (o1.getInventorySpaceRequired() < o2.getInventorySpaceRequired()) {
                    return 1;
                } else if (o2.getInventorySpaceRequired() < o1.getInventorySpaceRequired()) {
                    return -1;
                }
                return 0;
            }
        });
    }


    public LinkedList<BankRequest> getOptimizedList(MethodProvider provider) {
        applyDeposites(provider);
        sort();
        int invySpace = 0;
        Iterator<BankRequest> requestIterator = requests.iterator();
        while (requestIterator.hasNext()) {
            BankRequest request = requestIterator.next();
            if (request instanceof DepositRequest) {
                invySpace = -request.getInventorySpaceRequired();
                continue;
            } else if (invySpace + request.getInventorySpaceRequired() <= 28) {
                invySpace += request.getInventorySpaceRequired();
            } else if (invySpace < 28) {
                request.overrideAmount(28 - invySpace);
            } else {
                requestIterator.remove();
            }
        }
        return requests;
    }


}
