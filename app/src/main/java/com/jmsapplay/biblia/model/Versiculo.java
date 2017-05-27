package com.jmsapplay.biblia.model;

/**
 * Created by Jameson on 25/05/2017.
 */
public class Versiculo {

    private int versiculo;
    private String textoVersiculo;

    public void setVersiculo(int vers){
        this.versiculo = vers;
    }
    public int getVersiculo(){
        return this.versiculo;
    }

    public void setTextoVersiculo(String texto){
        this.textoVersiculo = texto;
    }
    public String getTextoVersiculo(){
        return this.textoVersiculo;
    }
}
