package com.tessoft.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tessoft.nearhere.R;
import com.tessoft.nearhere.ShareMyLocationActivity;
import com.tessoft.nearhere.ShareRealtimeMyLocationActivity;
import com.tessoft.nearhere.fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View rootView = null;

    private OnFragmentInteractionListener mListener;

    public LocationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_location, container, false);

        Button btnOneTime = (Button) rootView.findViewById(R.id.btnOneTime);
        btnOneTime.setOnClickListener(this);

        Button btnRealTime = (Button) rootView.findViewById(R.id.btnRealTime);
        btnRealTime.setOnClickListener(this);
        return rootView;
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
        if ( v.getId() == R.id.btnOneTime )
            goShareMyLocationActivity();
        else if ( v.getId() == R.id.btnRealTime )
            goShareRealtimeMyLocationActivity();
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

    public void goShareMyLocationActivity()
    {
        Intent intent = new Intent( getActivity(), ShareMyLocationActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }

    public void goShareRealtimeMyLocationActivity() {
        Intent intent = new Intent( getActivity(), ShareRealtimeMyLocationActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }
}
