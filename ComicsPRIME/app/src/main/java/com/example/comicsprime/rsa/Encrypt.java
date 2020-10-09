package com.example.comicsprime.rsa;

import static java.lang.Math.pow;

public class Encrypt {

    // Message to be encrypted
    private double msg;


    private static int gcd(int a, int h) {
        int temp;
        while (true) {
            temp = a % h;
            if (temp == 0)
                return h;
            a = h;
            h = temp;
        }
    }


    // Two random prime numbers
    int p = 11;
    int q = 19;
    // First part of public key:
    int n = p * q;

    // Finding other part of public key.
    // e stands for encrypt
    //private int e = 2;
    int mphi = (p - 1) * (q - 1);

    private static int updateE(int e, int mphi){
        while(e < mphi){
            // e must be co-prime to phi and smaller than phi.
            if (gcd(e, mphi) == 1) {
                break;
            } else {
                e++;
            }
        }

        return e;
    }

    int e = updateE(2, mphi);



    // Private key (d stands for decrypt)
    // choosing d such that it satisfies
    // d*e = 1 + k * totient
    int k = 2;  // A constant value
    double d = (1 + (k * mphi)) / e;


    public Encrypt(double msg) {
        this.msg = msg;
    }

    public String encrypt() {
        double c = pow(this.msg, this.e);
        c = c % n;

        return Double.toString(c);
    }


//        // Decryption m = (c ^ d) % n
//        double m = pow(c, d);
//        m = fmod(m, n);
//        printf("\nOriginal Message Sent = %lf", m);


}
