/**
 * Helper class to capture assertion on threads
 * copied from: https://stackoverflow.com/questions/2596493/junit-assert-in-thread-throws-exception
 */
package HttpListener;

public class AsyncTester {
    private Thread thread;
    private AssertionError exc;

    public AsyncTester(final Runnable runnable) {
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } catch (AssertionError e) {
                    exc = e;
                }
            }
        });
    }

    public void start() {
        thread.start();
    }

    public void test() throws InterruptedException {
        thread.join();
        if (exc != null)
            throw exc;
    }
}