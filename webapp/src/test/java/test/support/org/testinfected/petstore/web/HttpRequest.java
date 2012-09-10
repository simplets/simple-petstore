package test.support.org.testinfected.petstore.web;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.testinfected.petstore.Server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final Map<String, String> parameters = new HashMap<String, String>();
    
    private int timeout = 5000;
    private HttpMethod method = HttpMethod.GET;
    private String path = "/";
    private Paths paths  = Paths.root();
    private int port;

    public static HttpRequest aRequest() {
        return new HttpRequest();
    }

    public HttpRequest onPort(int port) {
        this.port = port;
        return this;
    }

    public HttpRequest withPath(String path) {
        this.path = path;
        return this;
    }

    public HttpRequest to(Server server) {
        onPort(server.getPort());
        return this;
    }

    public HttpRequest withParameter(String name, String value) {
        parameters.put(name, value);
        return this;
    }

    public HttpResponse send() throws IOException {
        WebClient client = new WebClient();
        client.setTimeout(timeout);
        WebRequest request = new WebRequest(requestUrl(), method);
        request.setRequestParameters(requestParameters());

        return new HttpResponse(client.loadWebResponse(request));
    }

    public HttpResponse get(String path) throws IOException {
        return withMethod(HttpMethod.GET).withPath(path).send();
    }

    public HttpResponse post(String path) throws IOException {
        return withMethod(HttpMethod.POST).withPath(path).send();
    }

    private HttpRequest withMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    private List<NameValuePair> requestParameters() {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (String name : parameters.keySet()) {
            pairs.add(new NameValuePair(name, parameters.get(name)));
        }
        return pairs;
    }

    private URL requestUrl() throws MalformedURLException {
        try {
            return new URL("http://localhost:" + port + paths.pathFor(path));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
