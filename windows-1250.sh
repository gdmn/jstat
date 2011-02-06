#! /bin/sh
recode utf-8..windows-1250 `find . | grep java`
echo "windows-1250" > kodowanie.txt

