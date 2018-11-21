package com.book.jogodedamas;

import android.graphics.Bitmap;
import android.graphics.Point;

public class Imagem {

    private Point dimensoes = new Point();
    private Bitmap imagem;

    public Imagem(Bitmap imagem){

        this.imagem = imagem;
        //dimensoes da imagem

        dimensoes.set(imagem.getWidth(),imagem.getHeight());
    }


    public Bitmap getImagem() { return imagem; }

    public Point getDimensoes() { return dimensoes; }
}
