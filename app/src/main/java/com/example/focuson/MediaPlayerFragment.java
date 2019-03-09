package com.example.focuson;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayerFragment extends Fragment {

    private boolean playingState = false; //Whether is playing or not!

    private List<DataPerSong> dataPerSongList = new ArrayList<>();
    private RecyclerView dataPerSongRV;
    private SongsListAdapter songsListAdapter;

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

    private void songListTesting() {
        DataPerSong dps1 = new DataPerSong("My polla", "Un pollon", "Las pollas del averno");
        dataPerSongList.add(dps1);
        songsListAdapter.notifyItemInserted(dataPerSongList.size());

        DataPerSong dps2 = new DataPerSong("STARTHACK APESTA", "Un pollon", "Las pollas del averno");
        dataPerSongList.add(dps2);
        songsListAdapter.notifyItemInserted(dataPerSongList.size());

        DataPerSong dps3 = new DataPerSong("STARTHACK APESTA", "Un pollon", "Las pollas del averno");
        dataPerSongList.add(dps3);
        songsListAdapter.notifyItemInserted(dataPerSongList.size());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_media_player, container, false);

        dataPerSongRV = (RecyclerView) view.findViewById(R.id.songs_list);

        songsListAdapter = new SongsListAdapter(dataPerSongList);
        dataPerSongRV.setLayoutManager(new LinearLayoutManager(getContext()));
        dataPerSongRV.setHasFixedSize(true);
        //dataPerSongRV.setItemAnimator(new DefaultItemAnimator());
        dataPerSongRV.setAdapter(songsListAdapter);

        songListTesting();

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

    public static class DataPerSong {
        public String name;
        public String artist;
        public String albumName;

        public DataPerSong() {
        }

        public DataPerSong(String name, String artist, String albumName) {
            this.name = name;
            this.artist = artist;
            this.albumName = albumName;
        }

        public String getName() {
            return name;
        }

        public String getArtist() {
            return artist;
        }

        public String getAlbumName() {
            return albumName;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public void setAlbumName(String albumName) {
            this.albumName = albumName;
        }
    }

    public static class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.Holder> {

        private List<DataPerSong> queuedSongs;

        public static class Holder extends RecyclerView.ViewHolder {
        public TextView name, artist, album;

        public Holder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            artist = (TextView) itemView.findViewById(R.id.artist);
            album = (TextView) itemView.findViewById(R.id.album);
        }
    }

        public SongsListAdapter(List<DataPerSong> queuedSongs) {
            this.queuedSongs = queuedSongs;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.songs_list, viewGroup, false);

            return new Holder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            DataPerSong dps = queuedSongs.get(position);
            holder.name.setText(dps.getName());
            holder.artist.setText(dps.getArtist());
            holder.album.setText(dps.getAlbumName());
        }

        @Override
        public int getItemCount() {
            return queuedSongs.size();
        }
    }
}
