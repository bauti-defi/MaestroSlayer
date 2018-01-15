package org.osbot.maestro.script.slayer.utils.banking;

import org.osbot.maestro.script.slayer.utils.slayeritem.SlayerItem;

public class WithdrawRequest extends BankRequest {

    public WithdrawRequest(SlayerItem item, boolean needExact) {
        super(item, needExact);
    }

}
