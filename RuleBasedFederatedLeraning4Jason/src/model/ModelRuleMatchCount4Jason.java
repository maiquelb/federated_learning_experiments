package model;

import java.util.Arrays;
import java.util.List;

import coordinator.ModelRuleMatchCount;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Pred;
import jason.asSyntax.Term;

import static jason.asSyntax.ASSyntax.createAtom;

public class ModelRuleMatchCount4Jason extends ModelRuleMatchCount {

	private int METADATA_COUNT = 2;

	public ModelRuleMatchCount4Jason() {
		super();
		this.nsamples = new int[0];
		this.classdist = new int[0][0];	
		System.out.println("********************* NEEWWW ***********88");
	}


	public Pred toPred() {
		Pred p = new Pred("dataset");
		
		Pred dataset_metrics = new Pred("dataset_metrics");
		
		Pred nsamples = new Pred("nsamples");
		ListTermImpl listNsamples = new ListTermImpl();
		for(int i=0;i<this.nsamples.length-1;i++)
			listNsamples.add(new NumberTermImpl(this.nsamples[i]));
		nsamples.addTerm(listNsamples);
		
		Pred nclasses = new Pred("nclasses");	
		nclasses.addTerm(new NumberTermImpl(this.nclasses));
		
		Pred classnames = new Pred("classnames");
		ListTermImpl listClassnames = new ListTermImpl();
		for(String s: this.classnames)
			listClassnames.add(createAtom(s));
		classnames.addTerm(listClassnames);
			
		
		Pred classdist = new Pred("classdist");
		ListTermImpl classdistList = new ListTermImpl();
		for(int i=0;i<this.classdist.length;i++) { //for each line in the classdist matrix
			ListTermImpl l = new ListTermImpl(); 
			for(int j=0;j<this.classdist[i].length;j++)
				l.add(new NumberTermImpl(this.classdist[i][j]));
			 
			
			classdistList.add(l);
		}
		classdist.addTerm(classdistList);
		
		Pred natt = new Pred("natt");
		natt.addTerm(new NumberTermImpl(this.natt));
		
		Pred rules = new Pred("rules");
		ListTermImpl listOfRules = new ListTermImpl();
		int count = 0;
		for(String[] rule: this.getRules()) { // for each rule
			ListTermImpl listRule = new ListTermImpl(); // each rule is converted to a list of terms 
			for(String s: rule)
				listRule.add(createAtom(s)); //add each atribute to the list of terms
			
			//add metrics for each rule - assuming the attribute rulesMetrics has the same line count of the attribute rules
			Pred support = new Pred("support");
			support.addTerm(new NumberTermImpl(this.getRulesMetrics().get(count)[0]));
			listRule.add(support);
			
			Pred error = new Pred("error");
			error.addTerm(new NumberTermImpl(this.getRulesMetrics().get(count)[1]));
			listRule.add(error);
			
			count++;
			//rules.addTerm(listRule);
			listOfRules.add(listRule);
		}
		rules.addTerm(listOfRules);
				
				
		
		dataset_metrics.addTerm(nsamples);
		dataset_metrics.addTerm(nclasses);
		dataset_metrics.addTerm(classnames);
		dataset_metrics.addTerm(classdist);
		dataset_metrics.addTerm(natt);
		
		
		p.addTerm(dataset_metrics);
		p.addTerm(rules);
		
		return p;
	}
	
	public boolean loadRules(Pred rules) {
		if(rules.getFunctor().toString().equals("rules"))
			if(rules.getTerm(0) instanceof ListTermImpl)//if the given value is a list. TODO: raise exception if it is not a list
				for(Term t: (ListTermImpl)rules.getTerm(0)) 
					if(t instanceof ListTermImpl) { //each rule is a list: TODO: raise exception if it is not a list
						String[] rule = new String[((ListTermImpl)t).size()-METADATA_COUNT]; //the rule is converted to an array of strings
						for(int i=0;i<((ListTermImpl)t).size()-METADATA_COUNT;i++) { //for each element of the list - the latest Terms are metadata
							rule[i] = ((ListTermImpl)t).get(i).toString();							
						}
						this.getRules().add(rule);

						//add rule metrics

						if(((Pred)((ListTermImpl)t).get(((ListTermImpl)t).size()-2)).getFunctor().toString().equals("support") &&
								((Pred)((ListTermImpl)t).get(((ListTermImpl)t).size()-1)).getFunctor().toString().equals("error")){
							String[] ruleMetrics = new String[2];
							ruleMetrics[0] = ((Pred)((ListTermImpl)t).get(((ListTermImpl)t).size()-2)).getTerm(0).toString();
							ruleMetrics[1] = ((Pred)((ListTermImpl)t).get(((ListTermImpl)t).size()-1)).getTerm(0).toString();
							this.getRulesMetrics().add(ruleMetrics);
						}
					}

		return false;
	}




