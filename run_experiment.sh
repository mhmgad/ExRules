#!/usr/bin/env bash



mkdir /GW/D5data-5/gadelrab/yago3/spmf/out2



sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine_integer_transactions.tsv2 -o /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_f1_f2_NEWLIFT.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine_mapping.tsv2 -s NEW_LIFT -f1 -f2  -oPrASP /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_f1_f2_NEWLIFT.prasp -stats

#no materialization
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine_integer_transactions.tsv2 -o /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_excep02_PNCONF_f1_f2_NEWLIFT.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine_mapping.tsv2 -ex -exMinSup 0.02 -exRank PNCONF -s NEW_LIFT -f1 -f2  -oPrASP /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_excep02_PNCONF_f1_f2_NEWLIFT.prasp -stats


#no weighted count
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine_integer_transactions.tsv2 -o /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_noWeight_f1_f2_NEWLIFT.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine_mapping.tsv2 -ex -exMinSup 0.02 -exRank PNCONF -s NEW_LIFT -f1 -f2 -pm -cPM 0.05 -oPrASP /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_noWeight_f1_f2_NEWLIFT.prasp -dPM /GW/D5data-5/gadelrab/yago3/spmf/materialization05.tsv -stats


#with PrASP and weihts
sh assemble/bin/mine_rules.sh -i /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine_integer_transactions.tsv2 -o /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_weight_f1_f2_NEWLIFT.tsv -minConf 0.25    -de -m /GW/D5data-5/gadelrab/yago3/spmf/in/facts_to_mine_mapping.tsv2 -ex -exMinSup 0.02 -exRank PNCONF -s NEW_LIFT  -w -f1 -f2 -pm -cPM 0.05 -oPrASP /GW/D5data-5/gadelrab/yago3/spmf/out2/rules_spmf_supp0001_conf25_100_excep02_PNCONF_cPM05_weight_f1_f2_NEWLIFT.prasp -dPM /GW/D5data-5/gadelrab/yago3/spmf/materialization05.tsv -stats




