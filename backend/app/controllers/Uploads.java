package controllers;

import exceptions.PortalSegurancaException;
import org.apache.tika.Tika;
import play.Play;
import play.data.Upload;
import play.libs.IO;
import utils.FileManager;

import java.io.IOException;

public class Uploads extends BaseController {

	public static void uploadArquivo(Upload file) throws IOException {

		Tika tika = new Tika();
		String realType = tika.detect(file.asFile());
		String tiposSuportados = Play.configuration.getProperty("application.extensoes");

		if(realType == null || !tiposSuportados.contains(realType)) {

			throw new PortalSegurancaException().userMessage("uploads.extensao.naoSuportada");

		}

		byte[] data = IO.readContent(file.asFile());
		FileManager fileManager = FileManager.getInstance();
		String extension = getExtension(fileManager, file);
		String key = fileManager.createFile(data, extension);

		renderText(key);

	}

	private static String getExtension(FileManager fileManager, Upload file) throws IOException {

		Tika tika = new Tika();
		String realType = tika.detect(file.asFile());
		String extension = fileManager.getFileExtention(file.getFileName());

		if(realType.contains("image/")) {

			return realType.substring(realType.indexOf("/") + 1);

		}

		return extension;

	}

}
