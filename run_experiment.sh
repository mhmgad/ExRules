#!/usr/bin/env bash



mkdir /GW/D5data-5/gadelrab/yago3/spmf/out2


#normal horn rules
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_f1_f2_NEWLIFT.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -s NEW_LIFT -f1 -f2  -oPrASP -oDLV -stats

#no materialization
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_excep02_PNCONF_f1_f2_NEWLIFT.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup 0.02 -exRank PNCONF -s NEW_LIFT -f1 -f2  -oPrASP -oDLV -stats


#no weighted count
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_noWeight_f1_f2_NEWLIFT.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup 0.02 -exRank PNCONF -s NEW_LIFT -f1 -f2 -pm -cPM 0.05 -oPrASP -oDLV  -stats


#with PrASP and weihts
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.transactions -o /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_weight_f1_f2_NEWLIFT.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine.mapping_predicates -ex -exMinSup 0.02 -exRank PNCONF -s NEW_LIFT  -w -f1 -f2 -pm -cPM 0.05 -oPrASP -oDLV -stats




