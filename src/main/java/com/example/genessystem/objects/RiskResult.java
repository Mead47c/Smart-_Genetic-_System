package com.example.genessystem.objects;

public class RiskResult {
    public double diseaseWeight;
    public String condition;
    public int firstRelative;
    public int secondRelative;
    public double totalScore;
    public String riskLevel;

    public RiskResult(
            double diseaseWeight,
            String condition,
            int firstRelative,
            int secondRelative,
            double totalScore,
            String riskLevel
    ) {
        this.diseaseWeight = diseaseWeight;
        this.condition = condition;
        this.firstRelative = firstRelative;
        this.secondRelative = secondRelative;
        this.totalScore = totalScore;
        this.riskLevel = riskLevel;
    }
}