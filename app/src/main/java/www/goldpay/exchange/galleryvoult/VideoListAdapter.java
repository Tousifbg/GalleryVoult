package www.goldpay.exchange.galleryvoult;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.MyViewHolder>{

    Context context;
    List<String> mList;
    OnItemClick listerner;

    public VideoListAdapter(Context context, List<String> mList, OnItemClick listerner) {
        this.context = context;
        this.mList = mList;
        this.listerner = listerner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=  inflater.inflate(R.layout.videolayout,parent,false);

        return new VideoListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListAdapter.MyViewHolder holder, int position) {
        //holder.videoView.setImageURI(Uri.parse(mList.get(position)));

        Uri uri = Uri.parse(mList.get(position));
        //holder.videoView.setImageURI(Uri.parse(mList.get(position)));
        //holder.videoView.getLayoutParams().height = getScreenWidth(context) * 9 /16;
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(String.valueOf(uri),
                MediaStore.Images.Thumbnails.MINI_KIND);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
        holder.imageView.setImageBitmap(thumb);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,VideoPlayer.class);
                intent.putExtra("myURL",String.valueOf(uri));
                context.startActivity(intent);
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

/*    public static int getScreenWidth(Context c) {
        int screenWidth = 0; // this is part of the class not the method
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }*/

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.myVideo);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listerner.onClick(getAdapterPosition());
                }
            });
        }
    }
}
