#!/bin/bash

# Hacked together shell script to allow easy updating (and installing) of the
# database.

TB_FILENAME=dixie_tables # Without file extension.
SP_PREFIX=dixie_sp_
TG_PREFIX=dixie_tg_
DATA_PREFIX=dixie_data_
SQL_SUFFIX=.sql

# Read options.

INSTALL=0

while [ "$1" != "" ]; do
  case $1 in
# If we ever want to read an argument after an option here's how:
#   -i | --file )      shift
#                      filename=$1
#                      ;;
    -i | --install )   INSTALL=1
                       ;;
    -h | --help )      usage
                       exit
                       ;;
    * )                usage
                       exit 1
  esac
  shift
done

# Default values
DBEXEC=mysql
DBNAME=dixie
DBUSER=root

echo -n "Database executeable (default: $DBEXEC): " # -n to skip trailing newline.
read INPUT

if [ "$INPUT" != "" ]
then
  DBEXEC=$INPUT
fi

echo -n "Database name (default: $DBNAME): "
read INPUT

if [ "$INPUT" != "" ]
then
  DBNAME=$INPUT
fi

echo -n "Database user (default: $DBUSER): "
read INPUT

if [ "$INPUT" != "" ]
then
  DBUSER=$INPUT
fi

while [ 1 ]
do
  if [ "$DBPASS" == "" ]
  then
    echo -n "Database password (required): "
    stty -echo # Don't show the password as it's entered.
    read DBPASS
    stty echo
    echo "" # Force trailing newline as user's newline not echoed.
  else
    break
  fi
done

# Must provide defaults incase not using mysql.

DBTRIGGERUSER=$DBUSER
DBTRIGGERPASS=$DBPASS

# If MySQL, check version - triggers may require superuser.
if echo "$DBEXEC" | grep -i "mysql" > /dev/null
then

  # Require a new password if the root password was not already entered.
  if [ "$DBUSER" != "root" ]
  then
    DBTRIGGERPASS=
  fi

  # Always default to root
  DBTRIGGERUSER=root

  echo ""
  echo "MySQL versions before 5.1.6 require a super user to add triggers."
  echo "Your MySQL version information is:"
  $DBEXEC -V
  echo ""
  while [ 1 ]
  do
    echo -n "Is your MySQL before 5.1.6? (Y/N): "
    read NEED_SU_FOR_TRIGGERS
    if [ "$NEED_SU_FOR_TRIGGERS" == "Y" ]
    then
      echo -n "MySQL super user (for triggers, default: $DBTRIGGERUSER): "
      read INPUT

      if [ "$INPUT" != "" ]
      then
        DBTRIGGERUSER=$INPUT
      fi

      while [ 1 ]
      do
        if [ "$DBTRIGGERPASS" == "" ]
        then
          echo -n "MySQL super password: "
          stty -echo
          read DBTRIGGERPASS
          stty echo
          echo ""
        else
          break
        fi
      done

      break
    elif [ "$NEED_SU_FOR_TRIGGERS" == "N" ]
    then
      break
    fi
  done
fi
echo ""

if [ $INSTALL == 1 ]
then
  echo "== INSERTING TABLES ================================================"
  echo "loading ./$TB_FILENAME$SQL_SUFFIX into $DBNAME"
  $DBEXEC -u $DBUSER --password=$DBPASS --force $DBNAME < $TB_FILENAME$SQL_SUFFIX
  echo ""

  echo "== INSERTING DATA =================================================="
  for FOUND in $(find . -iname "$DATA_PREFIX*$SQL_SUFFIX"); do
    echo "loading $FOUND into $DBNAME"
    $DBEXEC -u $DBUSER --password=$DBPASS $DBNAME < $FOUND
  done
  echo ""
fi

echo "== UPDATING STORED PROCEDURES ======================================"
for FOUND in $(find . -iname "$SP_PREFIX*$SQL_SUFFIX"); do
  echo "loading $FOUND into $DBNAME"
  $DBEXEC -u $DBTRIGGERUSER --password=$DBTRIGGERPASS $DBNAME < $FOUND
done
echo ""

echo "== UPDATING TRIGGERS ==============================================="
for FOUND in $(find . -iname "$TG_PREFIX*$SQL_SUFFIX"); do
  echo "loading $FOUND into $DBNAME"
  $DBEXEC -u $DBUSER --password=$DBPASS $DBNAME < $FOUND
done
echo ""