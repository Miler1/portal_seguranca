package jobs;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.Config;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;

import static play.Logger.error;
import static play.Logger.info;

@OnApplicationStart
public class JobCopiarImagens extends Job {

	@Override
	public void doJob() {

		info("[JOB-START] Iniciando cópia de imagens.");

		File srcFileModulo = new File(Play.applicationPath + "/public/images/MCU_logotipo.png");
		File destFileModulo = new File(Config.ARQUIVOS_PATH_LOGOTIPOS + "MCU_logotipo.png");

		try {

			// Copia a imagem do módulo fixo de Cadastro Unificado
			if(destFileModulo.length() > 0L && srcFileModulo.length() > 0L) {

				if(destFileModulo.exists()) {

					destFileModulo.delete();

					FileUtils.copyFile(srcFileModulo, destFileModulo);

					info("[JOB-END] Cópia de imagens finalizado com sucesso. ");

				}

			}

		} catch(IOException e) {

			error(e, "[JOB-ERROR] Erro ao copiar imagens.");

		}

	}

}
