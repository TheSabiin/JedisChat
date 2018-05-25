/**
 * @author MarcosSabino
 *
 */
public class Usuario {
	private String nickname;
	private String nome;
	private String dataCadastro;
	
	public Usuario() {}
	
	public Usuario(String apelido, String nome, String dataCadastro) {
		this.nickname = apelido;
		this.nome = nome;
		this.dataCadastro = dataCadastro;
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(String dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	@Override
	public String toString() {
		String output = String.format(""
				+ "\nApelido: %s "
				+ "\nNome: %s "
				+ "\nData de Cadastro: %s", nickname, nome, dataCadastro);
		return output;
	}
	
}
		