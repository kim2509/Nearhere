package com.tessoft.nearhere.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.tessoft.common.CommonWebViewClient;
import com.tessoft.common.Constants;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.activities.BaseActivity;
import com.tessoft.nearhere.activities.MainActivity;
import com.tessoft.nearhere.activities.PopupWebViewActivity;
import com.tessoft.nearhere.activities.SearchMapActivity;
import com.tessoft.nearhere.activities.UserProfileActivity;

import java.net.URLEncoder;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CarPoolTaxiFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CarPoolTaxiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarPoolTaxiFragment extends BaseFragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    protected View rootView = null;
    private View searchHeader = null;
    private WebView webView = null;
    EditText edtSearchDestination = null;

    private OnFragmentInteractionListener mListener;

    public CarPoolTaxiFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CarPoolTaxiFragment newInstance() {
        CarPoolTaxiFragment fragment = new CarPoolTaxiFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_REFRESH));
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_OPEN_SEARCH_PAGE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try
        {
            // Inflate the layout for this fragment
            if ( rootView == null )
            {
                rootView = inflater.inflate(R.layout.fragment_car_pool_taxi, container, false);

                CommonWebViewClient commonWebViewClient = new CommonWebViewClient((BaseActivity) getActivity());

                webView = (WebView) rootView.findViewById(R.id.webView);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebChromeClient(new WebChromeClient() {

                });

                webView.addJavascriptInterface(commonWebViewClient, "Android");

                String snsLoginYN = "snsLogin=N";

                if ( "Guest".equals(application.getLoginUser().getType()))
                    snsLoginYN = "snsLogin=Y";

                webView.loadUrl( Constants.getServerURL() + "/taxi/index.do?isApp=Y&" + snsLoginYN );
                webView.setWebViewClient( new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // Here put your code

                        if ( url.startsWith("nearhere://openURL?") )
                        {
                            Intent intent = new Intent(getActivity(), PopupWebViewActivity.class);

                            String params = url.substring(url.indexOf("?") + 1);

                            String[] paramAr = params.split("&");
                            for ( int i = 0; i < paramAr.length; i++ )
                            {
                                if ( paramAr[i].indexOf("=") >= 0 && paramAr[i].split("=").length > 1) {
                                    String key = paramAr[i].split("=")[0];
                                    String value = paramAr[i].split("=")[1];

                                    if (key != null && key.equals("title")) {
                                        intent.putExtra("title", java.net.URLDecoder.decode(value) );
                                    }
                                    else if (key != null && key.equals("url")) {
                                        intent.putExtra("url", java.net.URLDecoder.decode( value ));
                                    }
                                    else if (key != null && key.equals("showNewButton")) {
                                        intent.putExtra("showNewButton", value );
                                    }
                                }
                            }

                            startActivity(intent);
                        }
                        else if ( url.startsWith("nearhere://openUserProfile?userID=") )
                        {
                            String params = url.substring( url.indexOf("?") + 1);
                            String[] paramAr = params.split("&");
                            for ( int i = 0; i < paramAr.length; i++ )
                            {
                                if ( paramAr[i].indexOf("=") >= 0 && paramAr[i].split("=").length > 1)
                                {
                                    String key = paramAr[i].split("=")[0];
                                    String value = paramAr[i].split("=")[1];

                                    if ( key != null && key.equals("userID"))
                                    {
                                        Intent intent = new Intent( getActivity(), UserProfileActivity.class);
                                        intent.putExtra("userID", value );
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
                                    }
                                }
                            }
                        }
                        else if ( url.startsWith("nearhere://snsLogin") )
                        {
                            showYesNoDialog("확인", "SNS 계정으로 로그인하시겠습니까?", "snsLogin" );
                        }
                        else if ( url.startsWith("nearhere://showOKDialog?") )
                        {
                            String title = "";
                            String message = "";
                            String param = "";

                            String params = url.substring( url.indexOf("?") + 1);
                            String[] paramAr = params.split("&");
                            for ( int i = 0; i < paramAr.length; i++ )
                            {
                                if ( paramAr[i].indexOf("=") >= 0 && paramAr[i].split("=").length > 1)
                                {
                                    String key = paramAr[i].split("=")[0];
                                    String value = paramAr[i].split("=")[1];

                                    if ( key != null && key.equals("title"))
                                        title = java.net.URLDecoder.decode(value);
                                    if ( key != null && key.equals("message"))
                                        message = java.net.URLDecoder.decode(value);
                                    if ( key != null && key.equals("param"))
                                        param = java.net.URLDecoder.decode(value);
                                }
                            }

                            showOKDialog( title, message, param );
                        }
                        else
                        {
                            Intent intent = new Intent(getActivity(), PopupWebViewActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }

                        // return true; //Indicates WebView to NOT load the url;
                        return true; //Allow WebView to load url
                    }
                });
                edtSearchDestination = (EditText) rootView.findViewById(R.id.edtSearchDestination );
                edtSearchDestination.setOnClickListener(this);
            }
        }
        catch( Exception ex )
        {
            application.catchException(this , ex );
        }

        return rootView;
    }

    @Override
    public void onResume() {

        try
        {
            super.onResume();
        }
        catch( Exception ex )
        {

        }
    }

    @Override
    public void onPause() {
        try {
            super.onPause();
        }
        catch( Exception ex )
        {

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        try
        {
            if ( v.getId() == R.id.edtSearchDestination )
            {
                Intent intent = new Intent( getActivity(), SearchMapActivity.class );
                intent.putExtra("broadcastKey", Constants.BROADCAST_OPEN_SEARCH_PAGE);
                startActivity(intent);
            }
        }
        catch( Exception ex )
        {
            application.catchException(this, ex);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mMessageReceiver);
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                if ( intent.getAction().equals(Constants.BROADCAST_REFRESH))
                    webView.reload();
                else if ( intent.getAction().equals(Constants.BROADCAST_OPEN_SEARCH_PAGE))
                {
                    if ( intent.getExtras() != null && intent.getExtras().containsKey("resultData") )
                    {
                        HashMap param = (HashMap) intent.getExtras().get("resultData");
                        String latitude = param.get("latitude").toString();
                        String longitude = param.get("longitude").toString();
                        String address = param.get("address").toString();

                        String url = Constants.getServerURL() + "/taxi/searchDestination.do?latitude=" +
                                latitude + "&longitude=" + longitude + "&address=" + URLEncoder.encode(address, "utf-8");
                        openPopupWebViewActivity("검색결과", url);
                    }
                }
            }
            catch( Exception ex )
            {
                catchException(this, ex);
            }
        }
    };

    public void openPopupWebViewActivity( String title, String url )
    {
        Intent intent = new Intent(getActivity(), PopupWebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    @Override
    public void yesClicked(Object param) {

        if ( "snsLogin".equals( param ))
        {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.yesClicked("logout");
        }

        super.yesClicked(param);
    }
}
