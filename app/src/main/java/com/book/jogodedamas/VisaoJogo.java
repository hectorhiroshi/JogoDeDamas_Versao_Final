package com.book.jogodedamas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;


import java.util.ArrayList;
import java.util.Random;

public class VisaoJogo extends Visao implements Runnable {

    public VisaoJogo(Context context) {

        super(context);
    }

    private ArrayList<Peça> peçasVermelhas = new ArrayList<>(); // oponente
    private ArrayList<Peça> peçasBrancas = new ArrayList<>();
    ArrayList<Peça> peçasPossiveis = new ArrayList<>(); // peças que podem comer outras peças
    ArrayList<Casa> lugaresPossiveisRainha = new ArrayList<>();
    ArrayList<Casa> comerPecaRainha = new ArrayList<>();

    private Casa[][] casa = new Casa[8][8];
    private Peça peçaSelecionada = null;
    private Peça peçaDeletar = null;

    private Imagem tabuleiro;
    private Imagem minhaVezImg;
    private Imagem vezDoOponenteImg;

    private boolean selecionar = false;

    private int larguraTabuleiro = 680;
    private int alturaTabuleiro = 680;

    private Rect areaDeDesenho = new Rect();

    private RectF posicaoTabuleiro = new RectF();
    private RectF posicaoPeça = new RectF();

    private boolean minhaVez=true;

    //desenhar retangulos no local da movimentação
    Casa esquerda = null;
    Casa direita = null;

    // retangulos float do aviso de vez
    RectF minhaVezRect = new RectF();
    RectF vezDoOponent = new RectF();

    private boolean jogarNovamente = false;
    private boolean jogarNovamenteRainha = false;

    RectF vitoria = new RectF();
    RectF derrota = new RectF();

    private Imagem vit;
    private Imagem derrt;

    private boolean venceu=false;
    private boolean perdeu = false;


    @Override
    public void setup(){ // usado para criar as configurações do desenho

        larguraTabuleiro = getDimensoesDaTela().x - 40;
        alturaTabuleiro =  larguraTabuleiro;

        EncontrarImagem novaImagem = getNovaImagem();
        //adicionar imagem do tabuleiro
        tabuleiro = novaImagem.retorneImagem(R.drawable.tabuleirov2);
        minhaVezImg = novaImagem.retorneImagem(R.drawable.suavez);
        vezDoOponenteImg= novaImagem.retorneImagem(R.drawable.vezdooponente);

        // adicionar imagem das peças vermelhas, id do jogador
        for(int i =0; i < 12; i++){

            Peça peça = new Peça();
            peça.setImagemPeça(novaImagem.retorneImagem(R.drawable.pecavermelha));
            peça.setChaveDaPeça(i);
            peça.setJogador(1);
            peçasVermelhas.add(peça);

        }
        // adicionar imagem das peças azuis
        for(int i =0; i < 12; i++){

            Peça peça = new Peça();
            peça.setImagemPeça(novaImagem.retorneImagem(R.drawable.pecaazul));
            peça.setChaveDaPeça(i);
            peça.setJogador(2);
            peçasBrancas.add(peça);

        }

        Point dimensoesDaTela = getDimensoesDaTela();
        //bolinha no centro da tela
        Point centro = new Point(dimensoesDaTela.x / 2, dimensoesDaTela.y/2);

       // posição do tabuleiro na tela
        int esquerda = centro.x - larguraTabuleiro/2;
        int cima= centro.y - alturaTabuleiro/2;
        int direita=centro.x+larguraTabuleiro/2;
        int baixo=centro.y+alturaTabuleiro/2;

        posicaoTabuleiro.set(esquerda,cima,direita,baixo);

        vit = novaImagem.retorneImagem(R.drawable.avisodevitoria);
        derrt= novaImagem.retorneImagem(R.drawable.avisodederrota);

        // posicao dos avisos de turno
        minhaVezRect.set(centro.x - 105,
                          posicaoTabuleiro.bottom,
                         centro.x+105,
                  posicaoTabuleiro.bottom+120);

        vezDoOponent.set(centro.x - 105,
                         posicaoTabuleiro.top-120,
                             centro.x+120,
                               posicaoTabuleiro.top);

        vitoria.set(centro.x-105,
                centro.y - 60,
                centro.x + 105,
                centro.y + 60);

        derrota.set(centro.x-105,
                centro.y - 60,
                centro.x + 105,
                centro.y + 60);

        configurarPeças(centro); // posição das peças com o canvas
        configurarTabuleiro(centro); // matriz
    }

    @Override
    public void update(Canvas canvas) { // agora utilizando o SGRenderer para desenhar

        //metodo step é sobrescrito da classe SGView e vai desenhar os retangulos
        // metodo do loop de jogo

        Desenhar desenhar = getNovoDesenho();
        desenhar.novoDesenho(canvas, Color.BLACK);

        //mTempImageSource = tamanho do tabuleiro => (0,0) até (486,440)
        //posicao do tabuleiro é iniciada em setup e fica no meio
        areaDeDesenho.set(0,0,486,485);
        desenhar.desenharImagens(tabuleiro, posicaoTabuleiro,areaDeDesenho);

        // desenhar as peças em suas posições atuais
        if(peçaDeletar != null){

            peçasBrancas.remove(peçaDeletar);
            peçasVermelhas.remove(peçaDeletar);
            peçaDeletar = null;


        }

        for(int i =0; i < peçasVermelhas.size();i++) {


                areaDeDesenho.set(0, 0, 61, 62);
                desenhar.desenharImagens(peçasVermelhas.get(i).getImagemPeça(),
                        peçasVermelhas.get(i).getPosicaoPeça(),areaDeDesenho);


        }

        for(int i =0; i < peçasBrancas.size();i++) {

                areaDeDesenho.set(0, 0, 61, 62);
                desenhar.desenharImagens(peçasBrancas.get(i).getImagemPeça(),
                        peçasBrancas.get(i).getPosicaoPeça(),areaDeDesenho);


        }

        //desenhar quadros amarelos nos possíveis locais de movimentação
        if(esquerda != null){
            desenhar.desenharQuadros(Color.YELLOW,esquerda.getPosicao());

        }

        if(direita != null){

           desenhar.desenharQuadros(Color.YELLOW,direita.getPosicao());

        }

        if(!this.comerPecaRainha.isEmpty()){

            for(Casa c: comerPecaRainha){

                desenhar.desenharQuadros(Color.YELLOW,c.getPosicao());
            }
        }

        if(!this.lugaresPossiveisRainha.isEmpty()){

           for(Casa c: lugaresPossiveisRainha){

               desenhar.desenharQuadros(Color.YELLOW,c.getPosicao());
           }
        }
        areaDeDesenho.set(0,0,210,120);

        if(minhaVez){

            desenhar.desenharImagens(minhaVezImg,minhaVezRect,areaDeDesenho);
        }else{

            desenhar.desenharImagens(vezDoOponenteImg,vezDoOponent,areaDeDesenho);
        }

        areaDeDesenho.set(0,0,210,120);

        if(venceu){
            desenhar.desenharImagens(vit,vitoria,areaDeDesenho);
        }
        if(perdeu){
            desenhar.desenharImagens(derrt,derrota,areaDeDesenho);
        }

        //finalizar desenho

    }

