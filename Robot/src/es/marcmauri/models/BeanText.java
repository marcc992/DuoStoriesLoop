/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.marcmauri.models;

/**
 *
 * @author Marc.Mauri
 */
public class BeanText {
    private String content;
    private int centerX;
    private int centerY;

    public BeanText() {
    }

    public BeanText(String content, int centerX, int centerY) {
        this.content = content;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }
    
    
}
