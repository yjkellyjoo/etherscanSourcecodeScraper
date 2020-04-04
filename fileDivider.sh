#!/bin/bash

#json objects 쪼개기
contracts_dir="./upload_backup/contracts/"
original_filePath="./upload_backup/bq-results-20200401-171150-vp6f1zm8u2mt.json"

if (( `ls ${contracts_dir} | wc -w`==0 )); then
	mkdir -m 0775 ${contracts_dir}

	filename=1
	count=2000
	
	for row in `cat ${original_filePath}`; do
		if ((count==0)); then
			echo ${filename} done..
			((filename=filename+1))
			count=2000
		fi

		echo ${row} >> ./contracts/${filename}.json
		((count=count-1))
	done

	echo finished dividing elements...
fi
