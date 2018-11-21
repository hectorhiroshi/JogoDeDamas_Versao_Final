package com.book.jogodedamas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class Visao extends View {

    private boolean startSetup = false;
    private Point dimensoesDaTela = new Point();
    private Desenhar novoDesenho; // desenha na tela
    private EncontrarImagem novaImagem; // encontra uma imagem no diretorio drawable-nodpi


    public Visao(Context contexto) {
        super(contexto);

        novoDesenho = new Desenhar();
        novaImagem = new EncontrarImagem(contexto);

    }


    @Override
    protected void onDraw(Canvas canvas) {

        update(canvas);
        invalidate();

    }


    public void update(Canvas canvas){}
    public void setup(){}

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        dimensoesDaTela.set(w,h);

        if(startSetup == false){

            setup();
        }
    }


    public Point getDimensoesDaTela() { return dimensoesDaTela; }

    public Desenhar getNovoDesenho() { return novoDesenho; }

    public EncontrarImagem getNovaImagem() { return novaImagem; }
}
