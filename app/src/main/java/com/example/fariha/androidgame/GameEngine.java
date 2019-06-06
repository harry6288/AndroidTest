package com.example.fariha.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {
    private final String TAG = "SPARROW";

    // game thread variables
    private Thread gameThread = null;
    private volatile boolean gameIsRunning;

    // drawing variables
    private Canvas canvas;
    private Paint paintbrush;
    private SurfaceHolder holder;

    // Screen resolution varaibles
    private int screenWidth;
    private int screenHeight;

    // VISIBLE GAME PLAY AREA
    // These variables are set in the constructor
    int VISIBLE_LEFT;
    int VISIBLE_TOP;
    int VISIBLE_RIGHT;
    int VISIBLE_BOTTOM;
    int Distance_FromWall = 300;




    // SPRITES
    Square bullet;
    int SQUARE_WIDTH = 100;

    Square enemy;


    Sprite player;
    Sprite sparrow;
    Sprite cat;



    ArrayList<Square> bullets = new ArrayList<Square>();

    // GAME STATS
    int score = 0;


    public GameEngine(Context context, int screenW, int screenH) {
        super(context);

        // intialize the drawing variables
        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        // set screen height and width
        this.screenWidth = screenW;
        this.screenHeight = screenH;

        // setup visible game play area variables
        this.VISIBLE_LEFT = 20;
        this.VISIBLE_TOP = 10;
        this.VISIBLE_RIGHT = this.screenWidth - 20;
        this.VISIBLE_BOTTOM = (int) (this.screenHeight * 0.8);


        // initalize sprites
        this.player = new Sprite(this.getContext(), 100, 700, R.drawable.player64);
        this.sparrow = new Sprite(this.getContext(), 500, 200, R.drawable.bird64);
        this.cat = new Sprite(this.getContext(),1500,700,R.drawable.cat64);
//     make bullets
     this.bullets.add(new Square(context,300,500,SQUARE_WIDTH));
        this.bullets.add(new Square(context,300,600,SQUARE_WIDTH));
        this.bullets.add(new Square(context,300,700,SQUARE_WIDTH));



    }

    @Override
    public void run() {
        while (gameIsRunning == true) {
            updateGame();    // updating positions of stuff
            redrawSprites(); // drawing the stuff
            controlFPS();
        }
    }
    boolean Cat_Movingleft = true;
   final int Cat_Speed = 30;

    // Game Loop methods
    public void updateGame() {
        if (Cat_Movingleft == true) {
            this.cat.setxPosition(this.cat.getxPosition() - Cat_Speed);
            this.cat.hitbox.left = this.cat.hitbox.left - Cat_Speed;
            this.cat.hitbox.right = this.cat.hitbox.right - Cat_Speed;
        } else {


            this.cat.setxPosition(this.cat.getxPosition() + Cat_Speed);
            this.cat.hitbox.left = this.cat.hitbox.left + Cat_Speed;
            this.cat.hitbox.right = this.cat.hitbox.right + Cat_Speed;

        }
        if (this.cat.getxPosition()< this.VISIBLE_LEFT + Distance_FromWall)
        {
           Cat_Movingleft = false;
        }
        if(this.cat.getxPosition()> this.VISIBLE_RIGHT - Distance_FromWall )
        {
          Cat_Movingleft = true;
        }
//Random Sparrow position
              Random r = new Random();
                int randX = r.nextInt(this.screenWidth) + 1;
              int randY = r.nextInt(this.screenHeight) + 1;
                      // Moving Bird
        this.sparrow.setxPosition(randX - 10);
           this.sparrow.setyPosition(randY - 10);




        //bullets move
        for(int i=0; i<this.bullets.size();i++)
        {
            Square b=this.bullets.get(i);

        }



    }





    public void outputVisibleArea() {
        Log.d(TAG, "DEBUG: The visible area of the screen is:");
        Log.d(TAG, "DEBUG: Maximum w,h = " + this.screenWidth +  "," + this.screenHeight);
        Log.d(TAG, "DEBUG: Visible w,h =" + VISIBLE_RIGHT + "," + VISIBLE_BOTTOM);
        Log.d(TAG, "-------------------------------------");
    }



    public void redrawSprites() {
        if (holder.getSurface().isValid()) {

            // initialize the canvas
            canvas = holder.lockCanvas();
            // --------------------------------

            // set the game's background color
            canvas.drawColor(Color.argb(255,255,255,255));

            // setup stroke style and width
            paintbrush.setStyle(Paint.Style.FILL);
            paintbrush.setStrokeWidth(8);

            // --------------------------------------------------------
            // draw boundaries of the visible space of app
            // --------------------------------------------------------
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setColor(Color.argb(255, 0, 128, 0));

            canvas.drawRect(VISIBLE_LEFT, VISIBLE_TOP, VISIBLE_RIGHT, VISIBLE_BOTTOM, paintbrush);
            this.outputVisibleArea();

            // --------------------------------------------------------
            // draw player and sparrow
            // --------------------------------------------------------

            // 1. player
            canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(), this.player.getyPosition(), paintbrush);

            // 2. sparrow
            canvas.drawBitmap(this.sparrow.getImage(), this.sparrow.getxPosition(), this.sparrow.getyPosition(), paintbrush);

            // 3. Cat
            canvas.drawBitmap(this.cat.getImage(), this.cat.getxPosition(), this.cat.getyPosition(), paintbrush);

            //4. draw Cage

            canvas.drawRect(1700,150,1200,50,paintbrush);

            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            Rect r = player.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paintbrush);


            //draw the hit box on cat

            Rect r1 = cat.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r1, paintbrush);

            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            paintbrush.setTextSize(60);
            paintbrush.setStrokeWidth(5);
            String screenInfo = "Screen size: (" + this.screenWidth + "," + this.screenHeight + ")";
            canvas.drawText(screenInfo, 10, 100, paintbrush);


//Draw bulets

            for(int i=0;i<this.bullets.size();i++){

                paintbrush.setColor(Color.YELLOW);
                Square b=this.bullets.get(i);
                int x=b.getxPosition();
                int y=b.getyPosition();

                canvas.drawRect(x,y,x+b.getWidth(),y+b.getWidth(),paintbrush);
            }
            // --------------------------------
            holder.unlockCanvasAndPost(canvas);
        }

    }

    public void controlFPS() {
        try {
            gameThread.sleep(17);
        }
        catch (InterruptedException e) {

        }
    }


    // Deal with user input
    @Override

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return true;
    }


    // Game status - pause & resume
    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e) {

        }
    }
    public void  resumeGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}

