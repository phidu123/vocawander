package vn.edu.tdtu.vocawander.model;

public class FillBlank {
    String id;
    String question;
    String answer;
    String img;

    public FillBlank(String id, String question, String answer, String img) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
