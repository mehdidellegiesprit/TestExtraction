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
public class ExtraitBancaire {

    @Field("dateExtrait")
    private String dateExtrait;

    @Field("dateDuSoldeCrediteurDebutMois")
    private String dateDuSoldeCrediteurDebutMois;
    @Field("creditDuSoldeCrediteurDebutMois")
    private String creditDuSoldeCrediteurDebutMois;

    @Field("donneeExtraits")
    private ArrayList<DonneeExtrait> donneeExtraits;

    @Field("totalMouvementsDebit")
    private String totalMouvementsDebit;
    @Field("totalMouvementsCredit")
    private String totalMouvementsCredit;

    @Field("dateDuSoldeCrediteurFinMois")
    private String dateDuSoldeCrediteurFinMois;
    @Field("creditDuSoldeCrediteurFinMois")
    private String creditDuSoldeCrediteurFinMois;
}
