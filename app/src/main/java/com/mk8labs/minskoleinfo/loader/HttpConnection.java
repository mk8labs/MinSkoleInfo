package com.mk8labs.minskoleinfo.loader;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mk8labs.minskoleinfo.Log;
import com.mk8labs.minskoleinfo.PrefsMgr;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class HttpConnection {

	private static Context ctx = null;
	private static Boolean isInitialized = false;
	private static Boolean isLoggedOn = false;

	private final static String URLPREFIX = "https://";
	private final static String BASEPATH = "/parent/";
	private static String glbCurrentPath = "";
	private final static String LOGINURL="/Account/IdpLogin";

	private static CookieManager cookieManager = new CookieManager();

	private static String uri;
	private static String uid;
	private static String password;

	public static void open(Context context) {

		ctx = context;
		uri = PrefsMgr.getBaseURL(ctx);
		uid = PrefsMgr.getUserId(ctx);
		password = PrefsMgr.getPassword(ctx);

		CookieHandler.setDefault(cookieManager);

		isInitialized = true;
	}

	public static void close() {

	}
	
	private static HttpURLConnection connectWithRedirects(HttpURLConnection conn ) {
		
		
		int respCode;
		try {
			conn.connect();
			respCode = conn.getResponseCode();
			if (HttpsURLConnection.HTTP_OK==respCode) {
				return conn;
			}
			while (HttpsURLConnection.HTTP_MOVED_PERM==respCode
					 ||HttpsURLConnection.HTTP_MOVED_TEMP==respCode) {
				
				String urlString=conn.getHeaderField("Location");
				Log.v("Redir to: " + urlString);
				conn=getAbsoluteConnection(urlString, Method.GET);
				conn.connect();
				respCode=conn.getResponseCode();
				Log.v("Redir: " + respCode);
			}
			
		} catch (IOException e) {
			return null;
		}
		
		return conn;
	}

	private static boolean logon() {

		Log.v("login");
		// initialize
		if (!isInitialized)
			throw new Error();
		isLoggedOn = false;

		try {
			// perform get to set connection cookie
			HttpURLConnection conn = getRelativeConnection("", Method.GET);
//			conn.connect();
			conn=connectWithRedirects(conn);
			if (null==conn || 200!=conn.getResponseCode()) return false;
			
			String path=conn.getURL().getPath();
			if ("/sso/ssocomplete".equalsIgnoreCase(path)) {
				conn=logonHandleSSOComplete(conn);
			} else {
				conn=logonHandleIdpLogin(conn);
				conn=logonHandleSSOComplete(conn);
			}

			if (conn!=null && HttpsURLConnection.HTTP_OK==conn.getResponseCode()) {
			
				isLoggedOn=true;
				//set initial student context
				if (glbCurrentPath.length()==0) {
					String nn=conn.getURL().getPath(); // value= /parent/4564/Jens/Index
					glbCurrentPath=nn.replace("Index", "");
				}
			}
//			dumpCookies();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isLoggedOn;
	}

	private static HttpURLConnection logonHandleIdpLogin(HttpURLConnection conn) {
	
		try {
			//get token from login form
			Document reqdoc=Jsoup.parse(conn.getInputStream(),"UTF8","");
			Elements elmss=reqdoc.select("input[name=__RequestVerificationToken]");
			String token=elmss.get(0).attr("value");
			conn.disconnect();

			// prepare connection
			conn = getRelativeConnection(LOGINURL, Method.POST);

			// prepare login form data
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair("RoleType", "Parent"));
//			nameValuePairs.add(new BasicNameValuePair("__RequestVerificationToken", token));
//			nameValuePairs.add(new BasicNameValuePair("UserName", uid));
//			nameValuePairs.add(new BasicNameValuePair("Password", password));
//			UrlEncodedFormEntity form = new UrlEncodedFormEntity(nameValuePairs);

            HashMap<String, String> nameValuePairs=new HashMap<String, String>();
			nameValuePairs.put("RoleType", "Parent");
			nameValuePairs.put("__RequestVerificationToken", token);
			nameValuePairs.put("UserName", uid);
			nameValuePairs.put("Password", password);
            String form = getPostDataString(nameValuePairs);

			// send request
			OutputStream os = conn.getOutputStream();
            os.write(form.getBytes());
			os.close();
			return connectWithRedirects(conn);			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    private static HttpURLConnection logonHandleSSOComplete(HttpURLConnection conn) {

		try {
			// read the response to get the SAML assertion
			Document doc=Jsoup.parse(conn.getInputStream(),"UTF8", "");
			conn.disconnect();
			
			//get the SAML action and assertion
			Elements elm = doc.select("input[name=SAMLResponse]");
			String samlAssert=elm.get(0).attr("value");
			elm = doc.select("form");
			String action=elm.get(0).attr("action");
			
			//POST the SAML to logon, get main page
			conn=getAbsoluteConnection(action, Method.POST);
		
			// prepare login form data
			//List<NameValuePair> samlnameValuePairs = new ArrayList<NameValuePair>();
            //samlnameValuePairs.add(new BasicNameValuePair("SAMLResponse", samlAssert));
			//UrlEncodedFormEntity samlform = new UrlEncodedFormEntity(samlnameValuePairs);

            HashMap<String, String> samlnameValuePairs=new HashMap<String, String>();
            samlnameValuePairs.put("SAMLResponse", samlAssert);
            String samlform = getPostDataString(samlnameValuePairs);

			// send request
			OutputStream samlos = conn.getOutputStream();
			//samlform.writeTo(samlos);
            samlos.write(samlform.getBytes());
			samlos.close();
			
			conn.connect();
			
			Log.v("resp:" + conn.getResponseCode());

			return conn;
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		}

		return null;
		
	}

	public static void setStudentContext(String ID) {
		
		String name = PrefsMgr.getStudentName(ctx, ID).split(" ")[0];
		glbCurrentPath = BASEPATH + ID + "/" + name + "/";
		
//		try {
//			if (!isLoggedOn) {
//				logon(); 
//			}
//			HttpURLConnection conn=getRelativeConnection(glbCurrentPath + "Index", Method.GET);
//			conn.connect();
//			int res=conn.getResponseCode();
//			conn.disconnect();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}



	private enum Method {
		GET, POST
	};

	private static HttpURLConnection getRelativeConnection(String page, Method method) throws IOException {
		return getAbsoluteConnection(URLPREFIX + uri + page, method);
	}

	private static HttpURLConnection getAbsoluteConnection(String urlString, Method method)
			throws IOException {

		Log.v("URL: " + urlString);
		URL url = new URL(urlString);
		
		//TODO debug
//		trustEveryone();
//		Proxy proxy = new Proxy( Proxy.Type.HTTP, new InetSocketAddress("192.168.17.187", 8888));
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
		//debug
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		

		// setup connection
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setDoInput(true);

		if (method == Method.GET) {
			conn.setRequestMethod("GET");
			conn.setDoOutput(false);
		} else {
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
		}
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible )");
		conn.setRequestProperty("Accept", "*/*");

		// conn.setRequestProperty("User-Agent",
		// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36");
		// conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

		return conn;
	}

	 @SuppressLint("TrulyRandom")
	private static void trustEveryone() {
		 try {
			 HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
				 public boolean verify(String hostname, SSLSession session) {
					 return true;
				 }
			 });
			 SSLContext context = SSLContext.getInstance("TLS");
			 context.init(null, new X509TrustManager[]{new X509TrustManager(){
				 public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
			 public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			 public X509Certificate[] getAcceptedIssuers() {
				 return new X509Certificate[0];
			 }}}, new SecureRandom());
			 
			 HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		 } catch (Exception e) { // should never happen
		 e.printStackTrace();
		 }
	 }

	 
	public static InputStream get(String page, EventNotifier notif) {
		return get(page, notif, false);
	}
	 
    public static InputStream get(String page, EventNotifier notif, boolean accept302) {
    	return get(page,true,notif,accept302);
	}
	
	/**
	 * 
	 * 
	 * @param page
	 * @param notif
	 * @return
	 */
	public static InputStream getAbsolute(String page, EventNotifier notif) {
		return getAbsolute(page, notif, false);
	}
    public static InputStream getAbsolute(String page, EventNotifier notif, boolean accept302) {
    	return get(page,false,notif,accept302);
	}

	private static InputStream get(String page,boolean relativeURL, EventNotifier notif,	boolean accept302) {

		if (!isLoggedOn) {
			notif.onWorkInfo("Logger på FI");
			if (!logon()) {
				notif.onWorkInfo("Fejl ved logon til FI");
				return null;
			}
		}

		try {
			HttpURLConnection conn=null;
			if (relativeURL) {
				conn = getRelativeConnection(glbCurrentPath + page, Method.GET);
			} else {
				conn = getRelativeConnection(page, Method.GET);
			}

			conn.connect();
			int res = conn.getResponseCode();
			Log.v("get res: " + res);

			if (res == 200 || (accept302 && res == 302)) {
				return getData(conn, notif);

			} else {
				// retry with a logon in case the session has expired
				logon();
				conn = getRelativeConnection(glbCurrentPath + page, Method.GET);
				Log.v("get (retry): " + conn.getURL());

				conn.connect();

				if (res == 200 || (accept302 && res == 302)) {
					return getData(conn, notif);
				}
				notif.onWorkInfo("Der er opstået en fejl ved læsning :" + res);
			}
		} catch (SocketTimeoutException e) {
			notif.onWorkInfo("Timeout");
		} catch (IOException e) {
			notif.onWorkInfo("Der er opstået en fejl ved læsning :"
					+ e.getMessage());
		}
		return null;

	}

	private static InputStream getData(HttpURLConnection conn,
			EventNotifier notif) {

		try {
			return conn.getInputStream();
		} catch (IOException e) {
		}
		return null;
	}

	@SuppressWarnings("unused")
	private static void dumpCookies() {
		Log.v("COOKIES: ");

		List<HttpCookie> cookieJar = cookieManager.getCookieStore()
				.getCookies();
		Iterator<HttpCookie> iter = cookieJar.iterator();
		while (iter.hasNext()) {
			HttpCookie cookie = iter.next();
			Log.v("  " + cookie.toString());
		}

	}
}
