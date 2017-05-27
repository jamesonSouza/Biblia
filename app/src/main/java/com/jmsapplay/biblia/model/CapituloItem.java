package com.jmsapplay.biblia.model;

import java.util.ArrayList;

/**
 * Created by Jameson on 25/05/2017.
 */
public class CapituloItem {

    private int capitulo;
    private int idItem;

    public int getCapitulo(){
        return capitulo;
    }

    public void setCapitulo(int capitulo){
        this.capitulo = capitulo;
    }

    public int getIdItem(){
        return idItem;
    }
    public  void setIdItem(int item){
        this.idItem = item;
    }

    public CapituloItem(){}
    public CapituloItem(int cap, int item){
        this.capitulo = cap;
        this.idItem = item;
    }
}
