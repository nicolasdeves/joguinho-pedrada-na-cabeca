package com.nicolas.jogo_bolinha;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Pedra {
    public float x, y;
    public Bitmap imagem;

    private final int VELOCIDADE = 10;

    public Pedra(float x, float y, Bitmap imagem, int i) {
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

    public boolean colidiuCom(float jogadorX, float jogadorY, int largura, int altura) {
        return x < jogadorX + largura &&
                x + imagem.getWidth() > jogadorX &&
                y < jogadorY + altura &&
                y + imagem.getHeight() > jogadorY;
    }
}
