/*This file is part of ProcessingPuzzle15.
 *
 *   ProcessingPuzzle15 is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ProcessingPuzzle15 is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ProcessingPuzzle15.  If not, see <http://www.gnu.org/licenses/>.
 */

package processingSketch;
import processing.core.*;

import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class Sketch extends PApplet {
	public static final Random rand = new Random();
	
	public final int SIZEX=500;
	public final int SIZEY=500;
	public final int BACKGROUNDCOLOR=color(200, 200, 200);
	
	public class FileMan {
		String filename="scores.txt";
		List<String> lines; //first line is high score, second is best time
		File f = new File(filename);
		
		public FileMan() {
			Boolean fileExists=true;
			if (!f.isFile()) fileExists=false;
			try {
				f.createNewFile(); //creates new file if it doesn't exist
				lines = Files.readAllLines(Paths.get(f.getAbsolutePath()), Charset.defaultCharset());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!fileExists) {
				updateScores("0","0");
			}
		}
		
		public final int getHScore() {
			return Integer.parseInt(lines.get(0));
		}
		
		public final int getBstTime() {
			return Integer.parseInt(lines.get(1));
		}
		
		public void updateScores(String hscore, String bsttime) {
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f.getAbsolutePath()),"utf-8"))) {
				writer.write(hscore+"\n"+bsttime);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public class ImageMan {
		
		PImage pict;
		PImage [] mosaic = new PImage [16];
		
		public void loadPic() {
			selectInput("Select a file to process:", "fileSelected");
		}
		
		public void removePic() {
			pict=null;
			for (int i=0; i<mosaic.length; i++) mosaic[i]=null;
		}
		
		//would have put the fileSelected callback function inside the class but processing isn't helping
		
		public void makeMosaic() {
			//mosaic[0]= pict.get(0,0,50,50);
			int size = Tile.SIZE;
			int i=0, j=0, k=0;
			while (i<200) {
				j=0;
				while (j<200) {
					mosaic[k]=pict.get(j, i, size, size);
					j+=50;
					k++;
				}
				i+=50;
			}
		}
	}
	
	public void fileSelected(File selection) {
		String path=selection.getAbsolutePath().toString();
		if (	   path.endsWith(".jpg")
				|| path.endsWith(".png")
				|| path.endsWith(".jpeg")
				|| path.endsWith(".tiff")
				|| path.endsWith(".gif")
				|| path.endsWith(".bmp")
				|| path.endsWith(".svg")) {
			img.pict=loadImage(path);
			img.pict.resize(200, 200);
			img.makeMosaic();
		}
		else javax.swing.JOptionPane.showMessageDialog(null, "The file chosen isn't a supported picture\nSupported formats are jpeg, png, tiff, gif, bmp, svg");
	}
	
	public class Button {
		public final static int SIZEX=130;
		public final static int SIZEY=50;
		public int x=0;
		public int y=0;
		public int col=color(240, 136, 0);
		public String text="";
		
		
		public Button(String text_, int x_, int y_) {
			text=text_;
			x=x_;
			y=y_;
		}
		
		public Button(String text, int x_, int y_, int col_) {
			this(text,x_,y_);
			col=col_;
		}
		
		public void show() {
			fill(col);
			rect(x,y,SIZEX,SIZEY);
			
			fill(0);
			textAlign(CENTER);
			textSize(14);
			text(text,x, y);
		}
		
		public Boolean checkPressed() {
			if (x-SIZEX/2 <= mouseX && mouseX <= x+SIZEX/2)
				if (y-SIZEY/2 <= mouseY && mouseY <= y+SIZEY/2)
					return true;
			return false;
		}
	}
	
	
	public class Tile {
		public final static int SIZE=50;
		public final static int STROKESIZE=4;
		public final int TILECOLOR = color(0);
		public int num;
		public int x;
		public int y;
		
		public Tile(int num_) {
			num=num_;
		}
		
		public Tile(int num_, int x_, int y_) {
			num=num_;
			x=x_;
			y=y_;
		}
		
		public void show() {
			rectMode(CENTER);
			stroke(0, 88, 240);
			strokeWeight(STROKESIZE);
			if (num==16) {
				fill(255);
				stroke(255);
				rect(x,y,SIZE,SIZE);
			}
			
			else {
				fill(TILECOLOR);
				rect(x,y,SIZE,SIZE);
				stroke(0, 88, 240);
				strokeWeight(STROKESIZE);
				fill(255);
				textSize(16);
				textAlign(CENTER);
				text(Integer.toString(num),x,y+SIZE/4,SIZE,SIZE);
			}
			
		}
	}
	
	public class Score {
		public int highScore=-1;
		public int score=0;
		
		public Score() {
			int hs=fm.getHScore();
			if (hs>0) highScore=hs;
		}
	}
	
	public class Chronometer {
		public long bestTime=-1;
		public long startTime=0;
		public long currentTime=0;
		public Boolean started=false;
		
		public Chronometer() {
			int bt = fm.getBstTime();
			if (bt>0) bestTime=bt;
		}
		
		public void startChron() {
			startTime=System.currentTimeMillis()/1000;
		}
		
		public void updateTime() {
			if (!started) return;
			currentTime=System.currentTimeMillis()/1000 - startTime;
		}
	}
	
	public class Grid {
		public Score mScore= new Score();
		public Chronometer chron= new Chronometer();
		public int randTimes = 130;
		public Boolean gameStarted = false;
		
		public String getHScore() {
			String toRet = Integer.toString(mScore.highScore);
			return toRet;
		}
		
		public final String getBstTime() {
			String toRet = Long.toString(chron.bestTime);
			return toRet;
		}
		
		public Tile [] tileset = new Tile [16];
		public final static int RELX=100;
		public final static int RELY=100;
		public final int [][] MOVERULES= {
				{+1,+4},
				{-1,+1,+4},
				{-1,+1,+4},
				{-1,+4},
				{+1,-4,+4},
				{-1,+1,-4,+4},
				{-1,+1,-4,+4},
				{-1,+4,-4},
				{+1,-4,+4},
				{-1,+1,-4,+4},
				{-1,+1,-4,+4},
				{-1,+4,-4},
				{+1,-4},
				{-1,+1,-4},
				{-1,+1,-4},
				{-1,-4}
		};
		
		public Grid() {
			int [] mult = {0,1,2,3,0,1,2,3,0,1,2,3,0,1,2,3};
			int nlcount=0;
			for (int i=0; i<tileset.length; i++) {
				int tx=RELX+(mult[i]*(Tile.SIZE+Tile.STROKESIZE));
				int ty=RELY+(nlcount*(Tile.SIZE+Tile.STROKESIZE));
				tileset[i] = new Tile(i+1, tx, ty);
				if (mult[i]==3) nlcount++;
			}
		}
		
		public void show() {
			//show score and high score
			fill(255);
			textSize(12);
			textAlign(RIGHT);
			String score=Integer.toString(mScore.score);
			String hscore=Integer.toString(mScore.highScore);
			if (mScore.highScore==-1) hscore="0";
			text("Score: "+score
					+"\nHigh Score: "+hscore
					, SIZEX-50, RELY+50);
			
			//show time and best time
			fill(255);
			textSize(12);
			textAlign(RIGHT);
			
			String currTime=Long.toString(chron.currentTime);
			String bstTime;
			if (chron.bestTime==-1) bstTime="0";
			else bstTime=Long.toString(chron.bestTime);
			
			if (gameStarted) {
				chron.updateTime();
			}
			
			text("Time: "+currTime
					+"\nBest Time: "+bstTime
					, SIZEX-50, RELY+150);
			
			
			if (!gameStarted) { //show game over
				fill(255);
				textSize(30);
				textAlign(LEFT);
				text("GAME OVER", RELX, RELY-50);
			}
			rectMode(CENTER);

			//show normal tileset
			for (int i=0; i<tileset.length; i++) {
				tileset[i].show();
			}
			
			//optionally show ImageMan mosaic on top
			if (img.mosaic[15]!=null) {
				for (int i=0; i<tileset.length-1; i++) {
					image(img.mosaic[i], tileFromNum(i+1).x-Tile.SIZE/2, tileFromNum(i+1).y-Tile.SIZE/2);
				}
			}
		}
		
		public int getEmpPos() {
			for (int i=0; i<tileset.length; i++) {
				if (tileset[i].num == 16) return i;
			}
			return -1;
		}
		
		public int getPos(Tile t) {
			for (int i=0; i<tileset.length; i++) {
				if (tileset[i].num == t.num) return i;
			}
			return -1;
		}
		
		public Tile tileFromNum(int n) {
			for (int i=0; i<tileset.length; i++) {
				if (tileset[i].num == n) return tileset[i];
			}
			return null;
		}
		
		public Tile tileFromClick() {
			int x=mouseX, y=mouseY;
			for (int i=0; i<tileset.length; i++) {
				if (tileset[i].x-Tile.SIZE/2 <= x && x <= tileset[i].x+Tile.SIZE/2)
					if (tileset[i].y-Tile.SIZE/2 <= y && y <= tileset[i].y+Tile.SIZE/2) {
						//debug //System.out.println(Integer.toString(tileset[i].num));
						return tileset[i];
					}
			}
			
			return null;
		}
		
		public Boolean isMoveable(Tile t) {
			int tilePos=getPos(t);
			int empPos=getEmpPos();
			if (tilePos==empPos) return false; //the two values might be the same
			int [] rules = MOVERULES[empPos];
			for (int i=0; i<rules.length; i++) {
				if (empPos+rules[i]==tilePos) return true;
			}
			
			
			return false;
		}
		
		public Boolean moveTile(Tile t) { //returns true if success, false otherwise
			if (isMoveable(t)) {
				int targetPos = getEmpPos();
				Tile empTile=tileset[targetPos];
				int targetx=empTile.x;
				int targety=empTile.y;
				int newEmpX=t.x;
				int newEmpY=t.y;
				t.x=targetx;
				t.y=targety;
				empTile.x=newEmpX;
				empTile.y=newEmpY;
				int oldPos = getPos(t);
				tileset[targetPos]=t;
				tileset[oldPos]=empTile;
				t.show();
				empTile.show();
				//debug //System.out.println(getEmpPos());
				return true;
			}
			else return false;
		}
		
		public Boolean checkWin() {
			for (int i = 0; i < tileset.length - 1; i++) {
		        if (tileset[i].num > tileset[i + 1].num) {
		            return false; // It is proven that the array is not sorted.
		        }
		    }
		    return true;
		}
		
		public void win() {
			chron.started=false;
			gameStarted=false;
			if (mScore.score < mScore.highScore || mScore.highScore==-1)
				mScore.highScore = mScore.score;
			if (chron.currentTime < chron.bestTime || chron.bestTime==-1) {
				chron.bestTime=chron.currentTime;
			}
		}
		
		public void moveFromClick() {
			/* 
			 * Since Java is the spawn of the devil himself
			 * and processing is no better, random
			 * NullPointerExceptions occur. Placing a
			 * try/catch seems to be a necessary condition
			 * to keep the program functioning over time.
			 * Since nothing actually seems to be braking by
			 * just ignoring the exception.
			 * 
			 * F***ing Java
			 */
			if (gameStarted) {
				try {
				Tile t=tileFromClick();
				if (t==null) return;
				moveTile(t);
				mScore.score++;
				if (checkWin()) {
					win();
				}
				}
				catch(Exception e) {
					System.out.println("catched bro -> " + e.toString());
				}
			}
			
		}
		
		public void randomize() {
			mScore.score=0;
			int moves=randTimes;
			int i=0;
			while (i<moves) {
				Boolean result=moveTile(tileset[rand.nextInt(16)]);
				if (result) i++;
				//debug //System.out.println(i);
			}
			gameStarted=true;
			chron.started=true;
			chron.startChron();
		}
	}
	

	ImageMan img = new ImageMan();
	FileMan fm = new FileMan();
	
	Grid mainGrid = new Grid();
	Button restartBtn= new Button("Restart", 150, 350);
	Button loadBtn = new Button("Load Image", 150, 420);
	Button exitBtn = new Button("Quit", 300, 350);
	Button rmPicBtn = new Button("Remove picture", 300, 420);
	public void settings() {
		size(SIZEX,SIZEY);
	}
	
	public void setup() {
		background(BACKGROUNDCOLOR);
		mainGrid.randomize();
	}
	
	public void draw() {
		background(BACKGROUNDCOLOR);
		mainGrid.show();
		loadBtn.show();
		restartBtn.show();
		exitBtn.show();
		
		if (img.mosaic[0]!=null) rmPicBtn.show();
	}
	
	
	
	public void mouseClicked() {
		mainGrid.moveFromClick();
		
		if (restartBtn.checkPressed()) {
			mainGrid.randomize();
		}
		
		if (loadBtn.checkPressed()) {
			img.loadPic();
		}
		
		if (exitBtn.checkPressed()) {
			fm.updateScores(mainGrid.getHScore(), mainGrid.getBstTime());
			exit();
		}
		
		if (rmPicBtn.checkPressed()) {
			img.removePic();
		}
	}
}
