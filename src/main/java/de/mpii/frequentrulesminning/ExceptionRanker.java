package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.AssocRuleWithExceptions;
import de.mpii.frequentrulesminning.utils.AssocRulesExtended;
import de.mpii.frequentrulesminning.utils.ExceptionItem;
import de.mpii.frequentrulesminning.utils.Exceptions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by gadelrab on 4/13/16.
 */
public class ExceptionRanker {


    public enum Order{LIFT,PNCONF,SUPP,CONF};

    private  Evaluator evaluator;
    private Order orderingType;


    public ExceptionRanker(Evaluator evaluator, Order orderingType) {
        this.evaluator=evaluator;
        this.orderingType=orderingType;
    }



    // first configuration
    public void rankExceptions(AssocRulesExtended rules){
        // we can loop in parallel as rules are independent
        List<AssocRuleWithExceptions> rulesList=rules.getRules();

        rulesList.parallelStream().forEach( (rule) ->  rankExceptions(rule));

    }

    public void rankExceptions(AssocRuleWithExceptions rule) {

        computeScores(rule);
        Exceptions exceptionCandidates=rule.getExceptionCandidates();
        switch (orderingType) {
            case LIFT:
                exceptionCandidates.sortOnLift();
                break;
            case CONF:
                exceptionCandidates.sortOnConfidence();
                break;
            case PNCONF:
                exceptionCandidates.sortOnPosNegConfidence();
                break;
            case SUPP:
                exceptionCandidates.sortOnSupport();
                break;
        }
    }

    public void computeScores(AssocRuleWithExceptions rule) {
        rule.getExceptionCandidates().forEach((exc)-> {
//            exc.setCoverage(evaluator.coverage(rule, exc));
            exc.setConfidence(evaluator.confidence(rule, exc));
            exc.setLift(evaluator.lift(rule,exc));
            exc.setNegConfidence(evaluator.negativeRuleConfidence(rule,exc));
            exc.setJaccardCoefficient(evaluator.JaccardCoefficient(rule,exc));
            exc.setNegJaccardCoefficient(evaluator.negativeRuleJaccardCoefficient(rule,exc));
        });
    }

}