    public void configurarTabuleiro(Point centro){

        int x =1;
        int y=1;
        RectF posicaoCasa;
        Point referencial = new Point();

        referencial.x = centro.x - larguraTabuleiro /2;
        referencial.y = centro.y - alturaTabuleiro /2;

        for(int i =0; i < 8; i++){

            y = 1;

            for(int j =0; j < 8; j++){

                posicaoCasa = this.calcularPosicao(referencial,x,y);

                casa[i][j] = new Casa();

                casa[i][j].setPosicao(posicaoCasa);

                casa[i][j].setX(i);
                casa[i][j].setY(j);

                y = y + 2;

            }
            x = x + 2;
        }


        //posição das peças vermelhas na matriz      // peça armazena a sua posição
        casa[0][0].setPeça(peçasVermelhas.get(0));   peçasVermelhas.get(0).setXY(0,0);
        casa[2][0].setPeça(peçasVermelhas.get(1));   peçasVermelhas.get(1).setXY(2,0);
        casa[4][0].setPeça(peçasVermelhas.get(2));   peçasVermelhas.get(2).setXY(4,0);
        casa[6][0].setPeça(peçasVermelhas.get(3));   peçasVermelhas.get(3).setXY(6,0);
        casa[1][1].setPeça(peçasVermelhas.get(4));   peçasVermelhas.get(4).setXY(1,1);
        casa[3][1].setPeça(peçasVermelhas.get(5));   peçasVermelhas.get(5).setXY(3,1);
        casa[5][1].setPeça(peçasVermelhas.get(6));   peçasVermelhas.get(6).setXY(5,1);
        casa[7][1].setPeça(peçasVermelhas.get(7));   peçasVermelhas.get(7).setXY(7,1);
        casa[0][2].setPeça(peçasVermelhas.get(8));   peçasVermelhas.get(8).setXY(0,2);
        casa[2][2].setPeça(peçasVermelhas.get(9));   peçasVermelhas.get(9).setXY(2,2);
        casa[4][2].setPeça(peçasVermelhas.get(10));  peçasVermelhas.get(10).setXY(4,2);
        casa[6][2].setPeça(peçasVermelhas.get(11));  peçasVermelhas.get(11).setXY(6,2);

        // posição das peças brancas na matriz

        casa[1][5].setPeça(peçasBrancas.get(0));     peçasBrancas.get(0).setXY(1,5);
        casa[3][5].setPeça(peçasBrancas.get(1));     peçasBrancas.get(1).setXY(3,5);
        casa[5][5].setPeça(peçasBrancas.get(2));     peçasBrancas.get(2).setXY(5,5);
        casa[7][5].setPeça(peçasBrancas.get(3));     peçasBrancas.get(3).setXY(7,5);
        casa[0][6].setPeça(peçasBrancas.get(4));     peçasBrancas.get(4).setXY(0,6);
        casa[2][6].setPeça(peçasBrancas.get(5));     peçasBrancas.get(5).setXY(2,6);
        casa[4][6].setPeça(peçasBrancas.get(6));     peçasBrancas.get(6).setXY(4,6);
        casa[6][6].setPeça(peçasBrancas.get(7));     peçasBrancas.get(7).setXY(6,6);
        casa[1][7].setPeça(peçasBrancas.get(8));     peçasBrancas.get(8).setXY(1,7);
        casa[3][7].setPeça(peçasBrancas.get(9));     peçasBrancas.get(9).setXY(3,7);
        casa[5][7].setPeça(peçasBrancas.get(10));    peçasBrancas.get(10).setXY(5,7);
        casa[7][7].setPeça(peçasBrancas.get(11));    peçasBrancas.get(11).setXY(7,7);
    }

