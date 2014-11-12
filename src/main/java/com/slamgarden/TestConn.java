package com.slamgarden;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.lang.Exception;


import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.*;

import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import org.apache.http.*;
import org.apache.http.impl.conn.*;
import org.apache.http.conn.*;
import org.apache.http.conn.routing.*;
import org.apache.http.HttpHost;
import org.apache.http.protocol.HttpContext;
// see http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/protocol/package-summary.html
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.client.methods.HttpGet;


import org.apache.log4j.Logger;

import com.slamgarden.HttpLine;

/** Description of TestConn()
 *
 * A TestConn is used to make an entry point into one of the package modules without calling
 *   it with all normal operating parameters or setup
 * @throws           Description of myException
 */

public class TestConn
{
    static ConnectionRequest connRequest;
    static HttpLine httpline;
    static HttpRequestExecutor exeRequest;
    static HttpGet get;
    static BasicHttpClientConnectionManager connManager;
    static HttpClientContext context;
    static HttpRoute route;
    static HttpClientConnection conn;

    private static Logger logger = Logger.getLogger(TestConn.class);

    public static void main(String args[]) throws IOException, InterruptedException, ConnectionPoolTimeoutException, ExecutionException, HttpException {
        logger.info( "In method Main()");
        logger.debug("This is debug in TestConn()");

        String curHost = null;
        String lastHost = "";

        if (args.length > 0 ) {
          httpline = new HttpLine(args[0]);
        } else {
          httpline = new HttpLine();
        }
        int i = 0;

        while(httpline.next() ) {
          logger.debug("reading next line number " + i++ + ": " + httpline.getLine());

          curHost = httpline.getHostname();

          if(!curHost.equals(lastHost)) { // changed destination
            closeConn();
            try {
              openConn(httpline.getProto(),curHost,httpline.getPort());
              initReq(httpline.getHostname(),httpline.getPort(),httpline.getProto(),httpline.getUriPath());
              exeRequest.execute(get, conn, context);
              lastHost = httpline.getHostname();
            } 
            catch ( Exception e) {
              logger.error("Bad connection!" + e);
              lastHost = "FailedHost Connect";  // fail the lasthost comparison
            }
          } 
          else {
            initReq(httpline.getHostname(),httpline.getPort(),httpline.getProto(),httpline.getUriPath());
            exeRequest.execute(get, conn, context);
            lastHost = httpline.getHostname();
          }
        }

        System.out.println("End of TestConn");
    }
    
/* Init Req */

    static void initReq(String hostname, Integer port, String proto, String uriPath) {

        exeRequest = new HttpRequestExecutor();

// what is the purpose of setTargetHost in the context when calling HttpGet() right after??

        // context.setTargetHost((new HttpHost(hostname, port))); // this seems not to work

        get = new HttpGet(proto +"://" + hostname+uriPath);
        // get = new HttpGet(uriPath);

 
    }
 
/*
// high level
CloseableHttpClient client = HttpClients.custom().setConnectionManager(basicConnManager).build();
client.execute(get);

*/

/* Setting headers
// for one request

HttpClient client = HttpClients.custom().build();
HttpUriRequest request = RequestBuilder.get().setUri(SAMPLE_URL)
  .setHeader(HttpHeaders.CONTENT_TYPE, "application/json").build();
client.execute(request);

*/

/* Setting headers
// on all requests by default

Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
List<Header> headers = Lists.newArrayList(header);
HttpClient client = HttpClients.custom().setDefaultHeaders(headers).build();
HttpUriRequest request = RequestBuilder.get().setUri(SAMPLE_URL).build();
client.execute(request);

*/

/** Description of openConn()
 *
 * openConn() 
 * @throws          IOException, ConnectionPoolTimeoutEx    ception, InterruptedException, ExecutionException 
 */
    static public void openConn(String proto, String domain, int port) throws IOException, ConnectionPoolTimeoutException, InterruptedException, ExecutionException {

      connManager = new BasicHttpClientConnectionManager();

      context = HttpClientContext.create();

      route = new HttpRoute(new HttpHost(domain, port));

      connRequest = connManager.requestConnection(route, null);

      conn = connRequest.get(2,TimeUnit.SECONDS);
      try {
        // If not open
        if (!conn.isOpen()) {
        // establish connection based on its route info
        connManager.connect(conn, route, 1000, context);
        // and mark it as route complete
        connManager.routeComplete(conn, route, context);
        }
        // Do useful things with the connection.
      }
      catch ( ConnectionPoolTimeoutException e) {
        logger.error(e);
      }
    }

    static ConnectionRequest getConn() {
      return connRequest;
    }

    static void setConnProps() {
      logger.debug("in setConnProps, probably need to make specific property setters");
    }

    static void closeConn() {
        try {
          connManager.releaseConnection(conn, null, 1, TimeUnit.MINUTES);
        } catch (NullPointerException e) {
          logger.warn(e + "connManager has no connection to close.");
        }
    }

}

/*

HttpClientContext context = HttpClientContext.create();
HttpClientConnectionManager connMrg = new BasicHttpClientConnectionManager();
HttpRoute route = new HttpRoute(new HttpHost("localhost", 80));
// Request new connection. This can be a long process
ConnectionRequest connRequest = connMrg.requestConnection(route, null);
// Wait for connection up to 10 sec
HttpClientConnection conn = connRequest.get(10, TimeUnit.SECONDS);
try {
    // If not open
    if (!conn.isOpen()) {
        // establish connection based on its route info
        connMrg.connect(conn, route, 1000, context);
        // and mark it as route complete
        connMrg.routeComplete(conn, route, context);
    }
    // Do useful things with the connection.
} finally {
    connMrg.releaseConnection(conn, null, 1, TimeUnit.MINUTES);
}

*/

/*
BasicHttpClientConnectionManager basicConnManager = 
    new BasicHttpClientConnectionManager();
HttpClientContext context = HttpClientContext.create();
 
// low level
HttpRoute route = new HttpRoute(new HttpHost("www.baeldung.com", 80));
ConnectionRequest connRequest = basicConnManager.requestConnection(route, null);
HttpClientConnection conn = connRequest.get(10, TimeUnit.SECONDS);
basicConnManager.connect(conn, route, 1000, context);
basicConnManager.routeComplete(conn, route, context);
 
HttpRequestExecutor exeRequest = new HttpRequestExecutor();
context.setTargetHost((new HttpHost("www.baeldung.com", 80)));
HttpGet get = new HttpGet("http://www.baeldung.com");
exeRequest.execute(get, conn, context);
 
basicConnManager.releaseConnection(conn, null, 1, TimeUnit.SECONDS);
 
// high level
CloseableHttpClient client = HttpClients.custom().setConnectionManager(basicConnManager).build();
client.execute(get);

*/

/* Setting headers
// for one request

HttpClient client = HttpClients.custom().build();
HttpUriRequest request = RequestBuilder.get().setUri(SAMPLE_URL)
  .setHeader(HttpHeaders.CONTENT_TYPE, "application/json").build();
client.execute(request);

*/

/* Setting headers
// on all requests by default

Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
List<Header> headers = Lists.newArrayList(header);
HttpClient client = HttpClients.custom().setDefaultHeaders(headers).build();
HttpUriRequest request = RequestBuilder.get().setUri(SAMPLE_URL).build();
client.execute(request);

*/
