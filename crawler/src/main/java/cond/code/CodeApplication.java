package cond.code;

import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

@SpringBootApplication
public class CodeApplication implements CommandLineRunner {

	@Value("${server.port}")
	private Integer port;

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		try {
			context = SpringApplication.run(CodeApplication.class, args);
		} catch (Throwable e) {
			showErrorDialog("Erro ao iniciar a aplicação: " + e.getMessage());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			System.exit(1);
		}
	}

	@Override
	public void run(String... args) {
		try {
			openBrowser("http://localhost:" + port + "/create");
		} catch (WebServerException e) {
			showErrorDialog("Erro ao iniciar a aplicação: A porta " + port + " já está em uso.");
			System.exit(1);
		} catch (Exception e) {
			showErrorDialog("Erro ao executar a aplicação: " + e.getMessage());
			System.exit(1);
		}
	}

	private void openBrowser(String url) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
				Runtime.getRuntime().exec(new String[]{"/usr/bin/open", url});
			}
		} catch (IOException e) {
			showErrorDialog("Erro ao abrir o navegador: " + e.getMessage());
			System.exit(1);
		}
	}

	private static void showErrorDialog(String message) {
		if (GraphicsEnvironment.isHeadless()) {
			System.err.println(message);
		} else {
			SwingUtilities.invokeLater(() -> {
				JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE);
			});
		}
	}


	@RestController
	public static class ShutdownController {
		@DeleteMapping("/shutdown")
		public String shutdown() {
			SpringApplication.exit(context, () -> 0);
			return "shutdown";
		}
	}
}
