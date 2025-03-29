package com.example.genessystem.utils;


import com.example.genessystem.objects.RiskResult;

public class SGSAnalyzer {

    public static RiskResult analyzeRisk(
            String disease,
            double diseaseWeight,
            String condition,
            int firstRelative,
            int secondRelative
    ) {
        double conditionWeight;
        switch (condition.toLowerCase()) {
            case "injured":
                conditionWeight = 1.0;
                break;
            case "carrier":
                conditionWeight = 0.6;
                break;
            case "safe":
                conditionWeight = 0.2;
                break;
            default:
                conditionWeight = 0.0;
        }

        double totalScore = diseaseWeight * conditionWeight + (firstRelative * 0.5) + (secondRelative * 0.25);
        String riskLevel;

        if (totalScore < 10) {
            riskLevel = "Low";
        } else if (totalScore < 20) {
            riskLevel = "Moderate";
        } else {
            riskLevel = "High";
        }

        return new RiskResult(diseaseWeight, condition, firstRelative, secondRelative, totalScore, riskLevel);
    }
}


