
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import br.edu.opet.Reader;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 
 */

/**
 * @author MarcosSabino
 *
 */
public class MainRedisJavaChat {

	// Conectando no Redis server - localhost
	static Jedis jedis = new Jedis("localhost", 6379);
	static String loginApelido;

	static void conectaRedis() {
		try {
			jedis.connect();

			if (!jedis.exists("MENSAGEM_ID"))
				jedis.set("MENSAGEM_ID", "0");
		} catch (JedisConnectionException e) {
			System.out.printf("A conexão não pode ser realizada. \n%s", e.getMessage());
		}
	}

	public static void main(String[] args) {
		do {
			System.out.println("\nChatRedis");

			if (loginApelido != null)
				telaLogin();

			telaInicial();

		} while (false);
	}

	static void telaInicial() {
		int opcao = 0;

		do {
			opcao = Reader.readInt("\n1. Login. \n2. Cadastro. \n3. Sair.\n");

			switch (opcao) {
			case 1:
				telaLogin();
				break;

			case 2:
				telaCadastro();
				break;

			case 3:
				System.out.println("Até a próxima!\n Programa encerrado!");
				System.exit(1);
				break;

			default:
				System.out.println("\nOpção inválida.");
				break;
			}

		} while (true);
	}

	/**
	 * Exibe tela de login.
	 */
	static void telaLogin() {
		loginApelido = Reader.readString("Entre com seu apelido: ");		
		if (jedis.sismember("APELIDOS", loginApelido)) {			
			System.out.println("Usuário cadastrado!");
			telaUsuario();

		} else
			System.out.println("\nUsuário não cadastrado.");
		telaInicial();
	}

	static void telaCadastro() {
		Usuario usuario = new Usuario();
		DateTimeFormatter dataFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		do {
			usuario.setNickname(Reader.readString("Entre com um apelido: "));

			if (jedis.sismember("APELIDOS", usuario.getNickname()))
				System.out.println("\nUsuário ja foi cadastrado.");
			else
				break;

		} while (true);
		usuario.setNome(Reader.readString("Entre com seu nome completo: "));
		usuario.setDataCadastro(LocalDateTime.now().format(dataFormat));

		jedis.sadd("APELIDOS", usuario.getNickname());
		jedis.hset(usuario.getNickname(), "NOME", usuario.getNome());
		jedis.hset(usuario.getNickname(), "DATA_CADASTRO", usuario.getDataCadastro());

	}

	static void telaUsuario() {
		int opcao;

		do {
			opcao = Reader.readInt("\n1. Enviar mensagem. \n2. Caixa de entrada. \n3. Caixa de saída. \n4. Sair. \n\t");

			switch (opcao) {
			case 1:
				enviarMensagem();
				break;

			case 2:
				caixaDeEntrada();
				break;

			case 3:

				caixaDeSaida();
				break;

			case 4:
				loginApelido = null;
				telaInicial();
				break;

			default:
				System.out.println("\nOpção inválida.");
				break;
			}
		} while (true);
	}

	/**
	 * Realiza envio de mensagem.
	 */
	static void enviarMensagem() {
		Mensagem mensagem = new Mensagem();
		String destinatarios;
		String[] listDestinatarios;
		DateTimeFormatter dataFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		mensagem.setId(jedis.incr("MENSAGEM_ID"));
		mensagem.setRemetente(loginApelido);
		mensagem.setData(LocalDateTime.now().format(dataFormat));
		mensagem.setTexto(Reader.readString("Mensagem: "));

		jedis.zadd(("MENSAGEM:" + mensagem.getId()), 0, mensagem.getId().toString());
		jedis.zadd(("MENSAGEM:" + mensagem.getId()), 1, mensagem.getRemetente());
		jedis.zadd(("MENSAGEM:" + mensagem.getId()), 2, mensagem.getData());
		jedis.zadd(("MENSAGEM:" + mensagem.getId()), 3, mensagem.getTexto());

		destinatarios = Reader.readString("Informe os destinatários separados por (-).");
		listDestinatarios = destinatarios.split("-");

		for (String destinatario : listDestinatarios) {
			jedis.sadd(("MENSAGEM:" + mensagem.getId() + ":DESTINATARIOS"), destinatario);
			jedis.rpush(("MENSAGEM:RECEBIDAS:" + destinatario), ("MENSAGEM:" + mensagem.getId()));
			jedis.rpush(("MENSAGEM:ENVIADAS:" + loginApelido), ("MENSAGEM:" + mensagem.getId()));
		}

		System.out.println("\nMensagem enviada.");
	}

	/**
	 * Exibe a caixa de entrada do usuario
	 */
	static void caixaDeEntrada() {
		Long numeroMensagens = jedis.llen("MENSAGEM:RECEBIDAS:" + loginApelido);
		List<String> mensagens = jedis.lrange(("MENSAGEM:RECEBIDAS:" + loginApelido), 0, numeroMensagens);
		Character responder;
		if (mensagens.size() == 0) {

			System.out.println("Não há mensagens na sua caixa de entrada!");
			telaUsuario();

		}

		for (String mensagem : mensagens) {
			System.out.println("\nID: " + jedis.zrangeByScore(mensagem, 0, 0).toArray()[0].toString());
			System.out.println("Apelido: " + jedis.zrangeByScore(mensagem, 1, 1).toArray()[0].toString());
			System.out.println("Mensagem: " + jedis.zrangeByScore(mensagem, 3, 3).toArray()[0].toString());
			System.out.println("Data: " + jedis.zrangeByScore(mensagem, 2, 2).toArray()[0].toString());
		}
		
		responder = Reader.readCharacter("Deseja responder a mensagem? S/N");
		responder = Character.toLowerCase(responder);
		
		if(responder == 's') {
			
			enviarMensagem();
		}
		
	}

	/**
	 * Exibe a caixa de saida do usuario
	 */
	static void caixaDeSaida() {
		Long numerosMensagens = jedis.llen("MENSAGEM:ENVIADAS:" + loginApelido);
		List<String> mensagens = jedis.lrange(("MENSAGEM:ENVIADAS:" + loginApelido), 0, numerosMensagens);

		if (mensagens.size() == 0) {

			System.out.println("Não há mensagens na sua caixa de saída!");
			telaUsuario();

		}

		for (String mensagem : mensagens) {
			System.out.println("\nApelido: " + jedis.zrangeByScore(mensagem, 0, 0).toArray()[0].toString());
			System.out.println("Mensagem: " + jedis.zrangeByScore(mensagem, 2, 2).toArray()[0].toString());
			System.out.println("Data: " + jedis.zrangeByScore(mensagem, 1, 1).toArray()[0].toString());
		}
	}

}
