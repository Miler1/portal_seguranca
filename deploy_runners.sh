#!/bin/bash

### Params
	
project_name="portal-seguranca"
main_folder=$(pwd)
version_folder=dist
version_name_prefix="${project_name}"
version_date=$(date +'%d_%m_%Y')

server_ssh_test="deploy@java3-5.ti.lemaf.ufla.br"
server_pass_test="deploy"

server_home_folder="~/"
server_temp_folder="/tmp/${project_name}"
server_backup_folder="/var/application/utilitarios/${project_name}/backups/${project_name}_bkp"

backup_file_name_test="${project_name}_test_$(date +"%d_%m_%Y_%Hh%Mm").tar.gz"

server_project_folder_test="/var/application/utilitarios/${version_name_prefix}/${project_name}"

service_name_test="/etc/init.d/${project_name}"

grunt_service_name_test="build:runners2test"

server_name_test="Testes - Runners"


# Número da versão
#versao=$(git rev-parse HEAD)
versao=$(git describe --tags)
data_atual=$(date +'%d/%m/%Y %H:%M')

### Verificação de status das operações
### $1 - status, $2 - successMessage, $3 - errorMessage
checkStatus() {

	if [ $1 -eq 0 ]
	then
		echo -e "\n<-- $2"
	else
		echo -e "\n<-- $3"
		exit 1
	fi

}

### Compilando frontend

compileFrontend() {

	killall grunt

	echo -e "\n--> Compilando o frontend"
	cd frontend/

	npm install
	bower install
	bower prune
	bower update

	grunt $grunt_service_name

	checkStatus $? "Frontend compilado com sucesso!" "Erro ao compilar o Frontend!"

	cd ..

}

### Copiando a versão para a pasta de distribuição

generateVersion() {

	echo -e "\n--> Gerando arquivos de versão da aplicação para publicação ou compactação"

	cd backend

	if [ ! -d dist ]
	then
		mkdir -p dist
	fi

	cp -R app/ lib/ public/ modules/ -t $version_folder

	checkStatus $? "Arquivos copiados com sucesso para o diretório $version_folder" "Erro ao copiar arquivos para o diretório $version_folder"

	mkdir -p $version_folder/conf
	cp conf/application.conf conf/routes conf/messages conf/play.plugins conf/log4j.runners2.properties -t $version_folder/conf

	checkStatus $? "Arquivos de configuração copiados com sucesso para o diretório $version_folder/conf" "Erro ao copiar arquivos de configuração para o diretório $version_folder/conf"

	mv $version_folder/conf/log4j.runners2.properties $version_folder/conf/log4j.properties

	checkStatus $? "Arquivo log4j copiado com sucesso para o diretório $version_folder/conf" "Erro ao copiar arquivo log4j para o diretório $version_folder/conf"

	# Adicionando informações de versionamento nas configurações
	echo "" >> $version_folder/conf/application.conf
	echo "server.version=$versao" >> $version_folder/conf/application.conf
	echo "server.update=$data_atual" >> $version_folder/conf/application.conf

	checkStatus $? "Número de versão inserido com sucesso no application.conf!" "Erro ao inserir número de versão no application.conf!"

}

### Limpando a versão da pasta de distribuição

