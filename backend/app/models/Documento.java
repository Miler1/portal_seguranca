package models;

import models.TipoDocumento.Tipos;
import org.apache.commons.io.FileUtils;
import play.db.jpa.GenericModel;
import utils.Config;
import utils.FileManager;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Entity
@Table(schema = "portal_seguranca", name = "documento")
public class Documento extends GenericModel {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_documento")
	@SequenceGenerator(name = "sq_documento", sequenceName = "portal_seguranca.sq_documento", allocationSize = 1)
	public Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tipo_documento", referencedColumnName = "id")
	public TipoDocumento tipoDocumento;

	@Column(name = "caminho")
	public String caminho;

	@Column(name = "nome")
	public String nome;

	@Column(name = "data_cadastro")
	@Temporal(TemporalType.DATE)
	public Date dataCadastro;

	@Transient
	public String key;
	
	public Documento() {

		super();

		this.dataCadastro = new Date();

	}

	public Documento(String nomeArquivoMovido, TipoDocumento tipoDocumento, String key) {

		super();

		this.tipoDocumento = tipoDocumento;
		this.nome = nomeArquivoMovido;
		this.key = key;
		this.dataCadastro = new Date();

	}

	@Override
	public Documento save() {

		if(this.id != null) { // evitando sobrescrever algum documento.

			throw new IllegalStateException("documentos.cadastro.jaSalvo");

		}

		if(this.tipoDocumento == null || this.key == null) {

			throw new IllegalStateException("documentos.cadastro.tipoKey.obrigatorios");

		}

		saveFile();

		return super.save();

	}

	private void saveFile() {

		try {
			
			Tipos tipo = this.tipoDocumento.getTipo();
			FileManager fm = FileManager.getInstance();
			File file = fm.getFile(this.key);
			this.key = null;

			createFolder();

			this.caminho = File.separator
					+ tipo.caminho
					+ File.separator
					+ this.nome
					+ "."
					+ fm.getFileExtention(file.getName());

			FileUtils.copyFile(file, new File(this.getFullPath()));

		} catch(IOException e) {

			throw new RuntimeException(e);

		}

	}

	public Documento saveFileGenerated(String nome) {

		this.caminho = "temp";

		Tipos tipo = this.tipoDocumento.getTipo();

		createFolder();

		this.caminho = File.separator + tipo.caminho
				+ File.separator + nome + "_" + this.id + ".csv";
		this.nome = this.nome + "_" + this.id + ".csv";

		return super.save();

	}

	private void createFolder() {

		Tipos tipo = this.tipoDocumento.getTipo();
		String caminho = Config.ARQUIVOS_PATH + File.separator + tipo.caminho;
		File pasta = new File(caminho);

		if(!pasta.exists()) {

			pasta.mkdirs();

		}

	}

	private String getFullPath() {

		return Config.ARQUIVOS_PATH + File.separator + this.caminho;

	}

	public File getFile() {

		return new File(getFullPath());

	}

	@Override
	public Documento delete() {

		super.delete();

		File file = getFile();

		if(file != null && file.exists()) {

			file.delete();

		}

		return this;

	}

}
