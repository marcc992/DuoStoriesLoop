/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.marcmauri.robot;

import es.marcmauri.engines.OcrEngine;
import es.marcmauri.models.BeanText;
import es.marcmauri.tools.MouseCorrectRobot;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;

/**
 *
 * @author marc.mauri
 */
public class MyRobot {
    
    /*
        Config
    */
    private static int N_RETRIES = 5;
    
    private static final double MIN_X = 0.008;
    private static final double MAX_X = 0.225;
    private static final double MIN_Y = 0.33;
    private static final double MAX_Y = 0.88;
    
    private static final double CHECKBOX_X = 0.02;
    private static final double CHECKBOX_HEIGHT = 0.0000;
    private static final double CHECKBOX_SEARCH_MIN_X = 0.03;
    private static final double CHECKBOX_SEARCH_MAX_X = MAX_X;
    private static final double CHECKBOX_SEARCH_MIN_Y = 0.0000; //TODO: DETERMINAR MIN Y PARA BUSCAR FRASES DE CHECKBOX
    private static final double CHECKBOX_SEARCH_MAX_Y = 0.0000; //TODO: DETERMINAR MAX Y PARA BUSCAR FRASES DE CHECKBOX
    
    private static final double WORD_HEIGHT = 0.000;
    private static final double WORDS_SEARCH_MIN_X = MIN_X;
    private static final double WORDS_SEARCH_MAX_X = MAX_X;
    private static final double WORDS_SEARCH_MIN_Y = 0.0000; // TODO: DETERMINAR MIN Y PARA BUSCAR PALABRAS (FUNCION QUE BUSQUE "Selecciona los pares")
    private static final double WORDS_SEARCH_MAX_Y = MAX_Y;
    
    private static final double DOWN_BUTTON_X = 0.09;
    private static final double DOWN_BUTTON_Y = 0.85;

    /*
        Variables
    */
    private static Robot robot;
    private static MouseCorrectRobot mouseTools;
    private static OcrEngine ocrEngine;
    
    private static final double  screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
    
    
    /*
        Set up
    */
    private static void setUp() throws AWTException, InterruptedException {
        robot = new Robot();
        mouseTools = new MouseCorrectRobot();
        ocrEngine = new OcrEngine(MIN_X, MIN_Y, MAX_X, MAX_Y);
    }
    
    /*
        Custom methods
    */
    
