package com.chensoul.myapp.model.request;

import jakarta.validation.constraints.NotEmpty;

public record CustomerRequest(@NotEmpty(message = "Text cannot be empty") String text) {}
