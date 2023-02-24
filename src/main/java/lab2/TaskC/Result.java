package lab2.TaskC;

//Класс для хранения числителя и знаменателя
public class Result {
    public double numerator;
    public double denominator;

    Result(){
        numerator=0;
        denominator=0;
    }

    public double getResult(){
        return numerator/denominator;
    }
}
