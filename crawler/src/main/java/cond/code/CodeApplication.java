package cond.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class CodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeApplication.class, args);

		// Adiciona um atraso para garantir que o servidor esteja pronto
		try {
			Thread.sleep(1000); // Espera 5 segundos
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		openBrowser("http://localhost:8080/create");
	}

	private static void openBrowser(String url) {
		String os = System.getProperty("os.name").toLowerCase();

		try {
			if (os.contains("win")) {
				// Windows
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
				// Unix/Linux/Mac
				Runtime.getRuntime().exec(new String[]{"/usr/bin/open", url});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
