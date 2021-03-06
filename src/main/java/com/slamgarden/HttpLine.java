package com.slamgarden;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/** Description of HttpLine()
 * reads a line from a url test file
 * actions: interprets all terms of the line and
 *   maps them into http options and parameters attributes.
 * These attributes are made to be consumed by a subseqent
 *   method call of an http client while the caller
 *   handles state, persistent data etc.
 * HttpLine will indicate if the caller should retain state for a
 *   subsequent call, based on the -reuse parameter in the line.
 * Ideally, HttpLine will return an object which contains a type
 *   of object which can be easily copied by the caller
 *   into it's own Http object.
 *
 */

public class HttpLine {

    private static Logger logger = Logger.getLogger(HttpLine.class);

    public static void main(String args[]) {
        logger.info( "In method Main()");
        logger.debug("This is debug in HttpLine()");
        System.out.println("hello fair world");
    }

    private String curline;
    private boolean proxy;
    private String[] header = new String[20];
    private boolean reuse;   /* caller should call with the returned and saved settings */
    private File file;
    private BufferedReader reader = null;
    private String filename;
    private String hostname;
    private String proto;
    private String uriPath;
    private Integer port;

    /**
     * Constructor for HttpLine object.
     * The string argument must specify a filesystem path to a jcurler command file.
     * <p>
     * @param filename
     * @returns new HttpLine object
     */
    public HttpLine() {}

