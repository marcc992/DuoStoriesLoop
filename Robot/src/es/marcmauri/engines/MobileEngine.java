/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.marcmauri.engines;

import es.marcmauri.models.BeanText;
import es.marcmauri.tools.MouseCorrectRobot;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author marc.mauri
 */
public class MobileEngine {
    
    private final Dimension PcScreenSize;
    private final Robot MyRobot;
    private final OcrEngine OCR;
    private final MouseCorrectRobot MouseTools;
    private final Rectangle MobileScreen;
    
    private final Point storiesAndContinueButton;
    private final Point firstStoryButton;
    private final Point closeButton;
    private final Point confirmCloseButton;
    
    private final int checkboxX;
    
    private final String[] espWords;
    private final String[] engWords;
    
    private final double MobileMinX;
    private final double MobileMinY;
    private final double MobileMaxX;
    private final double MobileMaxY;

    
    public MobileEngine(int posMinX, int posMinY, int posMaxX, int posMaxY, 
            Point continueButton, Point firstStoryButton, Point closeButton,
            Point confirmCloseButton, int checkboxX, String[] espWords, 
            String[] engWords) throws Exception {
        System.out.println("[info] MobileEngine() - START!");
        // Recuperamos el tamaño de la pantalla principal del PC
        PcScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Instanciamos el robot
        MyRobot = new Robot();
        
        // Instanciamos el OCR
        OCR = new OcrEngine();
        
        // Instanciamos el controlador del Mouse
        MouseTools = new MouseCorrectRobot();
        
        // Inizializamos la pantalla del movil como un Rectangle
        this.MobileScreen = new Rectangle(posMinX, posMinY, posMaxX - posMinX, posMaxY - posMinY);
        
        // Recuperamos los valores minX, maxX, minY, maxY E [0..1] para generar el rectangulo
        // que compone la mobileScreen, teniendo en cuenta el escalado de windows
        MobileMinX = MobileScreen.getX() / PcScreenSize.width;
        MobileMinY = MobileScreen.getY() / PcScreenSize.height;
        MobileMaxX = MobileScreen.getMaxX() / PcScreenSize.width;
        MobileMaxY = MobileScreen.getMaxY() / PcScreenSize.height;
        simulateMobileScreen(false);
        
        // Recuperamos la posicion de los botones
        this.storiesAndContinueButton = new Point(continueButton.x, continueButton.y);
        this.firstStoryButton = new Point(firstStoryButton.x, firstStoryButton.y);
        this.closeButton = new Point(closeButton.x, closeButton.y);
        this.confirmCloseButton = new Point(confirmCloseButton.x, confirmCloseButton.y);
        
        // Recuperamos la X para los checkbox
        this.checkboxX = checkboxX;
        
        // Recuperamos los vectores de palabras para comprobar las traducciones
        this.espWords = espWords;
        this.engWords = engWords;
        
        Thread.sleep(200);
        
        // Test buttons
        //testCriticalButtons();
        
        
        System.out.println("[info] MobileEngine() - END!");
    }
    
