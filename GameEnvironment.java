/*
 * Filename: GameBackground.java
 * Author:   Jeromey Klein
 * UserId:   cs11faxn
 * Date:     11/15/2018
 * Sources of help: None
 */

import objectdraw.*;
import Acme.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * This class defies an environment where the game is played.
 */
public class GameEnvironment implements ActionListener, KeyListener {

  private static final String SCORE_MSG = "Score: ", HIGH_MSG = "High Score: ";

  private static final int CAN_START[] = {0, 370, 760};
  private static final int CAN_WIDTH = 200;

  // GUI Components
  private JPanel scorePanel;
  private JLabel scoreLabel, highLabel;
  private VisibleImage backgroundImage, gameOverSlide;

  // References
  private Image[] trashImages, gameOverImages;
  private DrawingCanvas canvas;
  private TrashGame trashGame;

  // Game components
  private ArrayList<TrashItem> trash;
  private FilledRect[] cans;

  // Animation components
  private Random rgen;
  private javax.swing.Timer trashGenerator, timer;
  private int generationInit, animationInit, dxInit, dy;

  // Counters
  private int score, highScore, selectedIndex;
  private double generationDelay, animationDelay, dx;
  private boolean running, paused;
  
  public GameEnvironment(VisibleImage backgroundImage, FilledRect[] cans,
    Image[] trashImages, Image[] gameOverImages, int generationInit,
    int animationInit, int dxInit, int dy, DrawingCanvas canvas,
    TrashGame trashGame) {
    
    // Copy references
    this.backgroundImage = backgroundImage;
    this.cans = cans;
    this.trashImages = trashImages;
    this.gameOverImages = gameOverImages;
    this.generationInit = generationInit;
    this.animationInit = animationInit;
    this.dxInit = dxInit;
    this.dy = dy;
    this.canvas = canvas;
    this.trashGame = trashGame;
    paused = false;
    running = false;

    // Configure score panel
    scorePanel = new JPanel(new GridLayout(1, 2));
    scoreLabel = new JLabel();
    highLabel = new JLabel(HIGH_MSG + 23);
    scoreLabel.setHorizontalAlignment(JLabel.CENTER);
    highLabel.setHorizontalAlignment(JLabel.CENTER);
    scorePanel.add(scoreLabel);
    scorePanel.add(highLabel);
    trashGame.add(scorePanel, BorderLayout.NORTH);
    trashGame.validate();

    // Init game over slide
    gameOverSlide = new VisibleImage(gameOverImages[0], 0, 0, canvas);
    gameOverSlide.hide();

    // Init counters
    score = selectedIndex = 0;
    highScore = 23;
    
    // Init game components
    trash = new ArrayList<TrashItem>();
    rgen = new Random();
    trashGenerator = new javax.swing.Timer(generationInit, this);

    // Add as key listeners
    canvas.addKeyListener(this);

    // Hide components
    hide(false);
  }

  public void initGame() {

    // Init GUI components
    scoreLabel.setText(SCORE_MSG + 0);
    scorePanel.setVisible(true);
    backgroundImage.show();
    for (int i = 0; i < cans.length; i++) {
      cans[i].show();
      cans[i].moveTo(CAN_START[i], backgroundImage.getHeight() -
        cans[i].getHeight());
    }

    // Init counters
    paused = false;
    running = true;
    animationDelay = animationInit;
    generationDelay = generationInit;
    dx = dxInit;
    selectedIndex = 0;
    score = 4;

    // Init components
    trash.clear();
    trashGenerator.start();
  }

  public void hide(boolean panelVisible) {
    scorePanel.setVisible(panelVisible);
    backgroundImage.hide();
    for (int i = 0; i < cans.length; i++) {
      cans[i].hide();
    }
  }

  public void remove() {

  }

  public int check(TrashItem item) {
    int group = 0;
    while (group < cans.length && !cans[group].overlaps(item.getBorder())) {
      group++;
    }
    

    if (group != cans.length) {
      scoreLabel.setText(SCORE_MSG + ++score);
      if (selectedIndex == 0) {
        item.setSelected(false);
        trash.remove(item);
        trash.get(0).setSelected(true);
      } else {
        trash.remove(item);
        selectedIndex--;
      }
      return group;
    } else {
      return -1;
    }
  }

  public void endGame(int type) {
    
    scoreLabel.setText(SCORE_MSG + --score);
    if (score > highScore) {
      highScore = score;
      highLabel.setText(HIGH_MSG + highScore);
    }
    trashGenerator.stop();
    running = false;
    paused = true;

    while (trash.size() > 0) {
      trash.remove(0).remove();
    }

    hide(true);

    gameOverSlide.setImage(gameOverImages[type]);
    gameOverSlide.show();
  }
  
  /**
   * Generates new trash item when timer goes off.
   * 
   * @param e event generated when timer goes off.
   */
  public void actionPerformed(ActionEvent e) {
    
    // Generate random x value
    int x = rgen.nextInt(canvas.getWidth() - TrashItem.BORDER_WIDTH);
    
    // Generate random image type
    int type = rgen.nextInt(trashImages.length);

    // Calculate group
    int group = type / TrashGame.NUM_CANS;
    
    // Add new trash item
    trash.add(new TrashItem(x, -TrashItem.BORDER_HEIGHT, (int)dx, dy, type, 
      group, (int)animationDelay, trashImages[type], trash.size() == 0, this,
      canvas));
    
    // Add item as key listener
    canvas.addKeyListener(trash.get(trash.size() - 1));
  }
  
  /**                                                                           
   * Handles key functionality for snake game.                                  
   *                                                                            
   * @param evt event generated when key is pressed.                            
   */                                                                           
  public void keyPressed(KeyEvent evt) {                                        
    switch (evt.getKeyCode()) {                                                 
                                                                                
      // Set direction                                                          
      case KeyEvent.VK_UP:
        if (!paused && selectedIndex < trash.size() - 1) {
          trash.get(selectedIndex).setSelected(false);
          trash.get(++selectedIndex).setSelected(true);
        }
        break;                                                                  
      case KeyEvent.VK_DOWN:                                                    
        if (!paused && selectedIndex > 0) {
          trash.get(selectedIndex).setSelected(false);
          trash.get(--selectedIndex).setSelected(true);
        }
        break;                                                                  
                                                                                
      // Pause                                                                  
      case KeyEvent.VK_SPACE:
        if (running) {
          paused = !paused;
          if (paused) {
            trashGenerator.stop();
          } else {
            trashGenerator.start();
          }
        }
        break;   
      case KeyEvent.VK_R:
        if (!running) {
          gameOverSlide.hide();
          initGame();
        }
    }
  }
  
  /**                                                                           
   * This method is not used.                                                   
   *                                                                            
   * @param evt event generated when key is released (not used)                 
   */                                                                           
  public void keyReleased(KeyEvent evt) {}                                      
                                                                                
  /**                                                                           
   * This method is not used.                                                   
   *                                                                            
   * @param evt event generated when key is typed (not used)                    
   */                                                                           
  public void keyTyped(KeyEvent evt) {}      
}
