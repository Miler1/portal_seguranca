#!/bin/bash

UTILS_SELF_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_FOLDER="$UTILS_SELF_DIR/../.."
LOG_FOLDER="$UTILS_SELF_DIR/../logs"
SCRIPT_NAME=`basename "$0"`
LOG_FILE="$LOG_FOLDER/$SCRIPT_NAME.log"
OUTPUT_FOLDER="$UTILS_SELF_DIR/../output"

if [ -z "$MSG_PREFIX" ]; then
	MSG_PREFIX=" >> "
fi

user_confirm() {

	msg=$1
	echo ""
	echo ""
	read -p "$log_prefix $msg (s / n)" -n 1 -r
	echo
	if [[ $REPLY =~ ^[Ss]$ ]]
	then
    	return 0
    elif [[ $REPLY =~ ^[Nn]$ ]]
    then
    	return 1
    else
    	show_msg "Resposta inválida. Responda \"s\" ou \"n\"."

    	if confirmacao_usuario "$msg"; then
    		return 0
    	else
    		return 1
    	fi
	fi
}

validate_files_exist () {

	for dir in "$@"
	do
		if [ ! -d "$dir" ] && [ ! -f "$dir" ]; then
			echo ""
			echo ""
			echo "ERRO: O dirétorio/arquivo não existe: $dir"
			echo $2
			echo -e "\n\n"
			exit $E_BADARGS
		fi
	done
}


# Remove e cria novamente o arquivo.
# Uso: empty_file /meu/arquivo.txt
empty_file () {

	if [ -f "$1" ]; then
		rm $1
	fi

	touch $1
}


# Imprime texto em arquivo de log configurado em LOG_FILE.
# Uso: log "Meu texto de log"
log() {
	echo "" >> $LOG_FILE
	echo "" >> $LOG_FILE
	echo " >> $1 " >> $LOG_FILE
	echo "" >> $LOG_FILE
}


# Limpa o arquivo de log configurado em LOG_FILE.
# Uso: clean_log /meu/arquivo.log
clean_log() {
	
	if [ ! -d "$LOG_FOLDER" ]; then
		mkdir "$LOG_FOLDER"
	fi

	empty_file $LOG_FILE
}


# Exibe um título no terminal.
# Uso: show_title "Meu título"
show_title() {
	echo ""
	echo ""
	echo "*********************************************************"
	echo "*** $1 ***"
	echo "*********************************************************"
	echo ""
}


# Exibe uma mensagem no terminal com o prefixo configurado em MSG_PREFIX.
# Uso: show_msg "Minha mensagem"
show_msg() {
	echo ""
	echo "$MSG_PREFIX $1"
}


# Exibe uma mensagem de erro no terminal, imprime no log (LOG_FILE)
# e finaliza o script (exit).
# Uso: error "Minha mensagem de erro"
error() {
	echo ""
	echo ""
	echo ""
	echo "------ ERRO: $1"
	echo ""
	echo ""
	echo ""

	if [ ! -z "$LOG_FILE" ] && [ -f "$LOG_FILE" ]; then
		echo "------ LOG:"
		tail -n 20 $LOG_FILE
	fi

	exit 1
}

show_help() {
	echo ""
	echo "$MSG_PREFIX $1"
	echo ""
	echo "$MSG_PREFIX AJUDA:"
	echo "$HELP_TEXT"
	echo ""

	exit 1
}


verify_log_error() {

	errors_terms=$1
	msg=$2

	for term in "${errors_terms[@]}"
	do
		if tail -n 200 $LOG_FILE | grep -qw $term; then
			error $msg
		fi
	done
}
