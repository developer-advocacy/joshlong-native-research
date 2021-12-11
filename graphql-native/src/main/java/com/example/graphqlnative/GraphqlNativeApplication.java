package com.example.graphqlnative;

import graphql.execution.instrumentation.Instrumentation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.graphql.boot.GraphQlProperties;
import org.springframework.graphql.boot.GraphQlSourceBuilderCustomizer;
import org.springframework.graphql.boot.InvalidSchemaLocationsException;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.MissingSchemaException;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class GraphqlNativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlNativeApplication.class, args);
    }


    @Bean
    GraphQlSource myGraphqlSource(ResourcePatternResolver resourcePatternResolver,
                                  GraphQlProperties properties,
                                  ObjectProvider<DataFetcherExceptionResolver> exceptionResolversProvider,
                                  ObjectProvider<Instrumentation> instrumentationsProvider,
                                  ObjectProvider<GraphQlSourceBuilderCustomizer> sourceCustomizers,
                                  ObjectProvider<RuntimeWiringConfigurer> wiringConfigurers) {

        String location = properties.getSchema().getLocations()[0];
        List<Resource> schemaResources = List.of(new ClassPathResource(location));
        GraphQlSource.Builder builder = GraphQlSource.builder()
                .schemaResources(schemaResources.toArray(new Resource[0]))
                .exceptionResolvers(exceptionResolversProvider.orderedStream().collect(Collectors.toList()))
                .instrumentation(instrumentationsProvider.orderedStream().collect(Collectors.toList()));
        wiringConfigurers.orderedStream().forEach(builder::configureRuntimeWiring);
        sourceCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        try {
            return builder.build();
        } catch (MissingSchemaException exc) {
            throw new InvalidSchemaLocationsException(new String[]{location}, resourcePatternResolver, exc);
        }
    }
}

@Controller
class CustomerGraphqlController {

    @QueryMapping
    Collection<Customer> customers() {
        return List.of(new Customer(1, "Kimly"),
                new Customer(2, "Tammie"));
    }
}

record Customer(Integer id, String name) {
}