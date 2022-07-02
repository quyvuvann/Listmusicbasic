 package com.example.music_mp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

 public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);

        runtimePermistion();

    }

     private void runtimePermistion() {
         Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
             @Override
             public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                 displaySong();
             }

             @Override
             public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
             }
         }).check();
     }
     public ArrayList<File> fileArraylistFile(File file){
        ArrayList<File> filelist = new ArrayList<>();
        File[] files = file.listFiles();
        for(File singlefile : files){
            if(singlefile.isDirectory() && !singlefile.isHidden()){
                filelist.addAll(fileArraylistFile(singlefile));
            }
            else{
                if(singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")){
                    filelist.add(singlefile);
                }
            }
        }
        return filelist;
     }
     public void displaySong(){
        final ArrayList<File> songs =   fileArraylistFile(Environment.getExternalStorageDirectory());
        items = new String[songs.size()];
        for(int i = 0;i<songs.size();i++){
            items[i] = songs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
         CustomAdapter customAdapter = new CustomAdapter();
         listView.setAdapter(customAdapter);
         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 String songname = (String) listView.getItemAtPosition(position);
                 startActivity(new Intent(MainActivity.this,Play_music.class)
                         .putExtra("songs",songs).putExtra("songname",songname).putExtra("pos",position));
             }
         });
     }
     class CustomAdapter extends BaseAdapter{

         @Override
         public int getCount() {
             return items.length;
         }

         @Override
         public Object getItem(int position) {
             return null;
         }

         @Override
         public long getItemId(int position) {
             return 0;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
             View view = getLayoutInflater().inflate(R.layout.item_list,null);
             TextView textView = view.findViewById(R.id.namesinge);
             textView.setSelected(true);
             textView.setText(items[position]);
             return view;
         }
     }
 }