package com.example.gps_test.ui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gps_test.R;
import com.example.gps_test.Ruta;
import com.example.gps_test.SingleEventoPublicoActivity;
import com.tomtom.sdk.map.display.marker.BalloonViewAdapter;
import com.tomtom.sdk.map.display.marker.Marker;

import java.util.List;

import kotlin.Triple;

public class CustomBalloonViewAdapter implements BalloonViewAdapter {
    private final Context context;
    private List<ListMapEventsRoutes> data;
    public CustomBalloonViewAdapter(Context context, List<ListMapEventsRoutes> data) {
        this.context = context;
        this.data = data;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateBalloonView(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_infoballon, null);
        ListMapEventsRoutes a = null;
        for (ListMapEventsRoutes current: data)
        {
            if (current.getMarkerId().equals(marker.a))
            {
                a = current;
            }
        }
        ImageView balloonImage = view.findViewById(R.id.balloonImage);
        balloonImage.setImageDrawable(new BitmapDrawable(context.getResources(), a.getImagenEvento()));
        ListMapEventsRoutes finalA = a;
        balloonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SingleEventoPublicoActivity.class);
                intent.putExtra("singleImage", finalA.getEvento().getImagenEvento());
                intent.putExtra("singleEvento",finalA.getEvento().getNombreEvento());
                intent.putExtra("singleRuta",finalA.getEvento().getRuta());
                intent.putExtra("singleDescripcion",finalA.getEvento().getDescripcion());
                intent.putExtra("singleFechaEncuentro",finalA.getEvento().getFechaEncuentro());
                intent.putExtra("singleHoraEncuentro",finalA.getEvento().getHoraEncuentro());
                intent.putExtra("singleCupoMinimo",finalA.getEvento().getCupoMinimo());
                intent.putExtra("singleCupoMaximo",finalA.getEvento().getCupoMaximo());
                intent.putExtra("singleCategoria",finalA.getEvento().getCategoria());
                intent.putExtra("singleUserName",finalA.getEvento().getUserName());
                intent.putExtra("singleUserId",finalA.getEvento().getUserId());
                intent.putExtra("singleRating",finalA.getEvento().getRating());
                intent.putExtra("singlePublicoPrivado",finalA.getEvento().getPublicoPrivado());
                intent.putExtra("singleActivarDesactivar",finalA.getEvento().getActivadoDescativado());
                intent.putExtra("EventoId",finalA.getEvento().getIdEvento());
                intent.putExtra("TokenFCM",finalA.getEvento().getTokenFCM());
                context.startActivity(intent);
            }
        });


        TextView balloonTextTitle = view.findViewById(R.id.balloon_title_text);
        balloonTextTitle.setText(a.getEvento().getNombreEvento());
        TextView balloonTextTv = view.findViewById(R.id.balloon_text_tv);
        balloonTextTv.setText("Organizador: " + a.getEvento().getUserName() + "\n" + "Fecha Encuentro: "
        + a.getEvento().getFechaEncuentro());
        return view;
    }
}

