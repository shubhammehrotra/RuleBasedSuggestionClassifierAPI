package org.suggestionmining.RuleBasedSuggestionClassifier;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

@Path("/check") 
public class RBS {

	private static MaxentTagger tagger = new MaxentTagger("C:\\Users\\shubham_15294\\Downloads\\stanford-postagger-full-2015-04-20\\models\\english-left3words-distsim.tagger"); // Reading the Stanford Tagger


	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{input}")
	/*public String Check(@PathParam("input")String hello) {
		return hello;
	}*/
	public static String getSuggestion(@PathParam("input")String checkSuggestion) {
		String tagged, sample, tag;
		checkSuggestion = checkSuggestion.trim();
		checkSuggestion = checkSuggestion.replace("\n", "").replace("\r", "");
		if (checkSuggestion.isEmpty()) return "No Suggestions found !";
		tag = checkSuggestion;
		StringBuilder sb = new StringBuilder();
		String[] keywords = {"suggest","recommend","hopefully","go for","request","it would be nice","adding","should come with","should be able","could come with", "i need" , "we need","needs", "would like to","would love to"}; // Suggestions based on Keywords.For simple regular expression matching below. Can be extended. Index is taken dynamically at the bottom.
		// go_VB. VB is explicitly mentioned here to only catch if go occurs as a verb.
		String[] wishKeywords = {".*would like.*(if).*", ".*i wish.*",".*i hope.*",".*i want.*",".*hopefully.*",".*if only.*",".*would be better if.*", ".*(should).*",".*would that.*",".*(can't believe).*(didn't).*",".*(don't believe).*(didn't).*",".*(do want).*",".*i can has.*"};
		//goldberg et all wish detection
		String[] parts = tag.split("\\. ");
		//totalCount += parts.length;
		int flag = 0;
		//System.out.println(parts.length);
		for (int i = 0; i < parts.length; i++) {
			//System.out.println(parts[i]);
			tagged = tagger.tagString(parts[i]); //POS Tagging sentence.
			//System.out.println(tagged); //Printing POS Tagged sentence
			if (parts[i].isEmpty()) {
				flag = 1;
				return "";
			} else if((tagged.indexOf("_MD ") != -1 && tagged.indexOf("_VB",tagged.indexOf("_MD ")) != -1) && tagged.indexOf("_MD ") < tagged.indexOf("_VB",tagged.indexOf("_MD ")) && tagged.indexOf("_JJ",tagged.indexOf("_VB",tagged.indexOf("_MD "))) != -1) {
				flag = 1;
				sb.append(parts[i] + ". ");
				//System.out.println(parts[i] + " Rule 1 ");
			} else if ((tagged.indexOf("_") - tagged.indexOf("VB") == -1) || (tagged.indexOf("_") - tagged.indexOf("VBP") == -1) || (tagged.indexOf("_") - tagged.indexOf("VBZ") == -1)) {
				flag = 1;
				sb.append(parts[i] + ". ");
				//System.out.println(parts[i]  + " Rule 2 ");
			} else if ((parts[i].indexOf("need to") != -1 || parts[i].indexOf("needs to") != -1) && parts[i].indexOf("?") == -1) { // The need to rule. Will be extended with.
				flag = 1;
				sb.append(parts[i] + ". ");
				//System.out.println(parts[i]  + " Rule 3 ");
			} /*else if (lp.parse(tag).pennString().replaceAll("[\r\n]+", "").replaceAll(" ", "").indexOf("SBAR(S(VB") != -1) {
					 flag = 1;
					 sb.append(parts[i] + ". ");
					 System.out.println(parts[i]);
			*/else {
					 int check = 0;
					 for (int j = 0; j < keywords.length; j++) {
						 if (parts[i].toLowerCase().matches("(.*)" + keywords[j] + "(.*)")) {
							 flag = 1;
							 sb.append(parts[i] + ". ");
							 check = 1;
							 break;
							 //System.out.println(parts[i]  + " Rule 4 ");
						 } 
					 } if (check == 0) {
						 for (int j = 0; j < wishKeywords.length; j++) {
							 if (parts[i].toLowerCase().matches(wishKeywords[j])) {
								 flag = 1;
								 sb.append(parts[i] + ". ");
								 break;
								 //System.out.println(parts[i] + " Rule 5 ");
							 }
						 }
					 } 
				 } 	
		} if (sb.toString().isEmpty()) return "No Suggestions found !";
		String a = sb.toString();
		a = a.replace("\n", "").replace("\r", "");
		return a; 

	}

}
