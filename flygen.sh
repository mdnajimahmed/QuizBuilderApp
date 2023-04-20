#!/bin/bash
set -eu

while getopts n: flag
do
    case "${flag}" in
        n) filename=${OPTARG};;
    esac
done

version=$(date +%s)
flywayFullName=V1.0."$version"__"$filename"
flywayFullNameUndo=U1.0."$version"__"$filename"
echo '-- insert your migration script here' > src/main/resources/db/migration/"$flywayFullName".sql
echo "-- insert your undo script for $flywayFullName here" > src/main/resources/db/migration/"$flywayFullNameUndo".sql