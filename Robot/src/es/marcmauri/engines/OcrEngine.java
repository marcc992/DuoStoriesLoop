/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.marcmauri.engines;

import es.marcmauri.models.BeanText;
import es.marcmauri.tools.MouseCorrectRobot;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;

/**
 *
 * @author Marc.Mauri
 */
public class OcrEngine {
    
    private final Tesseract OCR;
    private final MouseCorrectRobot MouseTool;
    private final Rectangle Device;
    private final Dimension ScreenSize;
    private final Robot robot = new Robot();

    public OcrEngine(double minX, double minY, double maxX, double maxY) throws AWTException, InterruptedException {
        // Recuperamos el tamaño de la pantalla principal del PC
        ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Instanciamos y configuramos el OCR
        OCR = new Tesseract();
        OCR.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        OCR.setLanguage("eng");
        
        MouseTool = new MouseCorrectRobot();    
        // 1. Generamos el rectangulo del telefono donde escanear palabras
        // 1.1. Movemos el raton a los puntos extremos para recoger los valores
        MouseTool.moveMouseControlled(minX, minY);
        int x = MouseTool.lastX;
        int y = MouseTool.lastY;
        MouseTool.moveMouseControlled(maxX, maxY);
        int width = MouseTool.lastX - x;
        int height = MouseTool.lastY - y;
        // 1.2. Creamos el Rectangle con los valores de la pantalla del movil
        Device = new Rectangle(x, y, width, height);
        
        /* debug */
        System.out.println("[d] Esperamos datos (2 seg)");
        Thread.sleep(1000);
        double deviceMinX = Device.getX() / ScreenSize.width;
        double deviceMaxX = Device.getMaxX() / ScreenSize.width;
        double deviceMinY = Device.getY() / ScreenSize.height;
        double deviceMaxY = Device.getMaxY() / ScreenSize.height;
        System.out.println("[d] Coordenadas | porcentuales del Dispositivo:\n"
                + "\tMin X = " + Device.getX() + " | " + deviceMinX + "\n"
                + "\tMin Y = " + Device.getY() + " | " + deviceMinY + "\n"
                + "\tMax X = " + Device.getMaxX() + " | " + deviceMaxX + "\n"
                + "\tMax Y = " + Device.getMaxY() + " | " + deviceMaxY + "\n");
        
        Thread.sleep(500);
        System.out.println("[d] Simulando rectangulo ...");
        Thread.sleep(500);
        
        for (double j = Device.getMinY(); j < Device.getMaxY(); j += 40) {
            for (double i = Device.getMinX(); i < Device.getMaxX(); i += 40) {
                MouseTool.moveMouseControlledByPixels((int)i, (int)j);
                Thread.sleep(30);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                Thread.sleep(30);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }
        }
        
        
        /* !debug */
        
    }
    
     // LEVEL => 2: Lineas; 3: Palabras
    private List<BeanText> getStringsFromImage(BufferedImage img, int level) {
        List<BeanText> beanTextList = new ArrayList<>();
        
        try {
            // 1. Se recuperan todos los Words de la imagen, 
            //    ya sea como palabra individual o como linea de texto
            List<Word> words = OCR.getWords(img, level); // 2-> Lineas; 3-> Palabras
            
            // 2. De cada Word recuperaremos su texto y su posicion
            //    (Creamos las variables fuera del bucle para ahorrar memoria)
            Rectangle rect;
            String text;
            
            // 3. Empezamos a iterar...
            for (Word w : words) {
                
                // 4. Recuperamos la caja conceptual que contiene el texto
                rect = w.getBoundingBox();
                
                // -- Extra para cambiar "|" o "!" por "i"
                if (w.getText().equalsIgnoreCase("|") || w.getText().equalsIgnoreCase("!")) {
                    w = new Word("i", 2, w.getBoundingBox());
                }
                
                // 5. Recuperamos el texto
                System.out.println("x-[" + w.getText() + "]");
                text = w.getText().replaceAll("[^\\w\\s]", "").replaceAll("( )+", " ").trim();
                System.out.println("y-[" + text + "]");
                
                // 6. Si creemos que es texto valido, guardamos sus datos
                //    Nota: Las coordenadas empiezan en la esquina inferior izquierda
                if (!text.isEmpty() && text.length() >= 1) {
                    beanTextList.add(new BeanText(
                            text, 
                            (int) rect.getCenterX(), 
                            (int) rect.getCenterY()));
                }
            }
            
        } catch (Exception ex) {
            System.err.println("[ERROR] OcrEngine.getStringsFromImage => " + ex.toString());
        }
        
        return beanTextList;
    }
    
    public List<BeanText> getWordsFromImage(BufferedImage img, boolean isSpanish) {
        if (isSpanish) {
            OCR.setLanguage("spa");
        } else {
            OCR.setLanguage("eng");
        }
        return getStringsFromImage(img, 3);
    }
    
    public List<BeanText> getLinesFromImage(BufferedImage img, boolean isSpanish) {
        if (isSpanish) {
            OCR.setLanguage("spa");
        } else {
            OCR.setLanguage("eng");
        }
        return getStringsFromImage(img, 2);
    }
    
}
