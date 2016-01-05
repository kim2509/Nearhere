package com.tessoft.nearhere.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.tessoft.common.Constants;
import com.tessoft.domain.APIResponse;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.SearchMapActivity;
import com.tessoft.nearhere.adapters.CarPoolTaxiAdapter;

import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;

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
    protected ListView listMain = null;
    private View searchHeader = null;
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
                listMain = (ListView) rootView.findViewById(R.id.listMain);

                ArrayList<String> arGeneral = new ArrayList<String>();
                arGeneral.add("서울/경기");
                arGeneral.add("부산광역시");
                arGeneral.add("인천광역시");
                arGeneral.add("대전광역시");
                arGeneral.add("대구광역시");
                arGeneral.add("울산광역시");
                ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arGeneral);

//                CarPoolTaxiAdapter adapter = new CarPoolTaxiAdapter( getActivity(), 0 );

                listMain.setAdapter( adapter );

                searchHeader = inflater.inflate(R.layout.taxi_header_search, null);
                listMain.addHeaderView(searchHeader, null, false );
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
}
