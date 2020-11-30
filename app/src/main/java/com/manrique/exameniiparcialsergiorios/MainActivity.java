package com.manrique.exameniiparcialsergiorios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    EditText txtUsuario;
    EditText txtContra;
    TextView txtInfoStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtContra = findViewById(R.id.txtContra);
        txtInfoStatus = findViewById(R.id.txtInfoStatus);

        txtInfoStatus.setVisibility(View.INVISIBLE);


    }

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
                        final JSONObject payload = jres.getJSONObject("usuarioData");
                        final String status = jres.getString("status");

                        System.out.println(jres);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if(status.equals("Correcto")){
                                        Intent intent = new Intent(MainActivity.this, Mensajes.class);
                                        intent.putExtra("IdCuenta", payload.getString("IdCuenta"));
                                        txtUsuario.setText("");
                                        txtContra.setText("");
                                        startActivity(intent);
                                    } else {
                                        final Animation mostarElemento = new AlphaAnimation(0.0f, 1.0f);
                                        mostarElemento.setDuration(200);

                                        final Animation mantenerElemento = new AlphaAnimation(1.0f, 1.0f);
                                        mantenerElemento.setDuration(3000);

                                        final Animation ocultarElemento = new AlphaAnimation(1.0f, 0.0f);
                                        ocultarElemento.setDuration(3000);

                                        AnimationSet as = new AnimationSet(true);
                                        as.addAnimation(mostarElemento);
                                        as.setStartOffset(100);
                                        as.addAnimation(mantenerElemento);
                                        as.setStartOffset(100);
                                        as.addAnimation(ocultarElemento);

                                        txtUsuario.setText("");
                                        txtContra.setText("");

                                        txtInfoStatus.setVisibility(View.VISIBLE);
                                        txtInfoStatus.startAnimation(as);
                                        txtInfoStatus.setVisibility(View.INVISIBLE);
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

    public void onLoginClick(View view){
        JSONObject jreq = new JSONObject();
        JSONObject payload = new JSONObject();

        try{
            payload.put("usuario", txtUsuario.getText().toString());
            payload.put("contraseña", txtContra.getText().toString());
            jreq.put("payload", payload);

            System.out.println(jreq);

            get_request("http://192.168.0.15/login", jreq);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

}