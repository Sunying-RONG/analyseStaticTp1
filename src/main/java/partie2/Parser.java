package partie2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;

import org.eclipse.jdt.core.dom.Statement;

public class Parser {
	
	public static final String projectPath = "/Users/rongsunying/eclipse-workspace/promotion/";
	public static final String projectSourcePath = projectPath + "/src";
	// MacOs
	public static final String jrePath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home/jre/";
	// Linux
//	public static final String jrePath = "/usr/lib/jvm/java-8-openjdk-amd64/jre/";
	
	public static float classesNumber;
	public static float methodsNumber;
	public static List<String> packages = new ArrayList<>();
	public static float attributesNumber;
	public static float statementsNumberMethod;
	public static float statementsAllNumber;
	public static Map<SimpleName, Integer> class_methodsNumber = new HashMap<SimpleName, Integer>();
	public static Map<SimpleName, Integer> class_attributesNumber = new HashMap<SimpleName, Integer>();
	public static Map<SimpleName, Integer> method_statementsNumber = new HashMap<SimpleName, Integer>();
	public static Map<SimpleName, Integer> method_paramNumber = new HashMap<SimpleName, Integer>();
	
	public static CallGraph callGraph;
    public static List<Edge> edgeList = new ArrayList<Edge>();
    public static List<Node> nodeList = new ArrayList<Node>();
	
	public static void main(String[] args) throws IOException {

		// read java files
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

		// every file
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			// System.out.println(content);

			CompilationUnit parse = parse(content.toCharArray());
			// print package info
			printPackageInfo(parse);
			
			// print class info
			printClassInfo(parse);
			
			// print attribute info
			printAttributeInfo(parse);
			
			// print methods info
			printMethodInfo(parse);

			// print variables info
			printVariableInfo(parse);
			
			// print method invocations
			printMethodInvocationInfo(parse);
			
			// print statement info
			printStatementInfo(parse);
			
			// print all statement info
			printAllStatementInfo(parse);
			System.out.println("------------");

		}
		// whole application
		System.out.println("Package number of application: " + packages.size() + "\n");
		System.out.println("Class number of application: " + classesNumber + "\n");
		System.out.println("Attribute number of application: " + attributesNumber + "\n");
		System.out.println("Method number (of classes) of application: " + methodsNumber + "\n");
		System.out.println("All statement number of application: " + statementsAllNumber + "\n");
		System.out.println("Statement number of application's methods: " + statementsNumberMethod + "\n");
		
		System.out.println("Average attribute number per class: " + attributesNumber/classesNumber + "\n");
		System.out.println("Average method number per class: " + methodsNumber/classesNumber + "\n");
		System.out.println("Average statement number per method: " + statementsNumberMethod/methodsNumber + "\n");

		Map<SimpleName, Integer> class_attributesNumber_sorted = class_attributesNumber
		        .entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(),
						(entry1, entry2) -> entry2, LinkedHashMap::new));
	    System.out.println("Class - attributes number: " + class_attributesNumber_sorted + "\n");
	    
	    Map<SimpleName, Integer> class_methodsNumber_sorted = class_methodsNumber
		        .entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(),
						(entry1, entry2) -> entry2, LinkedHashMap::new));
	    System.out.println("Class - methods number: " + class_methodsNumber_sorted + "\n");
	    
//	    The 10% of classes that have the most attributes
	    int top10ClasNum = Math.round(classesNumber*0.1) < 1 ? 1 : (int)Math.round(classesNumber*0.1);
	    
	    List<SimpleName> clasAttributeTop = new ArrayList<SimpleName>(class_attributesNumber_sorted.keySet())
	    		.subList(0, top10ClasNum);
	    System.out.println("The 10% of classes that have the most attributes: ");
	    clasAttributeTop.forEach(System.out::println);
	    
//	    The 10% of classes that have the most methods
	    List<SimpleName> clasMethodTop = new ArrayList<SimpleName>(class_methodsNumber_sorted.keySet())
	    		.subList(0, top10ClasNum);
	    System.out.println("\n" + "The 10% of classes that have the most methods: ");
	    clasMethodTop.forEach(System.out::println);
	    
//	    Classes that are part of the two previous categories at the same time
	    List<SimpleName> commonClasTop = new ArrayList<SimpleName>(clasAttributeTop);
	    commonClasTop.retainAll(clasMethodTop);
	    System.out.println("\n" + "The 10% of classes that have the most attributes and methods: ");
	    commonClasTop.forEach(System.out::println);
	    
