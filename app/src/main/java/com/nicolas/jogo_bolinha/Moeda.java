package com.nicolas.jogo_bolinha;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Moeda {
    public float x, y;
    private Bitmap imagem;
    private final int VELOCIDADE = 7;

    public Moeda(float x, float y, Bitmap imagem) {
        this.x = x;
        this.y = y;
        this.imagem = imagem;
    }

    public void atualizar() {
        y += VELOCIDADE;
    }

    public void desenhar(Canvas canvas) {
        canvas.drawBitmap(imagem, x, y, null);
    }
}
