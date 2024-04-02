package vn.edu.tdtu.vocawander.model;

public class MultipleChoice {
    private String id;
    private String img;
    private String question;
    private String answer;

    public MultipleChoice(String id, String img, String question, String answer) {
        this.id = id;
        this.img = img;
        this.question = question;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
}
