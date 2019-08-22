package com.example.user.ujiankripto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {

    private ImageView image;
    private Button btn_submit;
    private EditText editnpm;
    private TextView nama, npm;

    // Validation
    private String getnpm, getNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onClikSubmit(View view){
        image = (ImageView) findViewById(R.id.foto);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        editnpm = (EditText) findViewById(R.id.innpm);
        nama = (TextView) findViewById(R.id.nama);
        npm = (TextView) findViewById(R.id.npm);

        getnpm = editnpm.getText().toString();

        if (getnpm.equals("140810160013")){
            image.setImageResource(R.drawable.raihan);
            nama.setText("Muhammad Raihan Akbar");
            getNama = "Muhammad Raihan Akbar";
            npm.setText(getnpm);
        }
        else{
            Toast.makeText(this,"Data tidak terdaftar", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClikMulai (View view){
        Button btn_mulai = (Button) findViewById(R.id.mulai);
        try{
            if (getnpm.equals("140810160013") || getNama.equals("Muhammad Raihan Akbar")){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
            else{
                Toast.makeText(this,"Data tidak terdaftar", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Login dengan NPM yang terdaftar", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClikPetunjuk (View view){
        Button btn_petunjuk = (Button) findViewById(R.id.petunjuk);
        Intent i = new Intent(getApplicationContext(), Petunjuk.class);
        startActivity(i);
    }
}
