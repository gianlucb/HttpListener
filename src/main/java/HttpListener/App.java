/**
* Simple HTTP Listener that implements a static (file-based) web server
* This implements only the GET method and some hard-coded MIME types
*
* @author  Gianluca Bertelli
* @version 1.0
* @since   2019-07-15 
*/
package HttpListener;

import java.util.logging.*;

public class App {

    public static void main(String[] args) {

        int port = 8080; // where to listen to
        String wwwroot = "./wwwroot"; // base path of the web content
        Logger logger = Logger.getLogger(App.class.getName());

        // if present use the first arg as port number
        if (args.length >= 1)
            port = Integer.parseInt(args[0]);

        // if present use the second arg as base wwwroot
        if (args.length >= 2)
            wwwroot = args[1];

        SimpleHttpListener listener = null;

        try {
            // input validation is done in the .ctor. Throws an exception in case of error
            listener = new SimpleHttpListener(port, wwwroot);

            Thread serverThread = new Thread(listener);
            serverThread.start();
            serverThread.wait();

        } catch (Exception ex) {
            if (listener != null)
                listener.interrupt();

            logger.warning(ex.getMessage());
        }
    }
}
