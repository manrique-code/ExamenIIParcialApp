package com.manrique.exameniiparcialsergiorios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EscribirMensaje extends AppCompatActivity {

    String idCuenta;
    TextView txtMensajePublicar;
    Spinner spnTipoMensaje;

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
                        if(url.equals("http://192.168.0.15/publicar_mensaje")) {
                            JSONObject jres = new JSONObject(response.body().string());
                            String status = jres.getString("status");
                            System.out.println(jres);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (status.equals("Correcto")) {
                                            Intent intent = new Intent(EscribirMensaje.this, Mensajes.class);
                                            intent.putExtra("IdCuenta", idCuenta);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(EscribirMensaje.this, "Mensaje no publicado, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {

                            JSONObject jres = new JSONObject(response.body().string());
                            final JSONObject payload = jres.getJSONObject("payload");

                            System.out.println(jres);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        int cantTipoMensaje = jres.getInt("CantTipoMensaje");
                                        List<String> listTipoMensaje = new ArrayList<String>();

                                        for(int i = 0; i < cantTipoMensaje; i++){
                                            listTipoMensaje.add(payload.getString(String.format("NombreTipoMensaje%d", i)));
                                        }
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EscribirMensaje.this, android.R.layout.simple_spinner_dropdown_item, listTipoMensaje);
                                        spnTipoMensaje.setAdapter(adapter);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
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
        setContentView(R.layout.activity_escribirmensaje);

        idCuenta = getIntent().getStringExtra("IdCuenta");

        txtMensajePublicar = findViewById(R.id.txtMensajesPublicar);
        spnTipoMensaje = findViewById(R.id.spnTipoMensaje);

        try{
            JSONObject saludo = new JSONObject();
            saludo.put("Hola", "Hola");

            get_request("http://192.168.0.15/ver_tipo_mensaje", saludo);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClickCancelar(View view){
        this.onBackPressed();
    }

    public void onClickPublicar(View view){
        String tipoMensaje = spnTipoMensaje.getSelectedItem().toString(),
                contenidoMensaje = txtMensajePublicar.getText().toString();

        JSONObject payload = new JSONObject();
        JSONObject datosCliente = new JSONObject();

        try{
            payload.put("ContenidoMensaje", contenidoMensaje);
            payload.put("TipoMensaje", tipoMensaje);
            payload.put("IdCuenta", idCuenta);

            datosCliente.put("payload", payload);
            
            get_request("http://192.168.0.15/publicar_mensaje", datosCliente);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


}