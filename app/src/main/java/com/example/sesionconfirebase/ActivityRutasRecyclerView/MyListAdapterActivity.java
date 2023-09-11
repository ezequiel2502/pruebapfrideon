package com.example.sesionconfirebase.ActivityRutasRecyclerView;


import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.gps_test.PlanificarRuta;
import com.example.sesionconfirebase.CrearEvento;
import com.example.sesionconfirebase.R;
import com.example.sesionconfirebase.Rutas;

import java.io.Serializable;
import java.util.List;


public class MyListAdapterActivity extends RecyclerView.Adapter<MyListAdapterActivity.ViewHolder>{
    private List<MyListDataActivity> listdata;
    private Context activityContext;

    private Dialog dialogInstance;

    // RecyclerView recyclerView;
    public MyListAdapterActivity(List<MyListDataActivity> listdata)
    {
        //MyListData[] myListData2Array = new MyListData[listdata.size()];
        this.listdata = listdata;
    }

    public void addContext (Context appContext)
    {
        this.activityContext = appContext;
    }
    public void addDialogInstance (Dialog instance)
    {
        this.dialogInstance = instance;
    }

    public List<MyListDataActivity> getCurrentList()
    {
      return listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_routes_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListDataActivity myListData = listdata.get(position);
        holder.textViewName.setText(listdata.get(position).getRouteName());
        holder.textViewDistance.setText(String.valueOf(listdata.get(position).getLenght()));
        holder.textViewCurves.setText(String.valueOf(listdata.get(position).getCurvesAmount()));
        holder.textViewStart.setText(listdata.get(position).getStartLocation());
        holder.textViewFinish.setText(listdata.get(position).getFinishLocation());
        holder.imageViewRoute.setVisibility(View.VISIBLE);
        Glide.with(activityContext)
                .load(listdata.get(position).getImgId())
                .transition(withCrossFade())
                .placeholder(holder.imageViewRoute.getDrawable())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .signature(new com.bumptech.glide.signature.ObjectKey(String.valueOf(System.currentTimeMillis())))
                .into(holder.imageViewRoute);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInstance.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        CrearEvento.dialogResult(listdata.get(holder.getAbsoluteAdapterPosition()).getDatabaseID());
                    }
                });
                Toast.makeText(view.getContext(),"click on item: "+myListData.getRouteName(),Toast.LENGTH_LONG).show();
                dialogInstance.dismiss();
            }
        });
        holder.routePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+myListData.getRouteName(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(activityContext, PlanificarRuta.class);
                intent.putExtra("List_Of_Points", (Serializable) listdata.get(holder.getAbsoluteAdapterPosition()).getGeometry());
                intent.putExtra("Invalidate_Controls", "True");
                activityContext.startActivity(intent);
            }
        });
        holder.deleteRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"click on item: "+myListData.getRouteName(),Toast.LENGTH_LONG).show();
                Rutas.Delete_Route(listdata.get(holder.getAbsoluteAdapterPosition()).getDatabaseID());
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewRoute;
        public TextView textViewName;
        public TextView textViewDistance;
        public TextView textViewCurves;
        public TextView textViewStart;
        public TextView textViewFinish;
        public ImageButton routePreview;

        public ImageButton deleteRoute;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageViewRoute = (ImageView) itemView.findViewById(R.id.imageViewRoute);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewDistance = (TextView) itemView.findViewById(R.id.textViewDistance);
            this.textViewCurves = (TextView) itemView.findViewById(R.id.textViewCurves);
            this.textViewStart = (TextView) itemView.findViewById(R.id.textViewStart);
            this.textViewFinish = (TextView) itemView.findViewById(R.id.textViewFinish);
            this.routePreview = (ImageButton) itemView.findViewById(R.id.previewRoute);
            this.deleteRoute = (ImageButton) itemView.findViewById(R.id.deleteRoute);
        }
    }
}
