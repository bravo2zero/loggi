package de.loggi.threads;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author CptSpaetzle
 */
public class NamingThreadFactory implements ThreadFactory {

    final AtomicInteger threadNumber = new AtomicInteger(1);
    final ThreadGroup group;
    final String namePrefix;

    public NamingThreadFactory(String namePrefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement());
    }
}
