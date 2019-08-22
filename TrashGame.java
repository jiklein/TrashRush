/*
 * Filename: TrashRush.java
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

public class TrashGame extends WindowController implements KeyListener {
  
  // Slides specifications
  public static int INTRO_SLIDES_LENGTH = 8, NUM_TRASH = 9, NUM_CANS = 3;
  private static String INTRO_FOLDER = "Intro/", TRASH_FOLDER = "TrashImages/",
    GAME_OVER_FOLDER = "GameOver/"; 
  private static String JPG = ".jpg";

  // Game environment files
  private static final String TITLE_FILE = "GameImages/title.jpg",
    BACKGROUND_FILE = "GameImages/background.jpg",
    CANS_FILE = "GameImages/cans.jpg";

  private static final int CANS_X[] = {5, 400, 780};
  private static final int CANS_WIDTH = 200, CANS_HEIGHT = 5;
  private static final int ANIMATION_INIT = 50, GENERATION_INIT = 4000,
    DX_INIT = 20, DY = 2;
  

 // private static final int FRAME_WIDTH = 920, FRAME_HEIGHT = 600; 
  
  // Title slide
  private VisibleImage titleSlide;
  
  // Game slides
  private VisibleImage[] introSlides, gameOverSlides;
  private Image[] gameOverImages;
  private int slideIndex;
  private boolean slideMode;

  // Game environment
  private GameEnvironment gameEnvt;
  

  public static void main(String[]args) {
    
    // Instantiate game                                                      
    new Acme.MainFrame(new TrashGame(), args, 0, 0); 
  }

  public void begin() {
    
    // Create slides
    introSlides = generateSlides(INTRO_SLIDES_LENGTH, INTRO_FOLDER);

    /* BUILD GAME ENVIRONMENT */
    
    // Create images
    VisibleImage backgroundImage = new VisibleImage(getImage(BACKGROUND_FILE),
      0, 0, canvas);

    // Create cans
    FilledRect[] cans = new FilledRect[NUM_CANS];
    for (int i = 0; i < cans.length; i++) {
      cans[i] = new FilledRect(CANS_X[i], 0, CANS_WIDTH, CANS_HEIGHT, canvas);
    }

    // Create trash images
    Image[] trashImages = generateImages(NUM_TRASH, TRASH_FOLDER); 
    Image[] gameOverImages = generateImages(NUM_TRASH, GAME_OVER_FOLDER);
    
    // Instantiate game environment
    gameEnvt = new GameEnvironment(backgroundImage, cans, trashImages,
      gameOverImages, GENERATION_INIT, ANIMATION_INIT, DX_INIT, DY, canvas,
      this);

    
    titleSlide = new VisibleImage(getImage(TITLE_FILE), 0, 0, canvas);
    this.setSize((int)titleSlide.getWidth(), (int)titleSlide.getHeight()); 
    
    

    canvas.requestFocusInWindow();
    canvas.addKeyListener(this);

    slideMode = true;

  }
  
  /**
   * Returns an array of Images generated from a given folder.
   *
   * @param length number of files in folder
   * @param folder name of folder
   * @return array of images
   */
  private Image[] generateImages(int length, String folder) {
    
    // Declare array
    Image[] images = new Image[length];
    
    // Fill array
    for (int i = 0; i < images.length; i++) {
      images[i] = getImage(folder + (i + 1) + JPG);
    }

    // Return images
    return images;
  }

  /**
   * Returns an array of slides generated from a given folder.
   *
   * @param length number of files in folder
   * @param folder name of folder
   * @return array of slides
   */
  private VisibleImage[] generateSlides(int length, String folder) {
    
    // Declare array
    VisibleImage[] slides = new VisibleImage[length];
    
    // Fill array
    for (int i = 0; i < slides.length; i++) {
      slides[i] = new VisibleImage(getImage(folder + (i + 1) + JPG), 0, 0,
        canvas);
      slides[i].hide();
    }

    // Return images
    return slides;
  }

  public void enterSlideMode() {
    slideMode = true;
  }

  /**                                                                           
   * Handles key functionality for snake game.                                  
   *                                                                            
   * @param evt event generated when key is pressed.                            
   */                                                                           
  public void keyPressed(KeyEvent evt) {                                        
    if (slideMode) {
      
      // Init start game flag
      boolean startGame = false;
      
      // Remove title slide
      if (titleSlide != null) {
        titleSlide.removeFromCanvas();
        titleSlide = null;
        slideIndex = 0;
        introSlides[0].show();
      } else {
        
        // Change slides
        switch (evt.getKeyCode()) {                                                 
          case KeyEvent.VK_LEFT:
            if (slideIndex != 0) {
              introSlides[slideIndex].hide();
              introSlides[--slideIndex].show();
            }
            break;                                                                  
          case KeyEvent.VK_RIGHT:                                                    
            if (slideIndex != introSlides.length - 1) {
              introSlides[slideIndex].hide();
              introSlides[++slideIndex].show();
            }
            break;                                                                 
          case KeyEvent.VK_SPACE:                                                   
            if (slideIndex != introSlides.length - 1) {
              introSlides[slideIndex].hide();
              introSlides[++slideIndex].show();
            } else {
              startGame = true;
            }
            break;
        }
      }
      switch (evt.getKeyCode()) {                                                 
        
        // Jump to start game
        case KeyEvent.VK_ESCAPE:
          startGame = true;
          break;

        // Jump to usage
        case KeyEvent.VK_U:
          introSlides[slideIndex].hide();
          slideIndex = introSlides.length - 1;
          introSlides[slideIndex].show();
          break;
      }
      // Check start game
      if (startGame) {
        introSlides[slideIndex].hide();
        slideMode = false;
        gameEnvt.initGame();
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
