package io.github.vipcxj.jasync.test.standalone;

import io.github.vipcxj.jasync.spec.JContext;
import io.github.vipcxj.jasync.spec.JHandle;
import io.github.vipcxj.jasync.spec.JPromise2;
import io.github.vipcxj.jasync.spec.functional.JAsyncCatchFunction1;
import io.github.vipcxj.jasync.spec.functional.JAsyncPromiseFunction1;
import io.github.vipcxj.jasync.spec.functional.JAsyncPromiseSupplier1;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Main {

    static class StaticSubPromise implements JPromise2<String> {

        class SubSubPromise extends StaticSubPromise {

            SubSubPromise(String msg) {
                super(JPromise2.just(msg));
            }
        }

        private final JPromise2<String> inner;

        StaticSubPromise(JPromise2<String> inner) {
            this.inner = inner;
        }

        @Override
        public <R> JPromise2<R> thenWithContext(JAsyncPromiseFunction1<String, R> mapper, boolean immediate) {
            return inner.thenWithContext(mapper, immediate);
        }

        @Override
        public JPromise2<String> doCatchWithContext(JAsyncCatchFunction1<Throwable, String> catcher, boolean immediate) {
            return inner.doCatchWithContext(catcher, immediate);
        }

        @Override
        public <R> JPromise2<String> doFinallyWithContext(JAsyncPromiseSupplier1<R> supplier, boolean immediate) {
            return inner.doFinallyWithContext(supplier, immediate);
        }

        @Override
        public JPromise2<String> onSuccess(BiConsumer<String, JContext> resolver) {
            return inner.onSuccess(resolver);
        }

        @Override
        public JPromise2<String> onError(BiConsumer<Throwable, JContext> reject) {
            return inner.onError(reject);
        }

        @Override
        public JPromise2<String> onFinally(Consumer<JContext> consumer) {
            return inner.onFinally(consumer);
        }

        @Override
        public JPromise2<String> onDispose(Runnable runnable) {
            return inner.onDispose(runnable);
        }

        @Override
        public void schedule(JContext context) {
            inner.schedule(context);
        }

        @Override
        public void dispose() {
            inner.dispose();
        }

        @Override
        public boolean isDisposed() {
            return inner.isDisposed();
        }

        @Override
        public boolean isResolved() {
            return inner.isResolved();
        }

        @Override
        public boolean isRejected() {
            return inner.isRejected();
        }

        @Override
        public void cancel() {
            inner.cancel();
        }

        @Override
        public String block(JContext context) throws InterruptedException {
            return inner.block(context);
        }

        @Override
        public JHandle<String> async(JContext context) {
            return inner.async(context);
        }
    }

    private JPromise2<Void> sleep(long ms) {
        return JPromise2.sleep(ms, TimeUnit.MILLISECONDS);
    }

    private JPromise2<Void> sayHello() {
        StaticSubPromise helloPromise = new StaticSubPromise(JPromise2.just("Hello"));
        String hello = helloPromise.await();
        String jasync = helloPromise.new SubSubPromise("JAsync").await();
        sleep(500).await();
        System.out.println("wait 500ms");
        sleep(1000).await();
        System.out.println(hello + " " + jasync + ".");
        return JPromise2.empty();
    }

    public static void main(String[] args) {
        try {
            new Main().sayHello().block();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
