package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

public class FileUtils extends org.apache.commons.io.FileUtils {

	/**
	 * Cria arquivo com o conteúdo especificado.
	 *
	 * @param diretorio
	 * @param nomeArquivo
	 * @param conteudoArquivo
	 * @return Arquivo criado
	 * @throws IOException
	 */
	public static File gravarArquivo(File diretorio, String nomeArquivo,
			File conteudoArquivo) throws IOException {

		// Arquivo
		File arquivo = new File(diretorio, nomeArquivo);

		// Grava o arquivo no sistema de arquivos
		OutputStream output = null;

		try {

			output = new FileOutputStream(arquivo);

			IOUtils.write(
					IOUtils.toByteArray(new FileInputStream(conteudoArquivo)),
					output);

			return arquivo;

		} finally {

			IOUtils.closeQuietly(output);

		}

	}

	public static void descompactarArquivos(File arquivoZip, File destino) throws ZipException, IOException {

		ZipFile zipFile = new ZipFile(arquivoZip);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements()) {

			ZipEntry entry = entries.nextElement();
			File entryDestination = new File(destino.getAbsolutePath(),  entry.getName());

			entryDestination.getParentFile().mkdirs();

			if (entry.isDirectory()) {

				entryDestination.mkdirs();

			}

			else {

				InputStream in = zipFile.getInputStream(entry);

				OutputStream out = new FileOutputStream(entryDestination);

				IOUtils.copy(in, out);
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);

			}

		}

		zipFile.close();

	}

	public static File findArquivoNoDiretorioComExtensao(File diretorio, String extensao) {

		File[] arquivos = diretorio.listFiles();
		File arquivoShape = null;

		for (File arquivo : arquivos) {

			if (arquivo.getName().toLowerCase().endsWith("." + extensao)) {

				arquivoShape = arquivo;

				break;

			}

		}

		return arquivoShape;

	}

}
