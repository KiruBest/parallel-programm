package utils;

//Класс для хранения числителя и знаменателя
public class ClusterRequest {
    public double numerator;
    public double denominator;

    public ClusterRequest() {
        numerator = 0;
        denominator = 0;
    }

    public double getResult() {
        return numerator / denominator;
    }
}
