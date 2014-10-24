package com.slamgarden;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.lang.Exception;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.*;

//import org.apache.http.impl.conn.BasicHttpClientConnectionManager

import org.apache.http.*;
import org.apache.http.impl.conn.*;
import org.apache.http.conn.*;
import org.apache.http.conn.routing.*;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;


import org.apache.log4j.Logger;

// import com.slamgarden.HttpLine;

/** Description of TestConn()
 *
 * A TestConn is used to make an entry point into one of the package modules without calling
 *   it with all normal operating parameters or setup
 * @throws           Description of myException
 */

public class TestConn
{
    ConnectionRequest connRequest;

    private static Logger logger = Logger.getLogger(TestConn.class);

    public static void main(String args[]) throws IOException, InterruptedException, ConnectionPoolTimeoutException, ExecutionException {
        logger.info( "In method Main()");
        logger.debug("This is debug in TestConn()");

        HttpLine httpline;

        TestConn testconn = new TestConn();

        testconn.initConn("http","www.slamgarden.com",80);
  
        if (args.length > 0 ) {
          httpline = new HttpLine(args[0]);
        } else {
          httpline = new HttpLine();
        }
        int i = 0;
        while(httpline.next() ) {
          logger.debug("reading next line number " + i++ + ": " + httpline.getLine());
        }

        System.out.println("End of TestConn");
    }

    public void initConn(String proto, String domain, int port) throws IOException, ConnectionPoolTimeoutException, InterruptedException, ExecutionException {

      BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager();

      HttpClientContext context = HttpClientContext.create();

      HttpRoute route = new HttpRoute(new HttpHost(domain, port));

      ConnectionRequest connRequest = connManager.requestConnection(route, null);

      HttpClientConnection conn = connRequest.get(2,TimeUnit.SECONDS);
      try {
        // If not open
        if (!conn.isOpen()) {
        // establish connection based on its route info
        connManager.connect(conn, route, 1000, context);
        // and mark it as route complete
        connManager.routeComplete(conn, route, context);
        }
        // Do useful things with the connection.
      } finally {
        connManager.releaseConnection(conn, null, 1, TimeUnit.MINUTES);
      }
    }

    public ConnectionRequest getConn() {
      return connRequest;
    }

    public void setConnProps() {
      logger.debug("in setConnProps, probably need to make specific property setters");
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
