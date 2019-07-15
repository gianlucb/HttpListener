package HttpListener;

import java.net.InetSocketAddress;
import org.junit.rules.ExternalResource;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Starts an HTTP Listener instance
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
        _server = new SimpleHttpListener(_port, _wwwroot);
        _server.run();
        System.out.println("Created a new Http listener on " + _port + " wwwroot: " + _wwwroot);
    }

    @Override
    protected void after() {
        if (_server != null) {
            _server.terminate();
        }
    }

}