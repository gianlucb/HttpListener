// /**
// * Simple HTTP Listener tests
// *
// * @author Gianluca Bertelli
// * @version 1.0
// * @since 2019-07-15
// */

// package HttpListener;

// import org.junit.Test;
// import org.junit.Rule;
// import static org.junit.Assert.*;

// import java.net.HttpURLConnection;
// import java.net.MalformedURLException;
// import java.net.URL;
// import java.nio.file.Paths;

// public class AppTest {

// private static final int PORT = 8081;
// private static final String WWWROOT = Paths.get("src", "test", "resources",
// "wwwroot").toString();
// private static final String _baseAddress = "http://localhost:" + PORT;

// @Rule
// public HttpListenerRule httpServer = new HttpListenerRule(PORT, WWWROOT);

// @Test
// public void shouldRespondGet() {
// // check with index.html
// assertTrue(GetResource(""));
// }

// @Test
// public void shouldRespondNotImplemented() {

// try {
// URL url = new URL(_baseAddress + "/abcdef");
// HttpURLConnection con = (HttpURLConnection) url.openConnection();
// con.setRequestMethod("POST");
// int status = con.getResponseCode();
// assertTrue(status == HttpURLConnection.HTTP_NOT_IMPLEMENTED);
// } catch (MalformedURLException mEx) {
// // shoul not happen
// fail();
// } catch (Exception ex) {
// fail(ex.getMessage());
// }
// }

// @Test
// public void shouldRespond404() {

// try {
// URL url = new URL(_baseAddress + "/abcdef");
// HttpURLConnection con = (HttpURLConnection) url.openConnection();
// con.setRequestMethod("GET");
// int status = con.getResponseCode();
// assertTrue(status == HttpURLConnection.HTTP_NOT_FOUND);
// } catch (MalformedURLException mEx) {
// // shoul not happen
// fail();
// } catch (Exception ex) {
// fail(ex.getMessage());
// }
// }

// @Test
// public void downloadPDF() {
// assertTrue(GetResource("cla.pdf"));
// }

// @Test
// public void downloadPNG() {
// assertTrue(GetResource("bradipo.png"));
// }

// @Test
// public void downloadJPG() {
// assertTrue(GetResource("zorba.jpg"));
// }

// @Test
// public void loadTest() {

// int maxRequests = 0;
// for (; maxRequests < 1; maxRequests++) {
// assertTrue(GetResource("zorba.jpg"));
// }

// }

// private Boolean GetResource(String resource) {
// try {
// URL url = new URL(_baseAddress + "/" + resource);
// HttpURLConnection con = (HttpURLConnection) url.openConnection();
// con.setRequestMethod("GET");
// int status = con.getResponseCode();
// if (status == HttpURLConnection.HTTP_OK)
// return true;

// return false;
// } catch (Exception ex) {
// return false;
// }
// }

// }
