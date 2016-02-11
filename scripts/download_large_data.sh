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


#uncompress
7z x $DATA_DIR/*.7z -o$DATA_DIR

#remove archives
rm $DATA_DIR/*.7z




