package www.goldpay.exchange.galleryvoult;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListAdapter  extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    Context context;
    List<String> mList;
    OnItemClick listerner;

    public ListAdapter(List<String> mList, OnItemClick listerner, Context context) {
        this.mList = mList;
        this.listerner = listerner;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=  inflater.inflate(R.layout.imagelayout,parent,false);
        return new ListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.MyViewHolder holder, int position) {

        Uri uri = Uri.parse(mList.get(position));
        holder.image.setImageURI(uri);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(mList.get(position));
                Intent intent = new Intent(context,ImageData.class);
                intent.putExtra("imgPath",String.valueOf(uri));
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

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image, delbtn;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.myPic);
            //name = (TextView)itemView.findViewById(R.id.name);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listerner.onClick(getAdapterPosition());
                }
            });
            //image.setOnClickListener(this);
        }
    }
}
