package edu.pjatk.inn.coffeemaker;

import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

public interface Payment {

    public Context pay(Context context) throws  RemoteException, ContextException;
}