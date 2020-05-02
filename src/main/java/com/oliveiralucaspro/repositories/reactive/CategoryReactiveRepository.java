package com.oliveiralucaspro.repositories.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.oliveiralucaspro.domain.Category;

import reactor.core.publisher.Mono;

public interface CategoryReactiveRepository extends ReactiveMongoRepository<Category, String> {

    Mono<Category> findByDescription(String description);
}
