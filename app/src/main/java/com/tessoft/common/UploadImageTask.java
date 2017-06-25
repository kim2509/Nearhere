package com.tessoft.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadImageTask extends AsyncTask<Bitmap, Void, String> {

	Context context = null;
	String requestInfo = "";
	private TransactionDelegate delegate;
	int requestCode = 0;

	public UploadImageTask(Context context, String requestInfo, int requestCode, TransactionDelegate delegate)
	{
		this.context = context;
		this.requestInfo = requestInfo;
		this.delegate = delegate;
		this.requestCode = requestCode;
	}

	protected String doInBackground(Bitmap... bitmaps) {
		if (bitmaps[0] == null)
			return null;
		//		setProgress(0);

		Bitmap bitmap = bitmaps[0];
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
		InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream

		DefaultHttpClient httpclient = new DefaultHttpClient();
		String responseString = "";

		try {
			HttpPost httppost = new HttpPost(
					Constants.getServerURL() + "/cafe/uploadImageFile.do"); // server

			MultipartEntity reqEntity = new MultipartEntity();

			reqEntity.addPart("requestInfo", requestInfo );
			reqEntity.addPart("file", "image", in);
			httppost.setEntity(reqEntity);

			HttpResponse response = null;
			response = httpclient.execute(httppost);
			responseString = EntityUtils.toString(response.getEntity());
			
			if ( response.getStatusLine().getStatusCode() != 200 )
			{
				throw new Exception("업로드중 오류");
			}
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			return Constants.FAIL;
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		return responseString;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		delegate.doPostTransaction( requestCode, result );

	}
}