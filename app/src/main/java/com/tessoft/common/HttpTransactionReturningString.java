package com.tessoft.common;

import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class HttpTransactionReturningString extends AsyncTask<Object, Integer, String> {

	private String url = "";
	int requestCode = 0;
	private TransactionDelegate delegate;
	
	public HttpTransactionReturningString( TransactionDelegate delegate, String url, int requestCode )
	{
		this.delegate = delegate;
		this.url = url;
		this.requestCode = requestCode;
	}

	protected void onPreExecute() {

	}
	
	private HttpClient getHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SFSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

	protected String doInBackground( Object... data ) {

		try
		{
//			HttpClient client = new DefaultHttpClient();
			HttpClient client = getHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
			HttpResponse response;
			
			Object json = ( data == null || data.length == 0 || data[0] == null ) ? "" : data[0];
			
			String serverURL = getServerURL();
			
			Log.d("HTTPRequest", "Connecting to URL:" + serverURL );
			
			HttpPost post = new HttpPost( serverURL );
			
			Log.d("HTTPRequest", "Request String:" + json.toString() );
			
			StringEntity se = new StringEntity( json.toString(), "UTF-8");
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8"));
			post.setEntity(se);
			response = client.execute(post);

			String responseString = EntityUtils.toString(response.getEntity());

			Log.d("HTTPRequest", "Successfully got response!!" );
			
			Log.d("HTTPRequest", "ResponseString:" + responseString );
			
			return responseString;

		}
		catch(Exception e){
			e.printStackTrace();
			
			Log.e("HTTPRequest", "Error while connecting to server." );
			
			writeLog( e.getMessage() );

			return Constants.FAIL;
		}
	}

	private String getServerURL() throws Exception 
	{
		if ( url == null || url.isEmpty() ) throw new Exception("요청 URL 이 올바르지 않습니다.");
		
		if ( Constants.bReal == false )
			return Constants.getServerURL() + url;
		
		if ( url.indexOf("/taxi/getRandomID.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		if ( url.indexOf("/taxi/getRandomIDV2.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/insertTermsAgreement.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/login.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/logout.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/getUserInfo.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/updateUserJobTitle.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/updateUserBirthday.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/updateUserInfo.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/getUserPushMessage.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/getUserMessageList.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/getUserMessage.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else if ( url.indexOf("/taxi/sendUserMessage.do") >= 0 )
			return Constants.getServerSSLURL() + url;
		else
			return Constants.getServerURL() + url;
	}

	protected void onProgressUpdate(Integer... progress) {

	}

	protected void onPostExecute(String result) {

		if ( delegate != null )
			delegate.doPostTransaction( requestCode, result );
		
	}

	public void writeLog( String log )
	{
		Log.e("FavorForMe", log );
	}
}
