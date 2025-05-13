package com.nicolas.jogo_bolinha;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
public class Componente extends View {
    private Paint paint = new Paint();
    private float x, y;
    private Bitmap parado, cima, baixo, esquerda, direita;
    private Bitmap imagemAtual;
    private String direcaoAtual = "parado";

    private final int BONECO_LARGURA = 400;
    private final int BONECO_ALTURA = 450;

    public Componente(Context context) {
        super(context);
        inicializar(context);
    }

    public Componente(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar(context);
        setFocusable(true);
    }

    private void inicializar(Context context) {
        parado = BitmapFactory.decodeResource(context.getResources(), R.drawable.parado);
        cima = BitmapFactory.decodeResource(context.getResources(), R.drawable.cima);
        baixo = BitmapFactory.decodeResource(context.getResources(), R.drawable.baixo);
        esquerda = BitmapFactory.decodeResource(context.getResources(), R.drawable.esquerda);
        direita = BitmapFactory.decodeResource(context.getResources(), R.drawable.direita);

        parado = Bitmap.createScaledBitmap(parado, BONECO_LARGURA, BONECO_ALTURA, true);
        cima = Bitmap.createScaledBitmap(cima, BONECO_LARGURA, BONECO_ALTURA, true);
        baixo = Bitmap.createScaledBitmap(baixo, BONECO_LARGURA, BONECO_ALTURA, true);
        esquerda = Bitmap.createScaledBitmap(esquerda, BONECO_LARGURA, BONECO_ALTURA, true);
        direita = Bitmap.createScaledBitmap(direita, BONECO_LARGURA, BONECO_ALTURA, true);

        imagemAtual = parado;

        x = 100;
        y = 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setARGB(255, 100, 100, 120);
        canvas.drawRect(10f, 10f, 20f, 20f, paint);
        paint.setARGB(255, 255, 0, 0);
        canvas.drawBitmap(imagemAtual, x, y, null);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float step = 1 + event.getPressure() * 10;

        float touchX = event.getX();
        float touchY = event.getY();

        float oldX = x;
        float oldY = y;

        float moveX = Math.abs(touchX - (x + BONECO_LARGURA/2));
        float moveY = Math.abs(touchY - (y + BONECO_ALTURA/2));

        if (moveX > moveY) {
            if (touchX < x + (BONECO_LARGURA / 2)) {
                x -= step;
                imagemAtual = esquerda;
                direcaoAtual = "esquerda";
            } else {
                x += step;
                imagemAtual = direita;
                direcaoAtual = "direita";
            }
        } else {
            if (touchY < y + (BONECO_ALTURA / 2)) {
                y -= step;
                imagemAtual = cima;
                direcaoAtual = "cima";
            } else {
                y += step;
                imagemAtual = baixo;
                direcaoAtual = "baixo";
            }
        }

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > getWidth() - BONECO_LARGURA) x = getWidth() - BONECO_LARGURA;
        if (y > getHeight() - BONECO_ALTURA) y = getHeight() - BONECO_ALTURA;

        if (oldX == x && oldY == y) {
            imagemAtual = parado;
            direcaoAtual = "parado";
        }

        invalidate();
        return true;
    }
}