    // invocado pelo setup e desenha as peças em suas posições corretas
    public void configurarPeças(Point point){

        // posicionar peças, encontrar fórmula matematica
       Point p = new Point();

       // ponto (0,0) da imagem do tabuleiro mas não da tela
       p.x = point.x - larguraTabuleiro /2 ;
       p.y = point.y - alturaTabuleiro / 2 ;

       RectF rec = new RectF();

       rec = calcularPosicao(p,1,1);
       peçasVermelhas.get(0).setPosicaoPeça(rec);

        rec = calcularPosicao(p,5,1);
        peçasVermelhas.get(1).setPosicaoPeça(rec);

        rec = calcularPosicao(p,9,1);
        peçasVermelhas.get(2).setPosicaoPeça(rec);

        rec = calcularPosicao(p,13,1);
        peçasVermelhas.get(3).setPosicaoPeça(rec);

        rec = calcularPosicao(p,3,3);
        peçasVermelhas.get(4).setPosicaoPeça(rec);

        rec = calcularPosicao(p,7,3);
        peçasVermelhas.get(5).setPosicaoPeça(rec);

        rec = calcularPosicao(p,11,3);
        peçasVermelhas.get(6).setPosicaoPeça(rec);

        rec = calcularPosicao(p,15,3);
        peçasVermelhas.get(7).setPosicaoPeça(rec);

        rec = calcularPosicao(p,1,5);
        peçasVermelhas.get(8).setPosicaoPeça(rec);

        rec = calcularPosicao(p,5,5);
        peçasVermelhas.get(9).setPosicaoPeça(rec);

        rec = calcularPosicao(p,9,5);
        peçasVermelhas.get(10).setPosicaoPeça(rec);

        rec = calcularPosicao(p,13,5);
        peçasVermelhas.get(11).setPosicaoPeça(rec);

        // peças brancas

        rec = calcularPosicao(p,3,11);
        peçasBrancas.get(0).setPosicaoPeça(rec);

        rec = calcularPosicao(p,7,11);
        peçasBrancas.get(1).setPosicaoPeça(rec);

        rec = calcularPosicao(p,11,11);
        peçasBrancas.get(2).setPosicaoPeça(rec);

        rec = calcularPosicao(p,15,11);
        peçasBrancas.get(3).setPosicaoPeça(rec);

        rec = calcularPosicao(p,1,13);
        peçasBrancas.get(4).setPosicaoPeça(rec);

        rec = calcularPosicao(p,5,13);
        peçasBrancas.get(5).setPosicaoPeça(rec);

        rec = calcularPosicao(p,9,13);
        peçasBrancas.get(6).setPosicaoPeça(rec);

        rec = calcularPosicao(p,13,13);
        peçasBrancas.get(7).setPosicaoPeça(rec);

        rec = calcularPosicao(p,3,15);
        peçasBrancas.get(8).setPosicaoPeça(rec);

        rec = calcularPosicao(p,7,15);
        peçasBrancas.get(9).setPosicaoPeça(rec);

        rec = calcularPosicao(p,11,15);
        peçasBrancas.get(10).setPosicaoPeça(rec);

        rec = calcularPosicao(p,15,15);
        peçasBrancas.get(11).setPosicaoPeça(rec);


    }

    private RectF calcularPosicao(Point p,int x, int y){

        Point p1 = new Point();

        p1.x = p.x + (larguraTabuleiro/16) *x;
        p1.y = p.y + (alturaTabuleiro/16) * y;

        float esquerda = p1.x - (larguraTabuleiro/8)/2;
        float cima = p1.y - (alturaTabuleiro/8)/2;
        float direita=p1.x +((larguraTabuleiro/8))/2;
        float baixo= p1.y + ((alturaTabuleiro/8))/2;

        return new RectF(esquerda,cima,direita+3f,baixo+3f);
    }


