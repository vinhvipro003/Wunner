package com.production.wunner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.production.wunner.Interface.ItemClickListener;
import com.production.wunner.Item;
import com.production.wunner.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Item> items= new ArrayList<>();
    int[] myImageList = {R.drawable.gradient1, R.drawable.gradient2,R.drawable.gradient3,
            R.drawable.gradient4,R.drawable.gradient5};
    int[] imgList= {R.drawable.anh3,R.drawable.anh1,R.drawable.anh6,R.drawable.anh4,R.drawable.anh5};

    public RecyclerAdapter(Context mContext, ArrayList<Item> items ) {
        this.mContext = mContext;
        this.items = items;

    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_layout_info,parent,false);
        ViewHolder viewHolder =new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(  ViewHolder holder, int position) {
        final Item item= items.get(position);
        holder.txt_title.setText(item.getTitle());
        holder.txt_desciption.setText(item.getDescription());
        holder.viewGradient.setBackgroundResource(myImageList[position%myImageList.length]);
        holder.imageView.setImageResource(imgList[position%imgList.length]);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView txt_title, txt_desciption;
        private View viewGradient;
        private View view;
        private ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            txt_title =itemView.findViewById(R.id.txt_title);
            txt_desciption = itemView.findViewById(R.id.txt_description);
            viewGradient = (View) view.findViewById(R.id.viewgradient);
            imageView =view.findViewById(R.id.imgitem);
      }

        public void Item_Click(final Item item, final ItemClickListener listener) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     listener.SelectItem(item);
                }
            });
        }
    }

}
