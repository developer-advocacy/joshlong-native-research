package com.example.jgitnative;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.CoreConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.net.URI;


@NativeHint(options = "--enable-url-protocols=https")
@ResourceHint(patterns = "org.eclipse.jgit.internal.JGitText", isBundle = true)
@TypeHint(
        types = {
                CoreConfig.AutoCRLF.class,
                CoreConfig.CheckStat.class,
                CoreConfig.EOL.class,
                CoreConfig.HideDotFiles.class,
                CoreConfig.EolStreamType.class,
                CoreConfig.LogRefUpdates.class,
                CoreConfig.SymLinks.class,
                org.eclipse.jgit.internal.JGitText.class,
        },
        access = {
                TypeAccess.DECLARED_CLASSES, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.DECLARED_FIELDS, TypeAccess.DECLARED_METHODS
        }
)
@SpringBootApplication
public class JgitNativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JgitNativeApplication.class, args);
    }

    @Bean
    ApplicationRunner gitRunner(
            @Value("${root}") File file,
            @Value("${uri}") URI uri) {
        return args -> {

            if (file.exists()) {
                FileSystemUtils.deleteRecursively(file);
            }
            var repo = Git.cloneRepository().setDirectory(file).setURI(uri.toString()).call()
                    .getRepository();
            try (var git = new Git(repo)) {
                var status = git.status().call();
                System.out.println("the status is " + status.toString());
            }
        };
    }
}
