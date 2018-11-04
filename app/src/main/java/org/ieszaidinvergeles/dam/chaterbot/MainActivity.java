package org.ieszaidinvergeles.dam.chaterbot;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBot;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBotFactory;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBotSession;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBotType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

//https://github.com/pierredavidbelanger/chatter-bot-api

public class MainActivity extends AppCompatActivity {

    private Button btSend;
    private EditText etTexto;
    private ScrollView svScroll;
    private TextView tvTexto;

    private ChatterBot bot;
    private ChatterBotFactory factory;
    private ChatterBotSession botSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        btSend = findViewById(R.id.btSend);
        etTexto = findViewById(R.id.etTexto);
        svScroll = findViewById(R.id.svScroll);
        tvTexto = findViewById(R.id.tvTexto);
        if(startBot()) {
            setEvents();
        }
    }

    private void setEvents() {
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text = getString(R.string.you) + " " + etTexto.getText().toString().trim();
                btSend.setEnabled(false);
                etTexto.setText("");
                tvTexto.append(text + "\n");
                IniciaChat inicia = new IniciaChat(text);
                inicia.execute();
            }
        });
    }

    private boolean startBot() {
        boolean result = true;
        String initialMessage;
        factory = new ChatterBotFactory();
        try {
            bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            botSession = bot.createSession();
            initialMessage = getString(R.string.messageConnected) + "\n";
        } catch(Exception e) {
            initialMessage = getString(R.string.messageException) + "\n" + getString(R.string.exception) + " " + e.toString();
            result = false;
        }
        tvTexto.setText(initialMessage);
        return result;
    }


    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private class IniciaChat extends AsyncTask<String, String, String > {

        private String text;
        private String respuesta;

        public IniciaChat(String text){
            this.text = text;
        }

        // Lo que se hace aquí, se puede hacer en el constructor
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // No es Hilo, se usa antes de ejecutar antes de empezar
        }

        @Override
        protected String doInBackground(String... string) {
            // Método similar a run de thread
            try {
                respuesta = getString(R.string.bot) + " " + botSession.think(text);
            } catch (final Exception e) {
                respuesta = getString(R.string.exception) + " " + e.toString();
            }
            return respuesta;
        }

        // No es de la clase Hilo.
        // Se ejecuta durante la ejecución del hilo
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        /*
         * No es de Hilo, se ejecuta al finalizar
         * Recibe como parámetro el resultado de doInBackground
         */
        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            etTexto.requestFocus();
            tvTexto.append(string + "\n");
            svScroll.fullScroll(View.FOCUS_DOWN);
            btSend.setEnabled(true);
            hideKeyboard();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    
}