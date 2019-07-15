/**
* Simple HTTP Listener tests
*
* @author  Gianluca Bertelli
* @version 1.0
* @since   2019-07-15 
*/

package HttpListener;

import org.junit.Test;
import org.junit.Rule;
import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class AppTest {

    private static final int PORT = 8081;
    private static final String WWWROOT = Paths.get("src", "test", "resources", "wwwroot").toString();
    private static final String _baseAddress = "http://localhost:" + PORT;

    @Rule
    public HttpListenerRule httpServer = new HttpListenerRule(PORT, WWWROOT);

    @Test
    public void shouldRespondGet() {

        try {
            // should respond with index.html
            URL url = new URL(_baseAddress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            assertTrue(status == HttpURLConnection.HTTP_OK);
        } catch (MalformedURLException mEx) {
            // shoul not happen
            fail();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void shouldRespondNotImplemented() {

        try {
            URL url = new URL(_baseAddress + "/abcdef");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            int status = con.getResponseCode();
            assertTrue(status == HttpURLConnection.HTTP_NOT_IMPLEMENTED);
        } catch (MalformedURLException mEx) {
            // shoul not happen
            fail();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void shouldRespond404() {

        try {
            URL url = new URL(_baseAddress + "/abcdef");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            assertTrue(status == HttpURLConnection.HTTP_NOT_FOUND);
        } catch (MalformedURLException mEx) {
            // shoul not happen
            fail();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void downloadPDF() {

        try {
            URL url = new URL(_baseAddress + "/cla.pdf");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            assertTrue(status == HttpURLConnection.HTTP_OK);
        } catch (MalformedURLException mEx) {
            // shoul not happen
            fail();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    public void downloadPNG() {

        try {
            URL url = new URL(_baseAddress + "/bradipo.png");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            assertTrue(status == HttpURLConnection.HTTP_OK);
        } catch (MalformedURLException mEx) {
            // shoul not happen
            fail();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    public void downloadJPG() {

        try {
            URL url = new URL(_baseAddress + "/zorba.jpg");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            assertTrue(status == HttpURLConnection.HTTP_OK);
        } catch (MalformedURLException mEx) {
            // shoul not happen
            fail();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

}
