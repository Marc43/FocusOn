package com.example.focuson;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MediaPlayerFragment extends Fragment {

    private boolean playingState = false; //Whether is playing or not!

    RequestQueue queue;
    public String serverURL = MainActivity.SERVER_URL;
    
    private List<DataPerSong> dataPerSongList = new ArrayList<>();
    private RecyclerView dataPerSongRV;
    private SongsListAdapter songsListAdapter;

    private OnFragmentInteractionListener mListener;

    public MediaPlayerFragment() {

    }

    public static MediaPlayerFragment newInstance(String param1, String param2) {
        MediaPlayerFragment fragment = new MediaPlayerFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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

    private void setupRecyclerView(View view) {
        dataPerSongRV = (RecyclerView) view.findViewById(R.id.songs_list);

        songsListAdapter = new SongsListAdapter(dataPerSongList);
        dataPerSongRV.setLayoutManager(new LinearLayoutManager(getContext()));
        dataPerSongRV.setHasFixedSize(true);
        //dataPerSongRV.setItemAnimator(new DefaultItemAnimator());
        dataPerSongRV.setAdapter(songsListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_media_player, container, false);

        setupRecyclerView(view);

        //songListTesting();

        final Button playstop = (Button)view.findViewById(R.id.playstop);
        playstop.setBackgroundResource(R.drawable.play); //Play button
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        playingState = false; //Not playing

        playstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playingState) {
                    playingState = true;
                    playstop.setBackgroundResource(R.drawable.stop);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET,
                            serverURL + "playMusic",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error");
                        }
                    });
                    queue.add(stringRequest);
                }
                else {
                    playingState = false;
                    playstop.setBackgroundResource(R.drawable.play);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET,
                            serverURL + "pauseMusic",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error");
                        }
                    });
                    queue.add(stringRequest);
                }
                whichSong(view);
                getUpcomingSongs(view);
            }
        });

        final Button previous = (Button)view.findViewById(R.id.previous);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        serverURL + "playPreviousSong",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error");
                    }
                });
                queue.add(stringRequest);
                whichSong(view);
                getUpcomingSongs(view);
            }
        });

        final Button next = (Button)view.findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        serverURL + "playNextSong",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error");
                    }
                });
                queue.add(stringRequest);
                whichSong(view);
                getUpcomingSongs(view);
            }
        });

        return view;
    }

    private void whichSong(final View view) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                serverURL + "whichSong",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            String name, artist, album, url;
                            name = object.getString("song");
                            artist = object.getString("artist");
                            album = object.getString("album");
                            url = object.getString("cover");

                            ImageView imageView = (ImageView) view.findViewById(R.id.album);
                            Bitmap bf = null;
                            try {
                                URL newurl = new URL(url);
                                bf = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
                            }
                            catch(Exception e) {
                                e.printStackTrace();
                            }
                            if (bf != null) {
                                imageView.setImageBitmap(bf);
                            }

                            TextView nameAndArtist = (TextView) view.findViewById(R.id.name_and_artist);
                            nameAndArtist.setText(name + " - " + artist);

                            TextView albumName = (TextView) view.findViewById(R.id.album_name);
                            albumName.setText(album);

                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error");
                    }
                });
                queue.add(stringRequest);

    }

    private void getUpcomingSongs(final View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                serverURL + "nextSongsOnTop",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray data = object.getJSONArray("data");

                            dataPerSongList.clear();
                            songsListAdapter.notifyItemInserted(dataPerSongList.size());

                            setupRecyclerView(view);

                            String name, artist, album;
                            for (int i = 0; i < data.length(); i++){
                                JSONObject o = data.getJSONObject(i);
                                name = o.getString("song");
                                artist = o.getString("artist");
                                album = o.getString("album");
                                DataPerSong dps = new DataPerSong(name, artist, album);
                                dataPerSongList.add(dps);
                                songsListAdapter.notifyItemInserted(dataPerSongList.size());
                            }

                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error");
            }
        });
        queue.add(stringRequest);
    }

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
