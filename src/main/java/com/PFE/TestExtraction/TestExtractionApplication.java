package com.PFE.TestExtraction;

import io.github.jonathanlink.PDFLayoutTextStripper;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class TestExtractionApplication implements CommandLineRunner {

	@Autowired
	private CustomerRepository customerRepository ;

	/**
	 * Extracts the list of operations from a CIC bank statement in PDF format.
	 *
	 * @param releveBancaire the bank statement to extract operations from
	 * @param util the utility object containing various helper methods
	 * @throws IOException if there is an I/O error while reading the PDF file
	 */
	public static void  extractListeOperationFromCicBank (ReleveBancaire releveBancaire, Util util)throws IOException {
		int positionMax = util.getChaineMaxEspace().length() - util.getMaxEspaces() - 1 ;
		String path = "C:\\Users\\mehdi\\Desktop\\mehdi_test_extract_pfe\\TestExtraction\\Ext1.pdf";
		File file = new File(path) ;
		FileInputStream fis = new FileInputStream(file) ;
		PDDocument pdfDocument = PDDocument.load(fis);
		Splitter splitter = new Splitter();
		List<PDDocument> splitpages = splitter.split(pdfDocument);
		// pdfTextStripper = pdfReader
		PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper() ;
		pdfTextStripper.setSortByPosition(true);
		pdfTextStripper.setAddMoreFormatting(true);




		String nameBank = "CIC" ;
		///ok c'est l 'indicateur pour extraire nom societe / adresse / nom du PDG
		boolean ok = false ;int nombreLigneNonVide = 0 ;


		boolean ok_test_Releve_bancaire = false;
		boolean ok_test_format_releve_bancaire = false;
		boolean ok_test_head_array = false;
		boolean ok_test_debut_operation_array = false;
		String date_prelevement_extrait ="" ;
		String premier_ligne_solde_crediteur_au_date = "";
		String premier_ligne_credit_euro = "";
		String dernier_ligne_solde_crediteur_au_date = "";
		String dernier_ligne_credit_euro = "";
		boolean ok_test_date_valeur_operation = false;
		String date_valeur_operation = "";
		String debit_ou_credit = "" ;
		String liste_opertation="" ;
		String dateOperation = "";
		String date_valeur_Operation="";
		boolean ok_test_fin_page;
		boolean ok_total_mouvement = false;
		String debit_total_mouvement="";
		String credit_total_mouvement="";
		String IBAN = "" ;
		String dateOperation_precedente ="";
		String date_valeur_Operation_precedente ="";




		DonneeExtrait donneeExtrait = new DonneeExtrait() ;
		ExtraitBancaire extraitBancaire = new ExtraitBancaire();



//		ArrayList<Operation>operationsArrayList = new ArrayList<>();
		ArrayList<ExtraitBancaire> extraits = new ArrayList<>();
		ArrayList<DonneeExtrait> donneeExtraits=new ArrayList<>();




		for (PDDocument page : splitpages) {
//			System.out.println("..................................................");
//			System.out.println(page);
//			System.out.println("..................................................");
			String contentPage = pdfTextStripper.getText(page); //Le contenu de la page i (1,2,3,4,5...)
			String[] arrayOfLignesContentPage = contentPage.split("\n"); // decouppage de content page ligne par ligne  par le delimiteur l /n
			ok_test_fin_page = false;
			ok_test_head_array = false;
			for (int i = 0; i < arrayOfLignesContentPage.length; i++) {
				System.out.println(arrayOfLignesContentPage[i]) ;//BREAK POINT
				// split = diviser
				//.split("\\s+") = split par espace ou plusieurs espaces
				String[] arrayOfWordsPerLigne = arrayOfLignesContentPage[i].split("\\s+");
				System.out.println(arrayOfWordsPerLigne) ;//BREAK POINT
				String chaine = convertArrayToString(arrayOfWordsPerLigne) ;
				System.out.println(chaine); //break point ici
				/***********************DEBUT DATA DE LA SOCIETE***************************/
				if (chaine.contains("RELEVE ET INFORMATIONS BANCAIRES")){
					ok_test_Releve_bancaire = true ;
					System.out.println("ok_test_Releve_bancaire="+ok_test_Releve_bancaire);
				}
				if (isValidFormatOfDateReleveBancaire(chaine)){
					ok_test_format_releve_bancaire = true ;
					date_prelevement_extrait = chaine;
					System.out.println("ok_test_format_releve_bancaire="+ok_test_format_releve_bancaire);
					System.out.println("date_prelevement_extrait="+date_prelevement_extrait);
				}
				if (chaine.contains("Date Date valeur Opération Débit EUROS Crédit EUROS")){
					ok_test_head_array = true ;
					System.out.println("ok_test_head_array="+ok_test_head_array);
				}
				if (chaine.contains("SOLDE CREDITEUR AU")){
					System.out.println("arrayOfWordsPerLigne ="+arrayOfWordsPerLigne) ;
					System.out.println("chaine ="+chaine);
				}
				///hethi fi awel tableau
				if (ok_total_mouvement==false){
					if (chaine.contains("SOLDE CREDITEUR AU") && isValidDateCrediteur(arrayOfWordsPerLigne[4]) &&  isValidSoldeCrediteur(arrayOfWordsPerLigne[5])){
						ok_test_debut_operation_array = true ;
						premier_ligne_solde_crediteur_au_date = arrayOfWordsPerLigne[4] ;
						premier_ligne_credit_euro = arrayOfWordsPerLigne[5] ;
						System.out.println("ok_test_debut_operation_array="+ok_test_debut_operation_array);
						System.out.println("premier_ligne_solde_crediteur_au_date="+premier_ligne_solde_crediteur_au_date);
						System.out.println("premier_ligne_credit_euro="+premier_ligne_credit_euro);
						//ok_total_mouvement=!ok_total_mouvement;
					}
				}
				///hethi fi e5er tableau
				if (ok_total_mouvement==true){
					if (chaine.contains("SOLDE CREDITEUR AU") && isValidDateCrediteur(arrayOfWordsPerLigne[7]) &&  isValidSoldeCrediteur(arrayOfWordsPerLigne[8])){
						ok_test_debut_operation_array = false ;
						dernier_ligne_solde_crediteur_au_date = arrayOfWordsPerLigne[7] ;
						dernier_ligne_credit_euro = arrayOfWordsPerLigne[8] ;
						System.out.println("ok_test_debut_operation_array="+ok_test_debut_operation_array);
						System.out.println("dernier_ligne_solde_crediteur_au_date="+dernier_ligne_solde_crediteur_au_date);
						System.out.println("dernier_ligne_credit_euro="+dernier_ligne_credit_euro);
						ok_total_mouvement=false ;
					}
				}

				if (arrayOfWordsPerLigne.length>=3){
					 dateOperation = arrayOfWordsPerLigne[1] ;
					 date_valeur_Operation = arrayOfWordsPerLigne[2] ;
				}else {
					dateOperation = "" ;
					date_valeur_Operation = "" ;
				}
				if (chaine.contains("IBAN")){
					ok_test_fin_page=true ;
					liste_opertation = "" ;
				}
				if (isValidDateCrediteur(dateOperation) && isValidDateCrediteur(date_valeur_Operation)  && ok_test_fin_page==false && ok_test_head_array==true){
					if (liste_opertation.equals("") == false){
						System.out.println("\n............................................................\n");
						System.out.println("dateOperation_precedente="+dateOperation_precedente);
						System.out.println("date_valeur_Operation_precedente="+date_valeur_Operation_precedente);
						System.out.println("liste_opertation="+liste_opertation);
						System.out.println("\n............................................................\n");

						donneeExtrait.setOperations(liste_opertation);
						System.out.println(donneeExtrait);
						donneeExtraits.add(donneeExtrait);

						System.out.println(donneeExtraits);
						//!!!!


						donneeExtrait=new DonneeExtrait();
						//operationsArrayList=new ArrayList<Operation>();
						liste_opertation = "" ;
						System.out.println("liste_opertation="+liste_opertation);
					}
					ok_test_date_valeur_operation=true ;
					//date_valeur_operation = dateOperation ;//02/11/2020
					dateOperation_precedente = dateOperation ;
					date_valeur_Operation_precedente = date_valeur_Operation;

					debit_ou_credit = arrayOfWordsPerLigne[arrayOfWordsPerLigne.length-1];

					int position = arrayOfLignesContentPage[i].indexOf(debit_ou_credit);






					System.out.println("debit_ou_credit="+debit_ou_credit);
					System.out.println("debit_ou_credit="+debit_ou_credit);
					liste_opertation = liste_opertation+getOperationPerLigne(arrayOfWordsPerLigne,3,arrayOfWordsPerLigne.length-2)+"***";


					//operationsArrayList.add(new Operation(getOperationPerLigne(arrayOfW0ordsPerLigne,3,arrayOfWordsPerLigne.length-2))) ;


					System.out.println("liste_opertation="+liste_opertation);//break point

					donneeExtrait.setDateDonneeExtrait(dateOperation_precedente);
					donneeExtrait.setDateValeurDonneeExtrait(date_valeur_Operation_precedente);

					if (position!=-1){
						if (position<positionMax){
							donneeExtrait.setDebit(debit_ou_credit);
							donneeExtrait.setCredit("");
						}
						if (position>positionMax){
							donneeExtrait.setDebit("");
							donneeExtrait.setCredit(debit_ou_credit);
						}
					};

				}
				if (chaine.contains("Information sur la protection des comptes :")){
					ok_test_fin_page=true;
					System.out.println("ok_test_fin_page="+ok_test_fin_page);//break point
				}
				if (ok_test_date_valeur_operation && isValidDateCrediteur(dateOperation) == false && isValidDateCrediteur(date_valeur_Operation) == false && chaine.length()!=0 && ok_test_fin_page==false && ok_test_head_array==true){
					if ((chaine.contains("Date Date valeur Opération Débit EUROS Crédit EUROS")==false) && (chaine.contains("SOLDE CREDITEUR AU")==false)&& (chaine.contains("Total des mouvements")==false) && (chaine.contains("IBAN")==false)){
						liste_opertation = liste_opertation+getOperationPerLigne(arrayOfWordsPerLigne,1,arrayOfWordsPerLigne.length-1)+"***";
						//operationsArrayList.add(new Operation(getOperationPerLigne(arrayOfWordsPerLigne,1,arrayOfWordsPerLigne.length-1)));
						System.out.println("liste_opertation="+liste_opertation);//break point
					}
				}
				//lezemni ntayah win toufa l page

				if (chaine.contains("Total des mouvements")){
					ok_total_mouvement=true;
					debit_total_mouvement=arrayOfWordsPerLigne[4];
					credit_total_mouvement=arrayOfWordsPerLigne[5];
//					mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
//					donneeExtrait.setOperations(liste_opertation);
//					System.out.println(donneeExtrait);
//					donneeExtraits.add(donneeExtrait);

//					mmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
					donneeExtrait.setOperations(liste_opertation);
					System.out.println(donneeExtrait);
					donneeExtraits.add(donneeExtrait);
//					mmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
//					System.out.println("ok_total_mouvement="+ok_total_mouvement);
//					System.out.println("debit_total_mouvement="+debit_total_mouvement);
//					System.out.println("credit_total_mouvement="+credit_total_mouvement);
				}
				if (chaine.contains("IBAN")){
					IBAN = getOperationPerLigne(arrayOfWordsPerLigne,3,arrayOfWordsPerLigne.length-1) ;
					System.out.println("------------------------------------------------------------");
					System.out.println("date_prelevement_extrait ="+date_prelevement_extrait);
					System.out.println("premier_ligne_solde_crediteur_au_date ="+premier_ligne_solde_crediteur_au_date);
					System.out.println("premier_ligne_credit_euro ="+premier_ligne_credit_euro);

					System.out.println("debit_total_mouvement="+debit_total_mouvement);
					System.out.println("credit_total_mouvement="+credit_total_mouvement);



					System.out.println("dernier_ligne_solde_crediteur_au_date ="+dernier_ligne_solde_crediteur_au_date);
					System.out.println("dernier_ligne_credit_euro ="+dernier_ligne_credit_euro);

					System.out.println("IBAN ="+IBAN);



					extraitBancaire.setDateExtrait(date_prelevement_extrait);
					extraitBancaire.setDateDuSoldeCrediteurDebutMois(premier_ligne_solde_crediteur_au_date);
					extraitBancaire.setCreditDuSoldeCrediteurDebutMois(premier_ligne_credit_euro);

					//donneeExtraits.add(operation_fin_tableau) ;
					extraitBancaire.setDonneeExtraits(donneeExtraits);


					extraitBancaire.setTotalMouvementsDebit(debit_total_mouvement);
					extraitBancaire.setTotalMouvementsCredit(credit_total_mouvement);

					extraitBancaire.setDateDuSoldeCrediteurFinMois(dernier_ligne_solde_crediteur_au_date);
					extraitBancaire.setCreditDuSoldeCrediteurFinMois(dernier_ligne_credit_euro);
					System.out.println(extraitBancaire);
					extraits.add(extraitBancaire);
					System.out.println(extraits);//break point
					extraitBancaire = new ExtraitBancaire() ;
					donneeExtraits=new ArrayList<>();

					//releveBancaire.setIban(IBAN);
				}




			}
		}
		releveBancaire.setIban(IBAN);
		releveBancaire.setExtraits(extraits);
		pdfDocument.close();
		fis.close();

	}

	/**
	 * Retrieves the text of an operation from an array of words.
	 *
	 * @param arrayOfWordsPerLigne the array of words to be searched
	 * @param debut the starting index of the operation
	 * @param fin the ending index of the operation
	 * @return the text of the operation, represented as a string
	 */
	private static String getOperationPerLigne(String[] arrayOfWordsPerLigne, int debut, int fin) {
		String ch = "" ;
		if (arrayOfWordsPerLigne.length!=0){
			for (int j = debut; j <= fin; j++) {
				ch = ch + arrayOfWordsPerLigne[j]+' ' ; ///BreakPoint
			}
			System.out.println("ch="+ch+"***");
			if (ch.charAt(ch.length()-1) == ' '){///BreakPoint
				ch = ch.substring(0,ch.length()-1) ;
			}
			System.out.println("rs="+ch+"***");///BreakPoint
		}
		return ch ;
	}

	/**
	 * Determines whether a given string represents a valid balance for a creditor transaction.
	 *
	 * @param s the string to be checked
	 * @return true if the string represents a valid balance, false otherwise
	 */
	private static boolean isValidSoldeCrediteur(String s) {
		boolean v = true ;
		if ((s!=null) && (s.length()!=0)){
			//348.107,92
			for(int i=0;i<s.length();i++){
				v = ( s.charAt(i)>='0' && s.charAt(i)<='9' ) || s.charAt(i) == '.' || s.charAt(i) == ',' ;
				if (v == false){
					return false ;
				}
			}
		}
		return v ;
	}

	/**
	 * Determines whether a given string represents a valid date for a creditor transaction.
	 *
	 * @param s the string to be checked
	 * @return true if the string represents a valid date, false otherwise
	 */
	private static boolean isValidDateCrediteur(String s) {
		boolean v = false ;
		if ((s!=null) && (s.length()==10)){
			String day_string = s.substring(0,1);
			String month_string = s.substring(3,4);
			String year_string = s.substring(6,9) ;
			v = (isNumeric(day_string) && isNumeric(month_string)) &&isNumeric(year_string)  ;
		}
		return v ;
	}



	/**
	 * Determines whether a given string is numeric.
	 *
	 * @param str the string to be checked
	 * @return true if the string is numeric, false otherwise
	 */
	private static boolean isNumeric(String str){
		return str != null && str.matches("[0-9.]+");
	}
	/**
	 * Determines whether a given string represents a valid date format for a bank statement.
	 *
	 * @param chaine the string to be checked
	 * @return true if the string is a valid date format, false otherwise
	 */
	private static boolean isValidFormatOfDateReleveBancaire(String chaine) {
		boolean v = false ;//				jj/mm/aaaaa a nee pas oublier l slach / !!!!!
		if (chaine.length()>=11){
			String day_string = chaine.substring(0,1);
			String month_string = chaine.substring(3,chaine.length()-5);
			String year_string = chaine.substring(chaine.length()-5+2,chaine.length()-1) ;
			v = (isNumeric(day_string) && isValidMonth(month_string)) &&isNumeric(year_string)  ;
		}
		return v ;
	}

	/**
	 * Determines whether a given string represents a valid month name.
	 *
	 * @param month_string the string to be checked
	 * @return true if the string represents a valid month name, otherwise false
	 */
	private static boolean isValidMonth(String month_string) {
		if (month_string.equals("janvier")) {
			return true ;
		}
		if (month_string.equals("février")) {
			return true ;
		}
		if (month_string.equals("mars")) {
			return true ;
		}
		if (month_string.equals("avril")) {
			return true ;
		}
		if (month_string.equals("mai")) {
			return true ;
		}
		if (month_string.equals("juin")) {
			return true ;
		}
		if (month_string.equals("juillet")) {
			return true ;
		}
		if (month_string.equals("août")) {
			return true ;
		}
		if (month_string.equals("septembre")) {
			return true ;
		}
		if (month_string.equals("october")) {
			return true ;
		}
		if (month_string.equals("novembre")) {
			return true ;
		}
		if (month_string.equals("décembre")) {
			return true ;
		}
		return false ;
	}

	/**
	 * Extracts the name of the company and the maximum spaces for debit and
	 * credit values from a CIC Bank transaction PDF.
	 *
	 * @param util a utility class providing access to the transaction PDF
	 * @return void
	 * @throws IOException if there is an error reading the PDF file
	 */
	public static void extractDataFromCicBank (Util util)throws IOException {
		String path = "C:\\Users\\mehdi\\Desktop\\mehdi_test_extract_pfe\\TestExtraction\\Ext1.pdf";
		File file = new File(path) ;
		FileInputStream fis = new FileInputStream(file) ;
		PDDocument pdfDocument = PDDocument.load(fis);
		Splitter splitter = new Splitter();
		List<PDDocument> splitpages = splitter.split(pdfDocument);
		// pdfTextStripper = pdfReader
		PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper() ;
		pdfTextStripper.setSortByPosition(true);
		pdfTextStripper.setAddMoreFormatting(true);




		String nameBank = "CIC" ;
		///ok c'est l 'indicateur pour extraire nom societe / adresse / nom du PDG
		boolean ok = false ;int nombreLigneNonVide = 0 ;
		String nomSociete = "" ;
		String dateOperation = "" ;
		String date_valeur_Operation = "" ;

		// Expression régulière pour trouver les espaces à partir de la fin de la chaîne jusqu'au premier caractère non-espace
		Pattern pattern = Pattern.compile("\\s+(?!.*\\s)");
		int maxEspaces = 0; // Variable pour stocker le maximum nombre d'espaces trouvés
		String chaine_max_espace = "" ;
		for (PDDocument page : splitpages) {
//			System.out.println("..................................................");
//			System.out.println(page);
//			System.out.println("..................................................");
			String contentPage = pdfTextStripper.getText(page); //Le contenu de la page i (1,2,3,4,5...)
			String[] arrayOfLignesContentPage = contentPage.split("\n"); // decouppage de content page ligne par ligne  par le delimiteur l /n
			for (int i = 0; i < arrayOfLignesContentPage.length; i++) {

				/// hethi normalement eli bech na3diha lel matcher !!!
				System.out.println(arrayOfLignesContentPage[i]) ;//BREAK POINT
				String[] arrayOfWordsPerLigne = arrayOfLignesContentPage[i].split("\\s+");///men e5er l page jusqu"a l awel caractere trouveè
				System.out.println(arrayOfWordsPerLigne) ;//BREAK POINT
				String chaine = convertArrayToString(arrayOfWordsPerLigne) ;
				if (arrayOfWordsPerLigne.length>=3){
					dateOperation = arrayOfWordsPerLigne[1] ;
					date_valeur_Operation = arrayOfWordsPerLigne[2] ;
				}else {
					dateOperation = "" ;
					date_valeur_Operation = "" ;
				}
				// je peux faire la somme en meme temps
				if (isValidDateCrediteur(dateOperation) && isValidDateCrediteur(date_valeur_Operation) ){
					System.out.println("---------------------------------------"+arrayOfLignesContentPage[i].length());
					//lezemni n3adi chaine eli meme exemple que que sublime texte e!!!
					Matcher matcher = pattern.matcher(arrayOfLignesContentPage[i]);
					// Si des espaces ont été trouvés
					if (matcher.find()) {
						int count = matcher.group().length();
						// Mettre à jour le maximum nombre d'espaces trouvés
						if (count > maxEspaces) {
							maxEspaces = count;
							chaine_max_espace = arrayOfLignesContentPage[i] ;
						}
					}
				}

				/***********************DEBUT NOM DE LA SOCIETE***************************/
				if (chaine.contains(nameBank) && (ok==false)){//BREAK POINT
					ok = true ;
				}
				if (ok){
					// il faut compter les nombres de ligne remplie par des caracteres !
					if (arrayOfWordsPerLigne.length!=0){
						nombreLigneNonVide = nombreLigneNonVide +1 ;
					}
					if ((nombreLigneNonVide == 6 ) && (nomSociete.equals(""))){
						nomSociete = chaine ;
						System.out.println("nomSociete="+nomSociete);
						//return nomSociete ;
					}
				}
				/***********************FIN NOM DE LA SOCIETE***************************/





			}
		}
		System.out.println("Le maximum nombre d'espaces trouvés de la fin de page jusqu'a premier caratere est !  est : " + maxEspaces);

		pdfDocument.close();
		fis.close();
		util.setNameSociete(nomSociete);
		util.setMaxEspaces(maxEspaces);
		util.setChaineMaxEspace(chaine_max_espace);

	}

	/**
	 * Searches a PDF document for the name of the bank.
	 *
	 * @return the name of the bank, or an empty string if the bank name could not be found
	 * @throws IOException if an I/O error occurs while reading the PDF document
	 */
	public static String findNameBank ()throws IOException {
		String path = "C:\\Users\\mehdi\\Desktop\\mehdi_test_extract_pfe\\TestExtraction\\Ext1.pdf";
		File file = new File(path) ;
		FileInputStream fis = new FileInputStream(file) ;
		PDDocument pdfDocument = PDDocument.load(fis);
		Splitter splitter = new Splitter();
		List<PDDocument> splitpages = splitter.split(pdfDocument);
		// pdfTextStripper = pdfReader
		PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper() ;
		pdfTextStripper.setSortByPosition(true);
		pdfTextStripper.setAddMoreFormatting(true);
		String nameBank = "" ;
		for (PDDocument page : splitpages) {
//			System.out.println("..................................................");
//			System.out.println(page);
//			System.out.println("..................................................");
			String contentPage = pdfTextStripper.getText(page); //Le contenu de la page i (1,2,3,4,5...)
			String[] arrayOfLignesContentPage = contentPage.split("\n"); // decouppage de content page ligne par ligne  par le delimiteur l /n
			for (int i = 0; i < arrayOfLignesContentPage.length; i++) {

				System.out.println(arrayOfLignesContentPage[i]) ;//BREAK POINT
				// split = diviser
				//.split("\\s+") = split par espace ou plusieurs espaces
				String[] arrayOfWordsPerLigne = arrayOfLignesContentPage[i].split("\\s+");
				System.out.println(arrayOfWordsPerLigne) ;//BREAK POINT
				String chaine = convertArrayToString(arrayOfWordsPerLigne) ;
				if (chaine.contains("CIC")){//BREAK POINT
					pdfDocument.close();
					fis.close();
					nameBank = chaine ;//BREAK POINT
					return nameBank ;//BREAK POINT
					//System.out.println("BANK ---CIC PARIS KLEBER--- BANK");
					//System.out.println("nameBank"+nameBank);
				}
				if (chaine.equals("AVOIR YA BRO LEZEM NCHOUFOU NOM EXACT EMTE3 L BANQUE POPULAIRE !!!!!")){//BREAK POINT
					pdfDocument.close();
					fis.close();
					nameBank = chaine ;//BREAK POINT
					return nameBank ;//BREAK POINT
					//System.out.println("BANK ---CIC PARIS KLEBER--- BANK");
					//System.out.println("nameBank"+nameBank);
				}
			}
		}
		pdfDocument.close();
		fis.close();
		return nameBank ;

	}

	public static void main(String[] args) throws IOException {
		String nameBank  = findNameBank() ;
		System.out.println("nameBank="+"*"+nameBank+"*");
//		if (nameBank.contains("CIC")){
//			extractDataFromCicBank();
//		}
//		String nomSociete = extractNameSocieteFromCicBank();
//		System.out.println("nomSociete="+"*"+nomSociete+"*");

		Util util = new Util() ;
		extractDataFromCicBank(util);
//		System.out.println("util="+ util);
//		System.out.println("length ch ="+ util.getChaineMaxEspace().length());
//		System.out.println("charAt ch ="+ util.getChaineMaxEspace().charAt(148-34-1));










		ReleveBancaire releveBancaire = new ReleveBancaire() ;
//
//		System.out.println(releveBancaire);
		releveBancaire.setNomBank(nameBank);
		extractListeOperationFromCicBank(releveBancaire, util);
		releveBancaire.setNomEntreprise(util.getNameSociete());
		System.out.println(releveBancaire);


	}

	private static String convertArrayToString(String[] arrayOfWordsPerLigne) {
		String ch = "" ;
		if (arrayOfWordsPerLigne.length!=0){
			for (int j = 0; j < arrayOfWordsPerLigne.length; j++) {
				boolean ok = arrayOfWordsPerLigne[j].equals("") ;
				if (ok==false){
					ch = ch + arrayOfWordsPerLigne[j]+' ' ; ///BreakPoint
				}
			}
			System.out.println("ch="+ch+"***");
			if (ch.charAt(ch.length()-1) == ' '){///BreakPoint
				ch = ch.substring(0,ch.length()-1) ;
			}
			System.out.println("rs="+ch+"***");///BreakPoint
		}
		return ch ;
	}

	@Override
	public void run(String... args) throws Exception {
		Customer c1 = new Customer("1","mehdi","dellegi") ;
		Customer c2 = new Customer("2","aa","aa...") ;
		Customer c3 = new Customer("3","bb","bb....") ;
		Customer c4 = new Customer("4","cc","cc....") ;
		customerRepository.save(c1) ;
		customerRepository.save(c2) ;
		customerRepository.save(c3) ;
		customerRepository.save(c4) ;

		System.out.println("**********************************");
		List<Customer> customers = customerRepository.findAll() ;
		for (Customer c : customers){
			System.out.println(c.toString()) ;
		}
	}
}
