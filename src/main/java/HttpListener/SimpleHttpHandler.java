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
import java.util.HashMap;
import java.util.logging.*;

public class SimpleHttpHandler implements HttpHandler {

    private Logger _logger;
    private File _wwwroot; // base path of the web content
    private HashMap<String, String> _mimeTypes; // base MIME types table
    public static final String GENERIC_MIME_TYPE = "application/octet-stream";

    /**
     * Handle each incoming call. Search the requested resource from the disk
     * 
     * @param wwwroot the base path on disk where to look for content
     * @throws Exception in case of invalid WWWROOT path
     */
    public SimpleHttpHandler(File wwwroot) throws Exception {
        _logger = Logger.getLogger(App.class.getName());

        // input validation
        if (!wwwroot.exists() || !wwwroot.canRead() || !wwwroot.isDirectory()) {
            throw new Exception("Invalid wwwroot");
        }
        _wwwroot = wwwroot;

        // this is hardcoded for sake of semplicity
        // it should be read at runtime from a config file
        _mimeTypes = new HashMap<String, String>();
        _mimeTypes.put("html", "text/html");
        _mimeTypes.put("htm", "text/html");
        _mimeTypes.put("css", "text/css");
        _mimeTypes.put("js", "application/javascript");
        _mimeTypes.put("pdf", "application/pdf");
        _mimeTypes.put("png", "image/png");
        _mimeTypes.put("jpg", "image/jpeg");
        _mimeTypes.put("jpeg", "image/jpeg");
        _mimeTypes.put("gif", "application/gif");
        _mimeTypes.put("mp3", "audio/mpeg3");
        _mimeTypes.put("mov", "video/quicktime");

    };

    /**
     * Method to handle the client request. If the request is for the root (/) it
     * returns the index.html file (if exists)
     */
    @Override
    public void handle(HttpExchange req) throws IOException {

        long threadId = Thread.currentThread().getId();

        // I handle only GET requests for sake of semplicity ignoring other methods

        String method = req.getRequestMethod();
        if (!method.equals("GET")) {
            _logger.info(String.format("[%s] Rejecting request as method %s is not allowed ", threadId, method));

            req.sendResponseHeaders(HttpURLConnection.HTTP_NOT_IMPLEMENTED, 0);
            return;
        }

        // here the method is valid
        _logger.info(String.format("[%s] Handling new request for %s", threadId, req.getRequestURI().toString()));

        String requestedResource = req.getRequestURI().getPath();

        // check for default document
        if (requestedResource.equals("/")) {
            RespondWithFile(req, "index.html");
            return;
        }

        // not default path, looking for a resource on the disk
        RespondWithFile(req, requestedResource);
    }

    /**
     * Check if the request's URI point to a valid file on the disk. If so return
     * that file. It sets also the right MIME content type based on file extension
     * 
     * @param req      the client request
     * @param filePath the file relative path
     * @throws IOException in case of not existing or not able to read the file
     */
    public void RespondWithFile(HttpExchange req, String filePath) throws IOException {

        File requestedFile = new File(_wwwroot.toPath() + File.separator + filePath).getCanonicalFile();

        // checking if the requested file exists, 404 in negative case
        if (!requestedFile.exists()) {
            _logger.warning(filePath + " does not exist");

            String response = "404 / NOT FOUND";
            req.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.length());
            OutputStream os = req.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            // file exists - response 200

            // setting the right MIME header
            String fileExt = getFileExtension(requestedFile.getPath());
            String mimeType = (fileExt.length() > 0) ? getMIMEType(fileExt) : GENERIC_MIME_TYPE;
            Headers h = req.getResponseHeaders();
            h.set("Content-Type", mimeType);
            req.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

            // read the file from the disk and write to output stream
            OutputStream os = req.getResponseBody();
            FileInputStream fs = new FileInputStream(requestedFile);
            final byte[] buffer = new byte[(int) requestedFile.length()];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0) {
                os.write(buffer, 0, count);
            }
            os.flush();
            os.close();
            fs.close();
        }
    }

    private String getFileExtension(String filename) {
        int fileExtPos = filename.lastIndexOf('.');
        return (fileExtPos > 0) ? filename.substring(fileExtPos + 1) : "";
    }

    public String getMIMEType(String fileExt) {
        String mimeType = (String) _mimeTypes.get(fileExt);
        return (mimeType != null) ? mimeType : GENERIC_MIME_TYPE;
    }
}
