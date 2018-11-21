package com.book.jogodedamas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Desenhar {


    private Canvas canvas;
    private Paint pincel = new Paint();
    private RectF areaParaDesenho = new RectF();

    public void novoDesenho(Canvas canvas, int CorDeFundo){

        this.canvas = canvas;
        canvas.drawColor(CorDeFundo);

    }

    //desenha retangulos float preenchidos
    public void desenharQuadros(int cor, RectF local){

        pincel.setColor(cor);
        pincel.setStyle(Paint.Style.FILL);
        canvas.drawRect(local, pincel);

    }

    public void desenharImagens(Imagem imagem,RectF local, Rect areaDaImagem ){


            Bitmap bitmap = imagem.getImagem();
            canvas.drawBitmap(bitmap, areaDaImagem,local,pincel);


    }
}
