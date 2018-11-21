package com.book.jogodedamas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;


public class AtividadeJogo extends Atividade {
    private VisaoJogo visao; //visão do jogo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        telaCheia(); // tela cheia
        telaAcesa(); // tela acesa

        visao = new VisaoJogo(this);// passa essa atividade como contexto para gameview

        setContentView(visao);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {

            visao.realizarJogada(event.getX(),event.getY());
        }

        return true;
    }
}


