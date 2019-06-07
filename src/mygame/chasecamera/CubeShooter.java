package mygame.chasecamera;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JOptionPane;
import sun.reflect.generics.visitor.Reifier;
/** Sample 8 - how to let the user pick (select) objects in the scene
 * using the mouse or key presses. Can be used for shooting, opening doors, etc. */
public class CubeShooter extends SimpleApplication {  public static void main(String[] args) {
    
      new CubeShooter().start();
    
  }
  Node mundo;
  Geometry marca;
  List<String> CubeNames = new ArrayList();
  int highScore;
  long startTime;
  long elapsedTime;
  long timeLeft;
  int volume;
  Map<String, Integer> highScores;
  Boolean pause = false;
  String userName;
  int dificuldade;
  
  @Override
  public void simpleInitApp() {
    initCrossHairs();
    initKeys();
    initMark();
    mundo = new Node("mesa");
    rootNode.attachChild(mundo);
    mundo.attachChild(makeFloor());
    volume = 5;
    highScores = new HashMap();

  }
  
  private void initKeys() {
    inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
    inputManager.addMapping("Tiro", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(actionListener, "Tiro");
  }
  
  private void createCubes(){
    float[] numbersx = {-10, -9, -8, -7, -6, -5, -4, -3,-2,-1,0,1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    float[] numbersz = {1,0,-1,-2,-3, -4, -5, -6, -7, -8, -9, -10, -11, -12, -13, -14};
    float[] numbersy = {1,0,-1,-2,-3,-4};
    
    Random randomGenerator = new Random();
    mundo.attachChild(makeCube("Objeto 1", numbersx[randomGenerator.nextInt(19)], numbersy[randomGenerator.nextInt(5)], numbersz[randomGenerator.nextInt(15)]));
    mundo.attachChild(makeCube("Objeto 2", numbersx[randomGenerator.nextInt(19)], numbersy[randomGenerator.nextInt(5)], numbersz[randomGenerator.nextInt(15)]));
    mundo.attachChild(makeCube("Objeto 3", numbersx[randomGenerator.nextInt(19)], numbersy[randomGenerator.nextInt(5)], numbersz[randomGenerator.nextInt(15)]));
    mundo.attachChild(makeCube("Objeto 4", numbersx[randomGenerator.nextInt(19)], numbersy[randomGenerator.nextInt(5)], numbersz[randomGenerator.nextInt(15)]));

    CubeNames.add("Objeto 1");
    CubeNames.add("Objeto 2");
    CubeNames.add("Objeto 3");
    CubeNames.add("Objeto 4");
  }
  
  private void removeCube(String name){
      System.out.println(name);
      if(!"the Floor".equals(name))
      {
         mundo.detachChildNamed(name);
         highScore++;
         timeLeft++;
         CubeNames.remove(name);
         if(CubeNames.size() < 1)
             createCubes();
      }
  }
  
  private ActionListener actionListener = new ActionListener() {
    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
       if (name.equals("Tiro") && !keyPressed) {
           
         // 1. Reinicia lista de resultados
        CollisionResults results = new CollisionResults();
        // 2. Redife o raio em função da localicação e direção da camera.
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        // 3. Verifica colisões e guarda em results
        mundo.collideWith(ray, results);
        // 4. Imprime colisões
        System.out.println("----- Colisões? " + results.size() + "-----");
        
        
        for (int i = 0; i < results.size(); i++) {
          float dist = results.getCollision(i).getDistance();
          Vector3f pt = results.getCollision(i).getContactPoint();
          String hit = results.getCollision(i).getGeometry().getName();
          System.out.println("O ray colidiu " + hit + " na posição " + pt + 
                  ", " + dist + " de distância.");
          
        }
        if (results.size() > 0){ // 5. Açao da colisão
          // Qual é a colisão mais próxima?
          CollisionResult closest = results.getClosestCollision();
          System.out.println("O mais proximo é: " + closest.getGeometry().getName());
          removeCube(closest.getGeometry().getName());
          //Posiciona a marca.
          marca.setLocalTranslation(closest.getContactPoint());
          rootNode.attachChild(marca);
        } else {
        // Remove a marca
          rootNode.detachChild(marca);
        }
      }
    }
  };
  /** A cube object for target practice */
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(new Vector3f(x, y, z), 1, 1, 1);
    Geometry cube = new Geometry(name, box);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    cube.setMaterial(mat1);
    return cube;
  }
  /** A floor to show that the "shot" can go through several objects. */
  protected Geometry makeFloor() {
    Box box = new Box(new Vector3f(0,-4,-5), 15,.2f,15);
    Geometry floor = new Geometry("the Floor", box);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.Gray);
    floor.setMaterial(mat1);
    return floor;
  }
  /** A red ball that marks the last spot that was "hit" by the "shot". */
  protected void initMark() {
    Sphere sphere = new Sphere(10, 10, 0.1f);
    marca = new Geometry("BOOM!", sphere);
    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mark_mat.setColor("Color", ColorRGBA.Red);
    marca.setMaterial(mark_mat);
  }
  /** A centred plus sign to help the player aim. */
  protected void initCrossHairs() {
    guiNode.detachAllChildren();
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
    ch.setText("+"); // crosshairs
    ch.setLocalTranslation( // center
      settings.getWidth()/2 - guiFont.getCharSet().getRenderedSize()/3*2,
      settings.getHeight()/2 + ch.getLineHeight()/2, 0);
    guiNode.attachChild(ch);
  }

    private void createHighscore() {
        guiNode.detachChildNamed("PLACAR");
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setName("PLACAR");
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("Score: " + highScore);
        helloText.setLocalTranslation(300, helloText.getLineHeight() + 30, 0);
        guiNode.attachChild(helloText);
    }
    
    private void createTimer() {
        guiNode.detachChildNamed("TIMER");
        timeLeft = 30 - (elapsedTime/1000);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setName("TIMER");
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("Time left: " + timeLeft);
        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
        guiNode.attachChild(helloText);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        if(timeLeft <= 0) {
            addHighscore();
            newGame();
        }
        moveCube();
        if(!pause)
        {
            createHighscore();
            elapsedTime = (new Date()).getTime() - startTime;
            createTimer();
        }
    }
    
    private void newGame() {
        userName = JOptionPane.showInputDialog("Insira seu nome");
        
        highScore = 0;
        if(CubeNames.size() > 0) {
            for(String name : CubeNames) {
                mundo.detachChildNamed(name);
            }
        }
        CubeNames = new ArrayList();
        timeLeft = 30;
        startTime = System.currentTimeMillis();
        elapsedTime = 0;
        createCubes();
    }
    
    private void setPause(Boolean action) {
        pause = action;
        if(pause) {
            inputManager.removeListener(actionListener);
        }
        else {
            inputManager.addListener(actionListener, "Tiro");
        }
    }
    
    private void addHighscore() {
        highScores.put(userName, highScore);
    }
    
    private void createMenu() {
        List<String> optionList = new ArrayList<String>();
        optionList.add("0");
        optionList.add("1");
        optionList.add("2");
        optionList.add("3");
        optionList.add("4");
        optionList.add("5");
        optionList.add("6");
        
        Object[] options = optionList.toArray();
        int value;
        value = JOptionPane.showOptionDialog(
                null,
                "Selecione um dos itens:\n "
                        + "0. Sair\n"
                        + " 1. Novo Jogo\n "
                        + "2. Placar\n "
                        + "3. Controles\n ",
                "Opção:",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                optionList.get(0));
            
                if (value == 1)
                    newGame();
                
                if(value == 2)
                    JOptionPane.showMessageDialog(null, highScores);
                
                if(value == 3)
                {
                    JOptionPane.showMessageDialog(null, "Utilize o mouse "
                            + "para mover a mira e pressione a tecla espaço para "
                            + "atirar. Tente fazer o maior placar possível no "
                            + "tempo de trinta segundos.");
                }
        }
    
    private void moveCube() {
        List<Spatial> cubos = mundo.getChildren();
        if(!cubos.isEmpty()) {
            for (Spatial cubo : cubos) {
                if(!"the Floor".equals(cubo.getName()))
                cubo.move((float) 0.0010, 0, 0);
            }
        }
    }
}






