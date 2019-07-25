/**
* Simple HTTP Listener tests
*
* @author Gianluca Bertelli
* @version 1.0
* @since 2019-07-15
*/

package HttpListener;

import org.junit.Test;
import org.junit.Rule;
import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AppTest {

    private static final int PORT = 8081;
    private static final String WWWROOT = Paths.get("src", "test", "resources", "wwwroot").toString();
    private static final String _baseAddress = "http://localhost:" + PORT;

    @Rule
    public HttpListenerRule httpServer = new HttpListenerRule(PORT, WWWROOT);

    @Test
    public void supportGET() {
        // check with index.html
        assertTrue(GetResource("", "GET"));
    }

    @Test
    public void supportHEAD() {
        // check with index.html
        assertTrue(GetResource("", "HEAD"));
    }

    @Test
    public void supportQueryString() {
        assertTrue(GetResource("?1232132&asdsa=232", "GET"));
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
        assertTrue(GetResource("cla.pdf", "GET"));
    }

    @Test
    public void downloadPNG() {
        assertTrue(GetResource("bradipo.png", "GET"));
    }

    @Test
    public void downloadJPG() {
        assertTrue(GetResource("zorba.jpg", "GET"));
    }

    private void SingleGet() {

    }

    // this is a workaround to get the assert exception on the main thread
    // copied from
    // https://stackoverflow.com/questions/2596493/junit-assert-in-thread-throws-exception
    @Test
    public void singleRequestOnADifferentThread() throws Exception {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<?> future = es.submit(() -> {
            assertTrue(GetResource("zorba.jpg", "GET"));
            return null;
        });
        future.get();
    }

    // test if the server support multiple request for the same resource
    @Test
    public void concurrentRequest() throws Exception {
        for (int i = 0; i < 1000; i++)
            singleRequestOnADifferentThread();
    }

    private Boolean GetResource(String resource, String method) {
        try {
            URL url = new URL(_baseAddress + "/" + resource);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK)
                return true;

            return false;
        } catch (Exception ex) {
            return false;
        }
    }

}