    private static void doLeftClick() throws InterruptedException {
        Thread.sleep(350);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(350);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    
    private static void doMove(final double perX, final double perY) throws InterruptedException {
        Thread.sleep(350);
        mouseTools.moveMouseControlled(perX, perY);
    }
    
    private static void clickOnDownButton() throws InterruptedException {
        // Continuar
        // Saltar anuncio
        //Thread.sleep(250);
        //mouseTools.moveMouseControlled(DOWN_BUTTON_X, DOWN_BUTTON_Y);
        //robot.mouseMove(225, 975);
        //robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        //Thread.sleep(250);
        //robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        
        Thread.sleep(250);
        System.out.println("--> Moviendo a MIN_X y MIN_Y");
        mouseTools.moveMouseControlled(MIN_X, MIN_Y);
        int rectangleX = mouseTools.lastX;
        int rectangleY = mouseTools.lastY;
        
        Thread.sleep(250);
        System.out.println("--> Moviendo a MAX_X y MIN_Y");
        mouseTools.moveMouseControlled(MAX_X, MIN_Y);
        
        Thread.sleep(250);
        System.out.println("--> Moviendo a MAX_X y MAX_Y");
        mouseTools.moveMouseControlled(MAX_X, MAX_Y);
        int rectangleWidth = mouseTools.lastX - rectangleX;
        int rectangleHeight = mouseTools.lastY - rectangleY;
        
        Thread.sleep(250);
        System.out.println("--> Recogiendo texto del rectangulo...");
        Thread.sleep(250);
        
        boolean found = clickOnWordWithinRectangle(rectangleX, rectangleY, rectangleWidth, rectangleHeight, "book", false);
        if (found) {
            System.out.println("Text found and clicked!");
        } else {
            System.out.println("Text not found... ");
        }
        
        
        //Thread.sleep(250);
        //mouseTools.moveMouseControlled(CHECKBOX_X, 0.66);
        //Thread.sleep(250);
        //mouseTools.moveMouseControlled(CHECKBOX_SEARCH_MIN_X, 0.66);
        //robot.mouseMove(62, 975);
    }
    
    private static void clickAndConfirmExitStory() throws InterruptedException {
        doMove(0.017, 0.1);
        doLeftClick();
        Thread.sleep(580);
        doMove(0.18, 0.525);
        doLeftClick();
        Thread.sleep(3700);
        doMove(0.017, 0.105);
        doLeftClick();
    }
    
    private static void simulateRectangle(Rectangle screen) throws InterruptedException {
        
        double x = screen.getX() / screenWidth;
        double xx = screen.getMaxX() / screenWidth;
        double y = screen.getY() / screenHeight;
        double yy = screen.getMaxY() / screenHeight;
        //System.out.println("x: " + x + ", y: " + y + ", max X: " + xx + ", max Y: " + yy);
        
        mouseTools.moveMouseControlled(x, y);
        //Thread.sleep(200);
        //robot.mouseMove(xx/2, y);
        //Thread.sleep(200);
        //mouseTools.moveMouseControlled(xx, y);
        //Thread.sleep(200);
        //robot.mouseMove(xx, yy/2);
        Thread.sleep(200);
        mouseTools.moveMouseControlled(xx, yy);
    }
    
    private static boolean clickOnWordWithinRectangle(int x, int y, int width, 
            int height, String text, boolean isSpanish) throws InterruptedException {
        
        boolean found = false;
        
        Rectangle screenPiece = new Rectangle(x, y, width, height);
        simulateRectangle(screenPiece);
        
        BufferedImage bufImg = robot.createScreenCapture(screenPiece);
        for (BeanText bText : ocrEngine.getWordsFromImage(bufImg, isSpanish)) {
            //System.out.println("[ " + bText.getContent() + " ]");
            if (bText.getContent().toLowerCase().equalsIgnoreCase(text.toLowerCase())) {
                found = true;
                //System.out.println("Found!\n\tx: " + bText.getCenterX() + "\n\ty: " + bText.getCenterY());
                mouseTools.moveMouseControlledByPixels(x + bText.getCenterX(), y + bText.getCenterY());
                doLeftClick();
                //break; // No detenemos la ejecucion por si detecta una palabra en texto no clicable
            }
        }
        
        return found;
    }
    
    private static boolean clickOnLineWithinRectangle(int x, int y, int width, 
            int height, String text, boolean isSpanish) throws InterruptedException {
        
        boolean found = false;
        
        Rectangle screenPiece = new Rectangle(x, y, width, height);
        simulateRectangle(screenPiece);
        
        BufferedImage bufImg = robot.createScreenCapture(screenPiece);
        for (BeanText bText : ocrEngine.getLinesFromImage(bufImg, isSpanish)) {
            //System.out.println("[ " + bText.getContent() + " ]");
            if (bText.getContent().toLowerCase().equalsIgnoreCase(text.toLowerCase())) {
                found = true;
                //System.out.println("Found!\n\tx: " + bText.getCenterX() + "\n\ty: " + bText.getCenterY());
                mouseTools.moveMouseControlledByPixels(x + bText.getCenterX(), y + bText.getCenterY());
                doLeftClick();
                //break; // No detenemos la ejecucion por si detecta una palabra en texto no clicable
            }
        }
        
        return found;
    }
    
    private static void clickOnContinue() throws InterruptedException {
        Thread.sleep(200);
        doMove(DOWN_BUTTON_X, DOWN_BUTTON_Y);
        doLeftClick();
    }
    
    public static boolean dummyResolveCuento() throws InterruptedException {
        /* */
        // Move to cuento's menu
        doMove(0.07, DOWN_BUTTON_Y);
        doLeftClick();
        
        // Move to cuento's icon
        doMove(0.05, 0.40);
        doLeftClick();
        
        // Esperar CONTINUAR disponible
        Thread.sleep(1500);
        clickOnContinue();
        
        // Texto: Good morning, honey.
        Thread.sleep(900);
        clickOnContinue();
        
        // Texto: Good morning, Lauren!
        Thread.sleep(900);
        clickOnContinue();
        
        // Texto: Que significa "honey" aqui?
        Thread.sleep(1000);
        /* */
        // Crear caja de busqueda
        Thread.sleep(200);
        //System.out.println("--> Moviendo a MIN_X y MIN_Y");
        mouseTools.moveMouseControlled(MIN_X, MIN_Y);
        int minX = mouseTools.lastX;
        int minY = mouseTools.lastY;
        
        Thread.sleep(200);
        //System.out.println("--> Moviendo a MAX_X y MAX_Y");
        mouseTools.moveMouseControlled(MAX_X, MAX_Y);
        int rectangleWidthX = mouseTools.lastX - minX;
        int rectangleHeightY = mouseTools.lastY - minY;
        
        /* */
        
        boolean found = false;
        int retry = 0;
        do {
            ++retry;
            
            found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "cario", true);
            if (!found) found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "cariño", true);
            if (!found) found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "carifo", true);
            if (!found) found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "carifo", true);
            if (!found) found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "carino", true);
            if (!found) found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "carino", true);
            
            if (!found) {
                Thread.sleep(450);
            }
        } while (!found && retry < N_RETRIES);

        if (found) {
            // Tenemos el cursor justo encima de la palabra, debemos moverlo horizontalmente hacia
            // la posicion del checkbox
            double yPercent = (double) mouseTools.lastY / screenHeight;
            mouseTools.moveMouseControlled(CHECKBOX_X, yPercent);
            doLeftClick();
        } else {
            System.err.print("[ERROR] La palabra 'cariño' no se ha encontrado. Detenemos programa");
            return false;
        }
        
        clickOnContinue();
        
        // Texto: Where is my Spanish book?
        Thread.sleep(2000);
        clickOnContinue();
        
        // Texto: Your book?
        Thread.sleep(750);
        clickOnContinue();
        
        // Texto: I have an important Spanish exam this morning
        Thread.sleep(3300);
        clickOnContinue();
        
        // Escucha y selecciona las palabras (I need my book)
        Thread.sleep(2900); //+1 seg para que de tiempo a posicionarse
        /* */
        
        String[] iNeedMyBook = {"I", "need", "my", "book"};
        found = false;
        for (retry = 0; !found && retry < N_RETRIES; ++retry) {
            found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "I", false);
            if (!found) found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "L", false);
            
            if (!found) {
                Thread.sleep(450);
            }
        }
        if (!found) {
            System.err.println("No se encontro la letra 'I' (I need my book)");
            return false;
        }
        
        for (int i = 1; i < iNeedMyBook.length; ++i) {
            retry = 0;
            do {
                ++retry;
                found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, iNeedMyBook[i], false);
                if (!found) {
                    Thread.sleep(450);
                }
            } while (!found && retry < N_RETRIES);
            
            if (!found) {
                System.err.println("No se encontro la palabra '" + iNeedMyBook[i] + "' (I need my book)");
                return false;
            }
        }
        Thread.sleep(100);
        clickOnContinue();
        
        // Texto: Lauren, your book is here on the table!
        Thread.sleep(3500);
        clickOnContinue();

        // Texto: Sorry honey, I worked a lot!
        Thread.sleep(4000);
        clickOnContinue();
        
        // Haz clic en la opcion que significa cansada
        Thread.sleep(1150);
        
        found = false;
        for (retry = 0; !found && retry < N_RETRIES; ++retry) {
            found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "tired", false);
            
            if (!found) {
                Thread.sleep(450);
            }
        }
        if (!found) {
            System.err.println("No se encontro la palabra 'tired'");
            return false;
        }
        clickOnContinue();
        
        // Texto: Do you want some coffee?
        Thread.sleep(1800);
        clickOnContinue();
        
        // Texto: Yes, with milk, please.
        Thread.sleep(2200);
        clickOnContinue();
        
        // Texto: OK. Here.
        Thread.sleep(1000);
        clickOnContinue();
        
        // Texto: Lauren puts sugar in the coffee.
        Thread.sleep(3500);
        clickOnContinue();
        
        // Que esta haciendo Lauren?
        Thread.sleep(900);
        
        found = false;
        for (retry = 0; !found && retry < N_RETRIES; ++retry) {
            found = clickOnLineWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "Poniendo azcar en su caf", true);
            
            if (!found) {
                Thread.sleep(450);
            }
        }
        if (found) {
            // Tenemos el cursor justo encima de la frase, debemos moverlo horizontalmente hacia
            // la posicion del checkbox
            double yPercent = (double) mouseTools.lastY / screenHeight;
            mouseTools.moveMouseControlled(CHECKBOX_X, yPercent);
            doLeftClick();
        } 
        else {
            System.err.println("No se encontro la frase 'Poniendo azúcar en su café.'");
            return false;
        }
        clickOnContinue();
        
        // Texto: Lauren puts sugar in the coffee.
        Thread.sleep(1500);
        clickOnContinue();
        
        // Texto: Yuck!
        Thread.sleep(600);
        clickOnContinue();
        
        // Texto: What?
        Thread.sleep(600);
        clickOnContinue();
        
        // Texto: It's salt!
        Thread.sleep(800);
        clickOnContinue();
        
        // Que significa "It's salt"?
        Thread.sleep(1000);
        
        found = false;
        for (retry = 0; !found && retry < N_RETRIES; ++retry) {
            found = clickOnLineWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "Es sal", true);
            
            if (!found) {
                Thread.sleep(450);
            }
        }
        if (found) {
            // Tenemos el cursor justo encima de la frase, debemos moverlo horizontalmente hacia
            // la posicion del checkbox
            double yPercent = (double) mouseTools.lastY / screenHeight;
            mouseTools.moveMouseControlled(CHECKBOX_X, yPercent);
            doLeftClick();
        } 
        else {
            System.err.println("No se encontro la frase 'Es sal!'");
            return false;
        }
        clickOnContinue();
        
        // Texto: Lauren, you are very tired!
        Thread.sleep(2500);
        clickOnContinue();
        
        // Texto: Yes, I need a new cup of coffe... with sugar, not salt!
        Thread.sleep(6500);
        clickOnContinue();
        
        // Que significa "It's salt"?
        Thread.sleep(800);
        
        found = false;
        for (retry = 0; !found && retry < N_RETRIES; ++retry) {
            found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, "puso", true);
            
            if (!found) {
                Thread.sleep(450);
            }
        }
        if (found) {
            // Tenemos el cursor justo encima de la frase, debemos moverlo horizontalmente hacia
            // la posicion del checkbox
            double yPercent = (double) mouseTools.lastY / screenHeight;
            mouseTools.moveMouseControlled(CHECKBOX_X, yPercent);
            doLeftClick();
        } 
        else {
            System.err.println("No se encontro la frase '... ella le puso sal a su cafe.'");
            return false;
        }
        clickOnContinue();
        
        
        // Selecciona losn pares
        Thread.sleep(950);
        
        String[] espWords = {"t", "muy", "cansada", "azcar", "taza"};
        String[] engWords = {"you", "very", "tired", "sugar", "cup"};
        
        for (int i = 0; i < espWords.length; ++i) {
            retry = 0;
            do {
                ++retry;
                found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, espWords[i], true);
                if (!found) {
                    Thread.sleep(450);
                }
            } while (!found && retry < N_RETRIES);
            
            if (!found) {
                System.err.println("No se encontro la palabra en espanol '" + espWords[i] + "'");
                return false;
            }
            
            retry = 0;
            do {
                ++retry;
                found = clickOnWordWithinRectangle(minX, minY, rectangleWidthX, rectangleHeightY, engWords[i], true);
                if (!found) {
                    Thread.sleep(450);
                }
            } while (!found && retry < N_RETRIES);
            
            if (!found) {
                System.err.println("No se encontro la palabra en ingles '" + engWords[i] + "'");
                return false;
            }
        }
        Thread.sleep(100);
        clickOnContinue();
        
        // Pulsamos en Continue unos segundos hasta que creemos que esta todo cerrado
        for (int i = 0; i < 7; ++i) {
            Thread.sleep(1000);
            clickOnContinue();
        }
        
        return true;
    }
    
    
    public static void main(String[] args) {
        try {
            setUp();
            //clickOnDownButton();
            /*
            int good = 0;
            int bad = 0;
            for (int times = 0; times < 1000; ++times) {
                System.out.println("\n\n---------------------");
                System.out.println("Script ejecutado " + (times) + " veces.");
                System.out.println("---------------------");
                System.out.println("Stats-> Good: " + good + "  |  Bad: " + bad);
                System.out.println("---------------------\n\n");
                
                if (!dummyResolveCuento()) {
                    ++bad;
                    System.err.println("------ WARNING ------");
                    System.err.println("No se pudo resolver el cuento");
                    System.err.println("Salimos pulsando en la X");
                    System.err.println("---------------------");
                    
                    clickAndConfirmExitStory();
                } else {
                    ++good;
                    System.out.println("--- Story Well-done! ---");
                }    
            }
            */
        }
        catch (Exception ex) {
            System.err.println();
            System.err.println("--> Exception! <--");
            System.err.println(ex.toString());
            System.err.println("-- ------------ --");
            System.err.println();
        }
    }
    
}
