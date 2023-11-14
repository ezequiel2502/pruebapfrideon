package com.example.sesionconfirebase.ActivitySingleEventoPostuladosRecycler;


import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.gps_test.PlanificarRuta;
import com.example.gps_test.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AssistanceAdapter extends RecyclerView.Adapter<AssistanceAdapter.ViewHolder>{
    private List<AssistanceData> listdata;
    private Context context;
    private String evento;
    private String disableAsistanceButtons;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    // RecyclerView recyclerView;
    public AssistanceAdapter(List<AssistanceData> listdata, Context context, String evento, String disableAsistanceButtons) {
        this.listdata = listdata;
        this.context = context;
        this.evento = evento;
        this.disableAsistanceButtons = disableAsistanceButtons;
    }

    public List<AssistanceData> getCurrentList()
    {
      return listdata;
    }

    public void clearData()
    {
        this.listdata = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(com.example.sesionconfirebase.R.layout.list_item_assitance, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AssistanceData myListData = listdata.get(position);
        holder.textView.setText(myListData.getDescription());
        holder.textView2.setText(myListData.getDescription2());
        if (listdata.get(position).getImgId() != null) {
            Glide.with(context)
                    .load(listdata.get(position).getImgId())
                    .transition(withCrossFade())
                    .placeholder(holder.imageView.getDrawable())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .signature(new com.bumptech.glide.signature.ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .into(holder.imageView);
        }

        DatabaseReference listaExistente =  database.getReference().child("Eventos").child("Eventos Publicos")
                .child(evento).child("listaPresentes").child(myListData.getIdUsuario());
        listaExistente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    if (snapshot.getValue(String.class).equals("True")) {
                        holder.accept.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#03fc56")));
                        holder.cancel.setEnabled(false);
                        holder.cancel.setImageAlpha(75);
                    } else if (snapshot.getValue(String.class).equals("False")) {
                        holder.cancel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f53b3b")));
                        holder.accept.setEnabled(false);
                        holder.accept.setImageAlpha(75);
                    }
                }
                else
                {
                    DatabaseReference listaExistente =  database.getReference().child("Eventos").child("Completados")
                            .child(evento).child("listaPresentes").child(myListData.getIdUsuario());
                    listaExistente.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                if (snapshot.getValue(String.class).equals("True")) {
                                    holder.accept.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#03fc56")));
                                    holder.accept.setEnabled(false);
                                    holder.accept.setImageAlpha(75);
                                    holder.cancel.setEnabled(false);
                                    holder.cancel.setImageAlpha(75);
                                } else if (snapshot.getValue(String.class).equals("False")) {
                                    holder.cancel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f53b3b")));
                                    holder.cancel.setEnabled(false);
                                    holder.cancel.setImageAlpha(75);
                                    holder.accept.setEnabled(false);
                                    holder.accept.setImageAlpha(75);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(disableAsistanceButtons == "True")
        {
            holder.cancel.setEnabled(false);
            holder.cancel.setImageAlpha(75);
            holder.accept.setEnabled(false);
            holder.accept.setImageAlpha(75);
        }

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.accept.getBackgroundTintList().getDefaultColor() == ContextCompat.getColor(context, R.color.white))
                {
                    database.getReference().child("Eventos").child("Eventos Publicos")
                            .child(evento).child("listaPresentes").child(myListData.getIdUsuario()).setValue("True");
                    holder.accept.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#03fc56")));
                    holder.cancel.setEnabled(false);
                    holder.cancel.setImageAlpha(75);
                }
                else
                {
                    database.getReference().child("Eventos").child("Eventos Publicos")
                            .child(evento).child("listaPresentes").child(myListData.getIdUsuario()).setValue("Undefined");
                    holder.accept.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                    holder.cancel.setEnabled(true);
                    holder.cancel.setImageAlpha(255);
                }
            }
        });
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cancel.getBackgroundTintList().getDefaultColor() == ContextCompat.getColor(context, R.color.white))
                {
                    database.getReference().child("Eventos").child("Eventos Publicos")
                            .child(evento).child("ListaPresentes").child(myListData.getIdUsuario()).setValue("False");
                    holder.cancel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f53b3b")));
                    holder.accept.setEnabled(false);
                    holder.accept.setImageAlpha(75);
                }
                else
                {
                    database.getReference().child("Eventos").child("Eventos Publicos")
                            .child(evento).child("ListaPresentes").child(myListData.getIdUsuario()).setValue("Undefined");
                    holder.cancel.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                    holder.accept.setEnabled(true);
                    holder.accept.setImageAlpha(255);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textView2;
        public ImageButton accept;
        public ImageButton cancel;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            this.textView2 = (TextView) itemView.findViewById(R.id.textView2);
            this.accept = (ImageButton) itemView.findViewById(com.example.sesionconfirebase.R.id.imageButtonAccept);
            this.cancel = (ImageButton) itemView.findViewById(com.example.sesionconfirebase.R.id.imageButtonCancel);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}
