package myapplication.callthelist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public abstract class RuntimePermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void izinIste(final String[] istenilenIzinler, final int requestCode) {


        int izinKontrol = PackageManager.PERMISSION_GRANTED;
        boolean mazeretGoster = false;


        //izinkontrol=0 ise izin/izinler verilmiştir
        //aksi durumda izin/izinler verilmemiştir.
        //mazeret göster = false ise ilk defa izin sorulmustur
        //mazeret goster= true ise kullanıcı izni reddetmiştir, ona bir mazeret sunabiliriz.

        for (String izin : istenilenIzinler) {

            izinKontrol = izinKontrol + ContextCompat.checkSelfPermission(this, izin);
            mazeretGoster = mazeretGoster || ActivityCompat.shouldShowRequestPermissionRationale(this, izin);
        }

        if (izinKontrol != PackageManager.PERMISSION_GRANTED) {

            if (mazeretGoster) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Neden izin vermelisin?");
                builder.setMessage("Arama yapmak istiyorsanız bu izni vermeniz gerekiyor");
                builder.setNegativeButton("YOKSAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("İZİN VER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(RuntimePermissionsActivity.this, istenilenIzinler, requestCode);
                    }
                });

                builder.show();

            } else {
                ActivityCompat.requestPermissions(RuntimePermissionsActivity.this, istenilenIzinler, requestCode);
            }

        } else {

            izinVerildi(requestCode);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int izinKontrol = PackageManager.PERMISSION_GRANTED;


        //izinkontrol=0 ise tüm izinler verilmiştir

        for (int izinDurumu : grantResults) {

            izinKontrol = izinKontrol + izinDurumu;

        }

        if ((grantResults.length > 0) && izinKontrol == PackageManager.PERMISSION_GRANTED) {

            izinVerildi(requestCode);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Neden izin vermelisin?");
            builder.setMessage("Arama yapmak istiyorsanız bu izni vermeniz gerekiyor");
            builder.setNegativeButton("YOKSAY", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("İZİN VER", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(intent);

                }
            });
            builder.show();
        }
    }

    public abstract void izinVerildi(int requestCode);



}
