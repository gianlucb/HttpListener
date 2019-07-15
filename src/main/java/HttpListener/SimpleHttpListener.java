package HttpListener;

import com.sun.net.httpserver.*;
import java.io.*;
import java.util.logging.*;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class SimpleHttpListener {

    private Logger _logger;
    private int _port;
    private File _wwwroot;
    private HttpServer _server;

    public SimpleHttpListener(int port, String wwwrootPath) throws Exception {
        _logger = Logger.getLogger(App.class.getName());

        // arguments validation
        if (port <= 0)
            throw new Exception("Invalid port number");
        _port = port;

        _wwwroot = new File(wwwrootPath);
        if (!_wwwroot.exists() || !_wwwroot.canRead() || !_wwwroot.isDirectory()) {
            throw new Exception("Invalid wwwroot");
        }
    };

    public void run() {
        try {
            _logger.info("SimpleHttpListener is starting on port " + _port);
            _logger.info("WWWROOT is set to " + _wwwroot.getAbsolutePath());

            ExecutorService executor;
            InetSocketAddress addr = new InetSocketAddress(_port);
            _server = HttpServer.create(addr, 0);
            _server.createContext("/", new SimpleHttpHandler(_wwwroot));
            executor = Executors.newCachedThreadPool();
            _server.setExecutor(executor);
            _server.start();
            _logger.info("SimpleHttpListener is listening...");
        } catch (Exception ex) {
            _logger.warning(ex.getMessage());
        }
    }

    public void terminate() {
        if (_server != null)
            _server.stop(0);
    }
}