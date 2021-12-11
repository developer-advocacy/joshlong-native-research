package com.example.templatenative;

import com.joshlong.templates.MarkdownService;
import com.joshlong.templates.MustacheService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
public class TemplateNativeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemplateNativeApplication.class, args);
	}

	@Bean
	ApplicationRunner runner(MarkdownService markdownService, MustacheService mustacheService) {
		return args -> {

			var markdown = """
									
				# Hello, World
									
				This is a test
									
				So is this. [This is a link](https://twitter.com/starbuxman).
									
				I *love* to emphasize things **strongly**.
									
				""";
			System.out.println("html: \n" + markdownService.convertMarkdownTemplateToHtml(markdown));

			var mustache = """
				Hello {{name}}
				You have just won {{value}} dollars!
				{{#in_ca}}
				Well, {{taxed_value}} dollars, after taxes.
				{{/in_ca}}
					""";

			System.out.println(
				"mustache: \n" +
					mustacheService.convertMustacheTemplateToHtml(mustache, Map.of(
						"name", "Chris",
						"value", 10000,
						"taxed_value", 10000 - (10000 * 0.4),
						"in_ca", true
					))
			);


		};
	}
}



