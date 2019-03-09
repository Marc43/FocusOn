package com.example.focuson;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MediaPlayerFragment extends Fragment {

    private boolean playingState = false; //Whether is playing or not!

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MediaPlayerFragment() {
        // Required empty public constructor
    }

    public static MediaPlayerFragment newInstance(String param1, String param2) {
        MediaPlayerFragment fragment = new MediaPlayerFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_media_player, container, false);

        final Button playstop = (Button)view.findViewById(R.id.playstop);
        playstop.setBackgroundResource(R.drawable.play); //Play button
        playingState = false; //Not playing

        playstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playingState) {
                    playingState = true;
                    playstop.setBackgroundResource(R.drawable.stop);

                    //TODO Call to the server API
                }
                else {
                    playingState = false;
                    playstop.setBackgroundResource(R.drawable.play);

                    //TODO Call to the server API
                }
            }
        });

        final Button previous = (Button)view.findViewById(R.id.previous);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Call to the server API
            }
        });

        final Button next = (Button)view.findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Call to the server API
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
