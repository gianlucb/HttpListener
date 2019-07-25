/**
* Open a socket in the specified TCP port and start listening for incoming client connections
* Define a cached threadpool in order to run each request in a different thread (new only if necessary)
*
* @author  Gianluca Bertelli
* @version 1.0
* @since   2019-07-15 
*/
package HttpListener;

import java.io.*;
import java.util.logging.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SimpleHttpListener extends Thread {

    private Logger _logger;
    private int _port;
    private File _wwwroot;
    private ServerSocket _serverSocket;

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
    @Override
    public void run() {
        try {
            _logger.info("SimpleHttpListener is starting on port " + _port);
            _logger.info("WWWROOT is set to " + _wwwroot.getAbsolutePath());

            _serverSocket = new ServerSocket();

            InetSocketAddress addr = new InetSocketAddress(_port);
            _serverSocket.bind(addr);
            _logger.info("SimpleHttpListener is listening...");

            // using a "unbounded pool" of threads - they are created only when needed
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

            try {

                while (true) {
                    Socket clientSocket = _serverSocket.accept();

                    SimpleHttpHandler requestHandler = new SimpleHttpHandler(_wwwroot, clientSocket);
                    executor.submit(requestHandler);
                }

            } catch (Exception socketEx) {
                _logger.severe(socketEx.getMessage());
                if (_serverSocket != null) {
                    _serverSocket.close();
                }
            }

        } catch (Exception ex) {

            _logger.warning(ex.getMessage());
        }
    }

    /**
     * Close the listening server
     */
    @Override
    public void interrupt() {
        try {
            if (_serverSocket != null) {
                _serverSocket.close();
            }
        } catch (Exception ex) {
            _logger.warning(ex.getMessage());
        }
    }
}