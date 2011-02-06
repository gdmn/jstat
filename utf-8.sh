#! /bin/sh
recode windows-1250..utf-8 `find . | grep java`
echo "utf-8" > kodowanie.txt

