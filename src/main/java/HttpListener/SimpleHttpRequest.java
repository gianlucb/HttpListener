/**
* Represent a HTTP request. This class is able to understand if the request is valid (well-formed).
* It also implements the logic to respond to the client, based on the METHOD requested
* It currently implements only GET and HEAD methods 
*
* @author  Gianluca Bertelli
* @version 1.0
* @since   2019-07-25 
*/

package HttpListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.*;

public class SimpleHttpRequest {
    private String _method;
    private String _version;
    private String _uri;
    private BufferedReader _input;
    private OutputStream _output;
    private Logger _logger;
    private HashMap<String, String> _mimeTypes; // base MIME types table
    private static final String GENERIC_MIME_TYPE = "application/octet-stream";
    private File _wwwroot;
    private String _queryString; // not realyl used in this example

    public SimpleHttpRequest() {

    }

    public SimpleHttpRequest(InputStream in, OutputStream out, File wwwroot) throws Exception {
        _input = new BufferedReader(new InputStreamReader(in));
        _output = out;
        _wwwroot = wwwroot;
        _logger = Logger.getLogger(App.class.getName());
        init();
    }

    private void init() {

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
    }

    /**
     * Checks if the request is a valid HTTP request. Validation is done using
     * https://tools.ietf.org/html/rfc2616 details. For semplicity this
     * implementation accepts only absolute URIs (not empty):
     * 
     * GET /path?querystring HTTP/1.1
     * 
     * @throws Exception in case of malformed request
     */
    private void ParseRequestLine() throws Exception {

        // the first line should be in the form METHOD (SP) URI (SP) VERSION
        // regex tested with https://www.freeformatter.com/java-regex-tester.html
        String requestLineRegEx = "(GET|POST|HEAD|OPTIONS|CONNECT|TRACE|DELETE|PUT)\\s+([^?\\s]+)((?:[?&][^&\\s]+)*)\\s+(HTTP/.*)";

        String requestLine = _input.readLine();
        if (!requestLine.matches(requestLineRegEx))
            throw new Exception("invalid request");

        // here the request line is well-formed
        String[] terms = requestLine.split(" ");

        _method = terms[0].trim();
        _uri = terms[1].trim();
        _version = terms[2].trim();

        // extract the querystring
        int queryStringStart = _uri.indexOf("?");
        if (queryStringStart > 0) {
            // there is a querystring
            _uri = _uri.substring(0, queryStringStart).trim();
            _queryString = _uri.substring(queryStringStart).trim();
        }
    }

    public String getUri() {
        return _uri;
    }

    public String getVersion() {
        return _version;
    }

    public String getMethod() {
        return _method;
    }

    /**
     * Based on the requested METHOD sends back the response to the client
     * 
     * @throws Exception in case is not able to find/open the resource requested
     */
    public void SendResponse() throws Exception {

        ParseRequestLine();

        // handle only the implemented methods
        switch (_method) {
        case "GET":
            responseWithEntity(true);
            break;
        case "HEAD":
            responseWithEntity(false);
            break;
        default:
            responseNotImplemented();
        }

        _output.flush();
        _input.close();
        _output.close();
    }

    private void responseNotImplemented() throws IOException {
        String statusLine = _version + " 501 Not implemented\r\n";
        _output.write(statusLine.getBytes());
    }

    /**
     * Check if the request's URI (entity) point to a valid file on the disk. If so
     * set the right response status line and the right headers. It also set the
     * correct MIME content type based on file extension
     * 
     * @param includeEntity specify if the Entity must be sent as Body. For some
     *                      VERB (like HEAD) the body must not be present
     * @throws Exception in case of not existing or not able to read the file
     */
    public void responseWithEntity(Boolean includeEntity) throws Exception {

        // handling default document
        if (_uri.equals("/"))
            _uri = "index.html";

        File requestedFile = new File(_wwwroot.toPath() + File.separator + _uri).getCanonicalFile();

        // checking if the requested file exists, 404 in negative case
        if (!requestedFile.exists()) {
            _logger.warning(_uri + " does not exist");
            String statusLine = _version + " 404 Not Found\r\n";
            _output.write(statusLine.getBytes());
            // flush and close
            _output.flush();

        } else {
            // resource exists - response 200

            // setting the right MIME header
            String fileExt = getFileExtension(requestedFile.getPath());
            String mimeType = (fileExt.length() > 0) ? getMIMEType(fileExt) : GENERIC_MIME_TYPE;

            // read the file from the disk and write to output stream

            // set the response headers
            String statusLine = _version + " 200 OK\r\n";
            _output.write(statusLine.getBytes());
            String length = "Content-Length: " + (int) requestedFile.length() + "\r\n";
            _output.write(length.getBytes("UTF-8"));
            _output.write(("Server: SimpleHttpListener\r\n").getBytes());
            _output.write(("Content-Type: " + mimeType + "\r\n").getBytes());

            // in case of HEAD method the Entity must not be sent
            if (includeEntity) {
                // body delimitator (newline)
                _output.write(("\r\n").getBytes());

                // body
                FileInputStream fs = new FileInputStream(requestedFile);
                final byte[] buffer = new byte[(int) requestedFile.length()];
                int count = 0;
                while ((count = fs.read(buffer)) >= 0) {
                    _output.write(buffer, 0, count);
                }
                fs.close();

                // flush and close
                _output.flush();
            }
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