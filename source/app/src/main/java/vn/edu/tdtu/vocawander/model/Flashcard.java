package vn.edu.tdtu.vocawander.model;

public class Flashcard {
    String id;
    String frontS;
    String backS;
    String img;

    public Flashcard(String id,String frontS, String backS, String img) {
        this.id = id;
        this.frontS = frontS;
        this.backS = backS;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrontS() {
        return frontS;
    }

    public String getBackS() {
        return backS;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setFrontS(String frontS) {
        this.frontS = frontS;
    }

    public void setBackS(String backS) {
        this.backS = backS;
    }
}
