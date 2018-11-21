package com.book.jogodedamas;

import android.os.Bundle;
import android.view.MotionEvent;



public class AtividadeMultiplayer extends Atividade {

    private VisaoMultiplayer vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        telaAcesa();
        telaCheia();

        vm = new VisaoMultiplayer(this,this);

        setContentView(vm);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {

            vm.realizarJogada(event.getX(),event.getY());
        }

        return true;

    }

    // desconectar do servidor


    @Override
    protected void onDestroy() {

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                vm.getEscrever().println("sair");
            }
        }); th.start();


        super.onDestroy();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onPause() {

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                vm.getEscrever().println("sair");
            }
        });
        th.start();

        super.onPause();
    }
}
