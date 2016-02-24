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




mvn install:install-file -DgroupId=spmf -DartifactId=spmf -Dversion=1.0 -Dpackaging=jar -Dfile=$BASEDIR/libs/spmf/spmf.jar -DgeneratePom=true -Dsources=$BASEDIR/libs/spmf/spmf.zip