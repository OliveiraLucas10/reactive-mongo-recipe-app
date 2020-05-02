package com.oliveiralucaspro.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.oliveiralucaspro.commands.IngredientCommand;
import com.oliveiralucaspro.converters.IngredientCommandToIngredient;
import com.oliveiralucaspro.converters.IngredientToIngredientCommand;
import com.oliveiralucaspro.domain.Ingredient;
import com.oliveiralucaspro.domain.Recipe;
import com.oliveiralucaspro.repositories.RecipeRepository;
import com.oliveiralucaspro.repositories.reactive.RecipeReactiveRepository;
import com.oliveiralucaspro.repositories.reactive.UnitOfMeasureReactiveRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;
    private final RecipeRepository recipeRepository;

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {

	return recipeReactiveRepository.findById(recipeId).flatMapIterable(Recipe::getIngredients)
		.filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId)).single().map(ingredient -> {
		    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
		    command.setRecipeId(recipeId);
		    return command;
		});

//        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
//
//        if (!recipeOptional.isPresent()){
//            //todo impl error handling
//            log.error("recipe id not found. Id: " + recipeId);
//        }
//
//        Recipe recipe = recipeOptional.get();
//
//        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
//                .filter(ingredient -> ingredient.getId().equals(ingredientId))
//                .map( ingredient -> ingredientToIngredientCommand.convert(ingredient)).findFirst();
//
//        if(!ingredientCommandOptional.isPresent()){
//            //todo impl error handling
//            log.error("Ingredient id not found: " + ingredientId);
//        }
//
//        //enhance command object with recipe id
//        IngredientCommand ingredientCommand = ingredientCommandOptional.get();
//        ingredientCommand.setRecipeId(recipe.getId());
//
//        return Mono.just(ingredientCommandOptional.get());
    }

    @Override
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
	Optional<Recipe> recipeOptional = recipeRepository.findById(command.getRecipeId());

	if (!recipeOptional.isPresent()) {

	    // todo toss error if not found!
	    log.error("Recipe not found for id: " + command.getRecipeId());
	    return Mono.just(new IngredientCommand());
	} else {
	    Recipe recipe = recipeOptional.get();

	    Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
		    .filter(ingredient -> ingredient.getId().equals(command.getId())).findFirst();

	    if (ingredientOptional.isPresent()) {
		Ingredient ingredientFound = ingredientOptional.get();
		ingredientFound.setDescription(command.getDescription());
		ingredientFound.setAmount(command.getAmount());
		ingredientFound.setUom(unitOfMeasureRepository.findById(command.getUom().getId()).block());

		// .orElseThrow(() -> new RuntimeException("UOM NOT FOUND"))); //todo address
		// this
		if (ingredientFound.getUom() == null) {
		    new RuntimeException("UOM NOT FOUND");
		}
	    } else {
		// add new Ingredient
		Ingredient ingredient = ingredientCommandToIngredient.convert(command);
		recipe.addIngredient(ingredient);
	    }

	    Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

	    Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
		    .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId())).findFirst();

	    // check by description
	    if (!savedIngredientOptional.isPresent()) {
		// not totally safe... But best guess
		savedIngredientOptional = savedRecipe.getIngredients().stream()
			.filter(recipeIngredients -> recipeIngredients.getDescription()
				.equals(command.getDescription()))
			.filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
			.filter(recipeIngredients -> recipeIngredients.getUom().getId()
				.equals(command.getUom().getId()))
			.findFirst();
	    }

	    // todo check for fail

	    // enhance with id value
	    IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand
		    .convert(savedIngredientOptional.get());
	    ingredientCommandSaved.setRecipeId(recipe.getId());

	    return Mono.just(ingredientCommandSaved);
	}

    }

    @Override
    public Mono<Void> deleteById(String recipeId, String idToDelete) {

	log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

	Recipe recipe = recipeRepository.findById(recipeId).get();

	if (recipe != null) {

	    log.debug("found recipe");

	    Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
		    .filter(ingredient -> ingredient.getId().equals(idToDelete)).findFirst();

	    if (ingredientOptional.isPresent()) {
		log.debug("found Ingredient");

		recipe.getIngredients().remove(ingredientOptional.get());
		recipeRepository.save(recipe);
	    }
	} else {
	    log.debug("Recipe Id Not found. Id:" + recipeId);
	}
	return Mono.empty();
    }
}
