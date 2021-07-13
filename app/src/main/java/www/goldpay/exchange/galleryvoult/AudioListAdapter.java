package www.goldpay.exchange.galleryvoult;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.MyViewHolder>{

    Context context;
    List<String> mList;
    OnItemClick listerner;
    MediaPlayer mediaPlayer;

    public AudioListAdapter(Context context, List<String> mList, OnItemClick listerner) {
        this.context = context;
        this.mList = mList;
        this.listerner = listerner;
    }

    public void release(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=  inflater.inflate(R.layout.audiolayout,parent,false);

        return new AudioListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioListAdapter.MyViewHolder holder, int position) {
        //holder.videoView.setImageURI(Uri.parse(mList.get(position)));

        Uri uri = Uri.parse(mList.get(position));
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer = MediaPlayer.create(context,uri);
        MediaPlayer finalMediaPlayer = mediaPlayer;
        holder.myIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalMediaPlayer.start();
                Toast.makeText(context, "PLAYING", Toast.LENGTH_SHORT).show();
                holder.myIcon.setVisibility(View.GONE);
                holder.myIconStop.setVisibility(View.VISIBLE);
            }
        });

        holder.myIconStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalMediaPlayer.isPlaying()){
                    Toast.makeText(context, "STOP", Toast.LENGTH_SHORT).show();
                    holder.myIcon.setVisibility(View.VISIBLE);
                    holder.myIconStop.setVisibility(View.GONE);
                    finalMediaPlayer.pause();
                    finalMediaPlayer.seekTo(0);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout my_audio_view;
        ImageView myIcon,myIconStop;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            my_audio_view = (LinearLayout) itemView.findViewById(R.id.my_audio_view);
            myIcon = (ImageView) itemView.findViewById(R.id.myIcon);
            myIconStop = (ImageView) itemView.findViewById(R.id.myIconStop);

            my_audio_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listerner.onClick(getAdapterPosition());
                }
            });
        }
    }
}
