package com.jaay.beats.core;

public class FFT {
    private int n, m;
    private double[] cos, sin;

    public FFT(int n) {
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));

        cos = new double[n / 2];
        sin = new double[n / 2];

        for (int i = 0; i < n / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }
    }

    public void fft(double[] real, double[] imag) {
        int i, j, k, n1, n2, a;
        double c, s, t1, t2;

        j = 0;
        for (i = 1; i < n - 1; i++) {
            n1 = n / 2;
            while (j >= n1) {
                j -= n1;
                n1 /= 2;
            }
            j += n1;

            if (i < j) {
                t1 = real[i];
                real[i] = real[j];
                real[j] = t1;
                t1 = imag[i];
                imag[i] = imag[j];
                imag[j] = t1;
            }
        }

        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++) {
            n1 = n2;
            n2 += n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (k = j; k < n; k += n2) {
                    t1 = c * real[k + n1] - s * imag[k + n1];
                    t2 = s * real[k + n1] + c * imag[k + n1];
                    real[k + n1] = real[k] - t1;
                    imag[k + n1] = imag[k] - t2;
                    real[k] += t1;
                    imag[k] += t2;
                }
            }
        }
    }

    public void ifft(double[] real, double[] imag) {
        for (int i = 0; i < real.length; i++) {
            imag[i] = -imag[i];
        }
        fft(real, imag);
        for (int i = 0; i < real.length; i++) {
            real[i] /= n;
            imag[i] /= n;
        }
    }
}

