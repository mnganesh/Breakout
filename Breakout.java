/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Diameter of the ball in pixels */
	private static final int BALL_DIAMETER = 2 * BALL_RADIUS;
	
/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
/**	Pause Time */
	private static final int DELAY = 10;

	public static void  main(String[] args){
		new Breakout().start(args);
	}
	
	public void run(){
		//setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		setup();
		addMouseListeners();
		play();
	}
	
	private void setup() {
		createTarget();
		createPaddle();
		createBall();
		startScreen();
	}
	
	private void createTarget(){
		numBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
		brickY = BRICK_Y_OFFSET;
		create2RowBrick(Color.RED);
		create2RowBrick(Color.ORANGE);
		create2RowBrick(Color.YELLOW);
		create2RowBrick(Color.GREEN);
		create2RowBrick(Color.CYAN);
	}
	
	private void create2RowBrick(Color c){
		for(int j=0;j<2;j++){
			brickX = (APPLICATION_WIDTH - (NBRICK_ROWS * BRICK_WIDTH + (NBRICK_ROWS-1) * BRICK_SEP))/2;
			for(int i=0;i<NBRICK_ROWS;i++){
				GRect brick = new GRect(brickX,brickY,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setColor(c);
				brick.setFilled(true);
				add(brick);
				brickX += BRICK_SEP + BRICK_WIDTH;
			}
			brickY += BRICK_SEP + BRICK_HEIGHT;
		}
	}
	
	private void createPaddle() {
		 int x = (getWidth() - PADDLE_WIDTH) / 2;
		 int y = getHeight() - PADDLE_Y_OFFSET;
		 paddle = new GRect(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		 paddle.setFilled(true);
		 add(paddle);		 
	}
	
	public void mouseMoved(MouseEvent e){
		double mPaddleX =(e.getX()-paddle.getWidth()/2);
		//Fix to Keep Mouse at BOUNDARIES AND MIDDLE OF PADDLE		
		if(mPaddleX<0)
			mPaddleX = 0;
		else if(mPaddleX+paddle.getWidth()>getWidth())
			mPaddleX = getWidth() - paddle.getWidth();
		paddle.setBounds(mPaddleX, paddle.getY(), paddle.getWidth(), paddle.getHeight());
		
	}
	
	private void createBall(){
		int x = getWidth()/2 - BALL_RADIUS;
		int y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_DIAMETER, BALL_DIAMETER);
		ball.setFilled(true);
		add(ball);
	}
	
	private void startScreen()
	{
		label = new GLabel("Click to Serve!!");
		label.setFont(new Font("Serif", Font.BOLD, 18));
		add(label,getWidth()/2-label.getWidth()/2,getHeight()/2-label.getAscent()/2);
		ball.setVisible(false);
		initailizeBall();
		started = false;
		
	}
	private void initailizeBall(){
		vy = 3.0;
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		ball.setBounds(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, BALL_DIAMETER, BALL_DIAMETER);
	}
	
	public void mouseClicked(MouseEvent e){
		if(!started){
			started = true;
			remove(label);
			ball.setVisible(true);
		}
	}
	
	private void play(){
		while(!compleated){
			if(numBricks==0)
				gameOver(1);
			if(started){
				moveBall();
				checkForCollision();
				pause(DELAY);
			}
		}
	}
	
	private void moveBall(){
		ball.move(vx, vy);
	}
	
	private void checkForCollision(){
		int x = (int)ball.getX();
		int y = (int)ball.getY();
		
		checkWallCollision(x,y);
		
		GObject collider = getCollidingObject(x,y);
		if (collider==paddle){
			if(vy>0)
				vy = -vy;
			//hits++;
			//if(hits%7==0)
				//vx = 2 * vx;
			bounceClip.play();
		}
		else if(collider!=null){
			remove(collider);
			vy = -vy;
			numBricks--;
			bounceClip.play();
		}
	}
	
	private void checkWallCollision(int x, int y){
		if(x<=0 || (x+BALL_DIAMETER)>APPLICATION_WIDTH){
			vx = -vx;
			bounceClip.play();
		}
		if(y<=0) {
			vy = -vy;
			bounceClip.play();
		}
		if ((y+BALL_DIAMETER)>(getHeight() - PADDLE_Y_OFFSET + PADDLE_HEIGHT)){
			turnRemaining--;
			if(turnRemaining==0)
				gameOver(0);
			else
				startScreen();
		}
	}
	
	private GObject getCollidingObject(int x,int y){
		GObject collObj;
		collObj = getElementAt(x,y);
		if(collObj!=null) return collObj;
		collObj = getElementAt(x+BALL_DIAMETER,y);
		if(collObj!=null) return collObj;
		collObj = getElementAt(x,y+BALL_DIAMETER);
		if(collObj!=null) return collObj;
		collObj = getElementAt(x+BALL_DIAMETER,y+BALL_DIAMETER);
		return collObj;
	}
	

	
	private void gameOver(int s){
		switch(s){
		case 1:
			label = new GLabel("YOU WON");
			label.setColor(Color.green);
			break;
		case 0:
			label = new GLabel("YOU LOST");
			label.setColor(Color.RED);
		}
		label.setFont(new Font("Serif", Font.BOLD, 18));
		add(label,getWidth()/2-label.getWidth()/2,getHeight()/2-label.getAscent()/2);
		ball.setVisible(false);
		compleated = true;
	}
	
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	private int hits = 0;
	private int brickX, brickY;
	private int numBricks;
	private GRect paddle;
	private GOval ball;
	private double vx,vy;
	private boolean started = false;
	private boolean compleated = false;
	private int turnRemaining = NTURNS;
	GLabel label=null;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
}
