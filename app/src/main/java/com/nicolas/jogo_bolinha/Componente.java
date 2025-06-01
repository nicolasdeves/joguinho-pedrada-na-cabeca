package com.nicolas.jogo_bolinha;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Componente extends View {
    private Paint paint = new Paint();
    private float x, y;
    private Bitmap parado, cima, baixo, esquerda, direita;
    private Bitmap imagemAtual;

    private Bitmap imagemFundo;


    private Bitmap imagemPedra, imagemMoeda, imagemCoracao;
    private final int BONECO_LARGURA = 120;
    private final int BONECO_ALTURA = 150;

    private final int POSICAO_INICIAL_X = 300;
    private final int POSICAO_INICIAL_Y = 700;

    private SoundPool soundPool;
    private int somDeMoeda, somDeDano, somDeVida, somDeDerrota, somDeFundo;
    private int streamIdFundo = 0;

    private ArrayList<Pedra> pedras = new ArrayList<>();
    private ArrayList<Moeda> moedas = new ArrayList<>();
    private ArrayList<Coracao> coracoes = new ArrayList<>();
    private Random random = new Random();

    private int vidas = 3;
    private int pontos = 0;
    private int tempo = 0;
    private boolean gameOver = false;

    private Handler handler = new Handler();
    private int dificuldade = 0;

    private Runnable gerarPedras = new Runnable() {
        @Override
        public void run() {
            if (!gameOver && getWidth() > 0) {
                pedras.add(new Pedra(random.nextInt(getWidth() - 80), 0, imagemPedra, 10 + dificuldade));
                handler.postDelayed(this, 1500 - dificuldade * 50); // Aumenta dificuldade
            }
        }
    };

    private Runnable gerarMoedas = new Runnable() {
        @Override
        public void run() {
            if (!gameOver && getWidth() > 0) {
                moedas.add(new Moeda(random.nextInt(getWidth() - 60), 0, imagemMoeda));
                handler.postDelayed(this, 2500);
            }
        }
    };

    private Runnable gerarCoracoes = new Runnable() {
        @Override
        public void run() {
            if (!gameOver && getWidth() > 0 && random.nextFloat() < 0.2f) { // 20% de chance
                coracoes.add(new Coracao(random.nextInt(getWidth() - 60), 0, imagemCoracao));
            }
            handler.postDelayed(this, 10000); // Tenta gerar a cada 10 segundos
        }
    };

    private Runnable contarTempo = new Runnable() {
        @Override
        public void run() {
            if (!gameOver) {
                tempo++;
                if (tempo % 10 == 0 && dificuldade < 20) {
                    dificuldade++; // aumenta a velocidade das pedras a cada 10s
                }
                handler.postDelayed(this, 1000);
            }
        }
    };

    public Componente(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inicializar(context);
    }

    private void inicializar(Context context) {
        parado = BitmapFactory.decodeResource(context.getResources(), R.drawable.parado);
        cima = BitmapFactory.decodeResource(context.getResources(), R.drawable.cima);
        baixo = BitmapFactory.decodeResource(context.getResources(), R.drawable.baixo);
        esquerda = BitmapFactory.decodeResource(context.getResources(), R.drawable.esquerda);
        direita = BitmapFactory.decodeResource(context.getResources(), R.drawable.direita);

        imagemFundo = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);

        parado = Bitmap.createScaledBitmap(parado, BONECO_LARGURA, BONECO_ALTURA, true);
        cima = Bitmap.createScaledBitmap(cima, BONECO_LARGURA, BONECO_ALTURA, true);
        baixo = Bitmap.createScaledBitmap(baixo, BONECO_LARGURA, BONECO_ALTURA, true);
        esquerda = Bitmap.createScaledBitmap(esquerda, BONECO_LARGURA, BONECO_ALTURA, true);
        direita = Bitmap.createScaledBitmap(direita, BONECO_LARGURA, BONECO_ALTURA, true);

        soundPool = new SoundPool.Builder().setMaxStreams(4).build();
        somDeMoeda = soundPool.load(context, R.raw.moeda, 1);
        somDeDano = soundPool.load(context, R.raw.pedra, 1);
        somDeVida = soundPool.load(context, R.raw.vida, 1);
        somDeDerrota = soundPool.load(context, R.raw.derrota, 1);
        somDeFundo = soundPool.load(context, R.raw.fundo, 1);

        imagemAtual = parado;

        imagemPedra = BitmapFactory.decodeResource(context.getResources(), R.drawable.pedra);
        imagemPedra = Bitmap.createScaledBitmap(imagemPedra, 80, 80, true);

        imagemMoeda = BitmapFactory.decodeResource(context.getResources(), R.drawable.moeda);
        imagemMoeda = Bitmap.createScaledBitmap(imagemMoeda, 60, 60, true);

        imagemCoracao = BitmapFactory.decodeResource(context.getResources(), R.drawable.coracao);
        imagemCoracao = Bitmap.createScaledBitmap(imagemCoracao, 60, 60, true);

        x = POSICAO_INICIAL_X;
        y = POSICAO_INICIAL_Y;

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0 && sampleId == somDeFundo) {
                    streamIdFundo = soundPool.play(somDeFundo, 0.3f, 0.3f, 0, -1, 1f);
                }
            }
        });

        postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.post(gerarPedras);
                handler.post(gerarMoedas);
                handler.post(gerarCoracoes);
                handler.post(contarTempo);
            }
        }, 200); // aguarda o layout estar pronto
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (imagemFundo != null) {
            @SuppressLint("DrawAllocation") Rect destino = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(imagemFundo, null, destino, null);
        } else {
            // Se a imagem não carregou, pinta o fundo de cinza (fallback)
            paint.setARGB(255, 100, 100, 120);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }

        canvas.drawBitmap(imagemAtual, x, y, null);

        for (Pedra pedra : pedras) pedra.atualizar();
        for (Moeda moeda : moedas) moeda.atualizar();
        for (Coracao coracao : coracoes) coracao.atualizar();

        Iterator<Pedra> itPedra = pedras.iterator();
        while (itPedra.hasNext()) {
            Pedra pedra = itPedra.next();
            pedra.desenhar(canvas);
            if (pedra.y > getHeight()) itPedra.remove();
            if (colidiu(pedra.x, pedra.y, imagemPedra)) {
                itPedra.remove();
                soundPool.play(somDeDano, 1, 1, 0, 0, 1);
                vidas--;
                if (vidas <= 0) gameOver = true;
            }
        }

        Iterator<Moeda> itMoeda = moedas.iterator();
        while (itMoeda.hasNext()) {
            Moeda moeda = itMoeda.next();
            moeda.desenhar(canvas);
            if (moeda.y > getHeight()) itMoeda.remove();
            if (colidiu(moeda.x, moeda.y, imagemMoeda)) {
                itMoeda.remove();
                soundPool.play(somDeMoeda, 1, 1, 0, 0, 1);
                pontos++;
            }
        }

        Iterator<Coracao> itCoracao = coracoes.iterator();
        while (itCoracao.hasNext()) {
            Coracao coracao = itCoracao.next();
            coracao.desenhar(canvas);
            if (coracao.y > getHeight()) itCoracao.remove();
            if (colidiu(coracao.x, coracao.y, imagemCoracao)) {
                itCoracao.remove();
                if (vidas < 5) {
                    vidas++;
                    soundPool.play(somDeVida, 1, 1, 0, 0, 1);
                }
            }
        }

        paint.setARGB(255, 255, 255, 255);
        paint.setTextSize(40);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));

        int larguraTela = getWidth();

        int posYLinhaTopo = 30;

        int espacamentoCoracao = imagemCoracao.getWidth() + 10;

        for (int i = 0; i < vidas; i++) {
            int posXCoracao = larguraTela - ((i + 1) * espacamentoCoracao) - 30;
            canvas.drawBitmap(imagemCoracao, posXCoracao, posYLinhaTopo, null);
        }

        int posXMoeda = 30;
        canvas.drawBitmap(imagemMoeda, posXMoeda, posYLinhaTopo, null);

        int posXTextoPontos = posXMoeda + imagemMoeda.getWidth() + 10;
        int posYTextoPontos = posYLinhaTopo + imagemMoeda.getHeight() - 10;
        canvas.drawText(String.valueOf(pontos), posXTextoPontos, posYTextoPontos, paint);

        int posXTempo = 30;
        int posYTempo = posYLinhaTopo + imagemMoeda.getHeight() + 60;
        canvas.drawText("Tempo: " + tempo + "s", posXTempo, posYTempo, paint);

        if (gameOver) {
            soundPool.stop(streamIdFundo);
            soundPool.play(somDeDerrota, 0.6f, 0.6f, 0, 0, 1);

            paint.setTextSize(100);
            paint.setARGB(255, 255, 0, 0);
            canvas.drawText("Game Over", getWidth() / 4f, getHeight() / 2f, paint);

            paint.setTextSize(50);
            paint.setARGB(255, 255, 255, 255);
            canvas.drawText("Pontuação final: " + pontos, getWidth() / 4f, getHeight() / 2f + 180, paint);

            paint.setTextSize(60);
            paint.setARGB(255, 255, 255, 0);
            canvas.drawText("Toque para Reiniciar", getWidth() / 4f - 30, getHeight() / 2f + 100, paint);
        } else {
            postInvalidateDelayed(16);
        }
    }

    private boolean colidiu(float objX, float objY, Bitmap imagem) {
        return objX < x + BONECO_LARGURA &&
                objX + imagem.getWidth() > x &&
                objY < y + BONECO_ALTURA &&
                objY + imagem.getHeight() > y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            reiniciarJogo();
            return true;
        }

        if (gameOver) return false;

        float touchX = event.getX();
        float touchY = event.getY();

        float moveX = Math.abs(touchX - (x + BONECO_LARGURA / 2));
        float moveY = Math.abs(touchY - (y + BONECO_ALTURA / 2));

        if (moveX > moveY) {
            if (touchX < x) {
                x -= 15;
                imagemAtual = esquerda;
            } else {
                x += 15;
                imagemAtual = direita;
            }
        } else {
            if (touchY < y) {
                y -= 15;
                imagemAtual = cima;
            } else {
                y += 15;
                imagemAtual = baixo;
            }
        }

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > getWidth() - BONECO_LARGURA) x = getWidth() - BONECO_LARGURA;
        if (y > getHeight() - BONECO_ALTURA) y = getHeight() - BONECO_ALTURA;

        invalidate();
        return true;
    }

    private void reiniciarJogo() {
        vidas = 3;
        pontos = 0;
        tempo = 0;
        dificuldade = 0;
        x = POSICAO_INICIAL_X;
        y = POSICAO_INICIAL_Y;
        pedras.clear();
        moedas.clear();
        coracoes.clear();
        gameOver = false;

        soundPool.play(somDeFundo, 0.3f, 0.3f, 0, -1, 1f);

        handler.removeCallbacksAndMessages(null);

        handler.post(gerarPedras);
        handler.post(gerarMoedas);
        handler.post(gerarCoracoes);
        handler.post(contarTempo);

        invalidate();
    }
}
