package org.osbot.maestro.script.slayer.utils.banking;

import org.osbot.maestro.script.slayer.utils.slayeritem.SlayerItem;

public class DepositRequest extends BankRequest {

    public DepositRequest(SlayerItem item, boolean needExact) {
        super(item, needExact);
    }
}