    public HttpLine(String filename) {

        file = new File(filename);

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
        } catch (FileNotFoundException e) {
            logger.error("File not found: "+filename);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("IO Exception opening file: "+filename);
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
            } catch (AssertionError e) {
                logger.error("Assertion Exception, bufferedreader is null for file: "+filename);
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks to see if there is another jcurler rule in the command file by
     * attempting to read the next line.  If it fails, returns false. If it
     * succeeds, curline is populated with the line which subsequent calls to
     * getLine and other methods will for use in methods.
     * <p>
     * @returns true or false 
     */
    public boolean next() {
        logger.debug("In next()");
        try {
            curline = reader.readLine();
            if ( curline == null ) {
              throw new IOException("End of file");
            }
            parse();
            return true;
        } catch ( IOException e ) {
            logger.error("Read to end of file or other IO error: "+filename);
            return false;
        } 
    }

    /**
     * returns the current value of curline as read from the command file in a prior
     * call to next().
     * <p>
     * @returns String
     */
    public String getLine() {
      return getLineAsText();
    }

    /**
     * returns the current value of curline as read from the command file in a prior
     * call to next().
     * <p>
     * @returns String
     */
    public String getLineAsText() {
      return curline;
    }



    /**
     *   Promotes the line instance variable to the next line in the file if successful
     *   by reading the line.
     * <p>
     * @param <None>
     * @returns true of false based on success
     */

    /**
     *  Examines the current command file line ( following a call to nextLine ).
     *
     *   by attempting to read the next line.
     *
     * @param <None>
     * @returns true of false
     */
    void parse() {
        logger.debug("In parse()");
        Pattern pattern = Pattern.compile("(htt\\w{1,2})://([\\w\\.]+)(:\\d+|)(.*?)(\\s+.*|$)");
        Matcher matcher = pattern.matcher(curline);

        if(matcher.find()) {
          logger.debug("In parse() matcher.find() is true");
          for(int i = 1;i<5;i++) {
            logger.debug(String.format("In parse() matcher.start(%d) is %d",i,matcher.start(i)));
            logger.debug(String.format("In parse() matcher.end(%d) is %d",i,matcher.end(i)));
            logger.debug(String.format("In parse() matcher.group(%d) is %s",i,matcher.group(i)));
          }

          proto=matcher.group(1);

          if ( matcher.start(3) == matcher.end(3)) {
            if( proto.equals("http")) {
              port = 80;
            }
            else if ( proto.equals("https")) {
              port = 443;
            }
            else { 
              logger.error("Uncaught error proto ! http or https");
            }
          }
          else {
            port = Integer.parseInt(matcher.group(3).substring(1));
          }

          logger.debug("In parse() port = " + port);

          hostname=matcher.group(2);

          if(matcher.start(4) == matcher.end(4)) {
            uriPath="/";
          }
          else {
            uriPath=matcher.group(4);
          }
          logger.debug("In parse() uriPath = " + uriPath);

        } 
        else {
          logger.debug("In parse() matcher.find() is false");
        }
    }
    public String getHostname() {
      return(hostname);
    }
    public String getProto() {
      return(proto);
    }
    public Integer getPort() {
      return(port);
    }
    public String getUriPath() {
      return(uriPath);
    }
}

/*
URI uri = new URIBuilder()
 .setScheme("http")
 .setHost("www.google.com")
 .setPath("/search")
 .setParameter("q", "httpclient")
 .setParameter("btnG", "Google Search")
 .setParameter("aq", "f")
 .setParameter("oq", "")
 .build();

HttpGet httpget = new HttpGet(uri);

*/

/*
The most efficient way to obtain all headers of a given type is by using the HeaderIterator interface.

HeaderIterator it = response.headerIterator("Set-Cookie");
while (it.hasNext()) {
 System.out.println(it.next());
}

*/

/* file input
File file = new File("somefile.txt");
FileEntity entity = new FileEntity(file,
 ContentType.create("text/plain", "UTF-8"));
HttpPost httppost = new HttpPost("http://localhost/action.do");
httppost.setEntity(entity);

*/

/*

List<NameValuePair> formparams = new ArrayList<NameValuePair>();

formparams.add(new BasicNameValuePair("param1", "value1"));
formparams.add(new BasicNameValuePair("param2", "value2"));

UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

HttpPost httppost = new HttpPost("http://localhost/handler.do");

httppost.setEntity(entity);

*/

/*
In the following example the request configuration set by the initial request will be kept in the execution context and get propagated to the consecutive requests sharing the same context.

CloseableHttpClient httpclient = HttpClients.createDefault();

RequestConfig requestConfig = RequestConfig.custom()
 .setSocketTimeout(1000)
 .setConnectTimeout(1000)
 .build();

HttpGet httpget1 = new HttpGet("http://localhost/1");

httpget1.setConfig(requestConfig);

CloseableHttpResponse response1 = httpclient.execute(httpget1, context);

try {
 HttpEntity entity1 = response1.getEntity();
} finally {
 response1.close();
}

HttpGet httpget2 = new HttpGet("http://localhost/2");

CloseableHttpResponse response2 = httpclient.execute(httpget2, context);

try {
 HttpEntity entity2 = response2.getEntity();
} finally {
 response2.close();
}

*/

/*
HttpClient assumes non-entity enclosing methods such as GET and HEAD to be idempotent and entity
enclosing methods such as POST and PUT to be not.

*/

/*
HTTP requests being executed by HttpClient can be aborted at any stage of execution
by invoking HttpUriRequest#abort() method. This method is thread-safe and can be called from any
thread.
*/

/*

HttpClientContext localContext = HttpClientContext.create();
localContext.setAttribute("count", count);
HttpGet httpget = new HttpGet("http://localhost/");
for (int i = 0; i < 10; i++) {
 CloseableHttpResponse response = httpclient.execute(httpget, localContext);
 try {
 HttpEntity entity = response.getEntity();
 } finally {
 response.close();
 }
}

*/

/*
BasicHttpClientConnectionManager is a simple connection manager that maintains only one
connection at a time. Even though this class is thread-safe it ought to be used by one execution thread
only. BasicHttpClientConnectionManager will make an effort to reuse the connection for subsequent
requests with the same route.
*/

/*

Chapter 5. Fluent API
5.1. Easy to use facade API

As of version of 4.2 HttpClient comes with an easy to use facade API based on the concept of a fluent
interface. Fluent facade API exposes only the most fundamental functions of HttpClient and is intended
for simple use cases that do not require the full flexibility of HttpClient. For instance, fluent facade
API relieves the users from having to deal with connection management and resource deallocation.
Here are several examples of HTTP requests executed through the HC fluent API
// Execute a GET with timeout settings and return response content as String.
Request.Get("http://somehost/")
 .connectTimeout(1000)
 .socketTimeout(1000)
 .execute().returnContent().asString();

// Execute a POST with the 'expect-continue' handshake, using HTTP/1.1,
// containing a request body as String and return response content as byte array.
Request.Post("http://somehost/do-stuff")
 .useExpectContinue()
 .version(HttpVersion.HTTP_1_1)
 .bodyString("Important stuff", ContentType.DEFAULT_TEXT)
 .execute().returnContent().asBytes();

// Execute a POST with a custom header through the proxy containing a request body
// as an HTML form and save the result to the file
Request.Post("http://somehost/some-form")
 .addHeader("X-Custom-header", "stuff")
 .viaProxy(new HttpHost("myproxy", 8080))
 .bodyForm(Form.form().add("username", "vip").add("password", "secret").build())
 .execute().saveContent(new File("result.dump"));

*/
