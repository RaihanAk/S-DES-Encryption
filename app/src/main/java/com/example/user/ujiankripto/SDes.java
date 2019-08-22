package com.example.user.ujiankripto;

import java.io.DataInputStream;

public class SDes {

    //inisialisasi P10, P8, P4, IP, IPinvers, EP, Matriks S0, S1
    public static int K1, K2;                                                        //inisialisasi variable kunci
    public static final int P10[] = { 3, 5, 2, 7, 4, 10, 1, 9, 8, 6};         // P10 disimpan di array
    public static final int P10max = 10;                                      // P10 10 bit
    public static final int P8[] = { 6, 3, 7, 4, 8, 5, 10, 9};                // Untuk permutasi P8 saat membangun kunci
    public static final int P8max = 10;
    public static final int P4[] = { 2, 4, 3, 1};                             //elemen P4 disimpan di array
    public static final int P4max = 4;
    public static final int IP[] = { 2, 6, 3, 1, 4, 8, 5, 7};                 // untuk initial permutation
    public static final int IPmax = 8;
    public static final int IPI[] = { 4, 1, 3, 5, 7, 2, 8, 6};                //IP invers
    public static final int IPImax = 8;
    public static final int EP[] = { 4, 1, 2, 3, 2, 3, 4, 1};                 // array untuk ekstarksi EP
    public static final int EPmax = 4;
    public static final int S0[][] = {{ 1, 0, 3, 2},{ 3, 2, 1, 0},{ 0, 2, 1,  //matriks S0
            3},{ 3, 1, 3, 2}};
    public static final int S1[][] = {{ 0, 1, 2, 3},{ 2, 0, 1, 3},{ 3, 0, 1,  //matriks S1
            2},{ 2, 1, 0, 3}};

    public static int permutasi( int x, int p[], int pmax){
        int y = 0;
        for( int i = 0; i < p.length; ++i) {
            y <<= 1;
            y |= (x >> (pmax - p[i])) & 1;
        }
        return y;
    }

    public static int F( int R, int K){                            //proses 4 bit bagian kanan->Ep->P4
        int t = permutasi( R, EP, EPmax) ^ K;
        int t0 = (t >> 4) & 0xF;
        int t1 = t & 0xF;
        t0 = S0[ ((t0 & 0x8) >> 2) | (t0 & 1) ][ (t0 >> 1) & 0x3 ];
        t1 = S1[ ((t1 & 0x8) >> 2) | (t1 & 1) ][ (t1 >> 1) & 0x3 ];
        t = permutasi( (t0 << 2) | t1,   P4, P4max);
        return t;
    }

    public static int fK( int m, int K){
        int L = (m >> 4) & 0xF;
        int R = m & 0xF;
        return ((L ^ F(R,K)) << 4) | R;
    }

    public static int SW( int x) {                  //swap
        return ((x & 0xF) << 4) | ((x >> 4) & 0xF);
    }

    public byte encrypt( int m) {
        m = permutasi( m, IP, IPmax);
        m = fK( m, K1);
        m = SW( m);
        m = fK( m, K2);
        m = permutasi( m, IPI, IPImax);
        return (byte) m;
    }


    public byte decrypt( int m) {
        m = permutasi( m, IP, IPmax);
        m = fK( m, K2);
        m = SW( m);
        m = fK( m, K1);
        m = permutasi( m, IPI, IPImax);
        return (byte) m;
    }

    public static String printData( int x, int n){
        int mask = 1 << (n-1);
        StringBuilder result = new StringBuilder();
        while( mask > 0) {
            result.append( ((x & mask) == 0) ? '0' : '1');
            mask >>= 1;
        }
        return result.toString();
    }

    public SDes(int K){                                 //Generate Key
        K = permutasi( K, P10, P10max);                 // Master Key masuk P10
        int t1 = (K >> 5) & 0x1F;                       //5 bit bagian kiri
        int t2 = K & 0x1F;                              //5 bit bagian kanan
        t1 = ((t1 & 0xF) << 1) | ((t1 & 0x10) >> 4);    //left shift 1 bagian kiri
        t2 = ((t2 & 0xF) << 1) | ((t2 & 0x10) >> 4);    //left shift 1 bagian kanan
        K1 = permutasi( (t1 << 5)| t2, P8, P8max);   //hasil left shift masuk P8 dan menjadi K1
        t1 = ((t1 & 0x7) << 2) | ((t1 & 0x18) >> 3);    //hasil left shift 1 tadi di left shift 2 (kiri)
        t2 = ((t2 & 0x7) << 2) | ((t2 & 0x18) >> 3);    //hasil left shift 1 tadi di left shift 2 (kanan)
        K2 = permutasi( (t1 << 5)| t2, P8, P8max);   //masuk P8 dan menjadi K2
    }
}
