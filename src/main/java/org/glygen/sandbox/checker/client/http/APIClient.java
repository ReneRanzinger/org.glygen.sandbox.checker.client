package org.glygen.sandbox.checker.client.http;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class APIClient
{
    private String m_baseUrl = null;
    private BasicCookieStore m_cookieStore = null;
    private CloseableHttpClient m_httpclient = null;

    public APIClient(String a_baseURL) throws ClientProtocolException, IOException
    {
        this.m_baseUrl = a_baseURL;
        this.connect();
    }

    private void connect() throws ClientProtocolException, IOException
    {
        int timeout = 90;
        RequestConfig t_config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD)
                .setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();
        // configuration for self certificate
        SSLConnectionSocketFactory sslsf = null;
        try
        {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, (chain, authType) -> true);
            sslsf = new SSLConnectionSocketFactory(builder.build());
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
        // create cookie store and HTTP client
        this.m_cookieStore = new BasicCookieStore();
        this.m_httpclient = HttpClients.custom().setDefaultCookieStore(this.m_cookieStore)
                .setSSLSocketFactory(sslsf).setDefaultRequestConfig(t_config).build();
    }

    public void close() throws IOException
    {
        this.m_httpclient.close();
    }

    public String entityToString(HttpEntity a_entity)
            throws UnsupportedOperationException, IOException
    {
        StringWriter t_writer = new StringWriter();
        IOUtils.copy(a_entity.getContent(), t_writer, StandardCharsets.UTF_8);
        return t_writer.toString();
    }

    public WebResponse checkGlycanGet(String a_glycoCT, String a_tree, Boolean a_enzymes,
            Boolean a_relatedGlycans, Boolean a_debug) throws IOException
    {
        try
        {
            // build request
            HttpGet t_httpGet = new HttpGet(this.m_baseUrl);
            // parameter
            List<NameValuePair> t_nameValuePairs = new ArrayList<>();
            if (a_glycoCT != null)
            {
                t_nameValuePairs.add(new BasicNameValuePair("glycoct", a_glycoCT));
            }
            if (a_tree != null)
            {
                t_nameValuePairs.add(new BasicNameValuePair("type", a_tree));
            }
            if (a_enzymes != null)
            {
                t_nameValuePairs
                        .add(new BasicNameValuePair("enz", this.booleanToString(a_enzymes)));
            }
            if (a_relatedGlycans != null)
            {
                t_nameValuePairs.add(
                        new BasicNameValuePair("related", this.booleanToString(a_relatedGlycans)));
            }
            if (a_debug != null)
            {
                t_nameValuePairs
                        .add(new BasicNameValuePair("debug", this.booleanToString(a_debug)));
            }
            // URL encoding of parameters
            URI t_uri = new URIBuilder(t_httpGet.getURI()).addParameters(t_nameValuePairs).build();
            t_httpGet.setURI(t_uri);
            System.out.println(t_uri.toString());
            // execute request
            CloseableHttpResponse t_response = this.m_httpclient.execute(t_httpGet);
            HttpEntity t_entity = t_response.getEntity();
            // extract response
            String t_responseContent = this.entityToString(t_entity);
            WebResponse t_result = new WebResponse(t_responseContent,
                    t_response.getStatusLine().getStatusCode());
            // close response
            EntityUtils.consume(t_entity);
            t_response.close();
            return t_result;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }

    }

    public WebResponse checkGlycanPost(String a_glycoCT, String a_tree, Boolean a_enzymes,
            Boolean a_relatedGlycans, Boolean a_debug) throws IOException
    {
        try
        {
            // build request
            HttpPost t_httpPost = new HttpPost(this.m_baseUrl);
            // parameter
            List<NameValuePair> t_nameValuePairs = new ArrayList<>();
            if (a_glycoCT != null)
            {
                t_nameValuePairs.add(new BasicNameValuePair("glycoct", a_glycoCT));
            }
            if (a_tree != null)
            {
                t_nameValuePairs.add(new BasicNameValuePair("type", a_tree));
            }
            if (a_enzymes != null)
            {
                t_nameValuePairs
                        .add(new BasicNameValuePair("enz", this.booleanToString(a_enzymes)));
            }
            if (a_relatedGlycans != null)
            {
                t_nameValuePairs.add(
                        new BasicNameValuePair("related", this.booleanToString(a_relatedGlycans)));
            }
            if (a_debug != null)
            {
                t_nameValuePairs
                        .add(new BasicNameValuePair("debug", this.booleanToString(a_debug)));
            }
            // URL encoding of parameters
            t_httpPost
                    .setEntity(new UrlEncodedFormEntity(t_nameValuePairs, StandardCharsets.UTF_8));
            // execute request
            CloseableHttpResponse t_response = this.m_httpclient.execute(t_httpPost);
            HttpEntity t_entity = t_response.getEntity();
            // extract response
            String t_responseContent = this.entityToString(t_entity);
            WebResponse t_result = new WebResponse(t_responseContent,
                    t_response.getStatusLine().getStatusCode());
            // close response
            EntityUtils.consume(t_entity);
            t_response.close();
            return t_result;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }

    }

    private String booleanToString(Boolean a_boolean)
    {
        if (a_boolean)
        {
            return "true";
        }
        return "false";
    }

}
