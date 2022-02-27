#!/bin/bash


### Inicialização

SELF_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
LOG_FILE="$SELF_DIR/logs/criar_bd.log"

source "$SELF_DIR/utils/utils.sh"
source "$SELF_DIR/utils/postgresql_utils.sh"
source "$SELF_DIR/utils/db_evolutions.sh"

### Configurações


# Parâmetros
ambiente="$1"

# Banco de dados
DB_HOST="localhost"
DB_PORT="5432"
DB_USER="postgres"
DB_NAME=""

dev_db_name="utilitarios"
test_db_name="utilitarios_test"

#Evolutions
evolutions_folder="$OUTPUT_FOLDER/evolutions"
evolutions_sql=("evolutions.sql" "seeds.sql")
evolutions_jar_path="$SELF_DIR/utils/dbEvolutions-1.0.0.jar"
evolutions_config="$SELF_DIR/conf/evolutionsBuild.json"

clean_log


### Ambientes

ambientes=("dev" "test")

if [ "$ambiente" == "dev" ]; then
	DB_NAME="$dev_db_name"
elif [ "$ambiente" == "test" ]; then
	DB_NAME="$test_db_name"
else
	error "Ambiente inválido. Opções: dev, test."
fi


### Criação do banco de dados

show_title "Script para criação do banco de dados"

delete_db

create_db

configure_postgis

create_db_role_if_not_exist "licenciamento_pa" "licenciamento_pa"


### Evolutions

show_msg "Gerando e executando evolutions."

java -jar $jar_path joinUps -d $ROOT_FOLDER -c $evolutions_config > $LOG_FILE

if tail -n 200 $LOG_FILE | grep -qw "Erro"; then
	error "Erro ao gerar as evolutions."
fi

for sql in "${evolutions_sql[@]}"
do
	show_msg "  - Executando evolutions: $evolutions_folder/$sql"
	run_sql_file "$evolutions_folder/$sql"
done


show_msg "Criação do banco de dados finalizada com sucesso."
echo ""
