package vn.edu.tdtu.vocawander.model;

public class Vocabulary {
    private String id;
    private String word;
    private String meaning;

    private String example;
    private String imgUrl;
    private String idTopic;

    public Vocabulary(String url) {
        this.word = "";
        this.meaning = "";
        this.example = "";
        this.imgUrl = url;
    }

    public Vocabulary() {
        this.word = "";
        this.meaning = "";
        this.example = "";
        this.imgUrl = "";
    }

    public Vocabulary(String word, String meaning, String example, String imgUrl) {
        this.word = word;
        this.meaning = meaning;
        this.example = example;
        this.imgUrl = imgUrl;
    }
    public Vocabulary(String id, String word, String idTopic, String meaning, String example, String imgUrl) {
        this.id = id;
        this.word = word;
        this.idTopic = idTopic;
        this.meaning = meaning;
        this.example = example;
        this.imgUrl = imgUrl;
    }
    public Vocabulary(String idTopic, String word, String example, String meaning, String imgUrl) {
        this.idTopic = idTopic;
        this.word = word;
        this.meaning = meaning;
        this.example = example;
        this.imgUrl = imgUrl;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setId(int pos) {
        this.id = generateAuto(pos);
    }

    public String getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(String idTopic) {
        this.idTopic = idTopic;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    public void setExample(String example) {
        this.example = example;
    }
    public String getExample() {
        return this.example;
    }

    public void setImgUrl(String example) {
        this.imgUrl = example;
    }
    private String generateAuto(int pos){
        if(pos+1<10){
            return "IT000"+(pos+1);
        }else if(pos+1<100){
            return "IT00"+(pos+1);
        } else if (pos+1<1000) {
            return "IT0"+(pos+1);
        }else {
            return "IT"+(pos+1);
        }
    }
}
