package com.example.clase1503;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 101;
    private Context context;
    private Activity activity;
    //Version android
    private TextView versionAndroid;
    int versionSDK;
    //Bateria
    private ProgressBar pbLevelBaterry;
    private TextView  tvLevelBaterry;
    IntentFilter batteryFilter;
    //BT
    private BluetoothAdapter btAdapter;
    private  boolean flag = false;
    BluetoothManager bluetoothManager;
    private EditText nameFile ;

    private  boolean btPermission =false;
    //Conexión
    private TextView tvConexion;
    private ConnectivityManager conexion;
/******************************************************************************************************************/

ActivityResultLauncher<Intent> activityResultLauncher=
        registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        int resultado = result.getResultCode();
                        if(resultado==RESULT_OK){
                            Toast.makeText(context, "HABILITADO", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "OPERACION CANCELADA", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );


/******************************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //inicio componente
        begin();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        //bateria
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver,batteryFilter);
        checkConnection();



    }




    //check conexion
    private void checkConnection(){
        conexion = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conexion.getActiveNetworkInfo();
        boolean stateNet= networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (stateNet) tvConexion.setText("State ON");
        else tvConexion.setText("State OFF");
    }


    //BT
    private void methoFlag(){
        if(!flag){// flag != true
            bluetoothManager = getSystemService(BluetoothManager.class);
            btAdapter =bluetoothManager.getAdapter();
            flag= true;

        }


    }

    //Hablitar bt
    public void enableBT (View view){
        methoFlag();
        try {
            if(btAdapter != null){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.activityResultLauncher.launch(enableIntent);
            }

        }catch (Exception e){
            Log.i("Error BT","Habilitando BT"+e);


        }


    }

    public void disableBT(View view) {
        methoFlag();
        try {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if(btAdapter != null && btAdapter.isEnabled()){
                btAdapter.disable();
                Toast.makeText(this, "Bluetooth desactivado", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Error BT", "Desactivando BT: " + e.getMessage());
        }
    }




    //Verificar permisos de BT
    private void requestPermission() {
        if (versionSDK >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH},REQUEST_CODE);


            } else{
                btPermission=true;


            }

        }else{

            btPermission=true;
        }
    }

    //SO version


    @Override
    protected void onResume() {
        super.onResume();
        String versionSo = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText("Version SO: "+versionSo+" /  SDK"+versionSDK);
    }

    //Guardar archivo

   // public void saveFile(View view) {
     //   String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + nameFile.getText().toString() + ".txt";

        //String fileName = nameFile.getText().toString() + ".txt";
       // FileOutputStream outputStream;

        //try {
          //  outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            //outputStream.write("".getBytes());
            //outputStream.close();
            //Toast.makeText(this, "Archivo creado exitosamente", Toast.LENGTH_SHORT).show();
        //} catch (Exception e) {
          //  e.printStackTrace();
            //Toast.makeText(this, "Error al crear el archivo", Toast.LENGTH_SHORT).show();
        //}
    //}

    public void saveFile(View view) {
        EditText etNameFile = findViewById(R.id.etNameFile);
        String fileName = etNameFile.getText().toString().trim();

        if (fileName.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce un nombre para el archivo.", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName + ".txt";

        try {
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            writer.close();

            Toast.makeText(this, "Archivo guardado en la carpeta de descargas.", Toast.LENGTH_SHORT).show();

            // Si quieres enviar el archivo por correo electrónico o compartirlo de alguna otra manera,
            // puedes utilizar el siguiente código:
            //Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //shareIntent.setType("text/plain");
            //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            //startActivity(Intent.createChooser(shareIntent, "Enviar archivo"));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar el archivo.", Toast.LENGTH_SHORT).show();
        }
    }





    public void createEmptyTxtFile(String fileName) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), fileName + ".txt");
            if(!file.exists()){
                file.createNewFile();
                Toast.makeText(this, "Archivo creado: " + fileName + ".txt", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El archivo " + fileName + ".txt ya existe", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Error", "Error al crear archivo: " + e.getMessage());
        }
    }

    //bateria
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBaterry = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            pbLevelBaterry.setProgress(levelBaterry);
            tvLevelBaterry.setText("level Baterry"+levelBaterry+" %");
        }
    };



    private void begin(){
        this.context = getApplicationContext();
        this.activity = this;
        this.versionAndroid=findViewById(R.id.tvVersionAndroid);
        this.pbLevelBaterry=findViewById(R.id.pbLevelBaterry);
        this.tvLevelBaterry=findViewById(R.id.tvLevelBaterryLB);
        this.nameFile=findViewById(R.id.etNameFile);
        this.tvConexion=findViewById(R.id.tvConexion);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}