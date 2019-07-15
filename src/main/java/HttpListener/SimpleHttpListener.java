/**
* Open a socket in the specified TCP port and start listening for incoming client connections
* Define a cached threadpool in order to run each request in a different thread (new only if necessary)
*
* @author  Gianluca Bertelli
* @version 1.0
* @since   2019-07-15 
*/
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

    /**
     * Open the server for listening
     */
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

    /**
     * Close the listening server
     */
    public void terminate() {
        if (_server != null)
            _server.stop(0);
    }
}