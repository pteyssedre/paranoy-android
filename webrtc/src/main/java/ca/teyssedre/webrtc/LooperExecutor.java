/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Pierre Teyssedre
 * Created by  :  pteyssedre
 * Date        :  15-11-18
 * Application :  Paranoya .
 * Package     :  ca.teyssedre.webrtc .
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ca.teyssedre.webrtc;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

class LooperExecutor extends Thread implements Executor {

    private static final String TAG = "LooperExecutor";
    private final Object looperStartedEvent = new Object();
    private Handler handler = null;
    private boolean running = false;
    private long threadId;

    @Override
    public void run() {
        Looper.prepare();
        synchronized (looperStartedEvent) {
            Log.d(TAG, "Looper thread started.");
            handler = new Handler();
            threadId = Thread.currentThread().getId();
            looperStartedEvent.notify();
        }
        Looper.loop();
    }

    public synchronized void requestStart() {
        if (running) {
            return;
        }
        running = true;
        handler = null;
        start();
        // Wait for Handler allocation.
        synchronized (looperStartedEvent) {
            while (handler == null) {
                try {
                    looperStartedEvent.wait();
                } catch (InterruptedException e) {
                    Log.e(TAG, "Can not start looper thread");
                    running = false;
                }
            }
        }
    }

    public synchronized void requestStop() {
        if (!running) {
            return;
        }
        running = false;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Looper.myLooper().quit();
                Log.d(TAG, "Looper thread finished.");
            }
        });
    }

    // Checks if current thread is a looper thread.
    public boolean checkOnLooperThread() {
        return (Thread.currentThread().getId() == threadId);
    }

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     *                                    accepted for execution
     * @throws NullPointerException       if command is null
     */
    @Override
    public synchronized void execute(final Runnable command) {
        if (!running) {
            Log.w(TAG, "Running looper executor without calling requestStart()");
            return;
        }
        if (Thread.currentThread().getId() == threadId) {
            command.run();
        } else {
            handler.post(command);
        }
    }
}
