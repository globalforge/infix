#!/bin/sh
resources=$1
echo "resources dir=$resources"
cd $resources
antlr=$2
echo "antlr location=$antlr"
src=$3
echo "output source=$src"
alias antlr4="java -jar $antlr/antlr-4.4-complete.jar"
antlr4 -package com.globalforge.infix.antlr -o $src -visitor -listener *.g4
