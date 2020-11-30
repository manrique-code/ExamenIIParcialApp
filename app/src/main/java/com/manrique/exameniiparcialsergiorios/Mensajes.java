package com.manrique.exameniiparcialsergiorios;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Mensajes extends AppCompatActivity {
    String idCuenta;
    View linearLayout;
    TextView txtMensaje;
    TextView saludo;
    TextView hora;
    TextView tema;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("aplication/json; charset=UTF-8");

    void get_request(String url, JSONObject json) throws IOException {
        RequestBody body = RequestBody.create(json.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()){
                    throw new IOException("Unexpected code" + response);
                } else {
                    try{
                        JSONObject jres = new JSONObject(response.body().string());
                        final JSONObject datosMensajes = jres.getJSONObject("datosMensajes");
                        final JSONObject mensajes = datosMensajes.getJSONObject("Mensajes");
                        int cantMensajes = datosMensajes.getInt("CantidadMensajes");
                        String nombre = mensajes.getString("Nombre0");

                        System.out.println(jres);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    saludo.setText(String.format("Hola, %s", nombre));

                                    for(int i = 0; i < cantMensajes; i++){

                                        txtMensaje = new TextView(Mensajes.this);
                                        txtMensaje.setText(mensajes.getString(String.format("ContenidoMensaje%d", i)));
                                        txtMensaje.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        txtMensaje.setTextColor(Color.BLACK);
                                        txtMensaje.setTextSize(20);

                                        tema = new TextView(Mensajes.this);
                                        tema.setText(String.format("#%s", mensajes.getString(String.format("TipoMensaje%d", i))));
                                        tema.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        tema.setTextColor(Color.BLUE);
                                        tema.setTextSize(14);

                                        hora = new TextView(Mensajes.this);
                                        hora.setText(String.format("Publicado el %s a las %s\n\n",
                                                mensajes.getString(String.format("FechaMensaje%d", i)),
                                                mensajes.getString(String.format("HoraMensaje%d", i))));
                                        hora.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        hora.setTextColor(Color.GRAY);
                                        hora.setTextSize(14);
                                        hora.setGravity(Gravity.LEFT);

                                        ((LinearLayout) linearLayout).addView(txtMensaje);
                                        ((LinearLayout) linearLayout).addView(tema);
                                        ((LinearLayout) linearLayout).addView(hora);
                                    }

                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes);

        saludo = findViewById(R.id.txtSaludo);

        idCuenta = getIntent().getStringExtra("IdCuenta");
        System.out.println(idCuenta);

        linearLayout  = findViewById(R.id.LLMensajes);

        JSONObject payload = new JSONObject();
        JSONObject mensajeData = new JSONObject();

        try{
            payload.put("IdCuenta", idCuenta);
            mensajeData.put("payload", payload);
        } catch(Exception e){
            e.printStackTrace();
        }

        System.out.println(mensajeData);
        try {
            get_request("http://192.168.0.15/ver_mensajes", mensajeData);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onEscribirClick(View view){
        Intent intent = new Intent(Mensajes.this, EscribirMensaje.class);
        intent.putExtra("IdCuenta", idCuenta);
        startActivity(intent);
    }

    public void onCerraSesion(View view){
        Intent intent = new Intent(Mensajes.this, MainActivity.class);
        intent.putExtra("IdCuenta", idCuenta);
        startActivity(intent);
    }

}