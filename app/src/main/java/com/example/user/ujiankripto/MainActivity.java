package com.example.user.ujiankripto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseKirimJawaban;

    TextView pertanyaan;
    RadioGroup rg;
    RadioButton PilihanA, PilihanB, PilihanC, PilihanD;
    int nomor = 0;
    public static int hasil, benar, salah;

    // Data sooal, pilihan, dan jawaban
    String[] pertanyaan_kuis = new String[]{
            "1. Ibu kota Jawa Barat adalah ",
            "2. Unpad adalah singkatan dari",
            "3. Berikut adalah jurusan yang ada di Fakultas Ilmu Budaya, kecuali",
            "4. Nama rektor Univeritas Padjadjaran yang menjabat saat ini adalah",
            "5. Budi membeli onde dan terdapat 142 biji wijen di atasnya. Warna dari gedung PPBS adalah"
    };
    String[] pilihan_jawaban = new String[]{
            "Bandung","Jakarta","Surabaya","Jatinangor",
            "Universitas Padang","Universitas Padjadjaran","Universitas Pajajaran","Universitas Padjajaran",
            "Sastra Inggirs","Sastra Indonesia","Sastra Wibu","Sastra Jepang",
            "Tuan Tiga","Pak Three","Pak Tilu","Pak Tri",
            "Biru","Polkadot","Kuning","Pelangi"
    };
    String[] jawaban_benar = new String[]{
            "Bandung",
            "Universitas Padjadjaran",
            "Sastra Wibu",
            "Pak Tri",
            "Biru"
    };
    int[] jawaban_huruf = new int[]{
            65,    // A
            66,    // B
            67,    // C
            68,    // D
            65 };  // A
    int[] jawaban_dari_server = new int[]{};
    List<JawabanClass> listJawaban;
    int jawabanUser;

    // Enkripsi S-Des
    int K = 353;	// ASCII X (01011000) ditambah 01 diakhir (Master KEY)
    int m = 65;		// ASCII A (Plaintext Test)
    SDes A;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database
        listJawaban = new ArrayList<>();

        // Firebase
        databaseKirimJawaban = FirebaseDatabase.getInstance().getReference("kirimJawaban");

        //Ambil data dari Firebase Database
        databaseKirimJawaban.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Kalo update, clear dulu biar ga numpuk
                //respondenList.clear();

                // Kirim data perchild ke kelas responden
                int i = 0;
                for(DataSnapshot jawabanSnapshot : dataSnapshot.getChildren()){
                    //Responden responden = respondenSnapshot.getValue(Responden.class);
                    JawabanClass jawabanClass = jawabanSnapshot.getValue(JawabanClass.class);

                    listJawaban.add(jawabanClass);
                    //respondenList.add(responden);
                }

                //Masukin ke listViewnya
                /*RespondenList adapter = new RespondenList(DaftarRespondenActivity.this, respondenList);
                listViewResponden.setAdapter(adapter);*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Crypt S-Des
        A = new SDes(K);
        //Toast.makeText(this,"Key1 : " + SDes.printData(A.K1, 8), Toast.LENGTH_SHORT).show();

        // CBT nya
        pertanyaan = (TextView)findViewById(R.id.pertanyaan);
        rg = (RadioGroup) findViewById(R.id.radio_group);
        PilihanA = (RadioButton) findViewById(R.id.PilihanA);
        PilihanB = (RadioButton) findViewById(R.id.PilihanB);
        PilihanC = (RadioButton) findViewById(R.id.PilihanC);
        PilihanD = (RadioButton) findViewById(R.id.PilihanD);

        pertanyaan.setText(pertanyaan_kuis[nomor]);
        PilihanA.setText(pilihan_jawaban[0]);
        PilihanB.setText(pilihan_jawaban[1]);
        PilihanC.setText(pilihan_jawaban[2]);
        PilihanD.setText(pilihan_jawaban[3]);

        rg.check(0);
        benar = 0;
        salah = 0;
    }

    public void next(View view) {
        if (PilihanA.isChecked() || PilihanB.isChecked() || PilihanC.isChecked() || PilihanD.isChecked()) {
            RadioButton jawaban_user = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
            String ambil_jawaban_user = jawaban_user.getText().toString();

            // APasih yg user jawab
            if (PilihanA.isChecked())
                jawabanUser = 65;
            else if (PilihanB.isChecked())
                jawabanUser = 66;
            else if (PilihanC.isChecked())
                jawabanUser = 67;
            else if (PilihanD.isChecked())
                jawabanUser = 68;

            // Test enkripsi
            m = A.encrypt(jawabanUser);
            //Toast.makeText(this,"Encrypted : " + SDes.printData(m,8) ,Toast.LENGTH_SHORT).show();
            //submitJawaban(nomor, SDes.printData(m,8));


            // Penghitungan benar-salah
            rg.check(0);
            if (SDes.printData(m,8).equals(listJawaban.get(nomor).getEncrypted()) ){
                benar++;
                //Toast.makeText(this,"Betull (test)" ,Toast.LENGTH_SHORT).show();
            }
            else {
                salah++;
                //Toast.makeText(this, "Salahh(test)", Toast.LENGTH_SHORT).show();
            }

            // Next soalnya
            nomor++;
            if (nomor < pertanyaan_kuis.length) {
                // Pertanyaan selanjutnya
                pertanyaan.setText((pertanyaan_kuis[nomor]));
                PilihanA.setText(pilihan_jawaban[(nomor * 4) + 0]);
                PilihanB.setText(pilihan_jawaban[(nomor * 4) + 1]);
                PilihanC.setText(pilihan_jawaban[(nomor * 4) + 2]);
                PilihanD.setText(pilihan_jawaban[(nomor * 4) + 3]);

            } else {
                hasil = benar * 20;
                Intent selesai = new Intent(getApplicationContext(), HasilKuis.class);
                startActivity(selesai);
                finish();
            }
        }
        else{
            Toast.makeText(this,"Pilih Jawaban",Toast.LENGTH_SHORT).show();
        }
    }

    public void submitJawaban(int nomor, String kode){
        String no = Integer.toString(nomor);

        // Submit the data to Firebase database
        try{
            JawabanClass jawabanClass = new JawabanClass(nomor, kode);
            String num = Integer.toString(nomor);
            databaseKirimJawaban.child(num).setValue(jawabanClass);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