    public void realizarJogada(float x, float y) {


        // verificar se é minha vez
        if(minhaVez){
        // verificar se alguma(s) peça(s) podem comer outras peças


            Casa ladoEsquerdo=null;
            Casa ladoDireito = null;
            boolean booleano = true; // verificar se a peça ja foi adicionada na lista para comer peças
            boolean comerPeca = false;
            Peça peça=null;

        RectF peçaAtual;
        EncontrarImagem novaImagem = getNovaImagem();


        if (selecionar == false) {

            peçasPossiveis = new ArrayList<>();
            // verificar a possibilidade de comer alguma peça
            for(int i =0; i < peçasBrancas.size(); i ++){

                peça = peçasBrancas.get(i);

                if(!peça.isRainha()) {

                    // verificar se pode comer alguma peça à direita
                    if (peça.getPosX() < 6 & peça.getPosY() > 1) {
                        if (casa[peça.getPosX() + 1][peça.getPosY() - 1].getPeça() != null) {
                            if (casa[peça.getPosX() + 1][peça.getPosY() - 1].getPeça().getJogador() == 1) {
                                if (casa[peça.getPosX() + 2][peça.getPosY() - 2].getPeça() == null) {

                                    comerPeca = true;
                                    peça.setDireita(true);
                                    peçasPossiveis.add(peça);
                                }

                            }

                        }

                    }

                    // verificar se pode comer alguma peça à  esquerda
                    if (peça.getPosX() > 1 & peça.getPosY() > 1) {
                        if (casa[peça.getPosX() - 1][peça.getPosY() - 1].getPeça() != null) {
                            if (casa[peça.getPosX() - 1][peça.getPosY() - 1].getPeça().getJogador() == 1) {
                                if (casa[peça.getPosX() - 2][peça.getPosY() - 2].getPeça() == null) {

                                    comerPeca = true;
                                    peça.setEsquerda(true);
                                    peçasPossiveis.add(peça);
                                }

                            }

                        }
                    }

                }else{
                    peça.setArrayRainha(this.comerPecaComoRainha(peça));

                    if(!peça.getComerPecaRainha().isEmpty()){
                        peçasPossiveis.add(peça);
                    }
                }
            }

            // se existe peça para pegar
              if(!peçasPossiveis.isEmpty()){

                  for (int i = 0; i < peçasPossiveis.size(); i++) {

                      peçaAtual = peçasPossiveis.get(i).getPosicaoPeça();


                      if (x >= peçaAtual.left & x <= peçaAtual.right &
                              y >= peçaAtual.top & y <= peçaAtual.bottom) {

                          this.peçaSelecionada = peçasPossiveis.get(i);
                          selecionar = true;

                          if (!peçaSelecionada.isRainha()) {

                          if (peçaSelecionada.getDireita()) {

                              direita = casa[peçaSelecionada.getPosX() + 2][peçaSelecionada.getPosY() - 2];

                          }

                          //casa para movimentar à esquerda
                          if (peçaSelecionada.getEsquerda()) {

                              esquerda = casa[peçaSelecionada.getPosX() - 2][peçaSelecionada.getPosY() - 2];


                          }

                      }else{

                            this.comerPecaRainha = peçasPossiveis.get(i).getComerPecaRainha();
                          }

                          break;
                      }

                      minhaVez = true;
                  }

              }else {

                  // se não existir peça para pegar
                  for (int i = 0; i < peçasBrancas.size(); i++) {

                          peçaAtual = peçasBrancas.get(i).getPosicaoPeça();


                          if (x >= peçaAtual.left & x <= peçaAtual.right &
                                  y >= peçaAtual.top & y <= peçaAtual.bottom) {


                              this.peçaSelecionada = peçasBrancas.get(i);
                              selecionar = true;

                              if (peçaSelecionada.isRainha()) {

                                  this.movimentacaoRainha();
                              } else {

                                  if (peçaSelecionada.getPosX() < 7 & peçaSelecionada.getPosY() > 0) {


                                      if (casa[peçaSelecionada.getPosX() + 1]
                                              [peçaSelecionada.getPosY() - 1].getPeça() == null) {


                                          direita = casa[peçaSelecionada.getPosX() + 1]
                                                  [peçaSelecionada.getPosY() - 1];

                                      }

                                  }

                                  //casa para movimentar à esquerda
                                  if (peçaSelecionada.getPosX() > 0 & peçaSelecionada.getPosY() > 0) {

                                      if (casa[peçaSelecionada.getPosX() - 1]
                                              [peçaSelecionada.getPosY() - 1].getPeça() == null) {


                                          esquerda = casa[peçaSelecionada.getPosX() - 1]
                                                  [peçaSelecionada.getPosY() - 1];
                                      }

                                  }


                              }
                              break;
                          }

                      minhaVez = true;
                  }
              }

        } else {

            if (peçaSelecionada.isRainha()) {

                if(peçasPossiveis.isEmpty()) {

                    this.andarComARainha(x, y);

                }else{

                    this.comerPeçaRainha(x,y);
                }

            } else {
                // casa atual da minha peça
                Casa c = casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()];
                // casa À direita
                Casa c1 = null;
                // casa à esquerda
                Casa c2 = null;

                if (jogarNovamente) {

                    peçasPossiveis.add(peçaSelecionada);

                    comerPeca = true;

                } else if (!peçasPossiveis.isEmpty()) {


                    comerPeca = true;
                }
                // se for possível comer peça
                if (comerPeca) {

                    if (peçaSelecionada.getDireita() == true) {

                        c1 = casa[peçaSelecionada.getPosX() + 2][peçaSelecionada.getPosY() - 2];
                    } else {
                        c1 = null;
                    }
                    if (peçaSelecionada.getEsquerda() == true) {

                        c2 = casa[peçaSelecionada.getPosX() - 2][peçaSelecionada.getPosY() - 2];
                    } else {
                        c2 = null;
                    }
                } else {

                    // casa para movimentar à direita
                    if (peçaSelecionada.getPosX() < 7 & peçaSelecionada.getPosY() > 0) {

                        if (casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1]
                                .getPeça() != null) {

                            c1 = null;

                        } else {

                            c1 = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() - 1];
                        }
                    }

                    //casa para movimentar à esquerda
                    if (peçaSelecionada.getPosX() > 0 & peçaSelecionada.getPosY() > 0) {

                        // impedir passar por cima de uma peça própria
                        if (casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1]
                                .getPeça() != null) {

                            c2 = null;
                        } else {
                            c2 = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() - 1];
                        }

                    }
                }
                RectF atualizar;

                if (c1 != null) { // jogada para à direita

                    atualizar = c1.getPosicao();

                    if (x >= atualizar.left & x <= atualizar.right &
                            y >= atualizar.top & y <= atualizar.bottom) {

                        //avançar 1 casa à cima e à direita na matriz
                        c1.setPeça(peçaSelecionada);

                        // na classe da peça atualizar o seu local na matriz

                        // se foi possivel comer peça entao
                        if (peçasPossiveis.isEmpty() == false) {

                            peçaSelecionada.setXY
                                    (peçaSelecionada.getPosX() + 2, peçaSelecionada.getPosY() - 2);

                            // deletar peça do array inimigo

                            peçaDeletar = casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() + 1]
                                    .getPeça();

                            casa[peçaSelecionada.getPosX() - 1][peçaSelecionada.getPosY() + 1].removePeça();

                            peçaSelecionada.setPosicaoPeça
                                    (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()]
                                            .getPosicao());


                        } else {
                            // se não foi possível comer peça
                            peçaSelecionada.setXY
                                    (peçaSelecionada.getPosX() + 1, peçaSelecionada.getPosY() - 1);

                            //atualizar posição da peça
                            peçaSelecionada.setPosicaoPeça
                                    (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].getPosicao());
                        }


                        c.removePeça();
                        minhaVez = false;

                        if (peçaSelecionada.getPosY() == 0) {

                            peçaSelecionada.setImagemPeça(novaImagem.retorneImagem(
                                    (R.drawable.pecaazulrainha)));
                            peçaSelecionada.setRainha(true);
                        }


                    }

                }
                if (c2 != null) {// jogada para à esquerda

                    atualizar = c2.getPosicao();

                    if (x >= atualizar.left & x <= atualizar.right &
                            y >= atualizar.top & y <= atualizar.bottom) {

                        //avançar 1 casa à cima e à esquerda na matriz
                        c2.setPeça(peçaSelecionada);

                        // na classe da peça atualizar o seu local na matriz

                        //se for possivel comer peça
                        if (peçasPossiveis.isEmpty() == false) {

                            peçaSelecionada.setXY
                                    (peçaSelecionada.getPosX() - 2, peçaSelecionada.getPosY() - 2);

                            // deletar peça do array inimigo
                            peçaDeletar = casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() + 1]
                                    .getPeça();

                            casa[peçaSelecionada.getPosX() + 1][peçaSelecionada.getPosY() + 1].removePeça();

                            //atualizar posição da peça
                            peçaSelecionada.setPosicaoPeça
                                    (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].getPosicao());

                        } else {
                            // se não for
                            peçaSelecionada.setXY
                                    (peçaSelecionada.getPosX() - 1, peçaSelecionada.getPosY() - 1);

                            //atualizar posição da peça
                            peçaSelecionada.setPosicaoPeça
                                    (casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].getPosicao());
                        }

                        c.removePeça();
                        minhaVez = false;

                        //verificar se a peça se tornou rainha e atualizar a imagem
                        if (peçaSelecionada.getPosY() == 0) {

                            peçaSelecionada.setImagemPeça(novaImagem.retorneImagem
                                    (R.drawable.pecaazulrainha));
                            peçaSelecionada.setRainha(true);
                        }

                    }
                }


                //c.removePeça();

                for (Peça p : peçasPossiveis) {
                    p.setEsquerda(false);
                    p.setDireita(false);
                }

                boolean passarVez = false;
                jogarNovamente = false;

                if (comerPeca == true) {

                    passarVez = this.comerPeçaNovamente();

                }

                if (!passarVez) {

                    selecionar = false;
                    peçaSelecionada = null;
                    esquerda = direita = null;
                    this.lugaresPossiveisRainha.clear();

                    if (minhaVez == false) { // jogada do computador


                        Thread th = new Thread(this);
                        th.start();

                    }

                } // se for para passar a vez

            } // se a peça não for rainha
        }// fim do else " if selecionar == false "

    } // se for a minha vez

    } // fim do metodo realizar jogada

    // jogada do computador
    private void inteligenciaArtificial() {

        // algoritmo: 1- Escolher uma peça aleatória 2- verificar possibilidade 3- movimentar

        if(peçasVermelhas.isEmpty()){
            venceu = true;

            while(true){}
        }


        boolean fimDaJogadaComputador = false;
        boolean selecaoDePeca = true;
        Peça pecaEscolhida = null;

        int escolhidaX = 0;
        int escolhidaY = 0;

        Casa esquerda = null;
        Casa direita = null;

        Random rand = new Random();
        int movimentar;
        ArrayList<Peça> comerPeca = new ArrayList<>();

        // verificar se o oponente pode pegar alguma peça
        for(int i=0; i < peçasVermelhas.size();i++) {

            Peça p = peçasVermelhas.get(i);

            // se a peça atual for rainha
            if (p.isRainha()) {

                if(!this.comerPecaComoRainha(p).isEmpty()) {
                    p.setArrayRainha(this.comerPecaComoRainha(p));
                    comerPeca.add(p);
                }

            }else{
            // comer peça à esquerda
            if (p.getPosX() > 1 & p.getPosY() < 6) { //verificar limite do tabuleiro
                if (casa[p.getPosX() - 1][p.getPosY() + 1].getPeça() != null) { // verificar se existe peça
                    if (casa[p.getPosX() - 1][p.getPosY() + 1].getPeça().getJogador() == 2) {// verificar se é do oponente
                        if (casa[p.getPosX() - 2][p.getPosY() + 2].getPeça() == null) {// verificar se existe uma peça após a mesma

                            p.setEsquerda(true);
                            comerPeca.add(p);
                        }
                    }
                }
            }

            //comer peça à direita
            if (p.getPosX() < 6 & p.getPosY() < 6) {

                if (casa[p.getPosX() + 1][p.getPosY() + 1].getPeça() != null) { // verificar se existe peça
                    if (casa[p.getPosX() + 1][p.getPosY() + 1].getPeça().getJogador() == 2) {// verificar se é do oponente
                        if (casa[p.getPosX() + 2][p.getPosY() + 2].getPeça() == null) {// verificar se existe uma peça após a mesma

                            p.setDireita(true);
                            comerPeca.add(p);
                        }
                    }
                }
            }
        }

        }

        // comer peça se for possível

        if(!comerPeca.isEmpty()){

            Peça p = comerPeca.get(rand.nextInt(comerPeca.size()));

            if(!p.isRainha()) {
                if (p.getEsquerda()) { // comer peça à esquerda

                    casa[p.getPosX()][p.getPosY()].removePeça();
                    p.setXY(p.getPosX() - 2, p.getPosY() + 2);
                    casa[p.getPosX()][p.getPosY()].setPeça(p);
                    peçasBrancas.remove(casa[p.getPosX() + 1][p.getPosY() - 1].getPeça());
                    casa[p.getPosX() + 1][p.getPosY() - 1].removePeça();
                    p.setPosicaoPeça(casa[p.getPosX()][p.getPosY()].getPosicao());

                } else if (p.getDireita()) { // comer peça à direita

                    casa[p.getPosX()][p.getPosY()].removePeça();
                    p.setXY(p.getPosX() + 2, p.getPosY() + 2);
                    casa[p.getPosX()][p.getPosY()].setPeça(p);
                    peçasBrancas.remove(casa[p.getPosX() - 1][p.getPosY() - 1].getPeça());
                    casa[p.getPosX() - 1][p.getPosY() - 1].removePeça();
                    p.setPosicaoPeça(casa[p.getPosX()][p.getPosY()].getPosicao());
                }

                while (this.comerPeçaNovamenteInimigo(p)) {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (p.getEsquerda()) { // comer peça à esquerda

                        casa[p.getPosX()][p.getPosY()].removePeça();
                        p.setXY(p.getPosX() - 2, p.getPosY() + 2);
                        casa[p.getPosX()][p.getPosY()].setPeça(p);
                        peçasBrancas.remove(casa[p.getPosX() + 1][p.getPosY() - 1].getPeça());
                        casa[p.getPosX() + 1][p.getPosY() - 1].removePeça();
                        p.setPosicaoPeça(casa[p.getPosX()][p.getPosY()].getPosicao());

                    } else if (p.getDireita()) { // comer peça à direita

                        casa[p.getPosX()][p.getPosY()].removePeça();
                        p.setXY(p.getPosX() + 2, p.getPosY() + 2);
                        casa[p.getPosX()][p.getPosY()].setPeça(p);
                        peçasBrancas.remove(casa[p.getPosX() - 1][p.getPosY() - 1].getPeça());
                        casa[p.getPosX() - 1][p.getPosY() - 1].removePeça();
                        p.setPosicaoPeça(casa[p.getPosX()][p.getPosY()].getPosicao());
                    }
                }

                if (p.getPosY() == 7) {

                    p.setRainha(true);
                    p.setImagemPeça(this.getNovaImagem().retorneImagem(R.drawable.pecavermelharainha));
                }

            }else{ // se o p for rainha

                 while(!p.getComerPecaRainha().isEmpty()){

                     try {
                         Thread.sleep(1200);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }


                 Casa c = p.getComerPecaRainha().get(rand.nextInt(p.getComerPecaRainha().size()));

                if(c.getX() > p.getPosX() & c.getY() > p.getPosY()){

                    peçasBrancas.remove(casa[c.getX()-1][c.getY()-1].getPeça());
                    casa[c.getX()-1][c.getY()-1].removePeça();

                    casa[p.getPosX()][p.getPosY()].removePeça();
                    p.setXY(c.getX(),c.getY());
                    p.setPosicaoPeça(c.getPosicao());

                    casa[p.getPosX()][p.getPosY()].
                            setPeça(p);


                }
                //baixo esquerda
                else if(c.getX() <p.getPosX() & c.getY() > p.getPosY()){

                    peçasBrancas.remove(casa[c.getX()+1][c.getY()-1].getPeça());
                    casa[c.getX()+1][c.getY()-1].removePeça();

                    casa[p.getPosX()][p.getPosY()].removePeça();
                    p.setXY(c.getX(),c.getY());
                    p.setPosicaoPeça(c.getPosicao());

                    casa[p.getPosX()][p.getPosY()].
                            setPeça(p);
                }
                // cima esquerda
                else if(c.getX() < p.getPosX() & c.getY() < p.getPosY()){


                    peçasBrancas.remove(casa[c.getX()+1][c.getY()+1].getPeça());
                    casa[c.getX()+1][c.getY()+1].removePeça();

                    casa[p.getPosX()][p.getPosY()].removePeça();
                    p.setXY(c.getX(),c.getY());
                    p.setPosicaoPeça(c.getPosicao());

                    casa[p.getPosX()][p.getPosY()].
                            setPeça(p);
                }
                // cima direita
                else if(c.getX() > p.getPosX() & c.getY() < p.getPosY()){

                    peçasBrancas.remove(casa[c.getX()-1][c.getY()+1].getPeça());
                    casa[c.getX()-1][c.getY()+1].removePeça();

                    casa[p.getPosX()][p.getPosY()].removePeça();
                    p.setXY(c.getX(),c.getY());
                    p.setPosicaoPeça(c.getPosicao());

                    casa[p.getPosX()][p.getPosY()].
                            setPeça(p);

                }

                 p.setArrayRainha(this.comerPecaComoRainha(p));

                 }
            }

        }else {
            // se não, movimentar peça normalmente
            while (selecaoDePeca) {
                // escolher uma peça aleatoria
                pecaEscolhida = peçasVermelhas.get((rand.nextInt(peçasVermelhas.size())));
                // pegar a posicao X e Y dessa peça
                escolhidaX = pecaEscolhida.getPosX();
                escolhidaY = pecaEscolhida.getPosY();

                if(pecaEscolhida.isRainha()){
                    if(!this.movimentacaoRainhaInimiga(pecaEscolhida).isEmpty()){

                        selecaoDePeca = false;
                    }

                }else{

                if (escolhidaX < 7 & escolhidaY < 7) {

                    direita = casa[escolhidaX + 1][escolhidaY + 1];

                    if (direita.getPeça() != null) {

                        direita = null;

                    } else {

                        casa[escolhidaX][escolhidaY].removePeça();
                        selecaoDePeca = false;
                    }
                }

                if (escolhidaX > 0 & escolhidaY < 7) {


                    esquerda = casa[escolhidaX - 1][escolhidaY + 1];

                    if (esquerda.getPeça() != null) {
                        esquerda = null;

                    } else {

                        casa[escolhidaX][escolhidaY].removePeça();
                        selecaoDePeca = false;
                    }


                }
                }
            }
            // movimentar peça para um local aleatório, se o random for = 0 direita, se for =1 esquerda
            // se cair 0 e não puder movimentar para a direita, movimentar para a esquerda
            movimentar = rand.nextInt(2);

            if(pecaEscolhida.isRainha()){

                ArrayList<Casa> listaAtual = this.movimentacaoRainhaInimiga(pecaEscolhida);
                Casa c = listaAtual.get(rand.nextInt(listaAtual.size()));

                casa[pecaEscolhida.getPosX()][pecaEscolhida.getPosY()].removePeça();

                pecaEscolhida.setXY(c.getX(),c.getY());

                pecaEscolhida.setPosicaoPeça
                        (casa[pecaEscolhida.getPosX()][pecaEscolhida.getPosY()].getPosicao());

                casa[pecaEscolhida.getPosX()][pecaEscolhida.getPosY()].setPeça(pecaEscolhida);

            }else {
                if (movimentar == 0) {

                    if (direita != null) {

                        direita.setPeça(pecaEscolhida);
                        pecaEscolhida.setXY(escolhidaX + 1, escolhidaY + 1);
                        pecaEscolhida.setPosicaoPeça(direita.getPosicao());

                        //peça se do oponente se torna rainha
                        if (pecaEscolhida.getPosY() == 7) {

                            pecaEscolhida.setImagemPeça
                                    (getNovaImagem().retorneImagem(R.drawable.pecavermelharainha));

                            pecaEscolhida.setRainha(true);

                        }

                    } else {

                        esquerda.setPeça(pecaEscolhida);
                        pecaEscolhida.setXY(escolhidaX - 1, escolhidaY + 1);
                        pecaEscolhida.setPosicaoPeça(esquerda.getPosicao());

                        //peça se do oponente se torna rainha
                        if (pecaEscolhida.getPosY() == 7) {

                            pecaEscolhida.setImagemPeça
                                    (getNovaImagem().retorneImagem(R.drawable.pecavermelharainha));

                            pecaEscolhida.setRainha(true);
                        }
                    }

                } else if (movimentar == 1) {

                    if (esquerda != null) {

                        esquerda.setPeça(pecaEscolhida);
                        pecaEscolhida.setXY(escolhidaX - 1, escolhidaY + 1);
                        pecaEscolhida.setPosicaoPeça(esquerda.getPosicao());

                        //peça se do oponente se torna rainha
                        if (pecaEscolhida.getPosY() == 7) {

                            pecaEscolhida.setImagemPeça
                                    (getNovaImagem().retorneImagem(R.drawable.pecavermelharainha));

                            pecaEscolhida.setRainha(true);

                        }

                    } else {

                        direita.setPeça(pecaEscolhida);
                        pecaEscolhida.setXY(escolhidaX + 1, escolhidaY + 1);
                        pecaEscolhida.setPosicaoPeça(direita.getPosicao());

                        //peça se do oponente se torna rainha
                        if (pecaEscolhida.getPosY() == 7) {

                            pecaEscolhida.setImagemPeça
                                    (getNovaImagem().retorneImagem(R.drawable.pecavermelharainha));

                            pecaEscolhida.setRainha(true);

                        }
                    }
                }
            }
        }

        for(Peça p: peçasVermelhas){
            p.setEsquerda(false);
            p.setDireita(false);

            if(p.isRainha()){
                p.getComerPecaRainha().clear();
            }
        }

        peçasPossiveis.clear();

        if(peçasBrancas.isEmpty()){

            perdeu = true;

            while(true){

            }
        }


        minhaVez = true;
    }


    @Override
    public void run() {



        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        inteligenciaArtificial();
    }

    public boolean comerPeçaNovamente(){

        esquerda = direita = null;

        boolean dir = false;
        boolean esquerd= false;
        //comer novamente uma peça à direita
          if(peçaSelecionada.getPosX() < 6 & peçaSelecionada.getPosY() > 1){

              if(casa[peçaSelecionada.getPosX()+1][peçaSelecionada.getPosY()-1].getPeça()!=null){

                  if(casa[peçaSelecionada.getPosX()+1][peçaSelecionada.getPosY()-1].getPeça().
                               getJogador()==1){

                      if(casa[peçaSelecionada.getPosX()+2][peçaSelecionada.getPosY()-2].
                              getPeça()==null){

                          direita = casa[peçaSelecionada.getPosX()+2][peçaSelecionada.getPosY()-2];
                          dir = true;
                      }


                  }
              }
          }
        if(peçaSelecionada.getPosX() > 1 & peçaSelecionada.getPosY() > 1){

            if(casa[peçaSelecionada.getPosX()-1][peçaSelecionada.getPosY()-1].getPeça()!=null){

                if(casa[peçaSelecionada.getPosX()-1][peçaSelecionada.getPosY()-1].getPeça().
                        getJogador()==1){

                    if(casa[peçaSelecionada.getPosX()-2][peçaSelecionada.getPosY()-2].
                            getPeça()==null){

                        esquerda = casa[peçaSelecionada.getPosX()-2][peçaSelecionada.getPosY()-2];
                        esquerd = true;
                    }


                }
            }
        }
        if(esquerd == true || dir == true){

              peçaSelecionada.setDireita(dir);
              peçaSelecionada.setEsquerda(esquerd);
              jogarNovamente = true;
              minhaVez = true;

              return true;
        }

        return false;
    }

    public boolean comerPeçaNovamenteInimigo(Peça peça){

        for(Peça p: peçasVermelhas){
            p.setEsquerda(false);
            p.setDireita(false);
        }

        boolean dir = false;
        boolean esquerd= false;
        //comer novamente uma peça à direita
        if(peça.getPosX() < 6 & peça.getPosY() < 6){

            if(casa[peça.getPosX()+1][peça.getPosY()+1].getPeça()!=null){

                if(casa[peça.getPosX()+1][peça.getPosY()+1].getPeça().
                        getJogador()==2){

                    if(casa[peça.getPosX()+2][peça.getPosY()+2].
                            getPeça()==null){

                        dir = true;
                    }


                }
            }
        }
        if(peça.getPosX() > 1 & peça.getPosY() < 6){

            if(casa[peça.getPosX()-1][peça.getPosY()+1].getPeça()!=null){

                if(casa[peça.getPosX()-1][peça.getPosY()+1].getPeça().
                        getJogador()==2){

                    if(casa[peça.getPosX()-2][peça.getPosY()+2].
                            getPeça()==null){

                        esquerd = true;
                    }


                }
            }
        }
        if(esquerd == true || dir == true){

            peça.setDireita(dir);
            peça.setEsquerda(esquerd);

            return true;
        }

        return false;
    }

    public void movimentacaoRainha(){ // lugares possiveis para movimentação da rainha

       int x = peçaSelecionada.getPosX();
       int y = peçaSelecionada.getPosY();

       int posX = x;
       int posY = y;

       Casa casa1 = null;
        // movimentação cima esquerda
        while(posX > 0 & posY > 0){

             posX = posX -1;
             posY = posY -1;
             casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                this.lugaresPossiveisRainha.add(casa1);

            }else{
                break;
            }

        }

        // movimentação cima direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 7 & posY > 0){

            posX = posX +1;
            posY = posY -1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                this.lugaresPossiveisRainha.add(casa1);

            }else{
                break;
            }

        }
        // movimentação baixo esquerda

        posX = x;
        posY = y;
        casa1 = null;

        while(posX > 0 & posY < 7){

            posX = posX -1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                this.lugaresPossiveisRainha.add(casa1);

            }else{
                break;
            }

        }
        //movimentação baixo direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 7 & posY < 7){

            posX = posX +1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                this.lugaresPossiveisRainha.add(casa1);

            }else{
                break;
            }

        }

    }

    public ArrayList<Casa> movimentacaoRainhaInimiga(Peça p){

        int x = p.getPosX();
        int y = p.getPosY();

        int posX = x;
        int posY = y;

        ArrayList<Casa> lugaresPossiv = new ArrayList<>();

        Casa casa1 = null;
        // movimentação cima esquerda
        while(posX > 0 & posY > 0){

            posX = posX -1;
            posY = posY -1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                lugaresPossiv.add(casa1);

            }else{
                break;
            }

        }

        // movimentação cima direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 7 & posY > 0){

            posX = posX +1;
            posY = posY -1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                lugaresPossiv.add(casa1);

            }else{
                break;
            }

        }
        // movimentação baixo esquerda

        posX = x;
        posY = y;
        casa1 = null;

        while(posX > 0 & posY < 7){

            posX = posX -1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

                lugaresPossiv.add(casa1);

            }else{
                break;
            }

        }
        //movimentação baixo direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 7 & posY < 7){

            posX = posX +1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() == null){

               lugaresPossiv.add(casa1);

            }else{
                break;
            }

        }
        return lugaresPossiv;
    }

    public void andarComARainha(float x, float y){

        Casa c = null;

          for(Casa casa: lugaresPossiveisRainha){

              if(x >= casa.getPosicao().left & x <= casa.getPosicao().right &
                      y >= casa.getPosicao().top & y <= casa.getPosicao().bottom){

                  c = casa;
                  break;
              }
          }

          lugaresPossiveisRainha.clear();

          if(c != null){
              casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();

              peçaSelecionada.setXY(c.getX(),c.getY());
              peçaSelecionada.setPosicaoPeça(c.getPosicao());
              c.setPeça(peçaSelecionada);

              selecionar = false;
              minhaVez = false;

              Thread th = new Thread(this);
              th.start();

          }else{

             selecionar = false;
             minhaVez = true;
          }
    }

    public ArrayList<Casa> comerPecaComoRainha(Peça pe){

        int x = pe.getPosX();
        int y = pe.getPosY();
        int jogador = 1;

        if(!minhaVez){
            jogador = 2;
        }

        ArrayList<Casa> casas = new ArrayList<>();

        int posX = x;
        int posY = y;

        Casa casa1 = null;
        // movimentação cima esquerda
        while(posX > 1 & posY > 1){

            posX = posX -1;
            posY = posY -1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() != null){
                if(casa1.getPeça().getJogador() == jogador){
                   if(casa[casa1.getX()-1][casa1.getY()-1].getPeça() == null){


                       casas.add(casa[casa1.getX()-1][casa1.getY()-1]);
                       break;
                   }else{
                       break;
                   }

                }else{
                    break;
                }

            }

        }

        // movimentação cima direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 6 & posY > 1){

            posX = posX +1;
            posY = posY -1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() != null){
                if(casa1.getPeça().getJogador() == jogador){
                    if(casa[casa1.getX()+1][casa1.getY()-1].getPeça() == null){


                        casas.add(casa[casa1.getX()+1][casa1.getY()-1]);
                        break;
                    }else{
                        break;
                    }

                }else{
                    break;
                }

            }

        }
        // movimentação baixo esquerda

        posX = x;
        posY = y;
        casa1 = null;

        while(posX > 1 & posY < 6){

            posX = posX -1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() != null){
                if(casa1.getPeça().getJogador() == jogador){
                    if(casa[casa1.getX()-1][casa1.getY()+1].getPeça() == null){


                        casas.add(casa[casa1.getX()-1][casa1.getY()+1]);
                        break;
                    }else{
                        break;
                    }

                }else{
                    break;
                }

            }

        }
        //movimentação baixo direita
        posX = x;
        posY = y;
        casa1 = null;

        while(posX < 6 & posY < 6){

            posX = posX +1;
            posY = posY +1;
            casa1 = casa[posX][posY];

            if(casa1.getPeça() != null){
                if(casa1.getPeça().getJogador() == jogador){
                    if(casa[casa1.getX()+1][casa1.getY()+1].getPeça() == null){


                        casas.add(casa[casa1.getX()+1][casa1.getY()+1]);
                        break;
                    }else{
                        break;
                    }

                }else{
                    break;
                }

            }

        }

        return casas;
    }

    public void comerPeçaRainha(float x, float y){

        boolean comer = false;

        for(Casa c: this.comerPecaRainha){

            if(x >= c.getPosicao().left & x <= c.getPosicao().right &
                    y >= c.getPosicao().top & y <= c.getPosicao().bottom){

                comer = true;
                // baixo direita
                if(c.getX() > peçaSelecionada.getPosX() & c.getY() > peçaSelecionada.getPosY()){

                    peçasVermelhas.remove(casa[c.getX()-1][c.getY()-1].getPeça());
                    casa[c.getX()-1][c.getY()-1].removePeça();

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();
                    peçaSelecionada.setXY(c.getX(),c.getY());
                    peçaSelecionada.setPosicaoPeça(c.getPosicao());

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].
                            setPeça(peçaSelecionada);


                }
                //baixo esquerda
                else if(c.getX() < peçaSelecionada.getPosX() & c.getY() > peçaSelecionada.getPosY()){

                    peçasVermelhas.remove(casa[c.getX()+1][c.getY()-1].getPeça());
                    casa[c.getX()+1][c.getY()-1].removePeça();

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();
                    peçaSelecionada.setXY(c.getX(),c.getY());
                    peçaSelecionada.setPosicaoPeça(c.getPosicao());

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].
                            setPeça(peçaSelecionada);
                }
                // cima esquerda
                else if(c.getX() < peçaSelecionada.getPosX() & c.getY() < peçaSelecionada.getPosY()){


                    peçasVermelhas.remove(casa[c.getX()+1][c.getY()+1].getPeça());
                    casa[c.getX()+1][c.getY()+1].removePeça();

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();
                    peçaSelecionada.setXY(c.getX(),c.getY());
                    peçaSelecionada.setPosicaoPeça(c.getPosicao());

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].
                            setPeça(peçaSelecionada);
                }
                // cima direita
                else if(c.getX() > peçaSelecionada.getPosX() & c.getY() < peçaSelecionada.getPosY()){

                    peçasVermelhas.remove(casa[c.getX()-1][c.getY()+1].getPeça());
                    casa[c.getX()-1][c.getY()+1].removePeça();

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].removePeça();
                    peçaSelecionada.setXY(c.getX(),c.getY());
                    peçaSelecionada.setPosicaoPeça(c.getPosicao());

                    casa[peçaSelecionada.getPosX()][peçaSelecionada.getPosY()].
                            setPeça(peçaSelecionada);

                }

            }

        }

        for(Peça p:peçasPossiveis){

            if(p.isRainha()){
                p.getComerPecaRainha().clear();
            }else{

                p.setEsquerda(false);
                p.setDireita(false);
            }

        }


        if(comer == false){

            minhaVez = true;
            selecionar = false;

            if(jogarNovamenteRainha) {

                selecionar = true;

            }




        }else{

            if(!this.comerPecaComoRainha(peçaSelecionada).isEmpty()){

                minhaVez = true;
                selecionar = true;
                comerPecaRainha = this.comerPecaComoRainha(peçaSelecionada);
                peçasPossiveis.clear();
                peçasPossiveis.add(peçaSelecionada);
                jogarNovamenteRainha = true;

            }else {

                jogarNovamenteRainha = false;
                minhaVez = false;
                selecionar = false;


                Thread th = new Thread(this);
                th.start();
                peçaSelecionada = null;
                comerPecaRainha.clear();

            }
        }



    }

}
