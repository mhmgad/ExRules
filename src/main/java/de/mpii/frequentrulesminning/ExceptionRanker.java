package de.mpii.frequentrulesminning;

import de.mpii.frequentrulesminning.utils.AssocRuleWithExceptions;
import de.mpii.frequentrulesminning.utils.AssocRulesExtended;

import java.util.List;

/**
 * Created by gadelrab on 4/13/16.
 */
public class ExceptionRanker {



    private  Evaluator evaluator;

    public ExceptionRanker(Evaluator evaluator) {
        this.evaluator=evaluator;
    }



    // first configuration
    public void rankExceptions(AssocRulesExtended rules){
        // we can loop in parallel as rules are independent
        List<AssocRuleWithExceptions> rulesList=rules.getRules();

        rulesList.parallelStream().forEach( (rule) ->  rankExceptions(rule));


    }

    public void rankExceptions(AssocRuleWithExceptions rule) {

        rule.getExceptionCandidates().forEach((exc)-> {
            exc.setCoverage(evaluator.coverage(rule, exc));
            exc.setConfidence(evaluator.confidence(rule, exc));
            exc.setLift(evaluator.lift(rule,exc));
            exc.setPosNegConf(evaluator.computePosNegConfidence(rule,exc));
        });




    }

}
