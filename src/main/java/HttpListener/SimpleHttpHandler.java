/**
* Real executor class, runs the code to handle an incoming HTTP request
* This class is executed for each incoming call, via the Thread-Pool
* Implements only the GET and HEAD methods. It also supports only some pre-defined MIME types
*
* @author  Gianluca Bertelli
* @version 1.0
* @since   2019-07-15 
*/
package HttpListener;

import java.io.*;
import java.net.Socket;
import java.util.logging.*;

public class SimpleHttpHandler extends Thread {

    private Socket _clientSocket;
    private Logger _logger;
    private File _wwwroot; // base path of the web content

    /**
     * Handle each incoming call. For each client call creates an instance of @see
     * HttpListener.SimpleHttpRequest to parse the request and respond to the client
     *
     * @param wwwroot      the base path on disk where to look for content
     * @param clientSocket the incoming client connection
     * @throws Exception in case of invalid WWWROOT path
     */
    public SimpleHttpHandler(File wwwroot, Socket clientSocket) throws Exception {
        _logger = Logger.getLogger(App.class.getName());

        if (clientSocket == null)
            throw new Exception("socket is null");

        _clientSocket = clientSocket;

        // input validation
        if (!wwwroot.exists() || !wwwroot.canRead() || !wwwroot.isDirectory()) {
            throw new Exception("Invalid wwwroot");
        }
        set_wwwroot(wwwroot);
    };

    public File get_wwwroot() {
        return _wwwroot;
    }

    public void set_wwwroot(File _wwwroot) {
        this._wwwroot = _wwwroot;
    }

    @Override
    public void run() {

        // method executed at each new client connection
        long threadId = Thread.currentThread().getId();
        try {

            _logger.info(String.format("[%s] Handling a new client request", threadId));

            SimpleHttpRequest req = new SimpleHttpRequest(_clientSocket.getInputStream(),
                    _clientSocket.getOutputStream(), _wwwroot);

            req.SendResponse();

            // closing the socket for sake of semplicity
            _clientSocket.close();

            _logger.info(String.format("[%s] Client request completed", threadId));

        } catch (Exception e) {
            e.printStackTrace();

            // here an option can be to return 500 or BadRequest response
            // being a raw http server I decided to close the connection with an empty
            // response to signal a malfunctioning
            try {
                _clientSocket.close();
            } catch (IOException ioe) {
                _logger.warning("connection abandoned by the client");
            }
        }
    }
}
