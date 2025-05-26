package com.nicolas.jogo_bolinha;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Coracao {
    public float x, y;
    private Bitmap imagem;

    public Coracao(float x, float y, Bitmap imagem) {
        this.x = x;
        this.y = y;
        this.imagem = imagem;
    }

    public void atualizar() {
        y += 8; // velocidade fixa ou ajustar com tempo
    }

    public void desenhar(Canvas canvas) {
        canvas.drawBitmap(imagem, x, y, null);
    }
}

