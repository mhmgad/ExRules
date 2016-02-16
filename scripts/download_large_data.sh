#!/usr/bin/env bash

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASEDIR=`cd "$PRGDIR/.." >/dev/null; pwd`
DATA_DIR=$BASEDIR/data


#make new directory for data
mkdir -p $DATA_DIR


#Download
#wget -P $DATA_DIR <url>
wget -P $DATA_DIR http://resources.mpi-inf.mpg.de/yago-naga/yago/download/yago/yagoTaxonomy.tsv.7z
wget -P $DATA_DIR http://resources.mpi-inf.mpg.de/yago-naga/yago/download/yago/yagoSimpleTypes.tsv.7z
wget -P $DATA_DIR http://resources.mpi-inf.mpg.de/yago-naga/yago/download/yago/yagoGeonamesOnlyData.tsv.7z
wget -P $DATA_DIR http://resources.mpi-inf.mpg.de/yago-naga/yago/download/yago/yagoFacts.tsv.7z



#uncompress

for f in $DATA_DIR/*.7z; do
    echo $f
    7z x $f -o$DATA_DIR
done


#Filter data for reduction
grep '<isLocatedIn>' $DATA_DIR/yagoGeonamesOnlyData.tsv > $DATA_DIR/isLocatedInData.tsv
grep '<isLocatedIn>' $DATA_DIR/yagoFacts.tsv >> $DATA_DIR/isLocatedInData.tsv

#remove archives
rm $DATA_DIR/*.7z




