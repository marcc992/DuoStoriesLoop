/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.marcmauri.tools;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;

/**
 *
 * @author Marc.Mauri
 */
public class MouseCorrectRobot extends Robot
{
    final Dimension ScreenSize;// Primary Screen Size
    
    // Variables para recoger la ultima posicion donde se movio el mouse,
    // teniendo en cuenta el escalado (a causa del bug de windows)
    public int lastX = 0;
    public int lastY = 0;

    public MouseCorrectRobot() throws AWTException
    {
        super();
        ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    }

    private static double getTav(Point a, Point b)
    {
        return Math.sqrt((double) ((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)));
    }
    
    public void moveMouseControlledByPixels(int x, int y) {
        double percentX = ((double) x)/ScreenSize.width;
        double percentY = ((double) y)/ScreenSize.height;
        moveMouseControlled(percentX, percentY);
    }

    public void moveMouseControlled(double xbe, double ybe)// Position of the cursor in [0,1] ranges. (0,0) is the upper left corner
    {

        int xbepix = (int) (ScreenSize.width * xbe);
        int ybepix = (int) (ScreenSize.height * ybe);

        int x = xbepix;
        int y = ybepix;

        Point mert = MouseInfo.getPointerInfo().getLocation();
        Point ElozoInitPont = new Point(0, 0);

        int UgyanAztMeri = 0;
        final int UgyanAZtMeriLimit = 30;

        int i = 0;
        final int LepesLimit = 20000;
        while ((mert.x != xbepix || mert.y != ybepix) && i < LepesLimit && UgyanAztMeri < UgyanAZtMeriLimit)
        {
            ++i;
            if (mert.x < xbepix) {
                ++x;
            }
            else {
                --x;
            }
            
            if (mert.y < ybepix) {
                ++y;
            }
            else {
                --y;
            }
            
            mouseMove(x, y);

            mert = MouseInfo.getPointerInfo().getLocation();

            if (getTav(ElozoInitPont, mert) < 5) {
                ++UgyanAztMeri;
            }
            else {
                UgyanAztMeri = 0;
                ElozoInitPont.x = mert.x;
                ElozoInitPont.y = mert.y;
            }

        }
        
        lastX = x;
        lastY = y;
    }

}