package com.book.jogodedamas;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class EncontrarImagem {


    private Context contextoAtual;

    // construtor
    public EncontrarImagem(Context contexto){

     contextoAtual = contexto;

    }


    public Imagem retorneImagem(int enderecoRdrawable){

        Bitmap imagem = null;

        try{

            Resources r = contextoAtual.getResources();
            // codificar em inputStream
            InputStream codificar = r.openRawResource(enderecoRdrawable);
            // decodificar em bitmap
            imagem = BitmapFactory.decodeStream(codificar);

            codificar.close();

        }catch(IOException e){ }

        Imagem image = new Imagem(imagem);


        return image;
    }

    public Context getContextoAtual() { return contextoAtual; }
}
