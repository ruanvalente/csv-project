package example.csv.entity.dto;

import jakarta.validation.constraints.NotNull;

public record CSVRequest(@NotNull String name, @NotNull  String email) { }