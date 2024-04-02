package vn.edu.tdtu.vocawander.model;

public class Package {
    String id;
    String name;
    String owner;
    String Img;
    private boolean isSaved;

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
    public Package(String id, String name, String owner, String img) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.Img = img;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }
}
