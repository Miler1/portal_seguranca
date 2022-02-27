#!/bin/bash

### Params

project_name="portalSeguranca"
main_folder=$(pwd)
version_folder=dist
version_name_prefix="utilitarios-${project_name}"
version_date=$(date +'%d_%m_%Y')

server_ssh_gt4test="utilitarios@177.105.35.61"
server_ssh_homolog_prod="lemaf@177.105.35.45"

server_pass_gt4test="senhautilitarios"
server_pass_homolog_prod="id*jc)W7"

server_home_folder="~/"
server_temp_folder="${project_name}"
server_backup_folder="${project_name}_bkp"

server_project_folder_gt4test="/var/play/utilitarios/${project_name}"
server_project_folder_homolog="/home/lemaf/releases/releases_homolog"
server_project_folder_prod="/home/lemaf/releases/releases_prod"

backup_file_name_gt4test="${project_name}_test_$(date +"%d_%m_%Y_%Hh%Mm").tar.gz"

service_name_gt4test="test-${version_name_prefix}"

grunt_service_name_gt4test="build:gt4test"
grunt_service_name_homolog="build:homolog"
grunt_service_name_prod="build:prod"

# Número da versão
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

### Gerando o manual do usuário

generateManual() {

	echo -e "\n--> Gerando o manual do usuário"
	asciidoctor -a stylesheet=maker.css -a stylesdir=stylesheets frontend/app/manual/manual.adoc

	checkStatus $? "Manual do usuário gerado com sucesso!" "Erro ao gerar manual do usuário!"

}

### Compilando frontend

compileFrontend() {

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
	cp conf/application.conf conf/routes conf/messages conf/play.plugins conf/log4j.${ambiente}.properties -t $version_folder/conf

	checkStatus $? "Arquivos de configuração copiados com sucesso para o diretório $version_folder/conf" "Erro ao copiar arquivos de configuração para o diretório $version_folder/conf"

	mv $version_folder/conf/log4j.${ambiente}.properties $version_folder/conf/log4j.properties

	checkStatus $? "Arquivo log4j copiado com sucesso para o diretório $version_folder/conf" "Erro ao copiar arquivo log4j para o diretório $version_folder/conf"

	# Adicionando informações de versionamento nas configurações
	echo "" >> $version_folder/conf/application.conf
	echo "server.version=$versao" >> $version_folder/conf/application.conf
	echo "server.update=$data_atual" >> $version_folder/conf/application.conf

	checkStatus $? "Número de versão inserido com sucesso no application.conf!" "Erro ao inserir número de versão no application.conf!"

}

### Limpando a versão da pasta de distribuição

cleanVersion() {

	echo -e "\n--> Limpando arquivos de versão já publicada ou compactada"

	cd backend

	rm -R $version_folder/app/ $version_folder/conf/ $version_folder/lib/ $version_folder/public/ $version_folder/modules/

	checkStatus $? "Arquivos de versão limpados com sucesso do diretório $version_folder" "Erro ao limpar arquivos de versão do diretório $version_folder"

}

### Criando uma versão compactada da aplicação para deploy

createAndUploadVersionZip() {

	echo -e "\n--> Compactando versão da aplicação para publicação"

	cd ..

	file_name="release-${version_name_prefix}-${versao}-${ambiente}.tar.gz"

	cd backend/$version_folder

	mkdir ${version_name_prefix}-${versao}

	cp -R app/ conf/ lib/ public/ modules/ ${version_name_prefix}-${versao}

	tar -czf $file_name ${version_name_prefix}-${versao}

	checkStatus $? "Versão compactada com sucesso! Arquivo gerado: ${file_name}" "Erro ao compactar versão!"

	rm -R ${version_name_prefix}-${versao}

	echo -e "\n--> Enviando versão para o servidor"

	scp ${file_name} $server_ssh:$server_project_folder/

	checkStatus $? "Versão enviada com sucesso!" "Erro ao enviar versão!"

	cd $main_folder

}

### Acessando servidor e criando pasta temporária

createTempFolder() {

	echo -e "\n--> Acessando servidor e criando pasta temporária"

	ssh -T $server_ssh <<-SERVER

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
	scp -r $version_folder/app/ $version_folder/conf/ $version_folder/lib/ $version_folder/public/ $version_folder/modules/ $server_ssh:$server_temp_folder/

	checkStatus $? "Nova versão da aplicação enviada para o servidor com sucesso!" "Erro ao enviar nova versão da aplicação para o servidor! Exit: $?"

}

### Realizando o deploy da aplicação no servidor

deployApp() {

	ssh -T $server_ssh <<-SERVER

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

			sudo service $service_name start

		}

		stopPlayFramework() {

			echo -e "\n--> Parando a execução do Play"

			sudo service $service_name stop

		}

		copyNewApp() {

			echo -e "\n--> Copiando nova versão da aplicação para $server_project_folder"

			cp -R $server_home_folder/$server_temp_folder/* $server_project_folder

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

	echo -e "Usage: ./deploy environment [options]";
	echo -e "Environments: gt4test | homolog | prod";
	echo -e "Options:";
	echo -e "	-b | --backup 		Backup existing version on the environment.";

}


##### Main

backup=false;

for (( i=2;$i<=$#;i=$i+1 ))
do

	case ${!i} in
		-b | --backup )
			backup=true
			;;
		* )
			usage
			exit 1
			;;

	esac

done

ambiente=$1;

case $ambiente in

	gt4test)

		echo -e "--> FAZENDO DEPLOY DA APLICAÇÃO NO AMBIENTE DE TESTES"

		server_project_folder=$server_project_folder_gt4test
		backup_file_name=$backup_file_name_gt4test
		service_name=$service_name_gt4test
		grunt_service_name=$grunt_service_name_gt4test
		server_ssh=$server_ssh_gt4test
		server_pass=$server_pass_gt4test

		generateManual
		compileFrontend
		generateVersion
		createTempFolder
		uploadApp
		deployApp
		cleanVersion

		echo -e "\n--> DEPLOY DA APLICAÇÃO NO AMBIENTE DE TESTES CONCLUÍDO"
		;;

	homolog)

		echo -e "--> GERANDO VERSÃO DA APLICAÇÃO PARA DEPLOY NO AMBIENTE DE HOMOLOGAÇÃO"

		grunt_service_name=$grunt_service_name_homolog
		server_project_folder=$server_project_folder_homolog
		server_ssh=$server_ssh_homolog_prod
		server_pass=$server_pass_homolog_prod

		generateManual
		compileFrontend
		generateVersion
		createAndUploadVersionZip
		cleanVersion

		echo -e "\n--> GERAÇAO DE VERSÃO DA APLICAÇÃO PARA DEPLOY NO AMBIENTE DE HOMOLOGAÇÃO CONCLUÍDA"
		;;

	prod)

		echo -e "--> GERANDO VERSÃO DA APLICAÇÃO PARA DEPLOY NO AMBIENTE DE PRODUÇÃO"

		grunt_service_name=$grunt_service_name_prod
		server_project_folder=$server_project_folder_prod
		server_ssh=$server_ssh_homolog_prod
		server_pass=$server_pass_homolog_prod

		generateManual
		compileFrontend
		generateVersion
		createAndUploadVersionZip
		cleanVersion

		echo -e "\n--> GERAÇAO DE VERSÃO DA APLICAÇÃO PARA DEPLOY NO AMBIENTE DE PRODUÇÃO CONCLUÍDA"
		;;

	*)

		usage
		exit 1

		;;

esac

exit 0
