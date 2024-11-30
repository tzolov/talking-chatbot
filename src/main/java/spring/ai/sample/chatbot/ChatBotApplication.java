package spring.ai.sample.chatbot;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// @formatter:off
@SuppressWarnings("null")
@SpringBootApplication
public class ChatBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatBotApplication.class, args);
	}

	@Bean
	public CommandLineRunner talkingChatBot(ChatClient.Builder chatClientBuilder) {
		return args -> {
			// 1. Create the ChatClient with chat memory and Audio output options
			var chatClient = chatClientBuilder
				.defaultSystem("""
					You are useful assistant, expert in electric cars.
					Keep your answers short and to the point.
					""")
				.defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
				.build();

			// 2. Start the chat loop
			System.out.println("\nI am your personal electric car assistant.\n");
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					System.out.print("\nUSER: ");
					var response = chatClient.prompt(scanner.nextLine()).call().chatResponse().getResult().getOutput();
					System.out.println("\nASSISTANT: " + response.getContent());
					play(response.getMedia().get(0).getDataAsByteArray());
				}
			}
		};
	}

	public static void play(byte[] waveData) { // java utils to play wav audio
		try (Clip clip = AudioSystem.getClip();
			 AudioInputStream audio =
			 	AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteArrayInputStream(waveData)));) {
			clip.open(audio);
			clip.start();
			while (!clip.isRunning()) { Thread.sleep(1000); } // wait to start
			while (clip.isRunning()) { Thread.sleep(3000); } // wait to finish
		}
		catch (Exception e) { throw new RuntimeException(e); }
	}
}
// @formatter:on
