package com.tessoft.nearhere.activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.tessoft.common.CommonWebViewClient;
import com.tessoft.common.Constants;
import com.tessoft.common.UploadImageTask;
import com.tessoft.common.Util;
import com.tessoft.domain.User;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.fragments.DatePickerFragment;
import com.tessoft.nearhere.fragments.TimePickerFragment;

import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PopupWebViewActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener{

    private WebView webView = null;
    private String pageID = "";
    private int RESULT_LOAD_IMAGE = 3;
    private int REQUEST_IMAGE_CROP = 4;
    private int IMAGE_UPLOAD = 5;

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                if ( Constants.BROADCAST_REFRESH.equals( intent.getAction() ) )
                {
                    webView.reload();
                }
                else if ( Constants.BROADCAST_REFRESH_PAGE.equals( intent.getAction() ))
                {
                    if ( intent.getExtras() != null && pageID.equals(intent.getExtras().get("broadcastParam")) )
                        webView.reload();
                }
                else if ( Constants.BROADCAST_FINISH_ACTIVITY.equals( intent.getAction() ))
                {
                    if ( intent.getExtras() != null && pageID.equals(intent.getExtras().get("broadcastParam")) )
                        finish();
                }
            }
            catch( Exception ex )
            {
                catchException(this, ex);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_popup_web_view);

            CommonWebViewClient commonWebViewClient = new CommonWebViewClient(this, application );

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.setInitialScale(1);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);

            webView.setWebChromeClient(new WebChromeClient() {

            });

            webView.setBackgroundColor(0);

            if (getIntent() != null && getIntent().getExtras() != null )
            {
                if ( getIntent().getExtras().containsKey("title") )
                    setTitle( getIntent().getExtras().get("title").toString() );

                if ( getIntent().getExtras().containsKey("fullURL") ) {
                    String url = getIntent().getExtras().getString("fullURL");
                    webView.loadUrl( url );
                }
                else if ( getIntent().getExtras().containsKey("url") ) {
                    String url = getIntent().getExtras().getString("url");

                    if ( url.indexOf("?") >= 0 )
                        webView.loadUrl( url + "&isApp=Y");
                    else
                        webView.loadUrl( getIntent().getExtras().getString("url") + "?isApp=Y");
                }

                if ( getIntent().getExtras().containsKey("showNewButton") )
                {
                    if ( "Y".equals( getIntent().getExtras().getString("showNewButton") ) ) {
                        findViewById(R.id.btnAddPost).setVisibility(ViewGroup.VISIBLE);
                        Button btnAddPost = (Button) findViewById(R.id.btnAddPost);
                        btnAddPost.setOnClickListener(this);
                    }
                    else
                        findViewById(R.id.btnAddPost).setVisibility(ViewGroup.GONE);
                }
                else
                    findViewById(R.id.btnAddPost).setVisibility(ViewGroup.GONE);

                if ( !getIntent().getExtras().containsKey("disableWebViewClient")) {
                    webView.setWebViewClient(commonWebViewClient);
                    webView.addJavascriptInterface(commonWebViewClient, "Android");
                }

                if ( "Y".equals( getIntent().getExtras().getString("titleBarHidden") ) )
                    findViewById(R.id.titleBar).setVisibility(ViewGroup.GONE);
                else
                    findViewById(R.id.titleBar).setVisibility(ViewGroup.VISIBLE);

                if ( getIntent().getExtras().containsKey("pageID") )
                    pageID =  getIntent().getExtras().get("pageID").toString();
            }

            Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(this);

            registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_REFRESH));
            registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_REFRESH_PAGE));
            registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_FINISH_ACTIVITY));
        }
        catch( Exception ex )
        {
            catchException(this, ex);
        }
    }

    @Override
    public void onBackPressed() {
        try
        {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        }
        catch( Exception ex )
        {

        }
    }

    @Override
    public void onClick(View v) {
        try {
            if ( v.getId() == R.id.btnRefresh )
                webView.reload();
            else if ( v.getId() == R.id.btnAddPost )
            {
                webView.loadUrl("javascript:getNewPostURL();");
            }
        }
        catch( Exception ex )
        {
            catchException(this, ex);
        }
    }

    @Override
    public void doAction(String actionName, Object param) {
        super.doAction(actionName, param);

        if ( Constants.ACTION_SET_NEW_POST_URL.equals( actionName ) )
        {
            Intent intent = new Intent(this, NewTaxiPostActivity2.class);
            intent.putExtra("url", param.toString() );
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
        }
        else if ( "showNewButton".equals( actionName ) )
        {
            if ( "Y".equals( param ) )
                findViewById(R.id.btnAddPost).setVisibility(ViewGroup.VISIBLE);
            else
                findViewById(R.id.btnAddPost).setVisibility(ViewGroup.GONE);
        }
        else if ("finishActivity".equals( actionName ))
        {
            if ( !Util.isEmptyString( param.toString() ) )
            {
                sendBroadcast( new Intent( param.toString() ) );
            }

            finish();
        }
        else if ("finishActivity2".equals(actionName))
        {
            if ( !Util.isEmptyString( param.toString() ) )
            {
                handleJSONParam( param.toString() );
            }

            finish();
        }
        else if ("sendBroadcasts".equals(actionName))
        {
            if ( !Util.isEmptyString( param.toString() ) )
            {
                handleJSONParam( param.toString() );
            }
        }
        else if ("selectPhotoUpload".equals(actionName))
        {
            if ( !Util.isEmptyString( param.toString() ) )
            {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        }
        else if ( Constants.SHOW_PROGRESS_BAR.equals( actionName ) )
            findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
        else if ( Constants.HIDE_PROGRESS_BAR.equals( actionName ) )
            findViewById(R.id.marker_progress).setVisibility(ViewGroup.GONE);
        else if ( Constants.ACTION_OPEN_DATE_PICKER.equals( actionName ) )
        {
            DialogFragment newFragment = new DatePickerFragment( this );
            newFragment.show(getFragmentManager(), "datePicker");
        }
        else if ( Constants.ACTION_OPEN_TIME_PICKER.equals( actionName ) )
        {
            DialogFragment newFragment = new TimePickerFragment( this );
            newFragment.show(getFragmentManager(), "timePicker");
        }
    }

    public void handleJSONParam( String jsonString )
    {
        try
        {
            HashMap param = mapper.readValue(jsonString, new TypeReference<HashMap>(){});

            if ( !Util.isEmptyForKey(param, "broadcastList") )
            {
                List<HashMap> broadcastList = (List<HashMap>) param.get("broadcastList");

                for ( int i = 0; i < broadcastList.size(); i++ )
                {
                    Intent intent = new Intent( Util.getStringFromHash( broadcastList.get(i), "broadcastName") );
                    intent.putExtra("broadcastParam", Util.getStringFromHash( broadcastList.get(i), "broadcastParam") );
                    sendBroadcast(intent);
                }
            }
        }
        catch( Exception ex )
        {
            catchException(null, ex);
        }
    }

    @Override
    public void yesClicked(Object param) {

        if ( "snsLogin".equals( param ))
        {
            sendBroadcast(new Intent(Constants.BROADCAST_LOGOUT));
            finish();
        }

        super.yesClicked(param);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // TODO Auto-generated method stub
        String dateString = Util.getDateStringFromDate(new Date(year - 1900, monthOfYear, dayOfMonth), "yyyy-MM-dd");
        webView.loadUrl("javascript:onDateSet('" + dateString + "');");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        Date date = new Date();
        date.setHours( hourOfDay );
        date.setMinutes( minute );

        String timeString = Util.getDateStringFromDate( date, "HH:mm" );
        webView.loadUrl("javascript:onTimeSet('" + timeString + "');");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        try {

            if ( requestCode == RESULT_LOAD_IMAGE )
            {
                if( data != null && data.getData() != null && resultCode == RESULT_OK ) {

                    Uri _uri = data.getData();

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(_uri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    ExifInterface exif = new ExifInterface(picturePath);

                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    if(exifDegree != 0) {
                        Bitmap bitmap = getBitmap( picturePath);
                        Bitmap rotatePhoto = rotate(bitmap, exifDegree);
                        saveBitmap(rotatePhoto, picturePath );

                    }

                    cropImage( _uri );
                }

            }
            else if ( requestCode == REQUEST_IMAGE_CROP )
            {
                if ( data == null )
                    return;

                Bundle extras = data.getExtras();
                if(extras != null) {

                    Bitmap bitmap = (Bitmap)extras.get("data");
                    bitmap = resizeBitmapImageFn( bitmap, 1024 );

                    sendPhoto(bitmap);

                }
            }

        } catch (Exception ex) {
            catchException(null, ex);
        }
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public Bitmap getBitmap( String imagePath ) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inInputShareable = true;
        options.inDither=false;
        options.inTempStorage=new byte[32 * 1024];
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;

        File f = new File(imagePath);

        FileInputStream fs=null;
        try {
            fs = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }

        Bitmap bm = null;

        try {
            if(fs!=null) bm=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public static Bitmap rotate(Bitmap image, int degrees)
    {
        if(degrees != 0 && image != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float)image.getWidth(), (float)image.getHeight());

            try
            {
                Bitmap b = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);

                if(image != b)
                {
                    image.recycle();
                    image = b;
                }

                image = b;
            }
            catch(OutOfMemoryError ex)
            {
                ex.printStackTrace();
            }
        }
        return image;
    }

    public void saveBitmap(Bitmap bitmap, String mCurrentPhotoPath ) {
        File file = new File( mCurrentPhotoPath );
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) ;
        try {
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void cropImage(Uri contentUri) throws Exception{

        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(contentUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

    }

    /*
	 * 비트맵(Bitmap) 이미지의 가로, 세로 이미지를 리사이징
	 * @param bmpSource 원본 Bitmap 객체
	 * @param maxResolution 제한 해상도
	 * @return 리사이즈된 이미지 Bitmap 객체
	 */
    public Bitmap resizeBitmapImageFn(
            Bitmap bmpSource, int maxResolution){
        int iWidth = bmpSource.getWidth();      //비트맵이미지의 넓이
        int iHeight = bmpSource.getHeight();     //비트맵이미지의 높이
        int newWidth = iWidth ;
        int newHeight = iHeight ;
        float rate = 0.0f;

        //이미지의 가로 세로 비율에 맞게 조절
        if(iWidth > iHeight ){
            if(maxResolution < iWidth ){
                rate = maxResolution / (float) iWidth ;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        }else{
            if(maxResolution < iHeight ){
                rate = maxResolution / (float) iHeight ;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(
                bmpSource, newWidth, newHeight, true);
    }

    private void sendPhoto(Bitmap f) throws Exception {
        User user = application.getLoginUser();
        new UploadImageTask( this, user.getUserID() , IMAGE_UPLOAD, this ).execute(f);
    }

    @Override
    public void doPostTransaction(int requestCode, Object result) {

        try
        {
            super.doPostTransaction(requestCode, result);

            webView.loadUrl("javascript:onImageUploaded('" + result.toString() + "');");
        }
        catch( Exception ex )
        {
            catchException(null, ex);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mMessageReceiver);
    }
}
