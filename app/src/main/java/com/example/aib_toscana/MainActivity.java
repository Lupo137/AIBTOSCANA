package com.example.aib_toscana;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStream;

import okhttp3.OkHttpClient;


@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {
    private LocationListener locationListener;
    private LocationManager locationManager;
    private OkHttpClient Hclient;
    public String response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Hclient = new OkHttpClient();

        //GPS
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                updateText(R.id.Latitudine, String.valueOf(latitude));
                updateText(R.id.Longitudine, String.valueOf(longitude));

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }
        };

        //       if (Build.VERSION.SDK_INT >= 23) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return ;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 20, locationListener);
        }
//        } else {
        //           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 20, locationListener);
        //       }



        final TextView Squad= findViewById(R.id.Squad);
        final TextView Lat= findViewById(R.id.Latitudine);
        final TextView Long= findViewById(R.id.Longitudine);
        final TextView Evento= findViewById(R.id.Localita);
        final Button BEvent =findViewById(R.id.textViewev);
        final Button Bsquadra= findViewById(R.id.textView4);
        final Context context = this;
        final Button gps=findViewById(R.id.gps);



        //GESTIONE CLICK SU SCRITTA "EVENTO"
        BEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LayoutInflater li =LayoutInflater.from(context);
                View promptView = li.inflate(R.layout.ins_testo,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setView(promptView);
                final EditText userinput = (EditText) promptView.findViewById(R.id.editText);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Evento.setText(userinput.getText().toString().toUpperCase());
                                //                           Evento.setAllCaps(true);
                            }
                        })
                        .setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });





        //GESTIONE CLICK SU SCRITTA "SQUADRA"
        Bsquadra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LayoutInflater li =LayoutInflater.from(context);
                View promptView = li.inflate(R.layout.ins_testo,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setView(promptView);
                final EditText userinput = (EditText) promptView.findViewById(R.id.editText);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Squad.setText(userinput.getText().toString().toUpperCase());
                                //                              Squad.setAllCaps(true);
                            }
                        })
                        .setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });


        gps.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location!=null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    updateText(R.id.Latitudine, String.valueOf(latitude));
                    updateText(R.id.Longitudine, String.valueOf(longitude));
                }
                if (locationManager!=null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    updateText(R.id.enabled, "GPS ATTIVATO");
                else
                    updateText(R.id.enabled, "GPS DISATTIVATO");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 100, locationListener);
            }




        });

        final Button Invia= findViewById(R.id.Send);
        Invia.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //CONTROLLO CONNESSIONE
                boolean connessione;
                connessione = isOnline();
                Toast.makeText(getApplicationContext(), "CONTROLLO CONNESSIONE A INTERNET....", Toast.LENGTH_LONG).show();
                if (connessione)
                {
                    Toast.makeText(getApplicationContext(), "SEI CONNESSO A INTERNET", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"NON SEI CONNESSO A INTERNET",Toast.LENGTH_LONG).show();
                }
                URL test;



                //COSTRUZIONE STRINGA URL PER PASSAGGIO DATI IN MODALITA' GET
                String SSsquadra = "ASSOCIAZIONE=" + Squad.getText().toString();
                String SLat = "LAT=" + Lat.getText().toString();
                String SLong = "LONG=" + Long.getText().toString();
                String SEvento = "EVENTO=" + Evento.getText().toString();
                URL pagURL = null;
                String query = "http://93.41.154.40/post.php?" + SSsquadra + "&" + SLat + "&" + SLong + "&" + SEvento;

                Toast.makeText(getApplicationContext(), "CONNESSIONE AL DATABASE IN CORSO....", Toast.LENGTH_LONG).show();

                //PASSAGGIO DATI TRAMITE post.php E METODO HTTPURL
                try {
                    pagURL = new URL("http://93.41.154.40/post.php?" + SSsquadra + "&" + SLat + "&" + SLong + "&" + SEvento);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection client = null;

                Toast.makeText(getApplicationContext(), "CARICAMENTO DATI IN CORSO....", Toast.LENGTH_LONG).show();
                if (pagURL != null) {
                    try {
                        client = (HttpURLConnection) pagURL.openConnection();
                        BufferedReader reader = null;
                        int status = client.getResponseCode();
                        if(status<400){
                            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        }else{
                            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        }
                        InputStream risposta;
                        int statusCode = client.getResponseCode();
                        //          InputStream is = null;
                        updateText(R.id.enabled,String.valueOf(statusCode));

                        risposta = new BufferedInputStream(client.getInputStream());
                        String datiLetti = mostroDati(risposta);
                        Toast.makeText(getApplicationContext(), datiLetti, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        //SE NON FUNZIONA IL METODO HTTPURL PROCEDE CON METODO OKHTTP
                        getContent(query);
                        //                       }
                    }
                }
            }
        });

    }

    private void getContent(final String query){

        try{
            response = RestCall.get(Hclient,query);
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            Log.d("response",response);


        }catch (IOException e){
            e.printStackTrace();
            response = "CARICAMENTO NON RIUSCITO";
        }
        //          return response;

    }


    //CONTROLLO PERMESSI E GESTIONE

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 100, locationListener);
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
    }
    //METODO PER AGGIORNAMENTO CAMPI NEL FORM
    private void updateText(int id, String text) {
        TextView textView = (TextView) findViewById(id);
        textView.setText(text);
    }


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String mostroDati(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}






