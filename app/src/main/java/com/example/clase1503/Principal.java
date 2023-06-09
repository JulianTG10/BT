package com.example.clase1503;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class Principal extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    private String[] permissions;
    private short cantPermissions=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        listPermissions();
        requestPermissions();
    }


    private void  listPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissions= new String[]{
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION


            };
            cantPermissions=1;
        }
            else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
            permissions= new String[]{
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            };
            cantPermissions=2;
            }


    }

    private boolean checkHasPermissions(){
        for(String p:permissions){
            if(ActivityCompat.checkSelfPermission(getApplicationContext(),p)== PackageManager.PERMISSION_GRANTED)return true;
        }

        return false;
    }
    //si no hay lo solicitamos
    private void requestPermissions(){
        if(checkHasPermissions()){
            ActivityCompat.requestPermissions(this, permissions,REQUEST_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
            switch (cantPermissions){
                case 1:
                    if((grantResults[0]== PackageManager.PERMISSION_GRANTED)
                        &&  (grantResults[1]== PackageManager.PERMISSION_GRANTED)
                        && (grantResults[2]== PackageManager.PERMISSION_GRANTED)
                        && (grantResults[3]== PackageManager.PERMISSION_GRANTED)
                        && (grantResults[4]== PackageManager.PERMISSION_GRANTED)
                        && (grantResults[5]== PackageManager.PERMISSION_GRANTED)){
                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                    }else{
                        mensaje();
                    }
                    break;
                case 2:
                    if((grantResults[0]== PackageManager.PERMISSION_GRANTED)
                            &&  (grantResults[1]== PackageManager.PERMISSION_GRANTED)
                            && (grantResults[2]== PackageManager.PERMISSION_GRANTED)){
                        Intent intent = new Intent(this,MainActivity.class);
                        startActivity(intent);
                    } else{
                        mensaje();
                    }
                    break;
                default:
                    Toast.makeText(this, "ERROR en Permisos", Toast.LENGTH_SHORT).show();

            }
        }



    }

    private void mensaje() {
        new AlertDialog.Builder(this)
                .setTitle("Permisos de la aplicación")
                .setTitle("Usted ha denegado algun permiso por favor cambielo")
                .setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package",getPackageName(),null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                    }
                }) .setNegativeButton("Salida de APP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        }).create().show();


    }
}