/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.marcmauri.robot;

import es.marcmauri.engines.MobileEngine;
import java.awt.Point;

/**
 *
 * @author marc.mauri
 */
public class MyRobot {
    
    /*
        Config
    */
    private static final int N_RETRIES = 3;
    
    private static final int POSITION_MOBILE_SCREEN_MIN_X = 10;
    private static final int POSITION_MOBILE_SCREEN_MIN_Y = 630;
    private static final int POSITION_MOBILE_SCREEN_MAX_X = 500;
    private static final int POSITION_MOBILE_SCREEN_MAX_Y = 1240;
    
    private static final Point POSITION_BTN_CONTINUE_AND_STORY_BOTTOM = new Point(170, 1280);
    private static final Point POSITION_BTN_STORY_FIRST = new Point(135,740);
    private static final Point POSITION_BTN_CLOSE = new Point(40,385);
    private static final Point POSITION_BTN_CLOSE_CONFIRM = new Point(390,902);
    private static final int POSITION_CHECKBOX_X = 50;
    
    private static final String[] espWords = new String[]{"yo", "cansada", "leche", "buenos", "la"};
    private static final String[] engWords = new String[]{"i", "tired", "milk", "morning", "the"};

    /*
        Variables
    */
    private static MobileEngine MyMobile;
    
    /*
        Set up
    */
    private static void setUp() throws Exception {
        MyMobile = new MobileEngine(
            POSITION_MOBILE_SCREEN_MIN_X, POSITION_MOBILE_SCREEN_MIN_Y, 
            POSITION_MOBILE_SCREEN_MAX_X, POSITION_MOBILE_SCREEN_MAX_Y, 
            POSITION_BTN_CONTINUE_AND_STORY_BOTTOM, 
            POSITION_BTN_STORY_FIRST, 
            POSITION_BTN_CLOSE, 
            POSITION_BTN_CLOSE_CONFIRM, 
            POSITION_CHECKBOX_X,
            espWords, engWords);
    }
    
    private static void showSummary(int ok, int fail) {
        System.out.println();
        System.out.println();
        System.out.println("SUMMARY");
        System.out.println("-------");
        System.out.println("Success: " + ok + " | Failed: " + fail);
        System.out.println();
        System.out.println();
    }
    
    private static void pauseToCancelExecution() throws Exception {
        int seconds = 0;
        System.out.println("================================================");
        System.out.println("===  Pause of 8 seg to cancel the execution  ===");
        System.out.println("================================================");
        do {
            Thread.sleep(1000);
            ++seconds;
            System.out.println("=== " + seconds + " of 8 sec...                            ===");
        } while (seconds < 8);
        System.out.println("================================================");

    }
    
    public static void main(String[] args) {
        try {
            setUp();
            
            int goodRetries = 0;
            int badRetries = 0;
            int badRetriesInARow = 0;
            while (badRetriesInARow < N_RETRIES) {
                if (!MyMobile.runStory()) {
                    ++badRetries;
                    ++badRetriesInARow;
                } else {
                    ++goodRetries;
                    badRetriesInARow = 0;
                }
                showSummary(goodRetries, badRetries);
                
                // Every 5 attempts, we have a break to exit the program
                if ((goodRetries + badRetries)%5 == 0) {
                    pauseToCancelExecution();
                }
            }
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
