package de.mpii.frequentrulesminning.utils;

import java.io.IOException;

/**
 * Created by gadelrab on 4/21/16.
 */
public class DLV2Transactions {



    public static void main(String [] args) throws IOException {
        RDF2IntegerTransactionsConverter cv=new RDF2IntegerTransactionsConverter();


        cv.parseDLVOutput(args[1]);

        switch (RDF2IntegerTransactionsConverter.EncodingType.valueOf(args[0])){
            case SPMF:
                cv.exportTransactions(args[2]);
                break;
            case RDF:
                cv.exportToRDF(args[2]);
                break;
            case PrASP:
                cv.exportAsPrASP(args[2]);
                break;


        }

    }
}
