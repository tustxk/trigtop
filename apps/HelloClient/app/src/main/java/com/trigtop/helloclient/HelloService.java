package com.trigtop.helloclient;

import android.os.RemoteException;

/**
 * Created by k on 2018/3/6.
 */

public class HelloService extends IHelloService.Stub {

    static {
        System.loadLibrary("hello_jni");
    }

    public HelloService() {
        init_native();
    }

    @Override
    public void setVal(int val) throws RemoteException {
        setVal_native(val);

    }

    @Override
    public int getVal() throws RemoteException {
        return getVal_native();
    }

    private static native boolean init_native();
    private static native void setVal_native(int val);
    private static native int getVal_native();
}