//	    The 10% of methods that have the largest number of lines of code (for class)
	    Map<SimpleName, Integer> method_statementsNumber_sorted = method_statementsNumber
		        .entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(),
						(entry1, entry2) -> entry2, LinkedHashMap::new));
	    System.out.println("\n" + "Method - statements number: " + method_statementsNumber_sorted + "\n");
	    
	    int top10MethodNum = Math.round(methodsNumber*0.1) < 1 ? 1 : (int)Math.round(methodsNumber*0.1);
	    List<SimpleName> methodStateTop = new ArrayList<SimpleName>(method_statementsNumber_sorted.keySet())
	    		.subList(0, top10MethodNum);
	    System.out.println("The 10% of methods that have the largest number of statements (for methods of classes): ");
	    methodStateTop.forEach(System.out::println);
	    
//	    The maximum number of parameters compared to all methods of the application
	    Map<SimpleName, Integer> method_paramNumber_sorted = method_paramNumber
		        .entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(),
						(entry1, entry2) -> entry2, LinkedHashMap::new));
	    System.out.println("\n" + "Method - parameters number: " + method_paramNumber_sorted + "\n");
	    System.out.println("The maximum number of parameters compared to all methods of the application: " 
	    		+ method_paramNumber_sorted.values().stream().findFirst().get()+ "\n");
	    
//	    Classes that have more than X methods (the value of X is given)
	    Scanner sc= new Scanner(System.in);
	    System.out.print("Enter a number to show classes that have more than this number of methods: ");  
	    int num= sc.nextInt();
	    Map<SimpleName, Integer> class_methodsNumberX = class_methodsNumber_sorted.entrySet()
	            .stream().filter(x->x.getValue() > num)
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	    System.out.println("Classes that have more than "+ num + " methods: " + class_methodsNumberX);
	    
//	    create call graph
	    callGraph = new CallGraph(nodeList, edgeList);
	    createCallGraph();
	}
	
  public static void createCallGraph() {

      File imgFile = new File("./graph.png");
      try {
          imgFile.createNewFile();
      } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }

      DefaultDirectedGraph<String, DefaultEdge> g = 
        new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
      
      for (Node node : nodeList) {
          g.addVertex(node.getNodeName());
      }
      
      for (Edge edge : edgeList) {
          g.addEdge(edge.getDepartNode().getNodeName(), edge.getArriveNode().getNodeName());
      }
      
      JGraphXAdapter<String, DefaultEdge> graphAdapter = 
            new JGraphXAdapter<String, DefaultEdge>(g);
          mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
          layout.execute(graphAdapter.getDefaultParent());
          
          BufferedImage image = 
            mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
//        File imgFile = new File("src/test/resources/graph.png");
          try {
          ImageIO.write(image, "PNG", imgFile);
      } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }

