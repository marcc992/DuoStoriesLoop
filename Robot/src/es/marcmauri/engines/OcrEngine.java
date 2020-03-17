/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.marcmauri.engines;

import es.marcmauri.models.BeanText;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
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
    private final Robot robot;

    public OcrEngine() throws AWTException, InterruptedException {        
        // Instanciamos y configuramos el OCR
        OCR = new Tesseract();
        OCR.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        OCR.setLanguage("eng");
        
        // Instanciamos el robot
         robot = new Robot();
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
    
    public List<BeanText> getWordsFromDevice(final Rectangle device, boolean isSpanish) {
        if (isSpanish) {
            OCR.setLanguage("spa");
        } else {
            OCR.setLanguage("eng");
        }
        
        BufferedImage bufImg = robot.createScreenCapture(device);
        return getStringsFromImage(bufImg, 3);
    }
    
    public List<BeanText> getLinesFromDevice(final Rectangle device, boolean isSpanish) {
        if (isSpanish) {
            OCR.setLanguage("spa");
        } else {
            OCR.setLanguage("eng");
        }
        
        BufferedImage bufImg = robot.createScreenCapture(device);
        return getStringsFromImage(bufImg, 2);
    }
    
}
