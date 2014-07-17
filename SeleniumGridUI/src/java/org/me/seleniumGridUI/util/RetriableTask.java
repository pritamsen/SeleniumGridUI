/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

/**
 *
 * @author vkumar
 */
public class RetriableTask<T> implements Callable<T> {

    private Callable<T> task;
    public static final int DEFAULT_NUMBER_OF_RETRIES = 5;
    public static final long DEFAULT_WAIT_TIME = 1000;

    private int numberOfRetries; // total number of tries
    private int numberOfTriesLeft; // number left
    private long timeToWait; // wait interval

    public RetriableTask(Callable<T> task) {
        this(DEFAULT_NUMBER_OF_RETRIES, DEFAULT_WAIT_TIME, task);
    }

    public RetriableTask(int numberOfRetries, long timeToWait, Callable<T> task) {
        this.numberOfRetries = numberOfRetries;
        numberOfTriesLeft = numberOfRetries;
        this.timeToWait = timeToWait;
        this.task = task;
    }

    public T call() throws Exception {
        while (true) {
            try {
                return task.call();
            } catch (InterruptedException e) {
                throw e;
            } catch (CancellationException e) {
                throw e;
            } catch (Exception e) {
                numberOfTriesLeft--;
                if (numberOfTriesLeft == 0) {
                    throw new Exception(numberOfRetries
                            + " attempts to retry failed at " + timeToWait
                            + "ms interval", e);
                }
                Thread.sleep(timeToWait);
            }
        }
    }
}
