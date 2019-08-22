

import objectdraw.*;
import java.awt.*;
import java.awt.event.*;



public class TrashItem extends ActiveObject implements KeyListener {
  
  // Constants
  public static final int BORDER_WIDTH = 40, BORDER_HEIGHT = 50,
    IMAGE_WIDTH = 30, IMAGE_HEIGHT = 40, IMAGE_OFFSET = 5;

  // Components
  private FilledRect border;
  private VisibleImage image, message;
  private int dx, dy, delay, type, group;
  
  // References
  private GameEnvironment gameEnvt;

  // Counters
  private boolean running, paused, selected;

  public TrashItem(int x, int y, int dx, int dy, int type, int group, int delay,
    Image image, boolean selected, GameEnvironment gameEnvt,
    DrawingCanvas canvas) {
    
    // Instantiate components
    this.border = new FilledRect(x, y, BORDER_WIDTH, BORDER_HEIGHT, canvas);
    this.image = new VisibleImage(image, x + IMAGE_OFFSET, y + IMAGE_OFFSET,
    canvas);
    this.dx = dx;
    this.dy = dy;
    this.type = type;
    this.group = group;
    this.delay = delay;
    this.gameEnvt = gameEnvt;
    
    // Initialize counters
    setSelected(selected);
    running = true;
    paused = false;

    // Start thread
    start();
  }

  public void run() {
    while (running) {
      
      if (!paused) {
        
        // Move item
        move(0, dy);

        // Check for collision
        int check = gameEnvt.check(this);
        if (check != -1) {
          
          // Stop running
          running = false;
          remove();

          if (check != group) {
            gameEnvt.endGame(type);
          }
        }
      }
      


      pause(delay);
    }
  }

  public FilledRect getBorder() {
    return border;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
    border.setColor(selected ? Color.GREEN : Color.BLACK);
  }

  public void move(double dx, double dy) {
    if (!paused) {
      border.move(dx, dy);
      image.move(dx, dy);
    }
  }

  public void remove() {
    running = false;
    border.removeFromCanvas();
    image.removeFromCanvas();
  }
  
  /**                                                                           
   * Handles key functionality for snake game.                                  
   *                                                                            
   * @param evt event generated when key is pressed.                            
   */                                                                           
  public void keyPressed(KeyEvent evt) {                                        
    switch (evt.getKeyCode()) {                                                 
                                                                                
      case KeyEvent.VK_LEFT:
        if (!paused && selected) {
          
          move(-dx, 0);
        }
        break;                                                                  
      case KeyEvent.VK_RIGHT: 
        if (!paused && selected) {
          move(dx, 0);
        }
        break;                                                                  
                                                                                
      // Pause                                                                  
      case KeyEvent.VK_SPACE:
        paused = !paused;
        break;
      case KeyEvent.VK_U:
        paused = true;
        break;   

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
