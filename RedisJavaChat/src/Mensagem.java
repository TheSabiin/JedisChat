/**
 * 
 */

/**
 * @author Marcos Sabino
 * 
 * @since maio 24, 2018
 * 
 * @version 1.0
 *
 */
public class Mensagem {
	
	private Long id;
	private String texto;
	private String data;
	private String remetente;
	
	public Mensagem() {}
	
	public Mensagem(Long id, String texto, String data, String remetente) {
		this.id = id;
		this.texto = texto;
		this.data = data;
		this.remetente = remetente;
	}	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getRemetente() {
		return remetente;
	}
	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}

	@Override
	public String toString() {
		String output = String.format(""
				+ "\nID: %d "
				+ "\nTexto: %s "
				+ "\nData: %s "
				+ "\nRemetente: %s", id, texto, data, remetente);
		return output;		
	}

}
