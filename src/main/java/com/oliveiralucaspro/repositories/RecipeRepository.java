package com.oliveiralucaspro.repositories;

import com.oliveiralucaspro.domain.Recipe;
import org.springframework.data.repository.CrudRepository;


public interface RecipeRepository extends CrudRepository<Recipe, String> {
}
