package de.mpii.frequentrulesminning.utils;

import java.io.IOException;

/**
 * Created by gadelrab on 4/21/16.
 */
public class DLV2Transactions {


//TODO move all processing related to DLV here
    public static void main(String [] args) throws IOException {

        // TODO use CLI
        RDF2IntegerTransactionsConverter cv=new RDF2IntegerTransactionsConverter();


        cv.parseDLVOutput(args[1]);

        cv.loadPredicateMappingFromFile(args[2]);
        cv.loadSubjectMappingFromFile(args[3]);

        switch (RDF2IntegerTransactionsConverter.EncodingType.valueOf(args[0])){
            case SPMF:
                cv.exportTransactions(args[4]);
                break;
            case RDF:
                cv.exportToRDF(args[4]);
                break;
            case PrASP:
                cv.exportAsPrASP(args[4]);
                break;


        }

    }
}
