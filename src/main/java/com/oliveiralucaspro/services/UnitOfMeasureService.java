package com.oliveiralucaspro.services;

import com.oliveiralucaspro.commands.UnitOfMeasureCommand;

import reactor.core.publisher.Flux;

public interface UnitOfMeasureService {

    Flux<UnitOfMeasureCommand> listAllUoms();
}
