#!/bin/bash

# Test algH.py against bottle.c for all valid seeds.
# Aaron Williams July 2019

# Helpful links:
# https://www.cyberciti.biz/faq/bash-for-loop/
# https://stackoverflow.com/questions/4181703/how-to-concatenate-string-variables-in-bash
# https://stackoverflow.com/questions/4651437/how-do-i-set-a-variable-to-the-output-of-a-command-in-bash
# https://askubuntu.com/questions/229447/how-do-i-diff-the-output-of-two-commands
# https://bash.cyberciti.biz/guide/Shebang
# https://www.tldp.org/LDP/Bash-Beginners-Guide/html/sect_09_05.html
# http://tldp.org/LDP/abs/html/comparison-ops.html
# https://stackoverflow.com/questions/8920245/bash-conditionals-how-to-and-expressions-if-z-var-e-var

for i in {0..255}
do
  seed0=$(echo "obase=16; $i" | bc)
  echo "seed0: $seed0"

  for j in {0..255}
  do
    # Skip over seeds 00 00, 00 01, 01 00, and 01 01.
    if [ "$i" -le 1 ] && [ "$j" -le 1 ]
    then
      continue
    fi

    seed1=$(echo "obase=16; $j" | bc)

    commandA="java algHV $seed0 $seed1"
    commandB="python3 algHV.py $seed0 $seed1"

    #echo "diff <($commandA) <($commandB)"
    diff <($commandA) <($commandB)

  done

done
