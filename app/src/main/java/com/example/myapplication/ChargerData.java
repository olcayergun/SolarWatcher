package com.example.myapplication;


import java.math.BigDecimal;
import java.util.Date;

//
// Data structure for invoices
//

public class ChargerData {

    public int id;
    public String zaman;
    public String sarjAkimi;
    public String akuGerilimi;
    public String solarGerilimi;
    public String sebekeGerilimi;

    public ChargerData(int id, String msg) {
        this.id = id;
        this.zaman = null;
        this.sarjAkimi = msg;
        this.akuGerilimi = null;
        this.solarGerilimi = null;
        this.sebekeGerilimi = null;
    }

    public ChargerData(int id, String zaman, String sarjAkimi, String akuGerilimi, String solarGerilimi, String sebekeGerilimi) {
        this.id = id;
        this.zaman = zaman;
        this.sarjAkimi = sarjAkimi;
        this.akuGerilimi = akuGerilimi;
        this.solarGerilimi = solarGerilimi;
        this.sebekeGerilimi = sebekeGerilimi;
    }
}