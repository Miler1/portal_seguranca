#!/bin/bash

jar_path="$UTILS_SELF_DIR/dbEvolutions-1.0.0.jar"
evolutions_config="$UTILS_SELF_DIR/../conf/evolutionsBuild.json"

run_evolutions() {

	evolutions_sql=$1
	
	java -jar $jar_path joinUps -d $ROOT_FOLDER -c $evolutions_config > $LOG_FILE

	if tail -n 200 $LOG_FILE | grep -qw "Erro"; then
		error "Erro ao gerar as evolutions."
	fi
 	
	for sql in "${evolutions_sql[@]}"
	do
		show_msg "  - Executando evolutions: $sql"
		# executar_sql "$sql"
	done
}
