#!/bin/bash

#파일명 1부터로 바꾸기
filename=5000
contracts_dir="./upload_backup/contracts/"

numberOfFiles=`ls ${contracts_dir} | wc -w`
count=1

while (( count<=numberOfFiles )); do
	mv ${contracts_dir}${filename}.json ${contracts_dir}${count}.json
	echo ${filename} to ${count} done..

	(( filename=filename-1 ))
	(( count=count+1 ))
done

echo finished renaming files...