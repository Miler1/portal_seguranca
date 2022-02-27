#!/bin/bash

### Postgresql Utils
PG_LOG_FILE="$LOG_FOLDER/postgresql.log"

POSTGIS_OPTIONS=(
	"/usr/share/postgresql/9.6/contrib/postgis-2.3"
	"/usr/share/postgresql/9.5/contrib/postgis-2.2"
	"/usr/share/postgresql/9.4/contrib/postgis-2.1"
	"/usr/share/postgresql/9.2/contrib/postgis-1.5" 
	"/usr/local/share/postgresql/contrib/postgis-1.5")


pg_db_exists() {

	host=$1
	port=$2
	user=$3
	dbName=$4

	if psql -h $host -p $port -U $user -lqt | cut -d \| -f 1 | grep -qw $dbName; then
		return 0
	else
		return 1
	fi
}


pg_role_exists() {

	host=$1
	port=$2
	role_name=$3

	sql="SELECT rolname FROM pg_roles where rolname = '$role_name';"

	if psql -h $host -p $port -U postgres -qt -c "$sql" | grep -qw $role_name; then
		return 0
	else
		return 1
	fi
}


pg_create_db() {

	host=$1
	port=$2
	user=$3
	dbName=$4

	log "Criando novo banco de dados (createdb)"

	createdb -h $host -p $port -U $user -E UTF8 --lc-collate='pt_BR.UTF-8' --lc-ctype='pt_BR.UTF-8' -T template0 $dbName >> $LOG_FILE

	if ! pg_db_exists $host $port $user $dbName; then
		error "Não foi possível criar o banco de dados $db_name"
	fi

	show_msg "Novo banco de dados $db_name criado com sucesso."
}


pg_delete_db() {

	host=$1
	port=$2
	user=$3
	dbName=$4

	if pg_db_exists $host $port $user $dbName; then

		if ! user_confirm "O banco de dados \"$dbName\" já existe e será removido. Deseja prosseguir?"; then
			show_msg "Operação cancelada pelo usuário."
			exit 1
		fi
	fi

	log "Deletando banco de dados (dropdb)"

	dropdb -h $host -p $port -U $user $dbName --if-exists >> $LOG_FILE

	if pg_db_exists $host $port $user $dbName; then
		error "Não foi possível deletar o banco de dados $db_name"
	fi

	show_msg "Banco de dados $db_name deletado com sucesso."
}


pg_run_sql_file() {

	validate_files_exist "$5"

	log "psql -h $1 -p $2 -U $3 -d $4 -v ON_ERROR_STOP=1 --single-transaction -q -f $5"

	psql -h $1 -p $2 -U $3 -d $4 -v ON_ERROR_STOP=1 --single-transaction -q -f $5 >& $PG_LOG_FILE

	cat $PG_LOG_FILE >> $LOG_FILE

	pg_verify_sql_error
}

pg_run_sql() {

	log "psql -h $1 -p $2 -U $3 -d $4 -v ON_ERROR_STOP=1 --single-transaction -q -c \"$5\""

	psql -h $1 -p $2 -U $3 -d $4 -v ON_ERROR_STOP=1 --single-transaction -q -c "$5" >& $PG_LOG_FILE

	cat $PG_LOG_FILE >> $LOG_FILE

	pg_verify_sql_error
}


pg_verify_sql_error() {

	if tail -n 200 $LOG_FILE | grep -qw "ERROR:"; then
		error "Erro ao executar SQL '$5' no banco de dados $db_name."
	fi

	if tail -n 200 $LOG_FILE | grep -qw "ERRO:"; then
		error "Erro ao executar SQL '$5' no banco de dados $db_name."
	fi
}

pg_create_db_role_if_not_exist() {


	if ! pg_role_exists $1 $2 $5; then
		pg_run_sql $1 $2 postgres $4 "CREATE ROLE $5 LOGIN PASSWORD '$6' SUPERUSER;"
	fi
	
	if ! pg_role_exists $1 $2 $5; then
		error "Não foi possível criar o usuário $5."
	fi
}


configure_postgis_path() {

	for path in "${POSTGIS_OPTIONS[@]}"
	do
		if [ -d "$path" ]; then
			POSTGIS_PATH="$path"
			return
		fi
	done
}


### Funções para o banco de dados padrão (configurado nas variáveis DB_HOST, DB_PORT, DB_USER, DB_NAME).
### Após configuradas tais variáveis, as funções abaixo não requerem tais parâmetros.


verify_default_db_variables() {

	if [ -z "$DB_HOST" -o -z "$DB_PORT" -o -z "$DB_USER" -o -z "$DB_NAME" ]; then

		error "Para utilizar esta função as seguintes variaveis devem ser configuradas:
		- DB_HOST 
		- DB_PORT
		- DB_USER
		- DB_NAME ";
	fi
}


db_exists() {

	verify_default_db_variables
	return $(pg_db_exists $DB_HOST $DB_PORT $DB_USER $DB_NAME)
}


role_exists() {

	verify_default_db_variables
	role_name=$1
	return $(pg_role_exists $DB_HOST $DB_PORT $DB_USER $ROLE_NAME)
}


delete_db() {

	verify_default_db_variables
	pg_delete_db $DB_HOST $DB_PORT $DB_USER $DB_NAME
}


create_db() {

	verify_default_db_variables
	pg_create_db $DB_HOST $DB_PORT $DB_USER $DB_NAME
}


db_exists() {

	verify_default_db_variables
	return $(pg_db_exists $DB_HOST $DB_PORT $DB_USER $DB_NAME)
}


run_sql_file() {

	verify_default_db_variables
	pg_run_sql_file $DB_HOST $DB_PORT $DB_USER $DB_NAME $1
}


create_db_role_if_not_exist() {
	pg_create_db_role_if_not_exist $DB_HOST $DB_PORT $DB_USER $DB_NAME $1 $2
}


configure_postgis() {

	if [ ! -d "$POSTGIS_PATH" ]; then
		error "Não foi possível encontrar o path do postgis."
	fi

	run_sql_file "$POSTGIS_PATH/postgis.sql"
	run_sql_file "$POSTGIS_PATH/spatial_ref_sys.sql"

	if [ -f "$POSTGIS_PATH/legacy.sql" ]; then
		run_sql_file "$POSTGIS_PATH/legacy.sql"
	fi

	if [ -f "$POSTGIS_PATH/legacy_gist.sql" ]; then
		run_sql_file "$POSTGIS_PATH/legacy_gist.sql"
	fi
}


configure_postgis_path