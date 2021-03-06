package com.github.wenbo2018.fox.remoting.invoker.async;

import com.github.wenbo2018.fox.common.bean.InvokeRequest;
import com.github.wenbo2018.fox.common.bean.InvokeResponse;
import com.github.wenbo2018.fox.common.common.FoxConstants;
import com.github.wenbo2018.fox.remoting.invoker.api.CallFuture;

/**
 * Created by shenwenbo on 2017/3/25.
 */
public class CallbackFuture implements Callback {
    private CallFuture future;
    private boolean done = false;
    private boolean concelled = false;
    private boolean success = false;
    private InvokeResponse response;
    private InvokeRequest request;

    @Override
    public void run() {
        synchronized (this) {
            this.done = true;
            if (this.response.getMessageType() == FoxConstants.MESSAGE_TYPE_SERVICE) {
                this.success = true;
            }
            this.notifyAll();
        }
    }

    public InvokeResponse get() throws InterruptedException {
        return get(Long.MAX_VALUE);
    }

    public InvokeResponse get(long timeoutMillis) throws InterruptedException {
        synchronized (this) {
            long start = request.getCreateMillisTime();
            while (!this.done) {
                long timeoutMillis_ = timeoutMillis - (System.currentTimeMillis() - start);
                if (timeoutMillis_ <= 0) {

                } else {
                    this.wait(timeoutMillis_);
                }
            }
        }
        return response;
    }

    @Override
    public void callback(InvokeResponse response) {
        this.response = response;
    }

    @Override
    public void setRequest(InvokeRequest request) {
        this.request = request;
    }

    @Override
    public void dispose() {

    }

    public boolean isCancelled() {
        return this.concelled;
    }

    public boolean isDone() {
        return this.done;
    }
}
