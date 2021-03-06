/*
Copyright 2019 Peter Stoiber

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

Please contact the author if you need another license.
This Repository is provided "as is", without warranties of any kind.

*/

package humer.uvc_camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import noman.zoomtextview.ZoomTextView;

public class Main extends Activity {

    public static int camStreamingAltSetting;
    public static int camFormatIndex;
    public static int camFrameIndex;
    public static int camFrameInterval;
    public static int packetsPerRequest;
    public static int maxPacketSize;
    public static int imageWidth;
    public static int imageHeight;
    public static int activeUrbs;
    public static String videoformat;
    public static String deviceName;
    public static byte bUnitID;
    public static byte bTerminalID;
    public static byte[] bNumControlTerminal;
    public static byte[] bNumControlUnit;
    public static byte bStillCaptureMethod;
    public Button menu;
    private ZoomTextView tv;

    private final int REQUEST_PERMISSION_STORAGE_READ=1;
    private final int REQUEST_PERMISSION_STORAGE_WRITE=2;
    private final int REQUEST_PERMISSION_CAMERA=3;
    private static int ActivitySetUpTheUsbDeviceRequestCode = 1;
    private static int ActivityStartIsoStreamRequestCode = 2;

    final static float STEP = 200;
    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        tv = (ZoomTextView) findViewById(R.id.textDarstellung);
        tv.setText("Hello\n\nThis App may not work on Android 9 (PIE) and Android 10 (Q) Devices. In this case please use other Usb Camera Apps from the Play Store" +
                "\n\nYour current Values are:\n\n( - this is a sroll and zoom field - )\n\nPackets Per Request = " + packetsPerRequest +"\nActive Urbs = " + activeUrbs +
                "\nAltSetting = " + camStreamingAltSetting + "\nMaxPacketSize = " + maxPacketSize + "\nVideoformat = " + videoformat + "\ncamFormatIndex = " + camFormatIndex + "\n" +
                "camFrameIndex = " + camFrameIndex + "\nimageWidth = "+ imageWidth + "\nimageHeight = " + imageHeight + "\ncamFrameInterval = " + camFrameInterval + "" +
                "\n\nYou can edit these Settings by clicking on (Set Up The Camera Device).\nYou can then save the values and later restore them.");
        tv.setTextColor(darker(Color.BLUE, 100));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_STORAGE_READ:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Main.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Main.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION_STORAGE_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Main.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Main.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Main.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Main.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivitySetUpTheUsbDeviceRequestCode && resultCode == RESULT_OK && data != null) {
            // TODO Extract the data returned from the child Activity.

            camStreamingAltSetting=data.getIntExtra("camStreamingAltSetting",0);
            videoformat=data.getStringExtra("videoformat");
            camFormatIndex=data.getIntExtra("camFormatIndex",0);
            imageWidth=data.getIntExtra("imageWidth",0);
            imageHeight=data.getIntExtra("imageHeight",0);
            camFrameIndex=data.getIntExtra("camFrameIndex",0);
            camFrameInterval=data.getIntExtra("camFrameInterval",0);
            packetsPerRequest=data.getIntExtra("packetsPerRequest",0);
            maxPacketSize=data.getIntExtra("maxPacketSize",0);
            activeUrbs=data.getIntExtra("activeUrbs",0);
            deviceName=data.getStringExtra("deviceName");
            bUnitID = data.getByteExtra("bUnitID",(byte) 0);
            bTerminalID = data.getByteExtra("bTerminalID",(byte)0);
            bNumControlTerminal = data.getByteArrayExtra("bNumControlTerminal");
            bNumControlUnit = data.getByteArrayExtra("bNumControlUnit");
            bStillCaptureMethod = data.getByteExtra("bStillCaptureMethod",(byte) 0);


            tv.setText("Your current Values are:\n\nPackets Per Request = " + packetsPerRequest +"\nActive Urbs = " + activeUrbs +
                    "\nAltSetting = " + camStreamingAltSetting + "\nMaximal Packet Size = " + maxPacketSize + "\nVideoformat = " + videoformat + "\nCamera Format Index = " + camFormatIndex + "\n" +
                    "Camera FrameIndex = " + camFrameIndex + "\nImage Width = "+ imageWidth + "\nImage Height = " + imageHeight + "\nCamera Frame Interval = " + camFrameInterval)          ;
            tv.setTextColor(Color.BLACK);

        }
        if (requestCode == ActivityStartIsoStreamRequestCode && resultCode == RESULT_OK && data != null) {
            boolean exit = data.getBooleanExtra("closeProgram",false);
            if (exit == true) finish();
        }
    }

    ////////////////   BUTTONS  //////////////////////////////////////////7

    public void viewPrivatePolicy(View view) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(getApplicationContext(),
                PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    public void viewReadme (View view) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(getApplicationContext(),
                ReadMeActivity.class);
        startActivity(intent);
    }


    public void setUpTheUsbDevice(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (showStoragePermissionRead() && showStoragePermissionWrite() && showCameraPermissionCamera()) {

                Intent intent = new Intent(this, SetUpTheUsbDevice.class);
                Bundle bundle=new Bundle();
                bundle.putBoolean("edit", true);
                bundle.putInt("camStreamingAltSetting",camStreamingAltSetting);
                bundle.putString("videoformat",videoformat);
                bundle.putInt("camFormatIndex",camFormatIndex);
                bundle.putInt("imageWidth",imageWidth);
                bundle.putInt("imageHeight",imageHeight);
                bundle.putInt("camFrameIndex",camFrameIndex);
                bundle.putInt("camFrameInterval",camFrameInterval);
                bundle.putInt("packetsPerRequest",packetsPerRequest);
                bundle.putInt("maxPacketSize",maxPacketSize);
                bundle.putInt("activeUrbs",activeUrbs);
                bundle.putString("deviceName",deviceName);
                bundle.putByte("bUnitID",bUnitID);
                bundle.putByte("bTerminalID",bTerminalID);
                bundle.putByteArray("bNumControlTerminal", bNumControlTerminal);
                bundle.putByteArray("bNumControlUnit", bNumControlUnit);
                bundle.putByte("bStillCaptureMethod",bStillCaptureMethod);

                intent.putExtra("bun",bundle);
                super.onResume();
                startActivityForResult(intent, ActivitySetUpTheUsbDeviceRequestCode);
            }

        }
        else if (showStoragePermissionRead() && showStoragePermissionWrite()) {

            Intent intent = new Intent(this, SetUpTheUsbDevice.class);
            Bundle bundle=new Bundle();
            bundle.putBoolean("edit", true);
            bundle.putInt("camStreamingAltSetting",camStreamingAltSetting);
            bundle.putString("videoformat",videoformat);
            bundle.putInt("camFormatIndex",camFormatIndex);
            bundle.putInt("imageWidth",imageWidth);
            bundle.putInt("imageHeight",imageHeight);
            bundle.putInt("camFrameIndex",camFrameIndex);
            bundle.putInt("camFrameInterval",camFrameInterval);
            bundle.putInt("packetsPerRequest",packetsPerRequest);
            bundle.putInt("maxPacketSize",maxPacketSize);
            bundle.putInt("activeUrbs",activeUrbs);
            bundle.putString("deviceName",deviceName);
            bundle.putByte("bUnitID",bUnitID);
            bundle.putByte("bTerminalID",bTerminalID);
            bundle.putByteArray("bNumControlTerminal", bNumControlTerminal);
            bundle.putByteArray("bNumControlUnit", bNumControlUnit);
            bundle.putByte("bStillCaptureMethod",bStillCaptureMethod);

            intent.putExtra("bun",bundle);
            super.onResume();
            startActivityForResult(intent, ActivitySetUpTheUsbDeviceRequestCode);
        }
        else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(this, SetUpTheUsbDevice.class);
            Bundle bundle=new Bundle();
            bundle.putBoolean("edit", true);
            bundle.putInt("camStreamingAltSetting",camStreamingAltSetting);
            bundle.putString("videoformat",videoformat);
            bundle.putInt("camFormatIndex",camFormatIndex);
            bundle.putInt("imageWidth",imageWidth);
            bundle.putInt("imageHeight",imageHeight);
            bundle.putInt("camFrameIndex",camFrameIndex);
            bundle.putInt("camFrameInterval",camFrameInterval);
            bundle.putInt("packetsPerRequest",packetsPerRequest);
            bundle.putInt("maxPacketSize",maxPacketSize);
            bundle.putInt("activeUrbs",activeUrbs);
            bundle.putString("deviceName",deviceName);
            bundle.putByte("bUnitID",bUnitID);
            bundle.putByte("bTerminalID",bTerminalID);
            bundle.putByteArray("bNumControlTerminal", bNumControlTerminal);
            bundle.putByteArray("bNumControlUnit", bNumControlUnit);
            bundle.putByte("bStillCaptureMethod",bStillCaptureMethod);

            intent.putExtra("bun",bundle);
            super.onResume();
            startActivityForResult(intent, ActivitySetUpTheUsbDeviceRequestCode);
        }

    }

    public void log(String msg) {
        Log.i("UVC_Camera_Main", msg);
    }


    public void restoreCameraSettings (View view) {
        if (showStoragePermissionRead() && showStoragePermissionWrite()) {
            SaveToFile  stf;
            stf = new SaveToFile(this, this);
            stf.restoreValuesFromFile();
            stf = null;
        }
        else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            SaveToFile  stf;
            stf = new SaveToFile(this, this);
            stf.restoreValuesFromFile();
            stf = null;
        }
    }





    public void isoStream(View view){

        if (showStoragePermissionRead() && showStoragePermissionWrite()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if(!showCameraPermissionCamera()) return;
            }
            if (camFormatIndex == 0 || camFrameIndex == 0 ||camFrameInterval == 0 ||packetsPerRequest == 0 ||maxPacketSize == 0 ||imageWidth == 0 || activeUrbs == 0 ) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv = (ZoomTextView) findViewById(R.id.textDarstellung);
                        tv.setText("Values for the camera not correctly setted !!\nPlease set up the values for the Camera first.\nTo Set Up the Values press the Settings Button and click on 'Set up with Uvc Values' or 'Edit / Save / Restor' and 'Edit Save'");  }
                });
            } else {
                // TODO Auto-generated method stub
                Intent intent = new Intent(this, Start_Iso_StreamActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("camStreamingAltSetting",camStreamingAltSetting);
                bundle.putString("videoformat",videoformat);
                bundle.putInt("camFormatIndex",camFormatIndex);
                bundle.putInt("imageWidth",imageWidth);
                bundle.putInt("imageHeight",imageHeight);
                bundle.putInt("camFrameIndex",camFrameIndex);
                bundle.putInt("camFrameInterval",camFrameInterval);
                bundle.putInt("packetsPerRequest",packetsPerRequest);
                bundle.putInt("maxPacketSize",maxPacketSize);
                bundle.putInt("activeUrbs",activeUrbs);
                bundle.putByte("bUnitID",bUnitID);
                bundle.putByte("bTerminalID",bTerminalID);
                bundle.putByteArray("bNumControlTerminal", bNumControlTerminal);
                bundle.putByteArray("bNumControlUnit", bNumControlUnit);
                bundle.putByte("bStillCaptureMethod",bStillCaptureMethod);

                intent.putExtra("bun",bundle);
                startActivityForResult(intent, ActivityStartIsoStreamRequestCode);
            }
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (camFormatIndex == 0 || camFrameIndex == 0 ||camFrameInterval == 0 ||packetsPerRequest == 0 ||maxPacketSize == 0 ||imageWidth == 0 || activeUrbs == 0 ) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv = (ZoomTextView) findViewById(R.id.textDarstellung);
                        tv.setText("Values for the camera not correctly setted !!\nPlease set up the values for the Camera first.\nTo Set Up the Values press the Settings Button and click on 'Set up with Uvc Values' or 'Edit / Save / Restor' and 'Edit Save'");  }
                });
            } else {
                // TODO Auto-generated method stub
                Intent intent = new Intent(this, Start_Iso_StreamActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("camStreamingAltSetting",camStreamingAltSetting);
                bundle.putString("videoformat",videoformat);
                bundle.putInt("camFormatIndex",camFormatIndex);
                bundle.putInt("imageWidth",imageWidth);
                bundle.putInt("imageHeight",imageHeight);
                bundle.putInt("camFrameIndex",camFrameIndex);
                bundle.putInt("camFrameInterval",camFrameInterval);
                bundle.putInt("packetsPerRequest",packetsPerRequest);
                bundle.putInt("maxPacketSize",maxPacketSize);
                bundle.putInt("activeUrbs",activeUrbs);
                bundle.putByte("bUnitID",bUnitID);
                bundle.putByte("bTerminalID",bTerminalID);
                bundle.putByteArray("bNumControlTerminal", bNumControlTerminal);
                bundle.putByteArray("bNumControlUnit", bNumControlUnit);
                bundle.putByte("bStillCaptureMethod",bStillCaptureMethod);

                intent.putExtra("bun",bundle);
                startActivityForResult(intent, ActivityStartIsoStreamRequestCode);
            }
        }

    }

    public void displayMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Main.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setTextTextView () {
        tv.setText("Your current Values are:\n\nPackets Per Request = " + packetsPerRequest +"\nActive Urbs = " + activeUrbs +
                "\nAltSetting = " + camStreamingAltSetting + "\nMaximal Packet Size = " + maxPacketSize + "\nVideoformat = " + videoformat + "\nCamera Format Index = " + camFormatIndex + "\n" +
                "Camera FrameIndex = " + camFrameIndex + "\nImage Width = "+ imageWidth + "\nImage Height = " + imageHeight + "\nCamera Frame Interval = " +
                camFrameInterval + "\n\nUVC Values:\nbUnitID = " + bUnitID + "\nbTerminalID = " + bTerminalID);
        tv.setTextColor(darker(Color.GREEN, 100));
    }

    private boolean showStoragePermissionRead() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showExplanation("Permission Needed:", "Read External Storage", Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_STORAGE_READ);
                return false;
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_STORAGE_READ);
                return false;
            }
        } else {
            //Toast.makeText(Main.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private boolean showStoragePermissionWrite() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permission Needed:", "Write to External Storage", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_STORAGE_WRITE);
                return false;
            } else {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_STORAGE_WRITE);
                return false;
            }
        } else {
            //Toast.makeText(Main.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private boolean showCameraPermissionCamera() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                showExplanation("Permission Needed:", "Camera", Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA);
                return false;
            } else {
                requestPermission(Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA);
                return false;
            }
        } else {
            //Toast.makeText(Main.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    public static int darker (int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }


}
