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

import com.tessoft.common.CommonWebViewClient;
import com.tessoft.common.Constants;
import com.tessoft.domain.APIResponse;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.activities.BaseActivity;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DriverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverFragment extends BaseFragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View rootView = null;
    private WebView webView = null;

    private OnFragmentInteractionListener mListener;

    public DriverFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DriverFragment newInstance() {
        DriverFragment fragment = new DriverFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapper = new ObjectMapper();

        getActivity().registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_REFRESH));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try
        {
            // Inflate the layout for this fragment
            if ( rootView == null )
            {
                rootView =  inflater.inflate(R.layout.fragment_driver, container, false);

                CommonWebViewClient commonWebViewClient = new CommonWebViewClient((BaseActivity) getActivity());

                webView = (WebView) rootView.findViewById(R.id.webView);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebChromeClient(new WebChromeClient() {});
                webView.setBackgroundColor(0);
                webView.addJavascriptInterface(commonWebViewClient, "Android");
                webView.loadUrl(Constants.getServerURL() + "/driver/driverMain.do?isApp=Y&userID=" +
                        java.net.URLEncoder.encode( application.getLoginUser().getUserID(), "utf-8") );
                webView.setWebViewClient(commonWebViewClient);

            }
        }
        catch( Exception ex )
        {
            showToastMessage( ex.getMessage() );
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

    @Override
    public void doPostTransaction(int requestCode, Object result) {
        try
        {
            if ( Constants.FAIL.equals(result) )
            {
                showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
                return;
            }

            super.doPostTransaction(requestCode, result);

            APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

            if ( "0000".equals(response.getResCode()) )
            {

            }
        }
        catch( Exception ex )
        {
            application.catchException(this, ex);
        }
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(Constants.BROADCAST_REFRESH))
                    webView.reload();
            } catch (Exception ex) {
                catchException(this, ex);
            }
        }
    };

    public boolean canGoBack()
    {
        return webView.canGoBack();
    }

    public void goBack()
    {
        webView.goBack();
    }
}
