package com.jmsapplay.biblia.model;

import java.util.ArrayList;

/**
 * Created by Jameson on 25/05/2017.
 */
public class Capitulo {

    private  int capitulo;

    private ArrayList<Versiculo> versiculos;


    public Capitulo() {
        this.versiculos = new ArrayList<Versiculo>();
    }

    public ArrayList<Versiculo> getVersiculos() {
        return versiculos;
    }

    public void setVersiculos(ArrayList<Versiculo> versiculos) {
        this.versiculos = versiculos;
    }

    public void setCapitulo(int idcap){
        this.capitulo = idcap;
    }
    public int getCapitulo(){
        return capitulo;
    }

    public void addVersiculo(Versiculo versiculo){
        versiculos.add(versiculo);
    }
}
