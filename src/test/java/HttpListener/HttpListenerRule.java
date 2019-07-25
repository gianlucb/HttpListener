package HttpListener;

import org.junit.rules.ExternalResource;

/**
 * Simple Rule to start an HTTP Listener isntance before to run the tests
 *
 */
public class HttpListenerRule extends ExternalResource {

    private SimpleHttpListener _server;
    private int _port;
    private String _wwwroot;

    public HttpListenerRule(int port, String wwwroot) {
        _port = port;
        _wwwroot = wwwroot;
    }

    @Override
    protected void before() throws Throwable {

        // if the input is wrong the class throws an Exception that will fail the
        _server = new SimpleHttpListener(_port, _wwwroot);
        Thread serverThread = new Thread(_server);
        serverThread.start();

        System.out.println("Created a new Http listener on " + _port + " wwwroot: " + _wwwroot);
    }

    @Override
    protected void after() {
        // terminate before to exit
        if (_server != null) {
            _server.interrupt();
        }
    }

}