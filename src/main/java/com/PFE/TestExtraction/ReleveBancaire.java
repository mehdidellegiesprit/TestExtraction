package com.PFE.TestExtraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleveBancaire {

    @Field("nomBank")
    private String nomBank;
    @Field("nomEntreprise")
    private String nomEntreprise;
    @Field("extraits")
    private ArrayList<ExtraitBancaire> extraits;
    @Field("iban")
    private String iban;

}
