package com.oliveiralucaspro.repositories.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.oliveiralucaspro.domain.Recipe;

public interface RecipeReactiveRepository extends ReactiveMongoRepository<Recipe, String> {

}
