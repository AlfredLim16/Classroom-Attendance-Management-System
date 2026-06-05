package lookup;

import exceptions.ValidationException;

public enum QuizType {
    LAB(1, "Lab"), QUIZ(2, "Quiz"), EXAM(3, "Exam");

    private final int quizTypeId;
    private final String quizTypeName;

    QuizType(int quizTypeId, String quizTypeName){
        this.quizTypeId = quizTypeId;
        this.quizTypeName = quizTypeName;
    }

    public int getQuizTypeId(){
        return quizTypeId;
    }

    public String getQuizTypeName(){
        return quizTypeName;
    }

    public static QuizType fromId(int id) throws ValidationException {
        for (QuizType q : values()) {
            if (q.quizTypeId == id) {
                return q;
            }
        }
        throw new ValidationException("quizTypeId", "Unknown quiz type id: " + id);
    }
}
