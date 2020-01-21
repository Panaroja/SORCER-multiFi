package edu.pjatk.inn.coffeemaker.impl;

import edu.pjatk.inn.coffeemaker.Payment;
import sorcer.core.invoker.IntegerIncrementor;
import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

public class PaymentImpl implements Payment {
    @Override
    public Context pay(Context context) throws RemoteException, ContextException {
        boolean result = false;
        if (context.getValue("payment/cost") != null && context.getValue("payment/balance") != null) {
            if((Integer) context.getValue("payment/balance") > (Integer) context.getValue("payment/cost")){
                context.putValue("payment/result", (Integer) context.getValue("payment/balance") > (Integer) context.getValue("payment/cost"));
                context.putValue("payment/balance", (Integer)context.getValue("payment/balance") - (Integer) context.getValue("payment/cost"));
                result = true;
            }
            else{
                context.putValue("payment/result", (Integer) context.getValue("payment/balance") > (Integer) context.getValue("payment/cost"));
                result = false;
            }
        }
        else {
            context.putValue("payment/result", false);
            result = false;
        }

        if (context.getReturnPath() != null) {
            context.setReturnValue(result);
        }
        return context;
    }
}