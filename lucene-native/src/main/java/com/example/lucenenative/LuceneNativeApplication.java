package com.example.lucenenative;

import com.joshlong.lucene.DocumentWriteMapper;
import com.joshlong.lucene.LuceneTemplate;
import lombok.SneakyThrows;
import org.apache.lucene.document.*;
import org.apache.lucene.index.Term;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TypeHint(
        types = {
                org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl.class ,
        },
        access = {
                TypeAccess.DECLARED_CLASSES,
                TypeAccess.DECLARED_CONSTRUCTORS,
                TypeAccess.DECLARED_FIELDS,
                TypeAccess.DECLARED_METHODS,
        })
@SpringBootApplication
public class LuceneNativeApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LuceneNativeApplication.class, args);
    }

    @SneakyThrows
    private Document buildBlogPost(BlogPost post) {
        var document = new Document();
        document.add(new TextField("id", post.id(), Field.Store.YES));
        document.add(new TextField("title", post.title(), Field.Store.YES));
        document.add(new TextField("originalContent", post.originalContent(), Field.Store.YES));
        document.add(new LongPoint("time", post.date().getTime()));
        document.add(new StringField("key", buildHashKeyFor(post), Field.Store.YES));
        return document;
    }

    @SneakyThrows
    private String buildHashKeyFor(BlogPost blogPost) {
        return blogPost.id();
    }


    @Bean
    ApplicationRunner runner(LuceneTemplate template) {
        return args -> {
            //
            var blogs = Stream.of( //
                            new BlogPost(UUID.randomUUID().toString(), "the title 2", new Date(), "<P> This is the second original content</P>"),
                            new BlogPost(UUID.randomUUID().toString(), "The title 1", new Date(), "<P> This is the original content</p>")
                    ) //
                    .collect(Collectors.toMap(BlogPost::id, blogPost -> blogPost));
            template.write(blogs.values(), entry -> {
                var doc = buildBlogPost(entry);
                return new DocumentWriteMapper.DocumentWrite(new Term("key", buildHashKeyFor(entry)), doc);
            });
            var results = template.search("  title : \"title 2\" ", 10, document -> blogs.get(document.get("id")));
            results.forEach(blogPost -> System.out.println("found " + blogPost));
        };
    }
}

record BlogPost(String id, String title, Date date, String originalContent) {
}