    private void simulateMobileScreen(boolean full) {
        try {
            /* debug */
            
            System.out.println("[d] Simulando rectangulo ...");
            Thread.sleep(500);

            if (full) {
                for (double j = MobileScreen.getMinY(); j < MobileScreen.getMaxY(); j += 40) {
                    for (double i = MobileScreen.getMinX(); i < MobileScreen.getMaxX(); i += 40) {
                        MouseTools.moveMouseControlledByPixels((int)i, (int)j);
                        Thread.sleep(30);
                        //MyRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        //Thread.sleep(30);
                        //MyRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    }
                }
            } else {
                MouseTools.moveMouseControlled(MobileMinX, MobileMinY);
                Thread.sleep(2000);
                MouseTools.moveMouseControlled(MobileMaxX, MobileMaxY);
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        
    }
    
    private void testCriticalButtons() throws Exception {
        // Empezar historia
        startStory();
        
        Thread.sleep(650);
        // Boton continuar
        clickOnContinue();
        Thread.sleep(1500);
        
        // Cerrar historia
        closeStory();
    }
    
    private void doLeftClick() throws InterruptedException {
        Thread.sleep(350);
        MyRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(350);
        MyRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    
    private void clickOnContinue() throws Exception {
        Thread.sleep(200);
        MouseTools.moveMouseControlledByPixels(this.storiesAndContinueButton.x, this.storiesAndContinueButton.y);
        doLeftClick();
    }
    
    private void startStory() throws Exception {
        Thread.sleep(200);
        // Menu historias
        clickOnContinue();
        Thread.sleep(300);
        
        // Boton historia facil
        MouseTools.moveMouseControlledByPixels(this.firstStoryButton.x, this.firstStoryButton.y);
        doLeftClick();
    }
    
    private void closeStory() throws Exception {
        System.err.print("[event] Saliendo de la historia ...");
        Thread.sleep(200);
        // Boton cerrar
        MouseTools.moveMouseControlledByPixels(this.closeButton.x, this.closeButton.y);
        doLeftClick();
        Thread.sleep(700);
        
        // Boton confirmar cierre
        MouseTools.moveMouseControlledByPixels(this.confirmCloseButton.x, this.confirmCloseButton.y);
        doLeftClick();
        Thread.sleep(5000);
        
        // Boton Ocultar anuncios
        MouseTools.moveMouseControlledByPixels(this.storiesAndContinueButton.x, this.storiesAndContinueButton.y);
        doLeftClick();
        Thread.sleep(500);
        
        // Boton No, gracias
        MouseTools.moveMouseControlledByPixels(this.storiesAndContinueButton.x, this.storiesAndContinueButton.y);
        doLeftClick();
    }
    
    private boolean clickOnItsCheckbox(String word, boolean isSpanish) throws Exception {
        boolean found = false;
        List<BeanText> foundItems;
        if (word.split(" ").length > 1) {
            foundItems = OCR.getLinesFromDevice(MobileScreen, isSpanish);
        } else {
            foundItems = OCR.getWordsFromDevice(MobileScreen, isSpanish);
        }
        
        for (BeanText item : foundItems) {
            if (word.equalsIgnoreCase(item.getContent())) {
                found = true;
                Thread.sleep(200);
                MouseTools.moveMouseControlledByPixels(checkboxX, (int) MobileScreen.getMinY() + item.getCenterY());
                doLeftClick();
            }
        }
        
        return found;
    }
    
    private boolean clickOnAllWord(String[] words, boolean isSpanish) throws Exception {
        // Recuperamos las palabras una sola vez
        List<BeanText> foundItems = OCR.getWordsFromDevice(MobileScreen, isSpanish);
        
        for (String word : words) {
            boolean found = false;
            for (BeanText item : foundItems) {
            if (word.equalsIgnoreCase(item.getContent())) {
                    found = true;
                    Thread.sleep(200);
                    MouseTools.moveMouseControlledByPixels(
                            (int) MobileScreen.getMinX() + item.getCenterX(), 
                            (int) MobileScreen.getMinY() + item.getCenterY());
                    doLeftClick();
                }
            }
            
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean clickOnPairs(String[] espWords, String[] engWords) throws Exception {
        // Recuperamos las palabras en ingles
        List<BeanText> foundEngItems = OCR.getWordsFromDevice(MobileScreen, false);
        List<BeanText> foundEspItems = OCR.getWordsFromDevice(MobileScreen, true);
        
        if (espWords.length != engWords.length) {
            return false;
        }
        
        // Guardamos todas las posiciones a clicar en una cola para no estropear
        // el layout con algun clic si saliera alguna busqueda mal
        Queue<BeanText> clickableItems = new LinkedList<>();
        
        for (int i = 0; i < espWords.length; ++i) {
            boolean found = false;
            for (BeanText item : foundEspItems) {
                if (espWords[i].equalsIgnoreCase(item.getContent())) {
                    found = clickableItems.add(item);
                }
            }
            
            if (!found) {
                return false;
            }
            
            found = false;
            for (BeanText item : foundEngItems) {
                if (engWords[i].equalsIgnoreCase(item.getContent())) {
                    found = clickableItems.add(item);
                }
            }
            
            if (!found) {
                return false;
            }
        }
        
        // Por cada posicion valida, hacemos clic
        while (!clickableItems.isEmpty()) {
            BeanText validItem = clickableItems.remove();
            
            Thread.sleep(200);
            MouseTools.moveMouseControlledByPixels(
                    (int) MobileScreen.getMinX() + validItem.getCenterX(), 
                    (int) MobileScreen.getMinY() + validItem.getCenterY());
            doLeftClick();
        }
        
        return true;
    }
    
    public boolean runStory() throws Exception {
        
        int MAX_RETRIES = 5;
        boolean wordsFound;
        int nRetry;
        
        startStory();
        
        System.out.println("Good Morning!");
        Thread.sleep(1700);
        clickOnContinue();
        
        System.out.println("Good morning, honey!");
        Thread.sleep(1200);
        clickOnContinue();
        
        System.out.println("Good morning, Lauren!");
        Thread.sleep(1200);
        clickOnContinue();
        
        System.out.println("Que significa 'honey' aqui?");
        Thread.sleep(1100);
        
        wordsFound = false;
        nRetry = 0;
        do {
            ++nRetry;
            Thread.sleep(350);
            wordsFound = clickOnItsCheckbox("cario", true);
        } while (!wordsFound && nRetry <= MAX_RETRIES);
        
        if (wordsFound) {
            Thread.sleep(150);
            clickOnContinue();
        } else {
            System.err.print("[error][runStory:305] No se ha encontrado la palabra 'cariño'");
            closeStory();
            return false;
        }
        
        System.out.println("Where is my Spanish book?");
        Thread.sleep(2500);
        clickOnContinue();
        
        System.out.println("Your book?");
        Thread.sleep(1000);
        clickOnContinue();
        
        System.out.println("I have an important Spanish exam this morning.");
        Thread.sleep(3650);
        clickOnContinue();
        
        System.out.println("Escucha y selecciona las palabras");
        Thread.sleep(1500);
        clickOnContinue();
        
        wordsFound = false;
        nRetry = 0;
        do {
            ++nRetry;
            Thread.sleep(350);
            wordsFound = clickOnAllWord(new String[]{"i", "need", "my", "book"}, false);
        } while (!wordsFound && nRetry <= MAX_RETRIES);
        
        if (wordsFound) {
            Thread.sleep(150);
            clickOnContinue();
        } else {
            System.err.print("[error][runStory:338] No se han encontrado las palabras 'I' 'need' 'my' 'book'");
            closeStory();
            return false;
        }
        
        System.out.println("Lauren, your book is here on the table!");
        Thread.sleep(3200);
        clickOnContinue();
        
        System.out.println("Sorry, honey. I'm tired. I work a lot!");
        Thread.sleep(4300);
        clickOnContinue();
        
        System.out.println("Haz clic en la opcion que significa 'cansada'");
        Thread.sleep(1000);
        clickOnContinue();
        
        wordsFound = false;
        nRetry = 0;
        do {
            ++nRetry;
            Thread.sleep(350);
            wordsFound = clickOnAllWord(new String[]{"tired"}, false);
        } while (!wordsFound && nRetry <= MAX_RETRIES);
        
        if (wordsFound) {
            Thread.sleep(150);
            clickOnContinue();
        } else {
            System.err.print("[error][runStory:367] No se han encontrado la palabra 'tired'");
            closeStory();
            return false;
        }
        
        System.out.println("Do you want some coffee?");
        Thread.sleep(1900);
        clickOnContinue();
        
        System.out.println("Yes, with milk, please.");
        Thread.sleep(2550);
        clickOnContinue();
        
        System.out.println("OK. Here.");
        Thread.sleep(1400);
        clickOnContinue();
        
        System.out.println("Lauren puts sugar in the coffee");
        Thread.sleep(2650);
        clickOnContinue();
        
        System.out.println("Que esta haciendo Lauren?");
        Thread.sleep(800);
        clickOnContinue();
        
        wordsFound = false;
        nRetry = 0;
        do {
            ++nRetry;
            Thread.sleep(350);
            wordsFound = clickOnItsCheckbox("Poniendo azcar en su caf", true);
        } while (!wordsFound && nRetry <= MAX_RETRIES);
        
        if (wordsFound) {
            Thread.sleep(150);
            clickOnContinue();
        } else {
            System.err.print("[error][runStory:404] No se ha encontrado la frase 'Poniendo azúcar en su café'");
            closeStory();
            return false;
        }
        
        System.out.println("She drinks her coffee.");
        Thread.sleep(2200);
        clickOnContinue();
        
        System.out.println("Yuck!");
        Thread.sleep(800);
        clickOnContinue();
        
        System.out.println("What?");
        Thread.sleep(800);
        clickOnContinue();
        
        System.out.println("It's salt!");
        Thread.sleep(900);
        clickOnContinue();
        
        System.out.println("Que significa \"It's salt\"?");
        Thread.sleep(800);
        clickOnContinue();
        
        wordsFound = false;
        nRetry = 0;
        do {
            ++nRetry;
            Thread.sleep(350);
            wordsFound = clickOnItsCheckbox("Es sal", true);
            if (!wordsFound) wordsFound = clickOnItsCheckbox("Es sall", true);
        } while (!wordsFound && nRetry <= MAX_RETRIES);
        
        if (wordsFound) {
            Thread.sleep(150);
            clickOnContinue();
        } else {
            System.err.print("[error][runStory:441] No se ha encontrado la frase 'Es sal!'");
            closeStory();
            return false;
        }
        
        System.out.println("Lauren, you are very tired!");
        Thread.sleep(2700);
        clickOnContinue();
        
        System.out.println("Yes, I need a new cup of coffee... with sugar, not salt!");
        Thread.sleep(5700);
        clickOnContinue();
        
        System.out.println("Lauren estaba tan cansada que...");
        Thread.sleep(900);
        clickOnContinue();
        
        wordsFound = false;
        nRetry = 0;
        do {
            ++nRetry;
            Thread.sleep(350);
            wordsFound = clickOnItsCheckbox("ella le puso sal a su caf", true);
        } while (!wordsFound && nRetry <= MAX_RETRIES);
        
        if (wordsFound) {
            Thread.sleep(150);
            clickOnContinue();
        } else {
            System.err.print("[error][runStory:455] No se ha encontrado la frase '... ella le puso sal a su café!'");
            closeStory();
            return false;
        }
        
        System.out.println("Selecciona los pares");
        Thread.sleep(1200);
        clickOnContinue();
        
        wordsFound = false;
        nRetry = 0;
        do {
            ++nRetry;
            Thread.sleep(350);
            wordsFound = clickOnPairs(this.espWords, this.engWords);
        } while (!wordsFound && nRetry <= MAX_RETRIES);
        
        if (wordsFound) {
            Thread.sleep(150);
            clickOnContinue();
        } else {
            System.err.println("[error][runStory:476] No se han encontrado los pares");
            closeStory();
            return false;
        }
        
        System.out.println("Llevas ### dias seguido! - Continuar en azul");
        Thread.sleep(1500);
        clickOnContinue();
        
        System.out.println("Ganaste ## EXP! - Continuar en azul");
        Thread.sleep(650);
        clickOnContinue();
        
        System.out.println("Ganaste # lingot! - Continuar en azul");
        Thread.sleep(650);
        clickOnContinue();
        
        System.out.println("Completaste el cuento! - Continuar en azul");
        Thread.sleep(650);
        clickOnContinue();
        
        System.out.println("PUBLICIDAD - Continuar en azul");
        Thread.sleep(5000);
        clickOnContinue();
        
        System.out.println("Premium? No, gracias - Continuar en fondo azul");
        Thread.sleep(650);
        clickOnContinue();
        
        return true;
    }
    
}
