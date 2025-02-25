package com.jbhunt.pickleballmatchmaker.mongo;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "pickleballUser")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PickleballUser {
    @Id
    private String id;

    @NotBlank(message = "userName is mandatory")
    @Indexed(unique = true)
    private String userName;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Age is mandatory")
    @Min(value = 0, message = "Age must be greater than or equal to 0")
    private Integer age;

    @NotNull(message = "Skill level is mandatory")
    private Double skillLevel;

    @NotNull(message = "Zip code is mandatory")
    private int zipCode;

    public String getUserName() {
        return userName;
    }

    public Double getSkillLevel() {
        return skillLevel;
    }
}