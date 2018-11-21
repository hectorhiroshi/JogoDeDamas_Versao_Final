package com.book.jogodedamas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

public class VisaoMenu extends Visao {

    public VisaoMenu(Context context) {
        super(context);
    }

    Imagem fundo;
    Imagem solo;
    Imagem multiplayer;
    Imagem sair;

    RectF fundo1 = new RectF();
    RectF solo1 = new RectF();
    RectF multiplayer1 = new RectF();
    RectF sair1 = new RectF();

    Rect retang = new Rect();

    @Override
    public void setup() {

        EncontrarImagem localizador = getNovaImagem();

        //definir as imagens do menu
        fundo = localizador.retorneImagem((R.drawable.menufundo));
        solo = localizador.retorneImagem((R.drawable.solo));
        multiplayer = localizador.retorneImagem((R.drawable.multiplayer));
        sair = localizador.retorneImagem((R.drawable.sair));

        // definir o retangulo do fundo e de cada botão

        Point dimensoesImg = fundo.getDimensoes();
        Point centro = new Point();

        centro.set(getDimensoesDaTela().x /2, getDimensoesDaTela().y/2);

        fundo1.set(0,0,getDimensoesDaTela().x,getDimensoesDaTela().y);

        dimensoesImg = multiplayer.getDimensoes();

        multiplayer1.set(centro.x - dimensoesImg.x/2,
                         centro.y+0,
                        centro.x+dimensoesImg.x/2,
                     centro.y + dimensoesImg.y);

        dimensoesImg = solo.getDimensoes();


        solo1.set(centro.x - dimensoesImg.x/2,
                multiplayer1.top - dimensoesImg.y - (dimensoesImg.y/2),
                centro.x+dimensoesImg.x/2,
                multiplayer1.top - dimensoesImg.y - (dimensoesImg.y/2) + dimensoesImg.y);

        dimensoesImg = sair.getDimensoes();

        sair1.set(centro.x - dimensoesImg.x/2,
                multiplayer1.bottom + (dimensoesImg.y/2) ,
                centro.x+dimensoesImg.x/2,
                multiplayer1.bottom + (dimensoesImg.y/2)+ dimensoesImg.y);

    }

    @Override
    public void update(Canvas canvas) {

        Desenhar desenhar = getNovoDesenho();
        desenhar.novoDesenho(canvas, Color.BLACK);

        retang.set(0,0,fundo.getDimensoes().x,fundo.getDimensoes().y);
        desenhar.desenharImagens(fundo,fundo1,retang);

        retang.set(0,0,sair.getDimensoes().x,sair.getDimensoes().y);
        desenhar.desenharImagens(sair,sair1,retang);

        retang.set(0,0,solo.getDimensoes().x,solo.getDimensoes().y);
        desenhar.desenharImagens(solo,solo1,retang);

        retang.set(0,0,multiplayer.getDimensoes().x,multiplayer.getDimensoes().y);
        desenhar.desenharImagens(multiplayer,multiplayer1,retang);

    }


    public Acao selecionarOpcao(float x, float y){


        if(x >=  solo1.left & y>= solo1.top & x <= solo1.right & y <= solo1.bottom){

            return Acao.Solo;

        }else

        if(x >=  sair1.left & y>= sair1.top & x <= sair1.right & y <= sair1.bottom){

           return Acao.Sair;

        }else

        if(x >=  multiplayer1.left & y>= multiplayer1.top &
                x <= multiplayer1.right & y <= multiplayer1.bottom){

          return Acao.Multiplayer;
        }

        return null;
    }

    public enum Acao{

        Solo, Multiplayer, Sair
    }

}
