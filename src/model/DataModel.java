/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


/**
 *
 * @author Odorifqi
 */
public class DataModel  {
    private double price;
    private String time;
    private double predicted;

    //konstruktor kelas dengan variabel data yang dibutuhkan
    public DataModel(String time, double price) {
        this.price = price;
        this.time = time;
    }
    
    public DataModel(String time, double price, double predicted) {
        this.price = price;
        this.time = time;
        this.predicted = predicted;
    }

    //Fungsi get untuk mengambil data menuju keluar kelas
    public double getPredicted() {return predicted;}
    public double getPrice() {return price;}
    public String getTime() {return time;}
}