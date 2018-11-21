package com.book.jogodedamas;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class Atividade extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // flags para tela cheia e remover barra de navegação
    public void telaCheia(){

        Window janela = getWindow();

        janela.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //remover o titulo do aplicativo
        janela.requestFeature(Window.FEATURE_NO_TITLE);
        //remover barra  de navegação comparando a versão da SDK
        if(Build.VERSION.SDK_INT>=19){

            janela.getDecorView().setSystemUiVisibility
                    (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    }

    // flag para manter tela acesa
    public void telaAcesa(){

        Window janela = getWindow();
        janela.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
