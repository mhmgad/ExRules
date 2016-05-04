Development:

Requirements

1. maven
2. Java 8
3. recommended IntelliJ (not a must)


1. Run scripts:

 sh scripts/local_libs.sh

 sh scripts/download_large_data.sh

2. Import the project as maven project



Running

 1. run scripts

  mvn compile

  mvn package

  mvn install

 2. Then run

    `sh run_experiment.sh` for yago experiments

    `sh run_IMDB_experiment.sh` fo IMDB experiments

    Note: fix the directories inside the scripst to point to facts_to_mine.tsv file


for running examples
 running_scripts_sample.txt for examples running the main rule mining script mine_rules.sh