cleanVersion() {

	echo -e "\n--> Limpando arquivos de versão já publicada ou compactada $(pwd)"

	if [ -d $version_folder ]
	then
		rm -rf $version_folder/*
	else
		mkdir $version_folder
	fi

	checkStatus $? "Arquivos de versão limpados com sucesso do diretório $version_folder" "Erro ao limpar arquivos de versão do diretório $version_folder"

}

### Define algumas informações imporatantes para gerar a versão ###
setVersionInfo() {

	version=$(git describe --tags --abbrev=0)
	date=$(date +'%d/%m/%Y %H:%M')
	last_change=$(git log -1 --pretty=%B)
	last_commit=$(git rev-parse HEAD)
	branch=$(git branch)
	generated_by=$(git config --get user.name)
}

includeVersion(){

	echo -e "\n--> Adicionando dados a respeito da versão"

	# Adicionando informações de versionamento nas configurações
	echo "" >> $version_folder/conf/application.conf
	echo "server.version=$version" >> $version_folder/conf/application.conf
	echo "server.update=$date" >> $version_folder/conf/application.conf
	echo "server.name=$server_name" >> $version_folder/conf/application.conf
	echo "server.ultimoCommit=$last_commit" >> $version_folder/conf/application.conf
	echo "server.ultimaAlteracao=$last_change" >> $version_folder/conf/application.conf

	checkStatus $? "Número de versão inserido com sucesso no application.conf!" "Erro ao inserir número de versão no application.conf!"

}

### Acessando servidor e criando pasta temporária

createTempFolder() {

	echo -e "\n--> Acessando servidor e criando pasta temporária"

	sshpass -p"$server_pass" ssh -T $server_ssh <<-SERVER

		### Criando pasta temporária

		cd $server_home_folder

		if [ ! -d $server_temp_folder ]
		then
			echo -e "\n--> Criando pasta temporária $server_temp_folder"
			mkdir -p $server_temp_folder
		else
			echo -e "\n--> Limpando pasta temporária $server_temp_folder"
			rm -Rf $server_temp_folder/*
		fi

		cd $server_temp_folder

	SERVER

}

### Copiando aplicação para a pasta temporária no servidor

uploadApp() {

	echo -e "\n--> Enviando nova versão da aplicação para a pasta temporária do servidor $server_temp_folder"
	sshpass -p"$server_pass" scp -r $version_folder/. $server_ssh:$server_temp_folder/

	checkStatus $? "Nova versão da aplicação enviada para o servidor com sucesso!" "Erro ao enviar nova versão da aplicação para o servidor! Exit: $?"

}

### Realizando o deploy da aplicação no servidor

deployApp() {

	sshpass -p"$server_pass" ssh -T $server_ssh <<-SERVER

		backupOldApp() {

			cd $server_home_folder

			if [ ! -d $server_backup_folder ]
			then
				echo -e "\n--> Criando pasta de backup $server_backup_folder"
				mkdir -p $server_backup_folder
			fi

			echo -e "\n--> Fazendo backup da aplicação de $server_project_folder em $server_home_folder$server_backup_folder"

			if tar -czf $server_home_folder$server_backup_folder/$backup_file_name $server_project_folder &> /dev/null;
			then
				echo -e "\n--> Backup da aplicação realizado com sucesso!"
			else
				echo -e "\n--> Erro ao realizar o backup da aplicação!"
				exit 1
			fi

		}

		cleanProjectFolder() {

			echo -e "\n--> Limpando a pasta do projeto em $server_project_folder"

			rm -R $server_project_folder/*

		}

		startPlayFramework() {

			echo -e "\n--> Iniciando a execução do Play"

			sudo $service_name_test start

		}

		stopPlayFramework() {

			echo -e "\n--> Parando a execução do Play"

			sudo $service_name_test stop

		}

		copyNewApp() {

			echo -e "\n--> Copiando nova versão da aplicação para $server_project_folder"

			cp -R $server_temp_folder/* $server_project_folder
 		    cd  $server_project_folder
		    ln -s ../arquivos .
		}

		if $backup
		then
			backupOldApp
		fi

		stopPlayFramework
		cleanProjectFolder
		copyNewApp
		startPlayFramework

	SERVER

}

### Rodando os scripts de acordo com o ambiente

function usage {

	echo -e "Usage: ./deploy.java-3-5.sh environment [options]";
	echo -e "Ambientes: test";
	echo -e "Options:";
	echo -e "	-b, --backup		Cria backup da versão que está rodando atualmente no ambiente(válido apenas para oção de test)";
	echo -e "	-c, --changelog		Mostra o changelog baseado nas últimas duas Tags";
	echo -e "	-f, --force		Pula todas as etapas de interação com o usuário(checagem da versão do Play, e criação de nova Tag)";
	echo -e "	-ng, --no-grunt		Pula a etapa de compilação do frontend";
	echo
}


##### Main

backup=false;

changelog=false

force=false

executarGrunt=true

for (( i=2;$i<=$#;i=$i+1 ))
do

	case ${!i} in
		-b | --backup )
			backup=true
			;;

		-c | --changelog )
			changelog=true
			;;

		-f | --force )
			force=true
			;;

		-ng | --no-grunt )
			executarGrunt=false
			;;
		* )
			usage
			exit 1
			;;

	esac

done

ambiente=$1;

case $ambiente in

	test)

		echo -e "--> FAZENDO DEPLOY DA APLICAÇÃO NO AMBIENTE DE TESTES RUNNERS"

		command -v sshpass >/dev/null 2>&1 || { echo >&2 "O comando 'sshpass' é necessário e não foi encontrado. Instale-o com seu gerenciador de pacotes e execute este script novamente."; exit 1; }

		setVersionInfo

		server_project_folder=$server_project_folder_test
		backup_file_name=$backup_file_name_test
		service_name=$service_name_test
		grunt_service_name=$grunt_service_name_test
		server_ssh=$server_ssh_test
		server_pass=$server_pass_test
		server_name=$server_name_test

		if $executarGrunt
		then
		compileFrontend
		fi

		generateVersion
		includeVersion
		createTempFolder
		uploadApp
		deployApp
		cleanVersion

		echo -e "\n--> DEPLOY DA APLICAÇÃO NO AMBIENTE DE TESTES RUNNERS CONCLUÍDO"
		;;
	*)

	usage
	exit 1
	;;

esac

exit 0
