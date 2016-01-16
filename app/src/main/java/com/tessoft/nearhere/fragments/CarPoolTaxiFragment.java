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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.tessoft.common.Constants;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.activities.PopupWebViewActivity;
import com.tessoft.nearhere.activities.SearchMapActivity;

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
                webView = (WebView) rootView.findViewById(R.id.webView);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl( Constants.getServerURL() + "/taxi/index.do");
                webView.setWebViewClient( new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // Here put your code
                        Intent intent = new Intent(getActivity(), PopupWebViewActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
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
                webView.reload();
            }
            catch( Exception ex )
            {
                catchException(this, ex);
            }
        }
    };
}