//        assertTrue(imgFile.exists());
  }
	// read all java files from specific folder
	public static ArrayList<File> listJavaFilesForFolder(final File folder) {
		ArrayList<File> javaFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(listJavaFilesForFolder(fileEntry));
			} else if (fileEntry.getName().contains(".java")) {
				// System.out.println(fileEntry.getName());
				javaFiles.add(fileEntry);
			}
		}

		return javaFiles;
	}

	// create AST
	private static CompilationUnit parse(char[] classSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
 
		parser.setUnitName("");
 
		String[] sources = { projectSourcePath }; 
		String[] classpath = {jrePath};
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(classSource);
		
		return (CompilationUnit) parser.createAST(null); // create and parse
	}
	
	// navigate package information
	public static void printPackageInfo(CompilationUnit parse) {
		PackageDeclarationVisitor visitor = new PackageDeclarationVisitor();
		parse.accept(visitor);
		
		for (PackageDeclaration pack : visitor.getPackages()) {
			System.out.println("Package: " + pack);
			if (!packages.contains(pack.toString())) {
				packages.add(pack.toString());
			}
		}
	}

	// navigate class information
	public static void printClassInfo(CompilationUnit parse) {
		ClassDeclarationVisitor visitor = new ClassDeclarationVisitor();
		parse.accept(visitor);

		for (TypeDeclaration clas : visitor.getClasses()) {
			System.out.println("Class name: " + clas.getName());
		}
		classesNumber = classesNumber + visitor.getClassesNumber();
	}
	
	// navigate field information
	public static void printFieldAccessInfo(CompilationUnit parse) {
		FieldAccessVisitor visitor = new FieldAccessVisitor();
		parse.accept(visitor);
		
		for (SimpleName field : visitor.getFields()) {
			System.out.println("Field access name: " + field.getFullyQualifiedName());
		}
	}
	
	// navigate attribute information
	public static void printAttributeInfo(CompilationUnit parse) {
		ClassDeclarationVisitor visitor1 = new ClassDeclarationVisitor();
		parse.accept(visitor1);
		for (TypeDeclaration clas : visitor1.getClasses()) {
			Integer attributeEachClass = 0;
			FieldDeclarationVisitor visitor2 = new FieldDeclarationVisitor();
			clas.accept(visitor2);
			
			for (FieldDeclaration field : visitor2.getFields()) {
				
				VariableDeclarationFragmentVisitor visitor3 = new VariableDeclarationFragmentVisitor();
				field.accept(visitor3);

				for (VariableDeclarationFragment variableDeclarationFragment : visitor3
						.getVariables()) {
					System.out.println("Attribute name in this class: "
							+ variableDeclarationFragment.getName());
				}
				attributesNumber = attributesNumber + visitor3.getVariablesNumber();
				attributeEachClass = attributeEachClass + visitor3.getVariablesNumber();
			}
			class_attributesNumber.put(clas.getName(), (Integer)attributeEachClass);
		}
	}
		
	// navigate method information
	public static void printMethodInfo(CompilationUnit parse) {
		ClassDeclarationVisitor visitor1 = new ClassDeclarationVisitor();
		parse.accept(visitor1);
		
		for (TypeDeclaration clas : visitor1.getClasses()) {
			
			MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
			clas.accept(visitor);
		
			for (MethodDeclaration method : visitor.getMethods()) {
				System.out.println("Method name: " + method.getName()
						+ ". Return type: " + method.getReturnType2());
				method_paramNumber.put(method.getName(), method.parameters().size());
			}
			methodsNumber = methodsNumber + visitor.getMethodsNumber();
			class_methodsNumber.put(clas.getName(), (Integer)visitor.getMethodsNumber());
			
		}
	}

	// navigate variables inside method
	public static void printVariableInfo(CompilationUnit parse) {

		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {

			VariableDeclarationFragmentVisitor visitor2 = new VariableDeclarationFragmentVisitor();
			method.accept(visitor2);

			for (VariableDeclarationFragment variableDeclarationFragment : visitor2
					.getVariables()) {
				System.out.println("variable name: "
						+ variableDeclarationFragment.getName()
						+ ". variable Initializer: "
						+ variableDeclarationFragment.getInitializer());
			}

		}
	}
	
	// navigate statements inside method
	public static void printStatementInfo(CompilationUnit parse) {
		
		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {
			
			StatementVisitor visitor2 = new StatementVisitor();
			method.accept(visitor2);
			statementsNumberMethod = statementsNumberMethod + visitor2.getStatementsNumber();
			method_statementsNumber.put(method.getName(), (Integer)visitor2.getStatementsNumber());
		}
	}
	
	// navigate all statements in application
	public static void printAllStatementInfo(CompilationUnit parse) {
		
		StatementAllVisitor visitor = new StatementAllVisitor();
		parse.accept(visitor);
		
//			for (Statement state : visitor.getStatements()) {
//				System.out.println("Statement: "+state);
//			}
		statementsAllNumber = statementsAllNumber + visitor.getStatementsNumber();
	}
	
	// navigate method invocations inside method
	public static void printMethodInvocationInfo(CompilationUnit parse) {
	    
		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {
			MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
			method.accept(visitor2);
			String methodReceiver = method.resolveBinding().getDeclaringClass().getQualifiedName();
//			System.out.println("$$method receiver: "+methodReceiver);
			for (MethodInvocation methodInvocation : visitor2.getMethods()) {
			    String methodInvocReceiver = methodInvocation.resolveMethodBinding().getDeclaringClass().getQualifiedName();
				System.out.println("method " + method.getName() 
				        + " invoc method " + methodInvocation.getName()
				        + " #receiverClass: " + methodInvocReceiver);

				Node caller = new Node(methodReceiver + "." + method.getName().toString());
                Node callee = new Node(methodInvocReceiver + "." + methodInvocation.getName().toString());
//                System.out.println("methodReceiver: "+methodReceiver+"\n"
//                        + "methodInvocReceiver: "+methodInvocReceiver);
                if (!nodeList.stream().anyMatch(s -> s.equals(caller.getNodeName()))) {
                    nodeList.add(caller);
                }
                if (!nodeList.stream().anyMatch(s -> s.equals(callee.getNodeName()))) {
                    nodeList.add(callee);
                }
               
                Edge call = new Edge(caller, callee);
                if (!edgeList.contains(call)) { // ?
                    edgeList.add(call);
                }
                
			}

		}
	}
	

}
