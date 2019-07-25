/**
* Real executor class, runs the code to handle an incoming HTTP request
* This class is executed for each incoming call, via the Thread-Pool
* Implements only the GET method and some pre-defined MIME types
*
* @author  Gianluca Bertelli
* @version 1.0
* @since   2019-07-15 
*/
package HttpListener;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.*;

public class SimpleHttpHandler extends Thread {

    private Socket _clientSocket;
    private Logger _logger;
    private File _wwwroot; // base path of the web content

    /**
     * Handle each incoming call. Search the requested resource from the disk
     *
     * @param wwwroot the base path on disk where to look for content
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
        long threadId = Thread.currentThread().getId();
        try {

            _logger.info(String.format("[%s] Handling a new client request", threadId));

            // DebugPrintRequest(in);

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

    private void DebugPrintRequest(BufferedReader in) throws IOException {
        String line = in.readLine();
        StringBuffer sb = new StringBuffer();
        while (line != null && line.length() > 0) {
            sb.append(line + '\n');
            line = in.readLine();
        }

        _logger.info(String.format("%s", sb.toString()));
    }

    // /**
    // * Method to handle the client request. If the request is for the root (/) it
    // * returns the index.html file (if exists)
    // */

    // public void handle(HttpExchange req) throws IOException {

    // long threadId = Thread.currentThread().getId();

    // // I handle only GET requests for sake of semplicity ignoring other methods

    // String method = req.getRequestMethod();
    // if (!method.equals("GET")) {
    // _logger.info(String.format("[%s] Rejecting request as method %s is not
    // allowed ", threadId, method));

    // req.sendResponseHeaders(HttpURLConnection.HTTP_NOT_IMPLEMENTED, 0);
    // return;
    // }

    // // here the method is valid
    // _logger.info(String.format("[%s] Handling new request for %s", threadId,
    // req.getRequestURI().toString()));

    // String requestedResource = req.getRequestURI().getPath();

    // // check for default document
    // if (requestedResource.equals("/")) {
    // respondWithFile(req, "index.html");
    // return;
    // }

    // // not default path, looking for a resource on the disk
    // respondWithFile(req, requestedResource);
    // }

}
