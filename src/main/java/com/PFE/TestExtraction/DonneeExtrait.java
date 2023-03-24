package com.PFE.TestExtraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonneeExtrait {


    @Field("dateDonneeExtrait")
    private String dateDonneeExtrait;

    @Field("dateValeurDonneeExtrait")
    private String dateValeurDonneeExtrait;

    @Field("operations")
    private String operations;


    @Field("debit")
    private String debit;

    @Field("credit")
    private String credit;
}
