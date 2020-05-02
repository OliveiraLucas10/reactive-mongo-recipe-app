package com.oliveiralucaspro.repositories;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.oliveiralucaspro.bootstrap.RecipeBootstrap;
import com.oliveiralucaspro.domain.UnitOfMeasure;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureRepositoryIT {

    @Autowired
    UnitOfMeasureRepository unitOfMeasureRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Before
    public void setUp() throws Exception {

	recipeRepository.deleteAll();
	unitOfMeasureRepository.deleteAll();
	categoryRepository.deleteAll();

	RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryRepository, recipeRepository,
		unitOfMeasureRepository);

	recipeBootstrap.onApplicationEvent(null);
    }

    @Test
    public void findByDescription() throws Exception {

	Optional<UnitOfMeasure> uomOptional = unitOfMeasureRepository.findByDescription("Teaspoon");

	assertEquals("Teaspoon", uomOptional.get().getDescription());
    }

    @Test
    public void findByDescriptionCup() throws Exception {

	Optional<UnitOfMeasure> uomOptional = unitOfMeasureRepository.findByDescription("Cup");

	assertEquals("Cup", uomOptional.get().getDescription());
    }

}