	public boolean loadDatasetMetrics(Pred metrics) {
		if(metrics.getFunctor().toString().equals("dataset_metrics")) {
			for(Term t: metrics.getTerms())
				if(t instanceof Pred){
					switch(((Pred) t).getFunctor().toString()) {
					case "nclasses": 
						if(((Pred)t).getTerm(0).isNumeric())
							this.nclasses =  (int) ((NumberTermImpl)((Pred)t).getTerm(0)).solve();	
						break;

					case "natt": 
						if(((Pred)t).getTerm(0).isNumeric())
							this.natt=  (int) ((NumberTermImpl)((Pred)t).getTerm(0)).solve();
						break;

					case "classnames":
						int i=0;
						String[] classnames = new String[((Pred)t).getTerms().size()];
						for(Term term: ((Pred)t).getTerms())	
							classnames[i++] = term.toString();
						this.setClassnames(classnames);
						break;

					case "nsamples":
						this.nsamples = Arrays.copyOf(nsamples, nsamples.length+1); //increase the nsamples array length
						this.nsamples[nsamples.length-1] = (int) ((NumberTermImpl)((Pred)t).getTerm(0)).solve();
						break;

					case "classdist":
						i=0;
						this.classdist = addRowToMatrix(this.classdist); //add a row to the classdist matrix
						if(((Pred)t).getTerms().size()>this.classdist[0].length) {
							this.classdist = adjustMatrixColumns(this.classdist, ((Pred)t).getTerms().size()); //set the columns of classdist to the proper size
						}
						for(Term term: ((Pred)t).getTerms()) //for each value in the classdist property	
							this.classdist[this.classdist.length-1][i++] =  (int) ((NumberTermImpl)term).solve();

					}

				}

			// set the majority class -- code piece taken from the superclass
			int maj = -1, summaj = -1;		
			for (int i = 0; i < nclasses; i++) {
				int sum = 0;
				for (int j = 0; j < classdist.length; j++) {
					sum += classdist[j][i];
				}
				if (sum > summaj) {
					summaj = sum;
					maj = i;
				}			
			}
			this.majority = classnames[maj];		

			return false;

		}
		//TODO: lançar exception se o functor for diferente de "dataset metrics
		return false;

	}

	private int[][] addRowToMatrix(int[][] matrix) {
		int rows = matrix.length;
		int cols = (rows > 0 ? matrix[0].length : 0);

		// create a new matrix with one extra row
		int[][] newMatrix = new int[rows + 1][cols];

		// copy existing rows
		for (int i = 0; i < rows; i++) {
			System.arraycopy(matrix[i], 0, newMatrix[i], 0, cols);
		}

		// the new row (last one) is automatically filled with zeros
		return newMatrix;
	}


	/**
	 * Adjusts the number of columns in a 2D int matrix.
	 * If newCols is greater than the current number of columns, 
	 * the extra columns are filled with 0.
	 * If newCols is smaller, the matrix is truncated to the given size.
	 *
	 * @param matrix  the original matrix
	 * @param newCols the desired number of columns
	 * @return a new matrix with the adjusted number of columns
	 */
	private int[][] adjustMatrixColumns(int[][] matrix, int newCols) {
		int rows = matrix.length;
		int[][] newMatrix = new int[rows][newCols];

		for (int i = 0; i < rows; i++) {
			int colsToCopy = Math.min(matrix[i].length, newCols);
			System.arraycopy(matrix[i], 0, newMatrix[i], 0, colsToCopy);
		}

		return newMatrix;
	}


}
