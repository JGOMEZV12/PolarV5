package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.misc.RoleplayManager;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ATMWebEvent implements IWebEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ATMWebEvent.class);

    @Override
    public void execute(GameClient client, String data, Channel channel) {
        if (client == null || client.getHabbo() == null) return;

        // Synchronize on the Habbo instance to guarantee absolute atomicity against concurrent/multi-click exploits
        synchronized (client.getHabbo()) {
            RoleplayUser rp = client.getHabbo().getRoleplay();
            if (rp == null) return;

            try {
                // data format: event_name,action,param1,param2,...
                String[] parts = data.split(",");
                if (parts.length < 2) return;

                String action = parts[1].toLowerCase();

                switch (action) {
                    case "open":
                        String sendData = rp.getBankAccount() + "," +
                                rp.getBankChequings() + "," +
                                rp.getBankSavings() + ",";
                        WebEventManager.getInstance().sendData(channel, "compose_atm|open|" + sendData);
                        break;

                    case "close":
                        // ATM closing
                        break;

                    case "withdraw":
                        if (parts.length < 4) return;
                        int withdrawAmount;
                        try {
                            withdrawAmount = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            WebEventManager.getInstance().sendData(channel, "compose_atm|error|solo numeros");
                            return;
                        }

                        String withdrawAccountType = parts[3]; // "Checkings" or other
                        int withdrawActualAmount = withdrawAccountType.equalsIgnoreCase("Checkings") ? rp.getBankChequings() : rp.getBankSavings();

                        if (withdrawAmount <= 0) {
                            WebEventManager.getInstance().sendData(channel, "compose_atm|error|Invalid amount!");
                            return;
                        }

                        if (withdrawAmount > withdrawActualAmount || withdrawActualAmount - withdrawAmount < 0) {
                            WebEventManager.getInstance().sendData(channel, "compose_atm|error|Usted no tiene ese tipo de dinero para retirar");
                            return;
                        }

                        if (withdrawAccountType.equalsIgnoreCase("Checkings")) {
                            if (rp.getBankAccount() < 1) {
                                WebEventManager.getInstance().sendData(channel, "compose_atm|error|¡No tienes una cuenta corriente!");
                                return;
                            }

                            if (rp.getBankTarget() < 1) {
                                WebEventManager.getInstance().sendData(channel, "compose_atm|error|¡Usted no tiene tarjeta de debito, vaya al banco y pida la suya escribiendo: tarjeta!");
                                return;
                            }

                            RoleplayManager.shout(client, "*Saca $" + withdrawAmount + " De su cuenta Corriente [-100$ Por retiro]*");
                            client.getHabbo().whisper("Si no deseas pagar comisión por retiro, dirigete al banco y escribe :retirar cantidad");

                            rp.setBankChequings(rp.getBankChequings() - withdrawAmount);

                            client.getHabbo().giveCredits(withdrawAmount - 100);

                            WebEventManager.getInstance().sendData(channel, "compose_atm|change_balance_1|" + rp.getBankChequings());
                        } else {
                            if (rp.getBankAccount() < 2) {
                                WebEventManager.getInstance().sendData(channel, "compose_atm|error|¡No tienes una cuenta de ahorros!");
                                return;
                            }

                            if (rp.getBankTarget() < 1) {
                                WebEventManager.getInstance().sendData(channel, "compose_atm|error|¡Usted no tiene tarjeta de debito, vaya al banco y pida la suya!");
                                return;
                            }

                            int taxAmount = (int) (withdrawAmount * 0.05);
                            RoleplayManager.shout(client, "*Saca $" + withdrawAmount + " De su Cuenta de Ahorros y lo coloca en sus bolsillos*");
                            client.getHabbo().whisper("Usted pagó un impuesto de $" + taxAmount + " Para retirar $" + withdrawAmount + "!");

                            rp.setBankSavings(rp.getBankSavings() - withdrawAmount);

                            client.getHabbo().giveCredits(withdrawAmount - taxAmount);

                            WebEventManager.getInstance().sendData(channel, "compose_atm|change_balance_2|" + rp.getBankSavings());
                        }
                        break;

                    case "deposit":
                        if (parts.length < 4) return;
                        int depositAmount;
                        try {
                            depositAmount = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            WebEventManager.getInstance().sendData(channel, "compose_atm|error|Solo numeros");
                            return;
                        }

                        String depositAccountType = parts[3]; // "Checkings" or other
                        int walletCredits = client.getHabbo().getHabboInfo().getCredits();

                        if (depositAmount <= 0) {
                            WebEventManager.getInstance().sendData(channel, "compose_atm|error|Monto invalido");
                            return;
                        }

                        if (depositAccountType.equalsIgnoreCase("Checkings")) {
                            if (rp.getBankAccount() < 1) {
                                WebEventManager.getInstance().sendData(channel, "compose_atm|error|¡No tienes una cuenta de corriente!");
                                return;
                            }

                            int required = depositAmount + 150;
                            if (!client.getHabbo().tryTakeCredits(required)) {
                                WebEventManager.getInstance().sendData(channel, "compose_atm|error|¡No tienes suficiente dinero en tu billetera!");
                                return;
                            }

                            RoleplayManager.shout(client, "*Mete $" + depositAmount + " que sacó de su bolsillo y los deposita en su cuenta de corriente [-150$ Comisión cajero]*");

                            rp.setBankChequings(rp.getBankChequings() + depositAmount);
                            RoleplayManager.giveMoneyToCompany(9, client, "bank", true, 100);

                            WebEventManager.getInstance().sendData(channel, "compose_atm|change_balance_1|" + rp.getBankChequings());
                        } else {
                            if (rp.getBankAccount() < 2) {
                                WebEventManager.getInstance().sendData(channel, "compose_atm|error|¡No tienes una cuenta de ahorros!");
                                return;
                            }

                            if (!client.getHabbo().tryTakeCredits(depositAmount)) {
                                WebEventManager.getInstance().sendData(channel, "compose_atm|error|¡No tienes suficiente dinero en tu billetera!");
                                return;
                            }

                            RoleplayManager.shout(client, "*Mete en la ranura del cajero $" + depositAmount + " que saca de su bolsillo y lo deposita en su cuenta de ahorros*");

                            rp.setBankSavings(rp.getBankSavings() + depositAmount);
                            RoleplayManager.giveMoneyToCompany(9, client, "bank", true, 100);

                            WebEventManager.getInstance().sendData(channel, "compose_atm|change_balance_2|" + rp.getBankSavings());
                        }
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("Error processing ATMWebEvent", e);
            }
        }
    }
}
